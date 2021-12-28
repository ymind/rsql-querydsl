package team.yi.rsql.querydsl

import com.querydsl.core.types.Expression
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import cz.jirutka.rsql.parser.RSQLParser
import team.yi.rsql.querydsl.exception.EntityNotFoundException
import team.yi.rsql.querydsl.exception.RsqlException
import team.yi.rsql.querydsl.exception.TypeNotSupportedException
import team.yi.rsql.querydsl.handler.FieldTypeHandler
import team.yi.rsql.querydsl.handler.SortFieldTypeHandler
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.RsqlUtil
import java.util.*
import javax.persistence.EntityManager

@Suppress("UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate")
class QuerydslRsql<E> private constructor(builder: Builder<E>) {
    private val predicateBuilder: PredicateBuilder<E>
    private val entityClass: Class<E>
    private val select: String?
    private val expressionSelect: Expression<*>?
    private val where: String?
    private val globalPredicate: BooleanExpression?
    private val offset: Long?
    private val limit: Long?
    private val sort: String?
    private val orderSpecifiers: List<OrderSpecifier<*>>?
    private val rsqlConfig: RsqlConfig<E>

    @Throws(RsqlException::class)
    fun buildJPAQuery(noPaging: Boolean = false): JPAQuery<*> = buildJPAQuery(buildSelectPath(), noPaging)

    @Throws(RsqlException::class)
    fun buildJPAQuery(selectFieldPath: List<Path<*>>?, noPaging: Boolean = false): JPAQuery<*> {
        return try {
            val queryFactory = JPAQueryFactory(rsqlConfig.entityManager)
            val fromPath = PathBuilder<Any?>(entityClass, entityClass.simpleName.lowercase(Locale.getDefault()))
            val predicate = buildPredicate()

            val jpaQuery = if (selectFieldPath.isNullOrEmpty() && expressionSelect == null) {
                queryFactory.from(fromPath).where(predicate)
            } else {
                val q = if (expressionSelect == null) {
                    queryFactory.select(*RsqlUtil.convertPathToExpression(selectFieldPath))
                } else {
                    queryFactory.select(expressionSelect)
                }

                q.from(fromPath).where(predicate) as JPAQuery<*>
            }

            if (!noPaging) {
                offset?.let { jpaQuery.offset(it) }
                limit?.let { jpaQuery.limit(it) }
            }

            val orderSpecifiers = buildOrder()

            if (orderSpecifiers.isNotEmpty()) jpaQuery.orderBy(*orderSpecifiers)

            jpaQuery
        } catch (ex: Exception) {
            throw RsqlException(ex)
        }
    }

    @Throws(RsqlException::class)
    fun fetch(): List<Any>? {
        val selectFieldPath = buildSelectPath()
        val jpaQuery = buildJPAQuery(selectFieldPath)

        return jpaQuery.fetch()
    }

    @Throws(RsqlException::class)
    fun buildPredicate(): Predicate? {
        entityClass.let {
            if (where.isNullOrBlank()) return globalPredicate

            val operators = RsqlUtil.getOperators(rsqlConfig.operators)
            val rootNode = RSQLParser(operators).parse(where)
            val predicate = rootNode.accept(PredicateBuilderVisitor(it, predicateBuilder))

            return when {
                globalPredicate == null && predicate == null -> null
                globalPredicate == null -> predicate
                else -> globalPredicate.and(predicate)
            }
        }
    }

    private fun buildOrder(): Array<OrderSpecifier<*>> {
        val orderSpecifiers: MutableList<OrderSpecifier<*>> = mutableListOf()

        if (sort == null) {
            this.orderSpecifiers?.let { orderSpecifiers.addAll(it) }
        } else {
            val sorts = RsqlUtil.parseSortExpression(sort)

            for (sortSelect in sorts.keys) {
                val sortPath = getSortPath(RsqlUtil.parseFieldSelector(entityClass, sortSelect))
                val path = sortPath as? Path<Comparable<*>>
                val order = OrderSpecifier(sorts[sortSelect], path)

                orderSpecifiers.add(order)
            }
        }

        return orderSpecifiers.toTypedArray()
    }

