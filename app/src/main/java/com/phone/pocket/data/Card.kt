package com.phone.pocket.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val name: String,
    val number: String, // encrypted
    val expiry: String,
    val cvv: String, // encrypted
    val type: String, // "Credit" or "Debit"
    val network: String = "Visa" // Card network: Visa, Mastercard, etc.
) 