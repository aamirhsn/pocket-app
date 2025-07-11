package com.phone.pocket.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY name ASC")
    fun getAllCards(): Flow<List<Card>>
    
    @Insert
    suspend fun insertCard(card: Card)
    
    @Update
    suspend fun updateCard(card: Card)
    
    @Delete
    suspend fun deleteCard(card: Card)
    
    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Int): Card?
} 