    fun buildSelectPath(): List<Path<*>>? = select?.let { RsqlUtil.parseSelect(select, entityClass) }

    @Throws(TypeNotSupportedException::class)
    private fun getSortPath(fieldMetadataList: List<FieldMetadata>): Expression<*> {
        val rootPath = Expressions.path(entityClass, entityClass.simpleName.lowercase(Locale.getDefault()))
        val processedPaths = mutableListOf<Expression<*>>()
        var typeHandler: SortFieldTypeHandler<E>

        for (i in fieldMetadataList.indices) {
            typeHandler = rsqlConfig.getSortFieldTypeHandler(fieldMetadataList[i])

            val path = typeHandler.getPath(if (i == 0) rootPath else processedPaths[i - 1])

            path?.let { processedPaths.add(it) }
        }

        return processedPaths[processedPaths.size - 1]
    }

    open class Builder<E> {
        val rsqlConfig: RsqlConfig<E>
        var entityClass: Class<E>? = null
        var entityName: String? = null
        var select: String? = null
        var expressionSelect: Expression<*>? = null
        var where: String? = null
        var globalPredicate: BooleanExpression? = null
        var offset: Long? = null
        var limit: Long? = null
        var sort: String? = null
        var orderSpecifiers: List<OrderSpecifier<*>>? = null

        constructor(rsqlConfig: RsqlConfig<E>) {
            this.rsqlConfig = rsqlConfig
        }

        constructor(entityManager: EntityManager) : this(entityManager, null, null)

        constructor(
            entityManager: EntityManager,
            operators: List<RsqlOperator>? = null,
            fieldTypeHandlers: List<Class<FieldTypeHandler<E>>>? = null,
            sortFieldTypeHandlers: List<Class<SortFieldTypeHandler<E>>>? = null,
            nodeInterceptors: List<RsqlNodeInterceptor>? = null,
            dateFormat: String? = null,
        ) : this(
            RsqlConfig.Builder<E>(entityManager)
                .operator(*(operators ?: emptyList()).toTypedArray())
                .fieldTypeHandler(*(fieldTypeHandlers ?: emptyList()).toTypedArray())
                .sortFieldTypeHandler(*(sortFieldTypeHandlers ?: emptyList()).toTypedArray())
                .nodeInterceptors(nodeInterceptors)
                .dateFormat(dateFormat)
                .build()
        )

        private constructor(builder: Builder<E>) {
            this.entityClass = builder.entityClass
            this.entityName = builder.entityName
            this.select = builder.select
            this.expressionSelect = builder.expressionSelect
            this.where = builder.where
            this.globalPredicate = builder.globalPredicate
            this.offset = builder.offset
            this.limit = builder.limit
            this.sort = builder.sort
            this.rsqlConfig = builder.rsqlConfig
            this.orderSpecifiers = builder.orderSpecifiers
        }

        fun from(entityName: String?): FromBuilder<E> {
            this.entityName = entityName

            return FromBuilder(this)
        }

        fun from(entityClass: Class<E>?): FromBuilder<E> {
            this.entityClass = entityClass

            return FromBuilder(this)
        }

        fun select(select: String?): SelectBuilder<E> {
            this.select = select

            return SelectBuilder(this)
        }

        fun select(expression: Expression<*>?): SelectBuilder<E> {
            expressionSelect = expression

            return SelectBuilder(this)
        }

        class SelectBuilder<E>(private val builder: Builder<E>) {
            fun from(entityName: String?): FromBuilder<E> {
                builder.entityName = entityName

                return FromBuilder(builder)
            }

            fun from(entityClass: Class<E>?): FromBuilder<E> {
                builder.entityClass = entityClass

                return FromBuilder(builder)
            }
        }

