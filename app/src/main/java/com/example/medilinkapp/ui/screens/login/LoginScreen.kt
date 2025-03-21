package com.example.medilinkapp.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medilinkapp.ui.theme.MedilinkAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    // Dummy state for email and password
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    MedilinkAppTheme {
        // Use a white background for a clean look
        Scaffold(
            containerColor = Color.White
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title using Times New Roman (Serif) style
                    Text(
                        text = "Welcome Back",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color.Black
                        )
                    )

                    // Email OutlinedTextField with custom styling
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = {
                            Text(
                                "Email",
                                style = TextStyle(fontFamily = FontFamily.Serif)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color.Black
                        )
                    )

                    // Password OutlinedTextField with custom styling
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = {
                            Text(
                                "Password",
                                style = TextStyle(fontFamily = FontFamily.Serif)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color.Black
                        )
                    )

                    // Primary Login Button
                    Button(
                        onClick = { navController.navigate("dashboard") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Login",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                    }

                    // Outlined Sign Up Button
                    OutlinedButton(
                        onClick = { navController.navigate("signup") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }

                    // Optional Forgot Password Button
                    TextButton(
                        onClick = { /* TODO: Implement forgot password action */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }
    }
}
