package org.w2fc.covs.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.w2fc.covs.model.WordEntity
import java.util.*

@Repository
interface WordRepository : JpaRepository<WordEntity, UUID> {
    fun findByWordAndLength(word: String, length: Int): WordEntity?

    @Query("select count(*) from words where length = :length", nativeQuery = true)
    fun countByLength(@Param("length") length: Int): Int

    @Query("select word from words where length = :length offset :offset limit 1", nativeQuery = true)
    fun getWordByLengthAndLimit(@Param("length") length: Int, @Param("offset") offset: Int): String
}