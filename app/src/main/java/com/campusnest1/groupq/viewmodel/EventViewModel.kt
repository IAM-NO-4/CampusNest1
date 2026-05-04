package com.campusnest1.groupq.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.data.AuthRepository
import com.campusnest1.groupq.data.EventRepository
import com.campusnest1.groupq.model.Event
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var events = mutableStateOf<List<Event>>(emptyList())
        private set

    var savedStatus = mutableStateMapOf<String, Boolean>()
        private set

    var isLoading = mutableStateOf(false)
        private set

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            isLoading.value = true
            events.value = repository.getEvents()
            isLoading.value = false
        }
    }

    fun toggleSavedEvent(eventId: String) {
        val userId = authRepository.getCurrentUser()?.userId ?: return
        viewModelScope.launch {
            val isSaved = repository.toggleSavedEvent(userId, eventId)
            savedStatus[eventId] = isSaved
        }
    }

    fun checkIfSaved(eventId: String) {
        val userId = authRepository.getCurrentUser()?.userId ?: return
        viewModelScope.launch {
            savedStatus[eventId] = repository.isEventSaved(userId, eventId)
        }
    }
}
