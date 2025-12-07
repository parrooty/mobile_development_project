package edu.ap.mobile_development_project.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.ap.mobile_development_project.domain.Comment
import edu.ap.mobile_development_project.domain.Rating
import edu.ap.mobile_development_project.viewModels.AuthViewModel
import edu.ap.mobile_development_project.viewModels.PoIViewModel

@Composable
fun CommentScreen(
    pointOfInterestId: String,
    poiViewModel: PoIViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val pointOfInterest by poiViewModel.getPoIByIdAsFlow(pointOfInterestId).collectAsState()
    val rating = remember { mutableIntStateOf(0) }
    val openRatingDialog = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (pointOfInterest == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        val poi = pointOfInterest!!
        var imageBytes: ByteArray? = null;

        try {
            imageBytes = Base64.decode(pointOfInterest?.image, Base64.DEFAULT)
        } catch (e: Exception) {
            imageBytes = Base64.decode(
                "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC",
                Base64.DEFAULT
            )
        }

        val ratings = pointOfInterest?.ratings
        val averageRating = if (ratings?.isNotEmpty() ?: false) ratings.map { it.rating }.average() else 0.0
        val ratingCount = ratings?.size
        val commentText = remember { mutableStateOf("") }

        Column {
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
                        text = pointOfInterest?.name!!,
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
                            for (category in pointOfInterest?.categories!!) {
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
                                onClick = { rating.intValue = 0; openRatingDialog.value = true; },
                            ) {
                                if (ratingCount != null) {
                                    Text(
                                        text = if (ratingCount > 0) "Rating: ".format(averageRating) else "Unreviewed"
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.hsl(54f, 0.89f, 0.50f)
                                )
                                if (ratingCount != null) {
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
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = commentText.value,
                    onValueChange = { newText ->
                        commentText.value = newText
                    },
                    label = { Text("Comment") },
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        content = { Text("Post") },
                        onClick = {
                            if (commentText.value.isNotBlank()) {
                                poiViewModel.addComment(
                                    comment = Comment(
                                        comment = commentText.value,
                                        pointOfInterestId = pointOfInterestId,
                                        userId = authViewModel.currentUser.value?.uid.toString()
                                    )
                                )
                                commentText.value = ""
                            }
                        },
                        modifier = Modifier.padding( 8.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                pointOfInterest?.comments?.forEach { comment ->
                    CommentItem(comment = comment, authViewModel = authViewModel)
                }
            }
        }
        if (openRatingDialog.value) RatingWindow(
            onDismissRequest = { openRatingDialog.value = false },
            onConfirmation = { openRatingDialog.value = false; poiViewModel.addRating(
                Rating(
                    rating = rating.intValue,
                    pointOfInterestId = pointOfInterestId,
                    userId = authViewModel.currentUser.value?.uid.toString()
                )
            ) },
            dialogTitle = "Rate Point of Interest",
            dialogText = "Please rate the point of interest",
            rating = rating
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
) {
    val userEmail = remember { mutableStateOf("Loading...") }

    // This effect runs once when the CommentItem is first displayed.
    // It launches a coroutine to fetch the user's email from their ID.
    LaunchedEffect(key1 = comment.userId) {
        authViewModel.getUserEmailById(comment.userId) { email ->
            userEmail.value = email ?: "Unknown User"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp) // Add some padding around each card
    ) {
        Column(
            modifier = Modifier.padding(12.dp) // Add padding inside the card
        ) {
            // Row for the user's email (the author)
            Text(
                text = userEmail.value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            // Spacer to add some vertical distance
            Spacer(modifier = Modifier.height(4.dp))
            // The actual comment text
            Text(
                text = comment.comment,
                fontSize = 16.sp,
                lineHeight = 22.sp // Improve readability for multi-line comments
            )
        }
    }
}