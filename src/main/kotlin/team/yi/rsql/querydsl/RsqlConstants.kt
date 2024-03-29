package team.yi.rsql.querydsl

import team.yi.rsql.querydsl.handler.*

object RsqlConstants {
    val defaultFieldTypeHandlers: List<Class<out FieldTypeHandler>>
        get() = listOf(
            NumberFieldTypeHandler::class.java,
            EnumFieldTypeHandler::class.java,
            StringFieldTypeHandler::class.java,
            CharacterFieldTypeHandler::class.java,
            DateTimeFieldTypeHandler::class.java,
            TemporalAccessorFieldTypeHandler::class.java,
            BooleanFieldTypeHandler::class.java,
            ListFieldTypeHandler::class.java,
            SetFieldTypeHandler::class.java,
            CollectionFieldTypeHandler::class.java,
            SimpleFieldTypeHandler::class.java,
        )

    val defaultSortFieldTypeHandlers: List<Class<out SortFieldTypeHandler>>
        get() = listOf(
            DefaultSortFieldTypeHandler::class.java,
        )
}
