package edu.ap.mobile_development_project.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import edu.ap.mobile_development_project.BuildConfig
import edu.ap.mobile_development_project.domain.PointOfInterest
import edu.ap.mobile_development_project.enums.Category
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import kotlin.io.encoding.Base64

@Composable
fun AddPoIScreen(
    navController: NavHostController,
    onAddPoI: (PointOfInterest) -> Unit,
    categories: List<Category>
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(listOf<Category>()) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
        }

    CameraPermissionContext() {
        Box (
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Name", style = MaterialTheme.typography.headlineSmall)
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Description", style = MaterialTheme.typography.headlineSmall)
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
                Text("Categories", style = MaterialTheme.typography.headlineSmall)
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
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.name) }, onClick = {
                                if (selectedCategories.contains(category)) {
                                    selectedCategories -= category
                                } else {
                                    selectedCategories += category
                                }

                                expanded = false
                            }, trailingIcon = {
                                if (selectedCategories.contains(category)) {
                                    Icon(Icons.Filled.Check, "Check")
                                }
                            })
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
                            trailingIcon = { Icon(Icons.Filled.Close, "Close") })
                    }
                }
                Text("Photo", style = MaterialTheme.typography.headlineSmall)
                Button(
                    onClick = { cameraLauncher.launch(uri) }) {
                    Text("Take photo")
                }
                if (capturedImageUri != Uri.EMPTY) {
                    ImageContainer {
                        Image(
                            bitmap = file.readBytes().decodeToImageBitmap(),
                            contentDescription = "Captured image",
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                } else {
                    ImageContainer {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Placeholder",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Button (
                    onClick = {
                        if (capturedImageUri == Uri.EMPTY || name == "" || description == "" || selectedCategories.isEmpty()) {
                            return@Button;
                        }
                        val image = Base64.encode(file.readBytes())
                        onAddPoI(
                            PointOfInterest(
                                name, 0.0, 0.0, image, selectedCategories, "-OfTQbH99gVHScu9Vxxr"
                            )
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Add, "Add")
                        Text("Add", modifier = Modifier, style = TextStyle(fontSize = TextUnit(4f,
                            TextUnitType.Em)))

                    }
                }

            }
        }
    }


}

@Composable
fun ImageContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionContext(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        content()
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                "To add a new point of interest, an image taken on site is required. For this reason, we need to access your camera."
            } else {
                "To add a new point of interest, an image taken on site is required. For this reason, we need to access your camera."
            }
            Text(textToShow, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Grant camera access")
            }
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}


@Preview
@Composable
fun AddPoIScreenPreview() {
    AddPoIScreen(
        navController = NavHostController(LocalContext.current),
        onAddPoI = {},
        categories = listOf(Category.Cafe, Category.Museum)
    )
}