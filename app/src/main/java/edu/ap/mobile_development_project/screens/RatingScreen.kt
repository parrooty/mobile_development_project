package edu.ap.mobile_development_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun RatingScreen(
    navController: NavHostController,
    onAddReview: (String) -> Unit,
    modifier: Modifier
) {
    var rating by remember { mutableIntStateOf(0) }

    var error by remember { mutableStateOf<String?>(null) }

    if (error != null) {
        AlertDialog(
            onDismissRequest = { error = null },
            title = { Text("Adding rating failed") },
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
        for (i in 1..5) {
            Button(
                onClick = {
                    rating = i
                }) {
                if (i <= rating) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                    )
                }
                else {
                    Icon(
                        imageVector = Icons.Outlined.StarOutline,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}