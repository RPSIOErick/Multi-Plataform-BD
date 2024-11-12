package com.example.firebaseauthclient.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.firebaseauthclient.ui.screens.LoadingIndicator

@Composable
fun ServiceDetailsScreen(serviceId: String, navController: NavController) {
    val serviceViewModel: ServiceViewModel = viewModel()
    val service by serviceViewModel.serviceDetail.collectAsStateWithLifecycle()

    LaunchedEffect(serviceId) {
        if (service == null) {
            serviceViewModel.getServiceDetails(serviceId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        if (service == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadingIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Carregando...", fontSize = 20.sp)
            }
        } else {
            ElevatedCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)),
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Serviço solicitado por: ${service?.client}.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color(0xFF121212)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${service?.description}",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth().padding(15.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color(0xFF121212))
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (service!!.i_date.isNotEmpty() && service!!.f_date.isNotEmpty()) {
                                "Pedido em: ${formatDate(service!!.i_date)} |-> ${formatDate(service!!.f_date)}."
                            } else {
                                "Sem datas definidas, contate o provedor para atualizar."
                            },
                            fontSize = 15.sp,
                            textAlign = TextAlign.Start,
                            color = Color(0xFFF5F5F5)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(top = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("service_form/${serviceId}") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Editar serviço")
                    }

                    Button(
                        onClick = {
                            serviceViewModel.deleteService(serviceId)
                            navController.popBackStack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCC3333),
                            contentColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Text(text = "Deletar serviço")
                    }
                }
            }
        }
    }
}
