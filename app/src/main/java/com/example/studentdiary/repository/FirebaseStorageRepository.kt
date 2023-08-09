package com.example.studentdiary.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageRepository(private val firebaseStorage: FirebaseStorage) {

    suspend fun updateUserPhoto(file: Uri, userId: String?): Uri {
        val reference =
            firebaseStorage.reference.child("user_photo/${userId}.jpg")
        return reference.putFile(file).await().storage.downloadUrl.await()
    }

    suspend fun removeUserPhoto(userId: String?) {
        val reference =
            firebaseStorage.reference.child("user_photo/${userId}.jpg")
        reference.delete().await()
    }
}