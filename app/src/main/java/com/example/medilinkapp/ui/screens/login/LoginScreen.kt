package com.example.medilinkapp.ui.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medilinkapp.ui.theme.MedilinkAppTheme

@Composable
fun LoginScreen(navController: NavController) {
    // Dummy state for email and password
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    MedilinkAppTheme {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Login", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(32.dp))
                    TextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // For now, simulate successful login by navigating to dashboard
                            navController.navigate("dashboard")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Login")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Don't have an account? Sign Up",
                        color = Color.Blue,
                        modifier = Modifier.clickable { navController.navigate("signup") }
                    )
                }
            }
        }
    }
}
