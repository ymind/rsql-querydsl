package team.yi.rsql.querydsl.model

import javax.persistence.*

@Entity
class Engine {
    @Id
    @GeneratedValue
    var id: Long? = null

    @Column
    var name: String? = null

    @Column
    var description: String? = null

    @OneToMany
    var screws: List<Screw>? = null
}
