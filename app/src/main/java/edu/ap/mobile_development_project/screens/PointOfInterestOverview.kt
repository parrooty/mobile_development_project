package edu.ap.mobile_development_project.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import edu.ap.mobile_development_project.Map
import edu.ap.mobile_development_project.domain.PointOfInterest
import edu.ap.mobile_development_project.enums.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PointOfInterestOverview(
    pointsOfInterest: List<PointOfInterest>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val openRatingDialog = remember { mutableStateOf(false) }
    val selectedPOIId = remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Map(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            )
            PointOfInterestList(
                pointsOfInterest = pointsOfInterest,
                modifier = modifier.fillMaxHeight(),
                openRatingDialog = openRatingDialog,
                selectedPOIId = selectedPOIId
            )
        }
    }
    if (openRatingDialog.value) RatingWindow(
        onDismissRequest = { openRatingDialog.value = false },
        onConfirmation = { openRatingDialog.value = false },
        dialogTitle = "Rate Point of Interest",
        dialogText = "Please rate the point of interest"
    )
}

@Composable
fun PointOfInterestList(
    pointsOfInterest: List<PointOfInterest>,
    scope: CoroutineScope = rememberCoroutineScope(),
    modifier: Modifier,
    openRatingDialog: MutableState<Boolean>,
    selectedPOIId: MutableState<String?>
) {
    val scrollState = rememberScrollState()
    var selectedCategories by remember { mutableStateOf<Set<Category>>(emptySet()) }

    val filteredPointsOfInterest by remember(selectedCategories, pointsOfInterest) {
        derivedStateOf {
            if (selectedCategories.isEmpty()) {
                pointsOfInterest
            } else {
                pointsOfInterest.filter { poi ->
                    poi.categories.any { it in selectedCategories }
                }
            }
        }
    }

    val (selectedChips, unselectedChips) = remember(selectedCategories) {
        Category.entries.partition { it in selectedCategories }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .padding(5.dp, 5.dp)
                .verticalScroll(scrollState),
        ) {
            Text(
                text = "Filters",
                modifier = Modifier,
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (selectedChips.isNotEmpty()) {
                    selectedChips.forEach { category ->
                        FilterChip(
                            text = category.toString(),
                            selected = true,
                            onClick = {
                                selectedCategories = selectedCategories - category
                            },
                            modifier = Modifier,
                        )
                    }
                }
                unselectedChips.forEach { category ->
                    val isSelected = category in selectedCategories
                    FilterChip(
                        text = category.toString(),
                        selected = isSelected,
                        onClick = {
                            selectedCategories += category
                        },
                        modifier = Modifier,
                    )
                }
            }
            filteredPointsOfInterest.forEach { pointOfInterest ->
                PointOfInterestItem(
                    pointOfInterest = pointOfInterest,
                    modifier = Modifier.height(20.dp),
                    openRatingDialog = openRatingDialog,
                    selectedPOIId = selectedPOIId
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        if (scrollState.value > 150) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        scrollState.animateScrollTo(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}


@Composable
fun PointOfInterestItem(
    pointOfInterest: PointOfInterest,
    modifier: Modifier,
    openRatingDialog: MutableState<Boolean>,
    selectedPOIId: MutableState<String?>
) {
    var imageBytes: ByteArray? = null;

    try {
        imageBytes = Base64.decode(pointOfInterest.image, Base64.DEFAULT)
    } catch (e: Exception) {
        imageBytes = Base64.decode(
            "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC",
            Base64.DEFAULT
        )
    }

    Card(
        modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Image(
                bitmap = BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.size
                )
                    .asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = pointOfInterest.name,
                modifier = Modifier
                    .height(40.dp)
                    .padding(5.dp, 10.dp, 0.dp, 0.dp),
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(5.dp, 0.dp, 5.dp, 10.dp)
                .fillMaxWidth()
        ) {
            Column(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    for (category in pointOfInterest.categories) {
                        Text(
                            text = category.toString()
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = { openRatingDialog.value = true; selectedPOIId.value = pointOfInterest.id },
                    ) {
                        Text(
                            text = "Review: 4.7"
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.hsl(54f, 0.89f, 0.50f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(text: String,
               selected: Boolean,
               onClick: () -> Unit,
               modifier: Modifier
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(text)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@Composable
fun RatingWindow(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    pointOfInterestId: String? = null
) {
    var rating by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .background(color = Color.White)
                    .border(
                        shape = RoundedCornerShape(10.dp),
                        width = 0.dp,
                        color = Color.Transparent
                    )
                    .height(100.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    for (i in 1..5) {
                        IconButton(
                            colors = IconButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(0.dp),
                            onClick = {
                                rating = i
                            }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Star $i",
                                tint = if (i <= rating) Color.hsl(54f, 0.89f, 0.50f) else Color.Gray,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                }
                Row {
                    Button(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Button(onClick = onConfirmation) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PointOfInterestListPreview(
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val scrollState = rememberScrollState()
    val pointsOfInterest = listOf<PointOfInterest>(
        PointOfInterest(
            "Point of Interest 1",
            1.0,
            1.0,
            "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC",
            listOf(
                Category.Cafe,
                Category.Museum
            ),
            "1"
        ),
        PointOfInterest(
            "Point of Interest 2",
            2.0,
            2.0,
            "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC",
            listOf(
                Category.Cafe,
            ),
            "1"
        )
    )
    PointOfInterestList(
        pointsOfInterest = pointsOfInterest,
        modifier = Modifier,
        openRatingDialog = remember { mutableStateOf(false) }
    )
}