package com.campusnest1.groupq.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.data.AuthRepository
import com.campusnest1.groupq.data.EventRepository
import com.campusnest1.groupq.model.Event
import com.campusnest1.groupq.utils.isEventExpired
import com.campusnest1.groupq.utils.isEventLive
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var events = mutableStateOf<List<Event>>(emptyList())
        private set

    var selectedCategory by mutableStateOf("All")
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
            try {
                val fetchedEvents = repository.getEvents()
                Log.d("EventVM", "Fetched ${fetchedEvents.size} events from repository")
                
                // TEMPORARILY: Show all events to verify UI is working
                events.value = fetchedEvents
                
                Log.d("EventVM", "Displaying ${events.value.size} events")
            } catch (e: Exception) {
                Log.e("EventVM", "Error loading events: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun setCategory(category: String) {
        selectedCategory = category
    }

    fun getFilteredEvents(): List<Event> {
        val category = selectedCategory
        return if (category == "All") {
            events.value
        } else {
            events.value.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    fun getLiveEvents(): List<Event> {
        val category = selectedCategory
        val allEvents = events.value
        val filtered = if (category == "All") {
            allEvents
        } else {
            allEvents.filter { it.category.equals(category, ignoreCase = true) }
        }
        
        return filtered.filter { isEventLive(it.date, it.startTime, it.endTime) }
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
