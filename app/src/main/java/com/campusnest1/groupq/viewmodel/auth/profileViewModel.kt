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
            
            //Information about the user
            db.collection("users").document(uid).get()
                .addOnSuccessListener { userDoc ->
                    val user = userDoc.toObject(User::class.java)
                    
                    // profile-specific information
                    db.collection("profiles").document(uid).get()
                        .addOnSuccessListener { profileDoc ->
                            val profile = profileDoc.toObject(Profile::class.java)
                            
                            uiState = uiState.copy(
                                userId = uid,
                                fname = user?.fname ?: "",
                                lname = user?.lname ?: "",
                                email = user?.email ?: "",
                                phone = user?.phone ?: "",
                                profileImageUrl = user?.profileImageUrl, // Assume User model has this or add to Profile
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
    
    fun onProfileImageChange(url: String) {
        uiState = uiState.copy(profileImageUrl = url)
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
            favHostels = uiState.favHostels
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

    var isNotificationsEnabled = mutableStateOf(true)
        private set

    fun toggleNotifications(enabled: Boolean) {
        isNotificationsEnabled.value = enabled
    }

    fun changeProfileImage(context: Context, uri: Uri, userId: String) {
        uploadImage(context, uri, userId)
    }

    fun uploadImage(context: Context, uri: Uri, userId: String) {

        uiState = uiState.copy(isLoading = true)

        uploadToCloudinary( context = context, imageUri = uri) { url ->

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

    private fun saveImageUrl(userId: String, url: String) {
        db.collection("users")
            .document(userId)
            .update("profileImageUrl", url)
    }

    fun uploadToCloudinary(
        context: Context,
        imageUri: Uri,
        onResult: (String?) -> Unit
    ) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()

            if (bytes == null) {
                onResult(null)
                return
            }

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

            OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {

                override fun onResponse(call: Call, response: Response) {
                    val json = JSONObject(response.body!!.string())
                    val url = json.getString("secure_url")
                    onResult(url)
                }

                override fun onFailure(call: Call, e: IOException) {
                    onResult(null)
                }
            })

        } catch (e: Exception) {
            onResult(null)
        }
    }




}
