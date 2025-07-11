package com.phone.pocket.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spends")
data class Spend(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val category: String,
    val amount: Double,
    val place: String,
    val online: Boolean,
    val date: Long,
    val paymentMode: String, // New field: Cash, UPI, Card
    val cardName: String? = null,
    val notes: String? = null
) 