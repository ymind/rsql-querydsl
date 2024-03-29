package team.yi.rsql.querydsl

import com.querydsl.core.types.*
import com.querydsl.core.types.dsl.*
import com.querydsl.jpa.impl.*
import cz.jirutka.rsql.parser.RSQLParser
import team.yi.rsql.querydsl.exception.RsqlException
import team.yi.rsql.querydsl.handler.SortFieldTypeHandler
import team.yi.rsql.querydsl.util.*

class QuerydslRsql<E> private constructor(builder: Builder<E>) {
    private val predicateBuilder: PredicateBuilder<E>
    private val from: PathBuilder<E>?
    private val selectString: String?
    private val selectExpressions: List<Expression<*>>?
    private val where: String?
    private val globalPredicate: BooleanExpression?
    private val offset: Long?
    private val limit: Long?
    private val sortString: String?
    private val sortExpressions: List<OrderSpecifier<*>>?
    private val rsqlConfig: RsqlConfig

    init {
        this.rsqlConfig = builder.rsqlConfig
        this.predicateBuilder = PredicateBuilder(rsqlConfig)

        RsqlUtil.validateOperators(rsqlConfig.operators)

        requireNotNull(rsqlConfig.entityManager) { "Entity manager cannot be null." }

        this.from = buildFrom(builder.entityClass, builder.entityName)
        this.selectString = builder.selectString
        this.selectExpressions = builder.selectExpressions
        this.where = builder.where
        this.globalPredicate = builder.globalPredicate
        this.offset = builder.offset
        this.limit = builder.limit
        this.sortString = builder.sort
        this.sortExpressions = builder.orderSpecifiers
    }

    fun buildJPAQuery(): JPAQuery<*> {
        requireNotNull(from)

        return buildJPAQuery(from)
    }

    fun buildJPAQuery(fromPath: PathBuilder<E>): JPAQuery<*> = buildJPAQuery(fromPath, buildSelectExpressions(fromPath))

    fun buildJPAQuery(fromPath: PathBuilder<E>, select: List<Expression<*>>?): JPAQuery<*> {
        return try {
            val entityManager = rsqlConfig.entityManager
            val templates = JPAProvider.getTemplates(entityManager)
            val queryFactory = JPAQueryFactory(templates, entityManager)
            val jpaQuery = queryFactory.from(fromPath).where(buildPredicate(fromPath))

            if (!select.isNullOrEmpty()) {
                if (select.size == 1) {
                    jpaQuery.select(select[0])
                } else {
                    jpaQuery.select(*select.toTypedArray())
                }
            }

            if (offset != null && offset >= 0) jpaQuery.offset(offset)
            if (limit != null && limit >= 1) jpaQuery.limit(limit)

            buildOrder(fromPath)?.let { jpaQuery.orderBy(*it.toTypedArray()) }

            jpaQuery
        } catch (ex: Exception) {
            throw RsqlException(ex)
        }
    }

