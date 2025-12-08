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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
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
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import edu.ap.mobile_development_project.Map
import edu.ap.mobile_development_project.Screen
import edu.ap.mobile_development_project.domain.PointOfInterest
import edu.ap.mobile_development_project.domain.Rating
import edu.ap.mobile_development_project.enums.Category
import edu.ap.mobile_development_project.viewModels.AuthViewModel
import edu.ap.mobile_development_project.viewModels.CitiesViewModel
import edu.ap.mobile_development_project.viewModels.MapViewModel
import edu.ap.mobile_development_project.viewModels.PoIViewModel
import edu.ap.mobile_development_project.viewModels.RatingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PointOfInterestOverview(
    pointsOfInterest: List<PointOfInterest>,
    navController: NavHostController,
    poiViewModel: PoIViewModel,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel,
    citiesViewModel: CitiesViewModel,
    modifier: Modifier = Modifier,
) {
    val openRatingDialog = remember { mutableStateOf(false) }
    val selectedPOIId = remember { mutableStateOf<String?>(null) }
    val rating = remember { mutableIntStateOf(0) }
    var selectedCategories by remember { mutableStateOf<Set<Category>>(emptySet()) }
    var currentCityOnly by remember { mutableStateOf(false) }

//    val filteredPointsOfInterest by remember(selectedCategories, pointsOfInterest) {
//        derivedStateOf {
//            if (selectedCategories.isEmpty()) {
//                pointsOfInterest
//            } else {
//                pointsOfInterest.filter { poi ->
//                    poi.categories.any { it in selectedCategories }
//                }
//            }
//        }
//    }

    val filteredPointsOfInterest by remember(selectedCategories, pointsOfInterest, currentCityOnly, mapViewModel.reverseEntry) {
        derivedStateOf {
            // Start with the base list
            val baseList = pointsOfInterest

            // First, filter by category if any are selected
            val categoryFiltered = if (selectedCategories.isEmpty()) {
                baseList
            } else {
                baseList.filter { poi ->
                    poi.categories.any { it in selectedCategories }
                }
            }

            // Then, filter by city if the checkbox is ticked
            if (currentCityOnly) {
                categoryFiltered.filter { poi ->
                    val userCity = mapViewModel.reverseEntry?.address?.city ?: mapViewModel.reverseEntry?.address?.town
                    val poiCity = citiesViewModel.getCityById(poi.cityId)?.name
                    // Only keep the POI if both cities are known and they match
                    userCity != null && poiCity != null && userCity == poiCity
                }
            } else {
                // If not filtering by city, return the category-filtered list
                categoryFiltered
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Map(
                filteredPointsOfInterest,
                navController = navController,
                mapViewModel = mapViewModel,
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            )
            Checkbox(
                currentCityOnly,
                onCheckedChange = {
                    currentCityOnly = it
                }
            )
            val debugText = "Debug: ${mapViewModel.reverseEntry.toString()}"

// Display the debug text on the screen
            Text(text = debugText)
            val cityText = if (mapViewModel.reverseEntry?.address?.city != null) {
                "Only show results in: ${mapViewModel.reverseEntry?.address?.city.toString()}"
            } else if (mapViewModel.reverseEntry?.address?.town != null) {
                "Only show results in: ${mapViewModel.reverseEntry?.address?.town.toString()}"
            } else {
                "Finding your current city..."
            }
            Text(text = cityText)
            PointOfInterestList(
                pointsOfInterest = filteredPointsOfInterest,
                selectedCategories = selectedCategories,
                onCategorySelected = { category, isSelected ->
                    selectedCategories = if (isSelected) {
                        selectedCategories + category
                    } else {
                        selectedCategories - category
                    }
                },
                navController = navController,
                poiViewModel = poiViewModel,
                authViewModel = authViewModel,
                modifier = modifier.fillMaxHeight(),
                openRatingDialog = openRatingDialog,
                selectedPOIId = selectedPOIId,
                rating = rating
            )
        }
    }
    if (openRatingDialog.value) RatingWindow(
        onDismissRequest = { openRatingDialog.value = false },
        onConfirmation = { openRatingDialog.value = false; poiViewModel.addRating(
            Rating(
                rating = rating.intValue,
                pointOfInterestId = selectedPOIId.value.toString(),
                userId = authViewModel.currentUser.value?.uid.toString()
            )
        ) },
        dialogTitle = "Rate Point of Interest",
        dialogText = "Please rate the point of interest",
        rating = rating
    )
}

@Composable
fun PointOfInterestList(
    pointsOfInterest: List<PointOfInterest>,
    selectedCategories: Set<Category>,
    onCategorySelected: (Category, Boolean) -> Unit,
    navController: NavHostController,
    poiViewModel: PoIViewModel,
    authViewModel: AuthViewModel,
    scope: CoroutineScope = rememberCoroutineScope(),
    modifier: Modifier,
    openRatingDialog: MutableState<Boolean>,
    selectedPOIId: MutableState<String?>,
    rating: MutableIntState
) {
    val scrollState = rememberScrollState()

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
                                onCategorySelected(category, false)
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
                            onCategorySelected(category, true)
                        },
                        modifier = Modifier,
                    )
                }
            }
            pointsOfInterest.forEach { pointOfInterest ->
                PointOfInterestItem(
                    pointOfInterest = pointOfInterest,
                    navController = navController,
                    modifier = Modifier.height(20.dp),
                    openRatingDialog = openRatingDialog,
                    poiViewModel = poiViewModel,
                    authViewModel = authViewModel,
                    selectedPOIId = selectedPOIId,
                    rating = rating
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
    navController: NavHostController,
    modifier: Modifier,
    openRatingDialog: MutableState<Boolean>,
    poiViewModel: PoIViewModel,
    authViewModel: AuthViewModel,
    selectedPOIId: MutableState<String?>,
    rating: MutableIntState
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

    val ratings = pointOfInterest.ratings
    val averageRating = if (ratings.isNotEmpty()) ratings.map { it.rating }.average() else 0.0
    val ratingCount = ratings.size

    Card(
        modifier = Modifier,
        onClick = {
            navController.navigate(Screen.CommentScreen.name + "/${pointOfInterest.id}")
        }
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
                        onClick = { rating.intValue = 0; openRatingDialog.value = true; selectedPOIId.value = pointOfInterest.id },
                    ) {
                        Text(
                            text = if (ratingCount > 0) "Rating: ".format(averageRating) else "Unreviewed"
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.hsl(54f, 0.89f, 0.50f)
                        )
                        Text(
                            // show all ratings value for this poi
                            text = if (ratingCount > 0) "%.1f (%d)".format(averageRating, ratingCount) else ""
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
    rating: MutableIntState
) {
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
                                rating.intValue = i
                            }) {
                            Icon(
                                imageVector = if (i <= rating.intValue) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Star $i",
                                tint = if (i <= rating.intValue) Color.hsl(54f, 0.89f, 0.50f) else Color.Gray,
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