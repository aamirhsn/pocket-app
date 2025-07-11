package com.phone.pocket.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpendDao {
    @Query("SELECT * FROM spends ORDER BY date DESC")
    fun getAllSpends(): Flow<List<Spend>>
    
    @Query("SELECT * FROM spends WHERE category = :category ORDER BY date DESC")
    fun getSpendsByCategory(category: String): Flow<List<Spend>>
    
    @Query("SELECT category, SUM(amount) as total FROM spends GROUP BY category")
    fun getSpendsByCategorySummary(): Flow<List<CategorySummary>>
    
    @Insert
    suspend fun insertSpend(spend: Spend)
    
    @Update
    suspend fun updateSpend(spend: Spend)
    
    @Delete
    suspend fun deleteSpend(spend: Spend)
    
    @Query("SELECT * FROM spends WHERE id = :id")
    suspend fun getSpendById(id: Int): Spend?
}

data class CategorySummary(
    val category: String,
    val total: Double
) 