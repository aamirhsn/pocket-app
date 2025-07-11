package com.phone.pocket.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.phone.pocket.data.Spend
import com.phone.pocket.data.SpendDao
import com.phone.pocket.data.PocketDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SpendViewModel(application: Application) : AndroidViewModel(application) {
    private val spendDao: SpendDao = PocketDatabase.getDatabase(application).spendDao()
    val spends: StateFlow<List<Spend>> = spendDao.getAllSpends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSpend(spend: Spend) {
        viewModelScope.launch {
            spendDao.insertSpend(spend)
        }
    }

    fun updateSpend(spend: Spend) {
        viewModelScope.launch {
            spendDao.updateSpend(spend)
        }
    }

    fun deleteSpend(spend: Spend) {
        viewModelScope.launch {
            spendDao.deleteSpend(spend)
        }
    }
} 