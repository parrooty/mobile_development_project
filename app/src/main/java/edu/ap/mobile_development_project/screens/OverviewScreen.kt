package edu.ap.mobile_development_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import edu.ap.mobile_development_project.Map
import edu.ap.mobile_development_project.domain.City

@Composable
fun OverviewScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var cities by remember { mutableStateOf(listOf<City>(
        City("City1", 1.0, 1.0),
        City("City2", 2.0, 2.0),
        City("City3", 3.0, 3.0),
        City("City4", 4.0, 4.0),
        City("City5", 5.0, 5.0))) }

    Column() {
        Map(modifier = modifier.fillMaxHeight(.5f))
        CityList(cities = cities, modifier = modifier.fillMaxHeight())
    }

}

@Composable
fun CityList(cities: List<City>, modifier: Modifier) {
    Column(modifier = modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
        cities.forEach { city ->
            CityItem(city = city, 0, modifier = Modifier)
        }
    }
}

@Composable
fun CityItem(city: City, poiAmount: Int, modifier: Modifier) {
    Card(
        modifier = Modifier.padding(10.dp).height(80.dp).fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = city.name,
                fontSize = TextUnit(20f, TextUnitType.Sp)
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = "$poiAmount point(s) of interest",
            )

        }
    }
}

@Preview
@Composable
fun OverviewScreenPreview() {
    var cities = listOf<City>(
        City("City1", 1.0, 1.0),
        City("City2", 2.0, 2.0),
        City("City3", 3.0, 3.0),
        City("City4", 4.0, 4.0),
        City("City5", 5.0, 5.0))
    CityList(cities = cities, modifier = Modifier.fillMaxHeight())
}