package edu.ap.mobile_development_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
            onClick = { onAddCity(name); navController.navigateUp() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add City")
        }
    }
}