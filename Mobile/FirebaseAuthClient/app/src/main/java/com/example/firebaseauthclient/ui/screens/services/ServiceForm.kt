package com.example.firebaseauthclient.ui.screens.services

import Services
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.firebaseauthclient.AuthState
import com.example.firebaseauthclient.AuthViewModel

@Composable
fun ServiceFormScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    serviceId: String? = null,
    authViewModel: AuthViewModel,
    serviceViewModel: ServiceViewModel = viewModel()
) {
    val authState = authViewModel.authState.observeAsState()
    val currentUser = authViewModel.currentUser.observeAsState()
    val serviceDetail by serviceViewModel.serviceDetail.collectAsStateWithLifecycle()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    var id by remember { mutableStateOf(serviceId ?: "") }
    var client by remember { mutableStateOf(currentUser.value?.displayName ?: "") }
    var user_id by remember { mutableStateOf(currentUser.value?.uid ?: "") }
    var price by remember { mutableStateOf("0.0") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Pendente") }


    LaunchedEffect(serviceId) {
        serviceId?.let { serviceViewModel.getServiceDetails(it) }
    }

    LaunchedEffect(serviceDetail) {
        serviceDetail?.let { service ->
            id = service.id
            client = service.client
            user_id = service.user_id
            price = service.price
            description = service.description
            status = service.status
        }
    }

    Scaffold(
        topBar = {
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 35.dp, start = 20.dp)
            ) {
                Text(text = "< Voltar", fontSize = 20.sp)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            TextField(
                value = client,
                onValueChange = { client = it },
                label = { Text("Seu nome:") },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                readOnly = true
            )
            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Preço") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )

            Text("Status", modifier = Modifier.padding(start = 8.dp, top = 16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .offset(x = (-15).dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                status = "Pendente"
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RadioButton(
                                selected = status == "Pendente",
                                onClick = { status = "Pendente" }
                            )
                            Text("Pendente", modifier = Modifier.padding(start = 8.dp))
                        }
                    }


                    Row(
                        modifier = Modifier
                            .clickable(enabled = serviceId == null) {
                                status = "Concluído"
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RadioButton(
                                selected = status == "Concluído",
                                onClick = {
                                    if (serviceId != null) status = "Concluído"
                                },
                                enabled = serviceId != null
                            )
                            Text("Concluído", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    if (serviceId == null) {
                        // Criar um novo serviço
                        val newService = currentUser.value?.uid?.let {
                            Services(
                                client = client,
                                user_id = it,
                                price = price,
                                description = description,
                                status = status
                            )
                        }
                        newService?.let { serviceViewModel.createService(it) }
                    } else {
                        // Editar um serviço existente
                        val editedService = currentUser.value?.uid?.let {
                            Services(
                                id = id,
                                client = client,
                                user_id = it,
                                price = price,
                                description = description,
                                status = status
                            )
                        }

                        editedService?.let {

                            serviceViewModel.editService(it, serviceId)
                        }
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(text = "Salvar", fontSize = 20.sp, fontWeight = FontWeight.Light)
            }
        }
    }
}
