package com.campusnest1.groupq.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusnest1.groupq.auth1.Authrepo
import com.campusnest1.groupq.data.HostelImplementationRepository
import com.campusnest1.groupq.data.HostelRepository
import com.campusnest1.groupq.model.Booking
import com.campusnest1.groupq.model.Hostel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HostelViewModel(
    private val repository: HostelRepository,
    private val authRepository: Authrepo
) : ViewModel() {

    var savedHostels = mutableStateOf<List<Hostel>>(emptyList())
        private set
    
    // Add state for tracking saved status of hostels by ID
    var savedStatus = mutableStateMapOf<String, Boolean>()
        private set

    var bookingHistory = mutableStateOf<List<Booking>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    // Inside HostelViewModel.kt
    var isNotificationsEnabled = mutableStateOf(true)
        private set

    // 2. Function to fetch data when the screen opens
    fun loadStudentData() {
        val userId = authRepository.getCurrentUser()?.uid ?: ""
        if (userId.isEmpty()) return

        viewModelScope.launch {
            isLoading.value = true

            // Fetch both lists in parallel
            val savedTask = async { repository.getSavedHostels(userId) }
            val bookingTask = async { repository.getBookingHistory(userId) }

            savedHostels.value = savedTask.await()
            bookingHistory.value = bookingTask.await()

            isLoading.value = false
        }
    }

    //notification settings
    fun toggleNotifications(enabled: Boolean) {
        isNotificationsEnabled.value = enabled
    }

    fun toggleFavorite(hostelId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val isSaved = repository.toggleSavedHostel(userId, hostelId)
            savedStatus[hostelId] = isSaved
            // Refresh saved hostels list
            savedHostels.value = repository.getSavedHostels(userId)
        }
    }

    fun checkIfSaved(hostelId: String) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            savedStatus[hostelId] = repository.isHostelSaved(userId, hostelId)
        }
    }
}
