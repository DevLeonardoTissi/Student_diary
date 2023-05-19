package com.example.studentdiary.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.ui.PUBLIC_TENDER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class PublicTenderRepository(private val firestore: FirebaseFirestore) {

    fun search(): LiveData<List<PublicTender>> {
        val liveData = MutableLiveData<List<PublicTender>>()
        firestore.collection(PUBLIC_TENDER_COLLECTION)
            .addSnapshotListener { value, _ ->
                value?.let { snapshot ->
                    val publicTenders = mutableListOf<PublicTender>()
                    for (document in snapshot.documents) {
                        val publicTender = document.toObject<PublicTender>()
                        publicTender?.let { publicTenderNotNull ->
                            publicTenders.add(publicTenderNotNull)
                        }
                    }
                    liveData.value = publicTenders
                }
            }
        return liveData
    }
}