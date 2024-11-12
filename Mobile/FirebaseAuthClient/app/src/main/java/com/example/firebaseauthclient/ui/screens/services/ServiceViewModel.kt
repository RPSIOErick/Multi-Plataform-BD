package com.example.firebaseauthclient.ui.screens.services

import Services
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServiceViewModel : ViewModel() {

    private var _serviceDetail = MutableStateFlow<Services?>(null)
    var serviceDetail = _serviceDetail.asStateFlow()

    val db = FirebaseFirestore.getInstance()

    fun getServiceDetails(serviceId: String) {
        db.collection("servicos")
            .document(serviceId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _serviceDetail.value = document.toObject(Services::class.java)
                    Log.d("ServiceViewModel", "Service loaded successfully: ${_serviceDetail.value}")
                } else {
                    Log.d("ServiceViewModel", "Service not found")
                    _serviceDetail.value = null
                }
            }
            .addOnFailureListener { e ->
                Log.d("ServiceViewModel", "Error loading service: $e")
                _serviceDetail.value = null
            }
    }

    fun editService(service: Services, serviceId: String) {

        val serviceMap = hashMapOf<String, Any>()

        service.client?.let { serviceMap["client"] = it }
        service.price?.let { serviceMap["price"] = it }
        service.description?.let { serviceMap["description"] = it }
        service.status?.let { serviceMap["status"] = it }

        if (serviceMap.isNotEmpty()) {
            db.collection("servicos")
                .document(serviceId)
                .update(serviceMap)
                .addOnSuccessListener {
                    Log.d("ServiceViewModel", "Service updated successfully")
                }
                .addOnFailureListener { exception ->
                    Log.d("ServiceViewModel", "Error updating service: $exception")
                }
        } else {
            Log.d("ServiceViewModel", "No fields to update")
        }
    }

    fun createService(service: Services) {
        db.collection("servicos")
            .add(service)
            .addOnSuccessListener { document ->
                Log.d("ServiceViewModel", "Service created successfully: ${document.id}")
            }
            .addOnFailureListener { exception ->
                Log.d("ServiceViewModel", "Error creating service: $exception")
            }
    }

    fun deleteService(serviceId: String) {
        db.collection("servicos")
            .document(serviceId)
            .delete()
            .addOnSuccessListener {
                Log.d("ServiceViewModel", "Service deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.d("ServiceViewModel", "Error deleting service: $exception")
            }
    }
}
