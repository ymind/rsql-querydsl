package team.yi.rsql.querydsl.model

import javax.persistence.*

@Entity
class Screw {
    @Id
    @GeneratedValue
    var id: Long? = null

    @Column
    var name: String? = null

    @Column
    var size: Long? = null

    @Column
    var description: String? = null

    @Column
    @Enumerated(EnumType.STRING)
    var screwType: ScrewType? = null
}
