package com.campusnest1.groupq.viewmodel.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.campusnest1.groupq.model.Profile
import com.campusnest1.groupq.ui.profile.ProfileUiState
import com.campusnest1.groupq.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class profileViewModel: ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    init {
        fetchProfileData()
    }

    private fun fetchProfileData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("profiles").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profile = document.toObject(ProfileUiState::class.java)
                        if (profile != null) {
                            uiState = profile.copy(userId = uid)
                        }
                    }
                }
        }
    }

    fun onCourseChange(course:String){
        uiState = uiState.copy(course = course)
    }
    fun onYearChange(year:String){
        uiState = uiState.copy(yearOfStudy = year)
    }
    fun onHostelChange(hostel:String){
        uiState = uiState.copy(currentHostel = hostel)
    }
    fun onRoomNoChange(roomNo:String){
        uiState = uiState.copy(currentRoomNo = roomNo)
    }

    fun resetSuccess() {
        uiState = uiState.copy(isSuccess = false)
    }

    fun saveProfile(userName: String, userEmail: String, userPhone: String,
                    onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        
        uiState = uiState.copy(isLoading = true, error = null)

        val newProfile = Profile(
            userId = uid,
            course = uiState.course ?: "",
            yearOfStudy = uiState.yearOfStudy ?: "",
            currentHostel = uiState.currentHostel ?: "",
            favHostels = uiState.favHostels ?: ""
        )
        val updatedUser = User(
            userId = uid,
            name = userName,
            email = userEmail,
            phone = userPhone
        )

        val userRef = db.collection("User").document(uid)
        val profileRef = db.collection("profiles").document(uid)

        val batch = db.batch()
        batch.set(userRef, updatedUser)
        batch.set(profileRef, newProfile)

        batch.commit()
            .addOnSuccessListener {
                uiState = uiState.copy(isLoading = false, isSuccess = true)
                onSuccess()
            }
            .addOnFailureListener {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
    }

    val currentUser = auth.currentUser
}
