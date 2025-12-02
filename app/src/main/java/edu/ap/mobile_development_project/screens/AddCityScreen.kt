package edu.ap.mobile_development_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AddCityScreen(
    navController: NavHostController,
    onAddCity: (String) -> Unit,
    modifier: Modifier
) {
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }


    if (error != null) {
        AlertDialog(
            onDismissRequest = { error = null },
            title = { Text("Adding city failed") },
            text = { Text(error!!) },
            confirmButton = {
                Button(onClick = { error = null }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("City Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onAddCity(name)
                    navController.navigateUp()
                } else {
                    error = "City name cannot be empty"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add City")
        }
    }
}