    fun buildPredicate(fromPath: PathBuilder<E>): Predicate? {
        fromPath.type.let {
            if (where.isNullOrBlank()) return globalPredicate

            val operators = RsqlUtil.getOperators(rsqlConfig.operators)
            val rootNode = RSQLParser(operators).parse(where)
            val predicate = rootNode.accept(PredicateBuilderVisitor(fromPath, predicateBuilder))

            return when {
                globalPredicate == null && predicate == null -> null
                globalPredicate == null -> predicate
                else -> globalPredicate.and(predicate)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun buildOrder(fromPath: PathBuilder<E>): MutableList<OrderSpecifier<*>>? {
        val orderSpecifiers = mutableListOf<OrderSpecifier<*>>()

        if (sortString == null) {
            this.sortExpressions?.let { orderSpecifiers.addAll(it) }
        } else {
            val sorts = RsqlUtil.parseSortExpression(sortString)

            sorts.keys.forEach { sortSelect ->
                val sortPath = getSortPath(fromPath, FieldMetadata.parseFieldSelector(fromPath.type, sortSelect))
                val path = sortPath as? Path<Comparable<*>>
                val order = OrderSpecifier(sorts[sortSelect], path)

                orderSpecifiers.add(order)
            }
        }

        return if (orderSpecifiers.isEmpty()) null else orderSpecifiers
    }

    fun buildSelectExpressions(fromPath: PathBuilder<E>): List<Expression<*>> {
        return this.selectExpressions ?: RsqlUtil.parseSelect(selectString ?: return emptyList(), fromPath)
    }

    private fun getSortPath(fromPath: PathBuilder<E>, fieldMetadataList: List<FieldMetadata>): Expression<*> {
        val processedPaths = mutableListOf<Expression<*>>()
        var typeHandler: SortFieldTypeHandler

        for (i in fieldMetadataList.indices) {
            typeHandler = rsqlConfig.getSortFieldTypeHandler(fieldMetadataList[i])

            val parent = if (i == 0) fromPath else processedPaths[i - 1]
            val path = typeHandler.getPath(parent) ?: continue

            processedPaths.add(path)
        }

        return processedPaths[processedPaths.size - 1]
    }

    private fun buildFrom(entityClass: Class<E>?, entityName: String?): PathBuilder<E>? {
        @Suppress("UNCHECKED_CAST")
        val entityClazz = when {
            entityClass != null -> entityClass
            entityName != null -> RsqlUtil.getClassForEntityString(entityName, rsqlConfig.entityManager) as Class<E>?
            else -> null
        } ?: return null

        return pathFactory.create(entityClazz)
    }

    open class Builder<E> {
        internal val rsqlConfig: RsqlConfig
        internal var entityClass: Class<E>? = null
        internal var entityName: String? = null
        internal var selectString: String? = null
        internal var selectExpressions: List<Expression<*>>? = null
        internal var where: String? = null
        internal var globalPredicate: BooleanExpression? = null
        internal var offset: Long? = null
        internal var limit: Long? = null
        internal var sort: String? = null
        internal var orderSpecifiers: List<OrderSpecifier<*>>? = null

        constructor(rsqlConfig: RsqlConfig) {
            this.rsqlConfig = rsqlConfig
        }

        private constructor(builder: Builder<E>) {
            this.entityClass = builder.entityClass
            this.entityName = builder.entityName
            this.selectString = builder.selectString
            this.selectExpressions = builder.selectExpressions
            this.where = builder.where
            this.globalPredicate = builder.globalPredicate
            this.offset = builder.offset
            this.limit = builder.limit
            this.sort = builder.sort
            this.rsqlConfig = builder.rsqlConfig
            this.orderSpecifiers = builder.orderSpecifiers
        }

        fun select(select: String?): BuildBuilder<E> = BuildBuilder(this).apply {
            this.selectString = select
        }

        fun select(vararg expression: Expression<*>?): BuildBuilder<E> = BuildBuilder(this).apply {
            this.selectExpressions = expression.filterNotNull().distinct()
        }

        fun from(entityName: String?): BuildBuilder<E> = BuildBuilder(this).apply {
            this.entityName = entityName
        }

        fun from(entityClass: Class<E>?): BuildBuilder<E> = BuildBuilder(this).apply {
            this.entityClass = entityClass
        }

        fun where(where: String?): BuildBuilder<E> = BuildBuilder(this).apply {
            this.where = where
        }

        class BuildBuilder<E>(builder: Builder<E>) : Builder<E>(builder) {
            fun globalPredicate(globalPredicate: BooleanExpression?): BuildBuilder<E> = this.apply {
                super.globalPredicate = globalPredicate
            }

            fun build(): QuerydslRsql<E> {
                return try {
                    QuerydslRsql(this)
                } catch (ex: Exception) {
                    throw RsqlException(ex)
                }
            }

            fun offset(offset: Int?): BuildBuilder<E> = offset(offset?.toLong())
            fun offset(offset: Long?): BuildBuilder<E> = this.apply { super.offset = offset }

            fun limit(limit: Int?): BuildBuilder<E> = limit(limit?.toLong())
            fun limit(limit: Long?): BuildBuilder<E> = this.apply { super.limit = limit }

            fun limit(offset: Int?, limit: Int?): BuildBuilder<E> = limit(offset?.toLong(), limit?.toLong())
            fun limit(offset: Long?, limit: Long?): BuildBuilder<E> = this.apply {
                super.offset = offset
                super.limit = limit
            }

            fun page(pageNumber: Int, pageSize: Int): BuildBuilder<E> = page(pageNumber.toLong(), pageSize.toLong())
            fun page(pageNumber: Int, pageSize: Long): BuildBuilder<E> = page(pageNumber.toLong(), pageSize)

            fun page(pageNumber: Long, pageSize: Long): BuildBuilder<E> = this.apply {
                super.limit = pageSize
                super.offset = pageNumber * pageSize
            }

            fun sort(sort: String?): BuildBuilder<E> = this.apply { super.sort = sort }
            fun sort(vararg expression: OrderSpecifier<*>?): BuildBuilder<E> = this.apply {
                super.orderSpecifiers = expression.filterNotNull().distinct()
            }

            fun sort(orderSpecifiers: List<OrderSpecifier<*>>?): BuildBuilder<E> = this.apply {
                super.orderSpecifiers = orderSpecifiers
            }
        }
    }

    companion object {
        val pathFactory = PathFactory()
    }
}
