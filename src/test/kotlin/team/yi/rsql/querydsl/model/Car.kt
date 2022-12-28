package team.yi.rsql.querydsl.model

import jakarta.persistence.*
import java.util.*

@Suppress("unused", "SpellCheckingInspection")
@Entity
class Car {
    @Id
    @GeneratedValue
    var id: Long? = null

    @Column
    var name: String? = null

    @Column
    var description: String? = null

    @Column
    var active: Boolean? = null

    @Column
    var mfgdt: Date? = null

    @OneToOne(fetch = FetchType.LAZY)
    var engine: Engine? = null

    @OneToMany(fetch = FetchType.LAZY)
    var screws: List<Screw>? = null
}
