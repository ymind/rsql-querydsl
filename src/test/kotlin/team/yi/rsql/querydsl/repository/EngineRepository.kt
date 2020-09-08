package team.yi.rsql.querydsl.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import team.yi.rsql.querydsl.model.Engine

@Repository
interface EngineRepository : JpaRepository<Engine, Long?>
