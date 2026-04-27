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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    // ========== FILTER ENGINE (Role 3) - StateFlow for real-time updates ==========
    
    // All available hostels (source data) - StateFlow for reactive updates
    private val _allHostels = MutableStateFlow<List<Hostel>>(emptyList())
    val allHostels: StateFlow<List<Hostel>> = _allHostels.asStateFlow()
    
    // Filter state - StateFlow for real-time UI updates
    private val _priceRange = MutableStateFlow(200_000f..3_000_000f)
    val priceRange: StateFlow<ClosedFloatingPointRange<Float>> = _priceRange.asStateFlow()
    
    private val _selectedLocation = MutableStateFlow("")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()
    
    private val _selectedRoomTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedRoomTypes: StateFlow<Set<String>> = _selectedRoomTypes.asStateFlow()
    
    // Active filter type (for bottom sheet)
    private val _activeFilterType = MutableStateFlow("")
    val activeFilterType: StateFlow<String> = _activeFilterType.asStateFlow()
    
    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

    // Computed filtered hostels - combines all filter flows for real-time updates
    val filteredHostels: StateFlow<List<Hostel>> = combine(
        _allHostels,
        _priceRange,
        _selectedLocation,
        _selectedRoomTypes
    ) { hostels, priceRange, location, roomTypes ->
        hostels.filter { hostel ->
            val price = hostel.highestPrice.toFloatOrNull() ?: 0f
            val locationMatches = location.isEmpty() || 
                hostel.location.toString().contains(location, ignoreCase = true)
            val priceMatches = price in priceRange
            val roomMatches = roomTypes.isEmpty() || 
                roomTypes.any { roomType ->
                    hostel.rooms.any { room -> 
                        room.type.contains(roomType, ignoreCase = true) 
                    }
                }
            locationMatches && priceMatches && roomMatches
        }
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filter actions
    fun setPriceRange(range: ClosedFloatingPointRange<Float>) {
        _priceRange.value = range
    }
    
    fun setLocation(location: String) {
        _selectedLocation.value = location
    }
    
    fun setRoomTypes(roomTypes: Set<String>) {
        _selectedRoomTypes.value = roomTypes
    }
    
    fun applyFilters(
        priceRange: ClosedFloatingPointRange<Float>,
        location: String,
        roomTypes: Set<String>
    ) {
        _priceRange.value = priceRange
        _selectedLocation.value = location
        _selectedRoomTypes.value = roomTypes
        _showFilterSheet.value = false
    }
    
    fun openFilterSheet(filterType: String) {
        _activeFilterType.value = filterType
        _showFilterSheet.value = true
    }
    
    fun closeFilterSheet() {
        _showFilterSheet.value = false
    }
    
    fun clearFilters() {
        _priceRange.value = 200_000f..3_000_000f
        _selectedLocation.value = ""
        _selectedRoomTypes.value = emptySet()
    }
    
    fun loadAllHostels(hostels: List<Hostel>) {
        _allHostels.value = hostels
    }

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
