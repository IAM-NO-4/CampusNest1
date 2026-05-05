package com.campusnest1.groupq.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Signpost
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.ui.theme.*
import com.campusnest1.groupq.viewmodel.HostelViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostelSearchScreen(navController: NavHostController, viewModel: HostelViewModel = koinViewModel()){
    var showFilterSheet by remember {mutableStateOf(false)} //Remember if the drawer open
    var activeFilterType by remember {mutableStateOf("")} // Remember which filter clicked

    var searchQuery by remember { mutableStateOf("") }
    var appliedPriceRange by remember { mutableStateOf<ClosedFloatingPointRange<Float>>(200_000f..3_000_000f) }
    var appliedSelectedRooms by remember { mutableStateOf(setOf<String>()) }
    var appliedSelectedLocation by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchHostelsData()
    }

    val filteredHostels = viewModel.hostels.filter { hostel ->
        val matchesSearch = searchQuery.isEmpty()
                || hostel.name.contains(searchQuery, ignoreCase = true)
                || hostel.location.toString().contains(searchQuery, ignoreCase = true)
        val matchesPrice = hostel.highestPrice.toFloat() in appliedPriceRange
        val matchesRooms = appliedSelectedRooms.isEmpty() || appliedSelectedRooms.any { roomType -> hostel.roomTypes.contains(roomType) }
        val matchesLocation = appliedSelectedLocation.isEmpty() || hostel.location == appliedSelectedLocation
        matchesSearch && matchesPrice && matchesRooms && matchesLocation
    }

    Scaffold(
        topBar = { SearchTopBar(navController) },
        containerColor = BackgroundLight
    ){ padding ->
        Column(modifier = Modifier.padding(padding)){
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search hostels...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp)
            )

            FilterChipsRow(
                selectedFilter = activeFilterType,
                onFilterClick = { filterName ->
                    activeFilterType = filterName
                    showFilterSheet = true
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredHostels){hostel->
                    SearchHostelCard(hostel, navController, viewModel)
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filterType = activeFilterType,
            onDismiss = { showFilterSheet = false},
            onApply = { range, rooms, loc ->
                appliedPriceRange = range as ClosedFloatingPointRange<Float>
                appliedSelectedRooms = rooms
                appliedSelectedLocation = loc
                showFilterSheet = false
            }
        )
    }
}

@Composable
fun SearchHostelCard(hostel: Hostel, navController: NavHostController, viewModel: HostelViewModel) {
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
        0 -> RedAccentLight
        in 1..5 -> OrangeAccentLight
        else -> Color(0xFFE8F5E9)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { navController.navigate("hostel_details/${hostel.hostelId}") },
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
                    IconButton(onClick = { /* TODO: Toggle fav from ViewModel */ }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isSaved) Color.Red else TextGrey,
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
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

// ... Rest of the filter and topbar components ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(filterType: String, onDismiss: () -> Unit, onApply: (ClosedFloatingPointRange<Float>, Set<String>, String) -> Unit) {
    var priceRange by remember {mutableStateOf<ClosedFloatingPointRange<Float>>(200_000f..3_000_000f)}

    val roomOptions = listOf("Single", "Double", "Triple")
    val locations = listOf("Kikoni", "Kikumi Kikumi", "Wandegeya", "Mulago", "Nakulabye")

    // Helper function to format price
    fun formatPrice(price: Float): String {
        val priceInt = price.toInt()
        return when {
            priceInt >= 1_000_000 -> "${priceInt / 1_000_000}M"
            else -> "${priceInt / 1000}k"
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Filter by $filterType",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (filterType) {
                "Price Range" -> {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min: UGX ${formatPrice(priceRange.start)}", fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Max: UGX ${formatPrice(priceRange.endInclusive)}", fontWeight = FontWeight.Bold, color = TextDark)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        RangeSlider(
                            value = priceRange,
                            onValueChange = onPriceRangeChange,
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
                                onRoomTypesChange(
                                    if (selectedRoomTypes.contains(option))
                                        selectedRoomTypes - option
                                    else
                                        selectedRoomTypes + option
                                )
                            }
                        ) {
                            Checkbox(checked = selectedRoomTypes.contains(option), onCheckedChange = null)
                            Text(text = option, modifier = Modifier.padding(start = 8.dp), color = TextDark)
                        }
                    }
                }
                "Location" -> {
                    Column {
                        locations.forEach { location ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable { onLocationChange(location) }
                            ) {
                                RadioButton(selected = (location == selectedLocation), onClick = null)
                                Text(text = location, modifier = Modifier.padding(start = 8.dp), color = TextDark)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onApply(priceRange, selectedRooms, selectedLocation) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            ) {
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
fun SearchTopBar(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            if (showSearchField) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search hostels...", color = TextGrey) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        cursorColor = TealPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )
            } else {
                Text(
                    text = "Search Hostels",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.padding(start = 8.dp).size(40.dp)
            ){
                IconButton(onClick = { navController.popBackStack() }){
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
                IconButton(onClick = onSearchToggle) {
                    Icon(
                        imageVector = if (showSearchField) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (showSearchField) "Close Search" else "Search",
                        tint = TextDark
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundLight,
            titleContentColor = TextDark,
            navigationIconContentColor = TextDark,
            actionIconContentColor = TextDark
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HostelSearchScreenPreview() {
    CampusNestTheme {
        HostelSearchScreen(
            navController = rememberNavController()
        )
    )
}