package com.campusnest1.groupq.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Signpost
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.ui.theme.BackgroundLight
import com.campusnest1.groupq.ui.theme.CampusNestTheme
import com.campusnest1.groupq.ui.theme.OrangeAccentLight
import com.campusnest1.groupq.ui.theme.RedAccent
import com.campusnest1.groupq.ui.theme.RedAccentLight
import com.campusnest1.groupq.ui.theme.TealPrimary
import com.campusnest1.groupq.ui.theme.TealSecondary
import com.campusnest1.groupq.ui.theme.TextGrey
import com.campusnest1.groupq.viewmodel.HostelViewModel

@Composable
fun HostelSearchScreen(
    hostel: Hostel,
    viewModel: HostelViewModel = viewModel()
){
    // Load hostels into ViewModel on first composition
    LaunchedEffect(Unit) {
        viewModel.loadAllHostels(MockData.mockHostels)
    }

    Scaffold(
        topBar = { SearchTopBar() },
        containerColor = BackgroundLight
    ){ padding ->
        Column(modifier = Modifier.padding(padding)){
            FilterChipsRow(
                selectedFilter = viewModel.activeFilterType,
                onFilterClick = { filterName ->
                    viewModel.openFilterSheet(filterName)
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(viewModel.filteredHostels){hostel ->
                    SearchHostelCard(hostel)
                }
            }
        }

    }

    if (viewModel.showFilterSheet){
        FilterBottomSheet(
            filterType = viewModel.activeFilterType,
            initialPriceRange = viewModel.priceRange,
            initialLocation = viewModel.selectedLocation,
            initialRooms = viewModel.selectedRoomTypes,
            onApply = { newPriceRange, newLocation, newRooms ->
                viewModel.applyFilters(newPriceRange, newLocation, newRooms)
            },
            onDismiss = { viewModel.closeFilterSheet() }
        )
    }
}

@Composable
fun SearchHostelCard(hostel: Hostel) {
    val statusText = when (hostel.availableRooms) {
        0 -> { "Sold Out" }
        in 1..5 -> { "${hostel.availableRooms} left" }
        else -> ("Available")
    }

    val statusColor = when (hostel.availableRooms) {
        0 -> { Color.Red }
        in 1..<5 -> { RedAccent }
        else -> (TealPrimary)
    }


        val statusBg = when (hostel.availableRooms) {
        0 -> { RedAccentLight }
        in 1..<5 -> { OrangeAccentLight }
        else -> (Color(0xFFE8F5E9)) //Light Green Bg
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),

    ){
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .size(width = 75.dp, height = 110.dp)
                    .clip(RoundedCornerShape(20.dp))
            ){
                //Hostel Image
                AsyncImage(
                    model = hostel.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop

                )

                //Favorites
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(28.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = TextGrey,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            //Hostel name
            Column(modifier = Modifier.weight(1f)){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = hostel.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    HostelRating(hostel)
                }

                //Distance
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Signpost,
                        contentDescription = null,
                        tint = TextGrey,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = hostel.distance,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextGrey
                    )
                }

                //Price
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.Bottom){
                        Text(
                            text = "UGX ${hostel.highestPrice} ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )

                        Text(
                            text = " /MO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGrey,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    //Room Status
                        Surface(color = statusBg, shape = RoundedCornerShape(12.dp)) {
                            Text(
                                text = statusText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filterType: String,
    initialPriceRange: ClosedFloatingPointRange<Float>,
    initialLocation: String,
    initialRooms: Set<String>,
    onApply: (ClosedFloatingPointRange<Float>, String, Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var priceRange by remember { mutableStateOf(initialPriceRange) }
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    var selectedRooms by remember { mutableStateOf(initialRooms) }

    val roomOptions = listOf("Single", "Double", "Triple")
    val locations = listOf("Kikoni", "Kikumi Kikumi", "Wandegeya", "Mulago", "Nakulabye")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ){
            Text(
                text = "Filter by $filterType",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Changes based on the filter type
            when(filterType){
                "Price Range"->{
                    Column{
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text("Min: UGX ${priceRange.start.toInt() / 1000}k", fontWeight = FontWeight.Bold)
                            Text("Max: UGX ${priceRange.endInclusive.toInt() / 1000}k", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        RangeSlider(
                            value = priceRange,
                            onValueChange = { priceRange = it },
                            valueRange = 200_000f..3_000_000f,
                            colors = SliderDefaults.colors(
                                thumbColor = TealPrimary,
                                activeTrackColor = TealPrimary,
                                inactiveTrackColor = TealPrimary.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
                "Room Type" -> {
                    roomOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable {
                                //Toggle Logic
                                selectedRooms = if (selectedRooms.contains(option)) {
                                    selectedRooms - option
                                } else {
                                    selectedRooms + option
                                }
                            }
                        ) {
                            Checkbox(
                                checked = selectedRooms.contains(option),
                                onCheckedChange = null
                            )
                            Text(text = option, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
                "Location" -> {
                    Column {
                        locations.forEach { location ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { selectedLocation = location }
                            ) {
                                RadioButton(
                                    selected = (location == selectedLocation),
                                    onClick = null
                                )
                                Text(text = location, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onApply(priceRange, selectedLocation, selectedRooms) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            ){
                Text("Apply Filter", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FilterChipsRow(selectedFilter: String, onFilterClick: (String) -> Unit) {
    val filters = listOf("Price Range", "Location", "Room Type")


    LazyRow(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp ),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters){ filter ->

            FilterChipItem(
                label = filter,
                isSelected = (filter == selectedFilter),
                onClick = { onFilterClick(filter) }

            )
        }
    }
}

@Composable
fun FilterChipItem(isSelected: Boolean, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, if(isSelected) TealPrimary else Color.LightGray.copy(0.5f)),
        color = if(isSelected) TealSecondary else Color.White
    ){
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if(isSelected) TealPrimary else Color.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if(isSelected) TealPrimary else Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Search Hostels",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.padding(start = 8.dp).size(40.dp)
            ){
                IconButton(onClick = { /* TODO navController popBackStack()*/}){
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        },
        actions = {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.padding(end = 8.dp).size(40.dp)
            ) {
                IconButton(onClick = {/* TODO */}) {
                    Icon(Icons.Default.Search, "Search")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = Color.Unspecified,
            actionIconContentColor = Color.Unspecified
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HostelSearchScreenPreview() {
    CampusNestTheme {
        HostelSearchScreen(
            hostel = MockData.mockHostels[0]
        )
    }
}