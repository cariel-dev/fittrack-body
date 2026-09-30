package com.fittrack.body.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fittrack.body.data.db.CircumferenceEntry
import com.fittrack.body.data.db.SkinfoldEntry
import com.fittrack.body.data.repo.TrackerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackerViewModel(private val repo: TrackerRepository) : ViewModel() {

    val weights = repo.observeWeights()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val circumferences = repo.observeCircumferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val skinfolds = repo.observeSkinfolds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveWeight(date: String, kg: Float, note: String = "") {
        viewModelScope.launch { repo.saveWeight(date, kg, note) }
    }

    fun saveCircumference(entry: CircumferenceEntry) {
        viewModelScope.launch { repo.saveCircumference(entry) }
    }

    fun saveSkinfold(entry: SkinfoldEntry) {
        viewModelScope.launch { repo.saveSkinfold(entry) }
    }

    fun deleteWeight(date: String) {
        viewModelScope.launch { repo.deleteWeight(date) }
    }

    fun deleteCircumference(date: String) {
        viewModelScope.launch { repo.deleteCircumference(date) }
    }

    fun deleteSkinfold(date: String) {
        viewModelScope.launch { repo.deleteSkinfold(date) }
    }

    companion object {
        fun factory(repo: TrackerRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TrackerViewModel(repo) as T
                }
            }
    }
}
