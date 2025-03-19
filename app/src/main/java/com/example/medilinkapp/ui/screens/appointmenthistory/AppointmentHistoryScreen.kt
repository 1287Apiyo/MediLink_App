package com.example.medilinkapp.ui.screens.appointmenthistory

import Appointment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentHistoryScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var appointments by remember { mutableStateOf(emptyList<Appointment>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    // Listen to appointments collection in Firestore
    LaunchedEffect(Unit) {
        listenerRegistration = firestore.collection("appointments")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    error = e.message
                    isLoading = false
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val list = it.documents.mapNotNull { doc ->
                        // Map the document to Appointment and include the document ID.
                        doc.toObject(Appointment::class.java)?.copy(id = doc.id)
                    }
                    appointments = list
                    isLoading = false
                }
            }
    }

    DisposableEffect(Unit) {
        onDispose { listenerRegistration?.remove() }
    }

    // Group appointments by status
    val upcomingAppointments = appointments.filter { it.status == "upcoming" }
    val pastAppointments = appointments.filter { it.status == "past" }

    // Tab state: 0 for Upcoming, 1 for Past
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Appointment History", fontFamily = FontFamily.Serif, color = MaterialTheme.colorScheme.onPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        // Overall white background.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Tab row to toggle between Upcoming and Past
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontFamily = FontFamily.Serif) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (selectedTab) {
                0 -> {
                    if (upcomingAppointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No upcoming appointments.", fontFamily = FontFamily.Serif)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(upcomingAppointments) { appointment ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(durationMillis = 500))
                                ) {
                                    EditableAppointmentCard(
                                        appointment = appointment,
                                        onDelete = {
                                            appointment.id?.let { id ->
                                                firestore.collection("appointments").document(id)
                                                    .delete()
                                            }
                                        },
                                        onUpdate = { updatedAppointment ->
                                            // Update the appointment in Firestore.
                                            updatedAppointment.id?.let { id ->
                                                firestore.collection("appointments").document(id)
                                                    .update(
                                                        mapOf(
                                                            "appointmentDate" to updatedAppointment.appointmentDate,
                                                            "appointmentTime" to updatedAppointment.appointmentTime,
                                                            "notes" to updatedAppointment.notes
                                                        )
                                                    )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    if (pastAppointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No past appointments.", fontFamily = FontFamily.Serif)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(pastAppointments) { appointment ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(durationMillis = 500))
                                ) {
                                    // For past appointments, editing may be disabled.
                                    CompactAppointmentCard(appointment = appointment, onDelete = {
                                        appointment.id?.let { id ->
                                            firestore.collection("appointments").document(id)
                                                .delete()
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableAppointmentCard(
    appointment: Appointment,
    onDelete: () -> Unit,
    onUpdate: (Appointment) -> Unit
) {
    var showEditSheet by remember { mutableStateOf(false) }

    // Use a custom card design for upcoming appointments
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditSheet = true },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Dr. ${appointment.doctorName}",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "Date: ${appointment.appointmentDate}",
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Time: ${appointment.appointmentTime}",
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Notes: ${appointment.notes}",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showEditSheet = true }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Appointment",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Appointment",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false }
        ) {
            EditAppointmentSheet(
                appointment = appointment,
                onSave = { updatedAppointment ->
                    onUpdate(updatedAppointment)
                    showEditSheet = false
                },
                onCancel = { showEditSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAppointmentSheet(
    appointment: Appointment,
    onSave: (Appointment) -> Unit,
    onCancel: () -> Unit
) {
    var date by remember { mutableStateOf(appointment.appointmentDate) }
    var time by remember { mutableStateOf(appointment.appointmentTime) }
    var notes by remember { mutableStateOf(appointment.notes) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Edit Appointment", fontFamily = FontFamily.Serif, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Appointment Date", fontFamily = FontFamily.Serif) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Appointment Time", fontFamily = FontFamily.Serif) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes", fontFamily = FontFamily.Serif) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel", fontFamily = FontFamily.Serif)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                onSave(appointment.copy(
                    appointmentDate = date,
                    appointmentTime = time,
                    notes = notes
                ))
            }) {
                Text("Save", fontFamily = FontFamily.Serif)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactAppointmentCard(
    appointment: Appointment,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Dr. ${appointment.doctorName}",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                Text(
                    text = "Date: ${appointment.appointmentDate}",
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Time: ${appointment.appointmentTime}",
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Notes: ${appointment.notes}",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Appointment",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
