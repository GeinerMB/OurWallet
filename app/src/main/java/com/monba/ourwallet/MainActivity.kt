package com.monba.ourwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monba.ourwallet.ui.theme.OurWalletTheme

// --- MODELOS DE DATOS ---
data class Bolsillo(
    val nombre: String,
    val saldo: Double,
    val color: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OurWalletTheme {
                // Cambiamos el boolean por un String para manejar las 3 pantallas
                var pantallaActual by remember { mutableStateOf("tarjetero") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        "inicio" -> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onLoginSuccess = { pantallaActual = "tarjetero" }
                            )
                        }

                        "tarjetero" -> {
                            MainWalletScreen(
                                modifier = Modifier.padding(innerPadding),
                                // Aquí es donde "conectas" el cable que faltaba
                                onIrARegistro = { pantallaActual = "registro" }
                            )
                        }

                        "registro" -> {
                            // Esta es la nueva pantalla que crearemos
                            // Por ahora, pondremos un botón para poder regresar
                            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Aquí irá el Registro de Movimientos", style = MaterialTheme.typography.headlineSmall)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(onClick = { pantallaActual = "tarjetero" }) {
                                        Text("Volver al Tarjetero")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PANTALLA 1: INICIO ---
@Composable
fun HomeScreen(modifier: Modifier = Modifier, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8F4FF)), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("OurWallet", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = Color(0xFF6200EE))
            Text("Ingresa tu correo para empezar", color = Color.Gray)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { if (email.isNotEmpty()) onLoginSuccess() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- PANTALLA 2: TARJETERO ---
@Composable
fun MainWalletScreen(modifier: Modifier = Modifier, onIrARegistro: () -> Unit) {
    val misBolsillos = listOf(
        Bolsillo("Alimentación", 120000.0, Color(0xFF6200EE)),
        Bolsillo("Citas y Salidas", 45000.0, Color(0xFF7E57C2)),
        Bolsillo("Servicios Casa", 85000.0, Color(0xFF9575CD))
    )

    val saldoTotal = misBolsillos.sumOf { it.saldo }

    Scaffold(
        floatingActionButton = {
            // Este botón ahora es exclusivo para CREAR NUEVOS BOLSILLOS
            FloatingActionButton(onClick = {}, containerColor = Color(0xFF6200EE), contentColor = Color.White, shape = CircleShape) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8F4FF))
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // TARJETA DE BALANCE TOTAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dinero Total", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = "₡${String.format("%,.0f", saldoTotal).replace(',', '.')}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF6200EE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- CAMBIO AQUÍ: Reemplazamos SeccionMovimientoRapido por el Botón ---
            BotonRegistrarMovimiento(onClick = { onIrARegistro() })
            // ---------------------------------------------------------------------

            Text(
                "Mis bolsillos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE),
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(misBolsillos) { bolsillo ->
                    TarjetaGrupo(bolsillo)
                }
            }
        }
    }
}

@Composable
fun TarjetaGrupo(grupo: Bolsillo) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = grupo.color)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(grupo.nombre, color = Color.White, fontWeight = FontWeight.Bold)
            // Corregido: Convertimos el Double a texto con formato de moneda
            Text(
                text = "₡${String.format("%,.0f", grupo.saldo).replace(',', '.')}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun BotonRegistrarMovimiento(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE) // Tu morado principal
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icono de flechas o intercambio para representar movimiento
            Icon(
                imageVector = Icons.Default.Add, // Necesitas import androidx.compose.material.icons.filled.Add
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Registrar Movimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// --- PREVIEWS ---
@Preview(showBackground = true, showSystemUi = true, name = "Vista de Bolsillos")
@Composable
fun BolsillosPreview() {
    OurWalletTheme {
        MainWalletScreen(onIrARegistro = {})
    }
}