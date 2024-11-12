package com.example.firebaseauthclient.ui.screens.services

import Services
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServicesListViewModel : ViewModel() {

    private var _servicesList = MutableStateFlow<List<Services>>(emptyList())
    var servicesList = _servicesList.asStateFlow()

    init {
        getServicesList()
    }

    fun getServicesList() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("servicos")
                .whereEqualTo("user_id", userId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        // Mapeia os documentos para incluir o ID
                        _servicesList.value = value.documents.map { document ->
                            val service = document.toObject(Services::class.java)
                            service?.let {
                                it.id = document.id
                            }
                            service
                        }.filterNotNull()
                    }
                }
        }
    }
}
