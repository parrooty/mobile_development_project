package edu.ap.mobile_development_project.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import edu.ap.mobile_development_project.enums.Category
import java.io.File
import kotlin.io.encoding.Base64

@Composable
fun AddPoIScreen(
    navController: NavHostController,
    onAddPoI: (PointOfInterest) -> Unit,
    categories: List<Category>,
    modifier: Modifier
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(listOf<Category>()) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val file = copyUriToFile(context, uri)
                image = Base64.encode(file.readBytes())
                Log.d("PhotoPicker", "Image Base64: $image")

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.height(100.dp)
        )
        Box() {
            Button(
                onClick = { expanded = !expanded }) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, "Dropdown Arrow")
                    Text("Select Categories")
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            if (selectedCategories.contains(category)) {
                                selectedCategories -= category
                            } else {
                                selectedCategories += category
                            }

                            expanded = false
                        },
                        trailingIcon = {
                            if (selectedCategories.contains(category)) {
                                Icon(Icons.Filled.Check, "Check")
                            }
                        }
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            selectedCategories.forEach { category ->
                InputChip(
                    selected = selectedCategories.contains(category),
                    onClick = { selectedCategories -= category },
                    label = { Text(category.name) },
                    trailingIcon = { Icon(Icons.Filled.Close, "Close") }
                )
            }
        }
        Button(
            onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        ) {
            Text("Select Image")
        }
        Button(
            onClick = {
                onAddPoI(
                    PointOfInterest(
                        name,
                        0.0,
                        0.0,
                        image,
                        selectedCategories,
                        "-OfTQbH99gVHScu9Vxxr"
                    )
                )
            }
        ) {
            Text("Add")
        }

    }
}

fun copyUriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File.createTempFile("selected_", ".jpg", context.cacheDir)

    inputStream.use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }
    return file
}

@Preview
@Composable
fun AddPoIScreenPreview() {
    AddPoIScreen(
        navController = NavHostController(LocalContext.current),
        onAddPoI = {},
        categories = listOf(Category.Cafe, Category.Museum),
        modifier = Modifier,
    )
}