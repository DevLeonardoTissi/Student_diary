package com.example.studentdiary.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageRepository(private val firebaseStorage: FirebaseStorage) {

    suspend fun updateUserPhoto(file: Uri, userEmail: String?): Uri {
        val reference =
            firebaseStorage.reference.child("user_photo/${userEmail}.jpg")
        return reference.putFile(file).await().storage.downloadUrl.await()
    }

    suspend fun removeUserPhoto(userEmail:String?){
        val reference =
            firebaseStorage.reference.child("user_photo/${userEmail}.jpg")
        reference.delete().await()
    }
}