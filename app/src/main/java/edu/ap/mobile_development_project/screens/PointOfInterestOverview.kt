package edu.ap.mobile_development_project.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import edu.ap.mobile_development_project.enums.Category

@Composable
fun PointOfInterestList(
    pointsOfInterest: List<PointOfInterest>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column() {
            pointsOfInterest.forEach { pointOfInterest ->
                PointOfInterestItem(pointOfInterest = pointOfInterest, modifier = Modifier)
            }
        }
    }
}

@Composable
fun PointOfInterestItem(pointOfInterest: PointOfInterest, modifier: Modifier) {
    val imageBytes = Base64.decode(pointOfInterest.image, Base64.DEFAULT)

    Card(
        modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                bitmap = BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.size)
                    .asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = pointOfInterest.name
            )
            for (category in pointOfInterest.categories) {
                Text(
                    text = category.toString()
                )
            }
            Text(
                text = "Review: 4.7"
            )
        }
    }
}

@Preview
@Composable
fun PointOfInterestListPreview() {
    val pointsOfInterest = listOf<PointOfInterest>(
        PointOfInterest(
            "Point of Interest 1",
            1.0,
            1.0,
            "image",
            listOf(
                Category.Cafe,
            ),
            "1"
        ),
        PointOfInterest(
            "Point of Interest 2",
            2.0,
            2.0,
            "image",
            listOf(
                Category.Cafe,
            ),
            "1"
        )
    )
    PointOfInterestList(pointsOfInterest = pointsOfInterest, navController = NavHostController(
        LocalContext.current))
}