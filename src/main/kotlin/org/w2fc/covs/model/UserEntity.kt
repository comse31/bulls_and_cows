package org.w2fc.covs.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity(name = "user_game")
data class UserEntity(

    @Id
    val id: UUID,

    @Column(name = "user_name")
    val userName: String,

    @Enumerated
    @Column(name = "status")
    var status: Status,

    @Column(name = "word")
    var word: String,

    @Column(name = "attempts")
    var attempts: Int,

    @Column(name = "level")
    var level: Int,

    @Column(name = "games")
    var games: Int,

    @Column(name = "attempts_min")
    var attemptsMin: Int,

    @Column(name = "attempts_max")
    var attemptsMax: Int
)
