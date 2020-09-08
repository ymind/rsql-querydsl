@file:Suppress("SpellCheckingInspection")

package team.yi.rsql.querydsl.model

import java.util.*
import javax.persistence.*

@Suppress("unused")
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
    @SuppressWarnings("SpellCheckingInspection")
    var mfgdt: Date? = null

    @OneToOne
    var engine: Engine? = null

    @OneToMany
    var screws: List<Screw>? = null
}
