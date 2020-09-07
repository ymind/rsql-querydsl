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
import team.yi.rsql.querydsl.operator.RsqlOperator
import team.yi.rsql.querydsl.util.RsqlUtil
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
    private val size: Long?
    private val sort: String?
    private val orderSpecifiers: List<OrderSpecifier<*>>?
    private val rsqlConfig: RsqlConfig<E>

    @Throws(RsqlException::class)
    fun buildQuery(selectFieldPath: List<Path<*>>?, noPaging: Boolean = false): JPAQuery<*> {
        return try {
            val queryFactory = JPAQueryFactory(rsqlConfig.entityManager)
            val fromPath: PathBuilder<*> = PathBuilder<Any?>(entityClass, entityClass.simpleName.toLowerCase())
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
                size?.let { jpaQuery.limit(it) }
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
        val jpaQuery = buildQuery(selectFieldPath)

        return jpaQuery.fetch()
    }

    @Throws(RsqlException::class)
    fun fetchOne(): Any? {
        val selectFieldPath = buildSelectPath()
        val jpaQuery = buildQuery(selectFieldPath)

        return jpaQuery.fetchOne()
    }

    @Throws(RsqlException::class)
    fun buildPredicate(): Predicate? {
        entityClass.let {
            if (where.isNullOrBlank()) return null

            val rootNode = RSQLParser(RsqlUtil.getOperators(rsqlConfig.operators)).parse(where)
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
                val sortPath = getPath(RsqlUtil.parseFieldSelector(entityClass, sortSelect))
                val path = sortPath as? Path<Comparable<*>>
                val order = OrderSpecifier(sorts[sortSelect], path)

                orderSpecifiers.add(order)
            }
        }

        return orderSpecifiers.toTypedArray()
    }

    fun buildSelectPath(): List<Path<*>>? = select?.let { RsqlUtil.parseSelect(select, entityClass) }

    @Throws(TypeNotSupportedException::class)
    private fun getPath(fieldMetadataList: List<FieldMetadata>): Expression<*> {
        val rootPath: Path<E> = Expressions.path(entityClass, entityClass.simpleName.toLowerCase())
        val processedPaths: MutableList<Expression<*>> = mutableListOf()
        var fieldType: FieldTypeHandler<E>

        for (i in fieldMetadataList.indices) {
            fieldType = rsqlConfig.getSortFieldTypeHandler(fieldMetadataList[i])

            val path = fieldType.getPath(if (i == 0) rootPath else processedPaths[i - 1])

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
        var size: Long? = null
        var sort: String? = null
        var orderSpecifiers: List<OrderSpecifier<*>>? = null

        constructor(rsqlConfig: RsqlConfig<E>) {
            this.rsqlConfig = rsqlConfig
        }

        constructor(entityManager: EntityManager?) : this(entityManager, null, null)

        constructor(
            entityManager: EntityManager?,
            operators: List<RsqlOperator>? = null,
            fieldTypeHandlers: List<Class<FieldTypeHandler<E>>>? = null
        ) : this(
            RsqlConfig.Builder<E>(entityManager!!)
                .operators(operators?.toMutableList())
                .fieldTypeHandlers(fieldTypeHandlers?.toMutableList())
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
            this.size = builder.size
            this.sort = builder.sort
            this.rsqlConfig = builder.rsqlConfig
            this.orderSpecifiers = builder.orderSpecifiers
        }

        fun select(select: String?): SelectBuilder<E> {
            this.select = select

            return SelectBuilder(this)
        }

        fun select(expression: Expression<*>?): SelectBuilder<E> {
            expressionSelect = expression

            return SelectBuilder(this)
        }

        fun selectFrom(entityName: String?): FromBuilder<E> {
            this.entityName = entityName

            return FromBuilder(this)
        }

        fun selectFrom(entityClass: Class<E>?): FromBuilder<E> {
            this.entityClass = entityClass

            return FromBuilder(this)
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

            fun limit(offset: Int?, size: Int?): BuildBuilder<E> = limit(offset?.toLong(), size?.toLong())
            fun limit(offset: Long?, size: Long?): BuildBuilder<E> = this.also {
                super.offset = offset
                super.size = size
            }

            fun limit(limit: String): BuildBuilder<E> {
                val limitParams: List<Int> = try {
                    RsqlUtil.parseTwoParamExpression(limit)
                } catch (ex: Exception) {
                    throw IllegalArgumentException("Invalid limit expression: '$limit' . Excepted format: '(offset,size)' .")
                }

                return limit(limitParams[0], limitParams[1])
            }

            fun page(pageNumber: Int, pageSize: Int): BuildBuilder<E> = page(pageNumber.toLong(), pageSize.toLong())
            fun page(pageNumber: Int, pageSize: Long): BuildBuilder<E> = page(pageNumber.toLong(), pageSize)

            fun page(pageNumber: Long, pageSize: Long): BuildBuilder<E> = this.also {
                super.size = pageSize
                super.offset = pageNumber * pageSize
            }

            fun page(page: String): BuildBuilder<E> {
                val pageParams: List<Int> = try {
                    RsqlUtil.parseTwoParamExpression(page)
                } catch (ex: Exception) {
                    throw IllegalArgumentException("Invalid page expression: '$page' . Excepted format: '(pageNumber,pageSize)' .")
                }

                return page(pageParams[0], pageParams[1])
            }

            fun offset(offset: Int?): BuildBuilder<E> = offset(offset?.toLong())
            fun offset(offset: Long?): BuildBuilder<E> = this.also { super.offset = offset }
            fun size(size: Int?): BuildBuilder<E> = size(size?.toLong())
            fun size(size: Long?): BuildBuilder<E> = this.also { super.size = size }
            fun sort(sort: String?): BuildBuilder<E> = this.also { super.sort = sort }
            fun sort(orderSpecifiers: List<OrderSpecifier<*>>?): BuildBuilder<E> = this.also { super.orderSpecifiers = orderSpecifiers }
            fun operators(operators: List<RsqlOperator>): BuildBuilder<E> = this.also { super.rsqlConfig.operators = operators.toMutableList() }
            fun operator(operator: RsqlOperator): BuildBuilder<E> = this.also { super.rsqlConfig.operators = mutableListOf(operator) }

            fun fieldTypeHandlers(fieldTypeHandlers: List<Class<FieldTypeHandler<E>>>?): BuildBuilder<E> =
                this.also { super.rsqlConfig.addFieldTypeHandlers(fieldTypeHandlers?.toMutableList()) }

            fun fieldTypeHandler(fieldTypeHandler: Class<FieldTypeHandler<E>>): BuildBuilder<E> =
                this.also { super.rsqlConfig.addFieldTypeHandlers(mutableListOf(fieldTypeHandler)) }

            fun dateFormat(dateFormat: String): BuildBuilder<E> = this.also { super.rsqlConfig.dateFormat = dateFormat }
        }
    }

    init {
        this.rsqlConfig = builder.rsqlConfig
        this.predicateBuilder = PredicateBuilder(rsqlConfig)

        RsqlUtil.validateOperators(rsqlConfig.operators)

        requireNotNull(rsqlConfig.entityManager) { "Entity manager cannot be null." }

        this.entityClass = when {
            builder.entityClass != null -> builder.entityClass
            else -> RsqlUtil.getClassForEntityString(builder.entityName!!, rsqlConfig.entityManager) as Class<E>?
        } ?: throw EntityNotFoundException("Can't find entity with name: " + builder.entityName, builder.entityName)

        this.select = builder.select
        this.expressionSelect = builder.expressionSelect
        this.where = builder.where
        this.globalPredicate = builder.globalPredicate
        this.offset = builder.offset
        this.size = builder.size
        this.sort = builder.sort
        this.orderSpecifiers = builder.orderSpecifiers
    }
}