        class FromBuilder<E>(private val builder: Builder<E>) {
            fun where(where: String?): BuildBuilder<E> {
                builder.where = where

                return BuildBuilder(builder)
            }
        }

        class BuildBuilder<E>(builder: Builder<E>) : Builder<E>(builder) {
            fun globalPredicate(globalPredicate: BooleanExpression?): BuildBuilder<E> = this.also { super.globalPredicate = globalPredicate }

            @Throws(RsqlException::class)
            fun build(): QuerydslRsql<E> {
                return try {
                    QuerydslRsql(this)
                } catch (ex: Exception) {
                    throw RsqlException(ex)
                }
            }

            fun offset(offset: Int?): BuildBuilder<E> = offset(offset?.toLong())
            fun offset(offset: Long?): BuildBuilder<E> = this.also { super.offset = offset }

            fun limit(limit: Int?): BuildBuilder<E> = limit(limit?.toLong())
            fun limit(limit: Long?): BuildBuilder<E> = this.also { super.limit = limit }

            fun limit(offset: Int?, limit: Int?): BuildBuilder<E> = limit(offset?.toLong(), limit?.toLong())
            fun limit(offset: Long?, limit: Long?): BuildBuilder<E> = this.also {
                super.offset = offset
                super.limit = limit
            }

            fun page(pageNumber: Int, pageSize: Int): BuildBuilder<E> = page(pageNumber.toLong(), pageSize.toLong())
            fun page(pageNumber: Int, pageSize: Long): BuildBuilder<E> = page(pageNumber.toLong(), pageSize)

            fun page(pageNumber: Long, pageSize: Long): BuildBuilder<E> = this.also {
                super.limit = pageSize
                super.offset = pageNumber * pageSize
            }

            fun sort(sort: String?): BuildBuilder<E> = this.also { super.sort = sort }
            fun sort(orderSpecifiers: List<OrderSpecifier<*>>?): BuildBuilder<E> = this.also { super.orderSpecifiers = orderSpecifiers }

            fun operator(vararg operator: RsqlOperator): BuildBuilder<E> = this.also { super.rsqlConfig.operators = operator.toList() }
            fun operators(operators: List<RsqlOperator>): BuildBuilder<E> = this.also { super.rsqlConfig.operators = operators.toList() }

            fun fieldTypeHandler(vararg typeHandler: Class<FieldTypeHandler<E>>): BuildBuilder<E> {
                return this.also { super.rsqlConfig.addFieldTypeHandler(*typeHandler) }
            }

            fun sortFieldTypeHandler(vararg typeHandler: Class<SortFieldTypeHandler<E>>): BuildBuilder<E> {
                return this.also { super.rsqlConfig.addSortFieldTypeHandler(*typeHandler) }
            }

            fun dateFormat(dateFormat: String): BuildBuilder<E> = this.also { super.rsqlConfig.dateFormat = dateFormat }
        }
    }

    init {
        this.rsqlConfig = builder.rsqlConfig
        this.predicateBuilder = PredicateBuilder(rsqlConfig)

        RsqlUtil.validateOperators(rsqlConfig.operators)

        requireNotNull(rsqlConfig.entityManager) { "Entity manager cannot be null." }

        val entityClass = builder.entityClass
        val entityName = builder.entityName

        this.entityClass = when {
            entityClass != null -> entityClass
            entityName != null -> RsqlUtil.getClassForEntityString(entityName, rsqlConfig.entityManager) as Class<E>?
            else -> null
        } ?: throw EntityNotFoundException("Can't find entity with name: $entityName", entityName)

        this.select = builder.select
        this.expressionSelect = builder.expressionSelect
        this.where = builder.where
        this.globalPredicate = builder.globalPredicate
        this.offset = builder.offset
        this.limit = builder.limit
        this.sort = builder.sort
        this.orderSpecifiers = builder.orderSpecifiers
    }
}
