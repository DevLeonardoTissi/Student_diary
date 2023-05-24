package com.example.studentdiary.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.ui.PUBLIC_TENDER_COLLECTION
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class PublicTenderRepository(private val firestore: FirebaseFirestore) {

    fun search(): LiveData<List<PublicTender>> {
        val liveData = MutableLiveData<List<PublicTender>>()
        firestore.collection(PUBLIC_TENDER_COLLECTION)
            .addSnapshotListener { value, _ ->
                value?.let { snapshot ->
                    val publicTenders : List<PublicTender> = snapshot.documents.mapNotNull {
                            document ->
                        converterToPublicTender(document)
                    }
                    liveData.value = publicTenders
                }
            }
        return liveData
    }

    private fun converterToPublicTender(document: DocumentSnapshot): PublicTender {

        val id = document.id
        val name = document.getString("name")
        val description = document.getString("description")
        val url = document.getString("url")
        val img = document.getString("img")
        val contest = document.getBoolean("contest") ?: false
        val course = document.getBoolean("course") ?: false
        return PublicTender(id, name, description, url, img, contest, course)
        }
}