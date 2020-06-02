package team.yi.rsql.querydsl.model

import java.util.*
import javax.persistence.*

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

    @OneToOne
    var engine: Engine? = null

    @OneToMany
    var screws: List<Screw>? = null
}
