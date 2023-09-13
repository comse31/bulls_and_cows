package org.w2fc.covs.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "words")
data class WordEntity(
    @Id
    val id: UUID,
    @Column(name = "word")
    val word: String,
    @Column(name = "length")
    val length: Int,
)
