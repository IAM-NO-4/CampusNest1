package com.campusnest1.groupq.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.campusnest1.groupq.model.Event
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class EventViewModel : ViewModel() {
    var events by mutableStateOf<List<Event>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val db = Firebase.firestore

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        isLoading = true
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                events = result.mapNotNull { document ->
                    document.toObject(Event::class.java).copy(eventId = document.id)
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                isLoading = false

            }
    }
}