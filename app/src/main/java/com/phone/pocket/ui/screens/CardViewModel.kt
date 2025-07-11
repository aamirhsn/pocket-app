package com.phone.pocket.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.phone.pocket.data.Card
import com.phone.pocket.data.CardDao
import com.phone.pocket.data.PocketDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : AndroidViewModel(application) {
    private val cardDao: CardDao = PocketDatabase.getDatabase(application).cardDao()
    val cards: StateFlow<List<Card>> = cardDao.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCard(card: Card) {
        viewModelScope.launch {
            cardDao.insertCard(card)
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            cardDao.updateCard(card)
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            cardDao.deleteCard(card)
        }
    }
} 