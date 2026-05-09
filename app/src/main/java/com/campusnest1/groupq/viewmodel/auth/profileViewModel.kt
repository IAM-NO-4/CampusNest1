package com.campusnest1.groupq.viewmodel.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import com.campusnest1.groupq.model.Profile
import com.campusnest1.groupq.ui.profile.ProfileUiState
import com.campusnest1.groupq.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class profileViewModel: ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val client = OkHttpClient()

    fun fetchProfileData(forceRefresh: Boolean = false) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            uiState = uiState.copy(error = "User not logged in", isLoading = false)
            return
        }

        // Avoid redundant loading if already loading
        if (uiState.isLoading && !forceRefresh) return
        
        // If we already have data and not forcing refresh, we can skip the full loading state
        val shouldShowLoading = forceRefresh || uiState.fname.isEmpty()

        viewModelScope.launch {
            if (shouldShowLoading) {
                uiState = uiState.copy(isLoading = true, error = null)
            }
            
            try {
                // Parallel fetching of User and Profile data
                // We use Source.DEFAULT to leverage cache first if available
                val userTask = async { db.collection("users").document(uid).get().await() }
                val profileTask = async { db.collection("profiles").document(uid).get().await() }

                val userDoc = userTask.await()
                val profileDoc = profileTask.await()

                var newState = uiState.copy(userId = uid)

                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                    newState = newState.copy(
                        fname = user?.fname ?: "",
                        lname = user?.lname ?: "",
                        email = user?.email ?: "",
                        phone = user?.phone ?: "",
                        profileImageUrl = user?.profileImageUrl ?: ""
                    )
                }

                if (profileDoc.exists()) {
                    val profile = profileDoc.toObject(Profile::class.java)
                    newState = newState.copy(
                        course = profile?.course ?: "",
                        yearOfStudy = profile?.yearOfStudy ?: "",
                        currentHostel = profile?.currentHostel ?: "",
                        currentRoomNo = profile?.currentRoomNo ?: "",
                        favHostels = profile?.favHostels ?: "",
                        priceChangeNotify = profile?.priceChangeNotify ?: false,
                        newEventNotify = profile?.newEventNotify ?: false,
                        roomAvailabilityNotify = profile?.roomAvailabilityNotify ?: false
                    )
                }
                
                uiState = newState.copy(isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(error = "Failed to load profile: ${e.message}", isLoading = false)
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
    
    fun onProfileImageChange(url: String) {
        uiState = uiState.copy(profileImageUrl = url)
    }

    fun updateNotificationPreference(type: String, enabled: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        
        uiState = when (type) {
            "price" -> uiState.copy(priceChangeNotify = enabled)
            "event" -> uiState.copy(newEventNotify = enabled)
            "room" -> uiState.copy(roomAvailabilityNotify = enabled)
            else -> uiState
        }

        // Persist to Firestore
        val field = when (type) {
            "price" -> "priceChangeNotify"
            "event" -> "newEventNotify"
            "room" -> "roomAvailabilityNotify"
            else -> null
        }

        field?.let {
            db.collection("profiles").document(uid)
                .update(it, enabled)
                .addOnFailureListener { e ->
                    // Optionally handle error, e.g., revert state
                    uiState = uiState.copy(error = "Failed to update preference: ${e.message}")
                }
        }
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
            course = uiState.course,
            yearOfStudy = uiState.yearOfStudy,
            currentHostel = uiState.currentHostel,
            currentRoomNo = uiState.currentRoomNo,
            favHostels = uiState.favHostels,
            priceChangeNotify = uiState.priceChangeNotify,
            newEventNotify = uiState.newEventNotify,
            roomAvailabilityNotify = uiState.roomAvailabilityNotify
        )
        val updatedUser = User(
            userId = uid,
            fname = userFName,
            lname = userLName,
            email = userEmail,
            phone = userPhone,
            profileImageUrl = uiState.profileImageUrl
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

    val currentUser = auth.currentUser

    fun logout() {
        auth.signOut()
    }

    fun changeProfileImage(context: Context, uri: Uri, userId: String) {
        uploadImage(context, uri, userId)
    }

    fun uploadImage(context: Context, uri: Uri, userId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            val url = uploadToCloudinary(context, uri)
            if (url != null) {
                uiState = uiState.copy(
                    profileImageUrl = url,
                    isLoading = false
                )
                saveImageUrl(userId, url)
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Image upload failed"
                )
            }
        }
    }

    private suspend fun saveImageUrl(userId: String, url: String) {
        try {
            coroutineScope {
                val userTask = async {
                    db.collection("users").document(userId).update("profileImageUrl", url).await()
                }
                val profileTask = async {
                    db.collection("profiles").document(userId).update("profileImageUrl", url).await()
                }
                userTask.await()
                profileTask.await()
            }
        } catch (e: Exception) {
            // Error handled in the UI through uiState if necessary, 
            // but here we just ensure both are attempted.
        }
    }

    private suspend fun uploadToCloudinary(
        context: Context,
        imageUri: Uri
    ): String? = withContext(Dispatchers.IO) {
        var imageUrl: String? = null
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.use { it.readBytes() } ?: return@withContext null

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "image.jpg",
                    bytes.toRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("upload_preset", "campusupload")
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/df1sj7zza/image/upload")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val json = JSONObject(bodyString)
                    if (json.has("secure_url")) {
                        imageUrl = json.getString("secure_url")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        imageUrl
    }




}
