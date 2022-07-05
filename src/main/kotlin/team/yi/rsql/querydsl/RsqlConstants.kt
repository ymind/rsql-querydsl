package team.yi.rsql.querydsl

import team.yi.rsql.querydsl.handler.*

object RsqlConstants {
    @Suppress("MemberVisibilityCanBePrivate")
    val defaultFieldTypeHandlers: List<Class<out FieldTypeHandler<*>>>
        get() = listOf(
            NumberFieldTypeHandler::class.java,
            EnumFieldTypeHandler::class.java,
            StringFieldTypeHandler::class.java,
            CharacterFieldTypeHandler::class.java,
            DateTimeFieldTypeHandler::class.java,
            BooleanFieldTypeHandler::class.java,
            ListFieldTypeHandler::class.java,
            SetFieldTypeHandler::class.java,
            CollectionFieldTypeHandler::class.java,
            SimpleFieldTypeHandler::class.java,
        )

    @Suppress("MemberVisibilityCanBePrivate")
    val defaultSortFieldTypeHandlers: List<Class<out SortFieldTypeHandler<*>>>
        get() = listOf(
            DefaultSortFieldTypeHandler::class.java,
        )
}
