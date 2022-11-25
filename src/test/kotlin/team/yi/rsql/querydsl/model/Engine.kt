package team.yi.rsql.querydsl.model

import jakarta.persistence.*

@Suppress("unused")
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
