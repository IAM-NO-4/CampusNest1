package com.campusnest1.groupq.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.campusnest1.groupq.auth1.Authrepo
import com.campusnest1.groupq.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class loginViewModel : ViewModel() {

    private val repository = Authrepo()
    private val db = Firebase.firestore

    fun login(email: String, password: String, getdata:(User?)-> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            return
        }

        repository.login(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    val uid = user?.uid

                    if (uid!= null){
                        db.collection("User")
                            .document(uid)
                            .get()
                            .addOnSuccessListener {doc ->
                                if(doc.exists()){
                                    val user = doc.toObject(User::class.java)
                                    getdata(user)
                                    val name = user?.name
                                    val email = user?.email
                                    val phone = user?.phone
                                    val userId = user?.userId

                                }else{
                                    getdata(null)
                                }

                            }
                            .addOnFailureListener {
                                getdata(null)
                            }
                    }


                    //wil redirect the user to homescreen
                } else {
                    val error = task.exception?.message

                }
            }
    }

}