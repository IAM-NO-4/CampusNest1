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

    fun fetchProfileData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            uiState = uiState.copy(isLoading = true, error = null)
            
            //Informn abt th user
            db.collection("users").document(uid).get()
                .addOnSuccessListener { userDoc ->
                    val user = userDoc.toObject(User::class.java)
                    
                    // profile-specific information from "profiles" collection
                    db.collection("profiles").document(uid).get()
                        .addOnSuccessListener { profileDoc ->
                            val profile = profileDoc.toObject(Profile::class.java)
                            
                            uiState = uiState.copy(
                                userId = uid,
                                fname = user?.fname ?: "",
                                lname = user?.lname ?: "",
                                email = user?.email ?: "",
                                phone = user?.phone ?: "",
                                course = profile?.course ?: "",
                                yearOfStudy = profile?.yearOfStudy ?: "",
                                currentHostel = profile?.currentHostel ?: "",
                                currentRoomNo = profile?.currentRoomNo ?: "",
                                favHostels = profile?.favHostels ?: "",
                                isLoading = false
                            )
                        }
                        .addOnFailureListener { e ->
                            uiState = uiState.copy(isLoading = false, error = e.message)
                        }
                }
                .addOnFailureListener { e ->
                    uiState = uiState.copy(isLoading = false, error = e.message)
                }
        }
    }


    fun onCourseChange(course:String){
        uiState = uiState.copy(course = course)
    }
    fun onFNameChange(fname:String){
        uiState = uiState.copy(fname = fname)
    }
    fun onLNameChange(lname:String){
        uiState = uiState.copy(lname = lname)
    }
    fun onYearChange(year:String){
        uiState = uiState.copy(yearOfStudy = year)
    }
    fun onHostelChange(hostel:String){
        uiState = uiState.copy(currentHostel = hostel)
    }
    fun onEmailChange(email:String){
        uiState = uiState.copy(email = email)
    }
    fun onPhoneChange(phone:String){
        uiState = uiState.copy(phone = phone)
    }

    fun onRoomNoChange(roomNo:String){
        uiState = uiState.copy(currentRoomNo = roomNo)
    }

    fun resetSuccess() {
        uiState = uiState.copy(isSuccess = false)
    }

    fun saveProfile(userFName: String,userLName: String, userEmail: String, userPhone: String,
                    onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        
        uiState = uiState.copy(isLoading = true, error = null)

        val newProfile = Profile(
            userId = uid,
            course = uiState.course ?: "",
            yearOfStudy = uiState.yearOfStudy ?: "",
            currentHostel = uiState.currentHostel ?: "",
            currentRoomNo = uiState.currentRoomNo ?: "",
            favHostels = uiState.favHostels ?: ""
        )
        val updatedUser = User(
            userId = uid,
            fname = userFName,
            lname = userLName,
            email = userEmail,
            phone = userPhone
        )

        val userRef = db.collection("users").document(uid)
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

}
