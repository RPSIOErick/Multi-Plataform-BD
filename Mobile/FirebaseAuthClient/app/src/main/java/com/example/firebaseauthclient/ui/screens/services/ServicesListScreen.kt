package com.example.firebaseauthclient.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale


fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun ServicesListScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: ServicesListViewModel = viewModel()) {

    val services by viewModel.servicesList.collectAsStateWithLifecycle()

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(services) { service ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxSize(),
                        onClick = { navController.navigate("service_details/${service.id}") }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = service.client,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Descrição: " + service.description,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            if (service.i_date.isNotEmpty() && service.f_date.isNotEmpty()) {
                                Text(
                                    text = "Data de Pedido: " + formatDate(service.i_date),
                                    modifier = Modifier.align(Alignment.Start)
                                )
                                Text(
                                    text = "Data Final: " + formatDate(service.f_date),
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }
                            else{
                                Text(
                                    text = "Pedido realizado sem data via aplicativo, por favor entre em contato com o provedor e atualize a data no site."
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (service.status == "Pendente") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(30.dp))
                                        .background(Color(0xFFCF3038))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "Status: Pendente",
                                        modifier = Modifier.align(Alignment.Center),
                                        color = Color.White
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(30.dp))
                                        .background(Color(0xFF43BC79))
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "Status: Concluído",
                                        modifier = Modifier.align(Alignment.Center),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if(services.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp)
                        .offset(y = (-20).dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Não há nenhum serviço registrado em seu nome!",
                        textAlign = TextAlign.Center
                    )
                }

            }

            FloatingActionButton(
                onClick = { navController.navigate("service_form") },
                modifier = Modifier
                    .absoluteOffset(x = (-20).dp,y = (-20).dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(50.dp)))
                    .align(Alignment.BottomEnd)
                    .zIndex(1f)
            ) {
                Text(text = "+", fontSize = 45.sp, fontWeight = FontWeight.Light)
            }
        }
    }
}
