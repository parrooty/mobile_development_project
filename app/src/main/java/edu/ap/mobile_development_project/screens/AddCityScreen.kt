package edu.ap.mobile_development_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AddCityScreen(
    navController: NavHostController,
    onAddCity: (String, String, String) -> Unit,
    modifier: Modifier
) {
    var name by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }

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
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onAddCity(name, longitude, latitude) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add City")
        }
    }
}