package com.campusnest1.groupq.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Signpost
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.campusnest1.groupq.model.Hostel
import com.campusnest1.groupq.ui.theme.*
import com.campusnest1.groupq.utils.formatCurrency
import com.campusnest1.groupq.viewmodel.HostelViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HostelSearchScreen(
    navController: NavHostController,
    viewModel: HostelViewModel = koinViewModel(),
    onScroll: (Boolean) -> Unit
) {
    val scrollState = rememberLazyListState()

    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    var activeFilterType by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showSearchField by rememberSaveable { mutableStateOf(false) }
    var priceRange by remember { mutableStateOf(200_000f..3_000_000f) }
    var selectedLocation by rememberSaveable { mutableStateOf("") }
    var selectedRoomTypes by remember { mutableStateOf(setOf<String>()) }
    val hasFetched = rememberSaveable { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (!hasFetched.value) {
            viewModel.fetchHostelsData()
            hasFetched.value = true
        }
    }

    val allHostels = viewModel.hostels
    val isLoading = viewModel.isLoading.value

    val filteredHostels = allHostels.filter { hostel ->
        val matchesSearch = searchQuery.isBlank() ||
            hostel.name.contains(searchQuery, ignoreCase = true) ||
            hostel.location.contains(searchQuery, ignoreCase = true)
        val price = hostel.highestPrice.replace(Regex("[^\\d.]"), "").toFloatOrNull() ?: 0f
        val matchesPrice = price >= priceRange.start && price <= priceRange.endInclusive
        val matchesLocation = selectedLocation.isBlank() ||
            hostel.location.contains(selectedLocation, ignoreCase = true)
        matchesSearch && matchesPrice && matchesLocation
    }

    val shouldShow = !scrollState.isScrollInProgress || scrollState.firstVisibleItemIndex == 0
    LaunchedEffect(shouldShow) {
        onScroll(shouldShow)
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                showSearchField = showSearchField,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchToggle = {
                    showSearchField = !showSearchField
                    if (!showSearchField) searchQuery = ""
                },
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterChipsRow(
                selectedFilter = activeFilterType,
                onFilterClick = { filterName ->
                    activeFilterType = filterName
                    showFilterSheet = true
                }
            )
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimary)
                    }
                }
                filteredHostels.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (allHostels.isEmpty()) "No hostels available" else "No hostels found",
                            color = TextGrey,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredHostels) { hostel ->
                            SearchHostelCard(
                                hostel = hostel,
                                isSaved = viewModel.savedStatus[hostel.hostelId] ?: false,
                                onFavoriteClick = { viewModel.toggleFavorite(hostel.hostelId) },
                                onCheckIfSaved = { viewModel.checkIfSaved(hostel.hostelId) },
                                onCardClick = { navController.navigate("hostelDetails/${hostel.hostelId}") }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filterType = activeFilterType,
            priceRange = priceRange,
            selectedLocation = selectedLocation,
            selectedRoomTypes = selectedRoomTypes,
            onPriceRangeChange = { priceRange = it },
            onLocationChange = { selectedLocation = it },
            onRoomTypesChange = { selectedRoomTypes = it },
            onDismiss = { showFilterSheet = false },
            onApply = { showFilterSheet = false }
        )
    }
}

@Composable
fun SearchHostelCard(
    hostel: Hostel,
    isSaved: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onCheckIfSaved: () -> Unit = {},
    onCardClick: () -> Unit = {}
) {
    val statusText = when (hostel.availableRooms) {
        0 -> "Sold Out"
        in 1..5 -> "${hostel.availableRooms} left"
        else -> "Available"
    }
    val statusColor = when (hostel.availableRooms) {
        0 -> Color.Red
        in 1..<5 -> RedAccent
        else -> TealPrimary
    }
    val statusBg = when (hostel.availableRooms) {
        0 -> RedAccentLight
        in 1..5 -> OrangeAccentLight
        else -> Color(0xFFE8F5E9)
    }
    
    LaunchedEffect(hostel.hostelId) {
        onCheckIfSaved()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onCardClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(width = 75.dp, height = 110.dp).clip(RoundedCornerShape(20.dp))) {
                AsyncImage(
                    model = hostel.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd).size(28.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                ) {

                    IconButton(onClick = onFavoriteClick) {
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

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = hostel.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    HostelRating(hostel)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Outlined.Signpost, contentDescription = null, tint = TextGrey, modifier = Modifier.size(16.dp))
                    Text(text = hostel.distance, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextGrey)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom){
                        Text(
                            text = "UGX ${formatCurrency(hostel.highestPrice)} ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(
                            text = " /Semester",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGrey,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                    Surface(color = statusBg, shape = RoundedCornerShape(12.dp)) {
                        Text(text = statusText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    priceRange: ClosedFloatingPointRange<Float>,
    selectedLocation: String,
    selectedRoomTypes: Set<String>,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onLocationChange: (String) -> Unit,
    onRoomTypesChange: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val roomOptions = listOf("Single", "Double", "Triple")
    val locations = listOf("Kikoni", "Kikumi Kikumi", "Wandegeya", "Mulago", "Nakulabye")

    fun formatPrice(price: Float): String {
        val p = price.toInt()
        return if (p >= 1_000_000) "${p / 1_000_000}M" else "${p / 1000}k"
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp)) {
            Text("Filter by $filterType", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(24.dp))
            when (filterType) {
                "Price Range" -> {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Min: UGX ${formatPrice(priceRange.start)}", fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Max: UGX ${formatPrice(priceRange.endInclusive)}", fontWeight = FontWeight.Bold, color = TextDark)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        RangeSlider(value = priceRange, onValueChange = onPriceRangeChange, valueRange = 200_000f..3_000_000f,
                            colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary, inactiveTrackColor = TealPrimary.copy(alpha = 0.2f)))
                    }
                }
                "Room Type" -> {
                    roomOptions.forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                            onRoomTypesChange(if (selectedRoomTypes.contains(option)) selectedRoomTypes - option else selectedRoomTypes + option)
                        }) {
                            Checkbox(checked = selectedRoomTypes.contains(option), onCheckedChange = null)
                            Text(text = option, modifier = Modifier.padding(start = 8.dp), color = TextDark)
                        }
                    }
                }
                "Location" -> {
                    Column {
                        locations.forEach { location ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { onLocationChange(location) }) {
                                RadioButton(selected = (location == selectedLocation), onClick = null)
                                Text(text = location, modifier = Modifier.padding(start = 8.dp), color = TextDark)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onApply, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                Text("Apply Filter", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FilterChipsRow(selectedFilter: String, onFilterClick: (String) -> Unit) {
    val filters = listOf("Price Range", "Location", "Room Type")

    LazyRow(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters) { filter ->
            FilterChipItem(label = filter, isSelected = (filter == selectedFilter), onClick = { onFilterClick(filter) })
        }
    }
}

@Composable
fun FilterChipItem(isSelected: Boolean, label: String, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = MaterialTheme.shapes.large, border = BorderStroke(1.dp, if (isSelected) TealPrimary else Color.LightGray.copy(0.5f)), color = if (isSelected) TealSecondary else Color.White) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = if (isSelected) TealPrimary else Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isSelected) TealPrimary else Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    showSearchField: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (showSearchField) {
                OutlinedTextField(
                    value = searchQuery, onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search hostels...", color = TextGrey) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary, unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                        focusedTextColor = TextDark, unfocusedTextColor = TextDark, cursorColor = TealPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )
            } else {
                Text("Search Hostels", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp, modifier = Modifier.padding(start = 8.dp).size(40.dp)) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                }
            }
        },
        actions = {
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp, modifier = Modifier.padding(end = 8.dp).size(40.dp)) {
                IconButton(onClick = onSearchToggle) {
                    Icon(imageVector = if (showSearchField) Icons.Default.Close else Icons.Default.Search, contentDescription = if (showSearchField) "Close" else "Search", tint = TextDark)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight, titleContentColor = TextDark, navigationIconContentColor = TextDark, actionIconContentColor = TextDark)
    )
}

@Preview(showBackground = true)
@Composable
fun HostelSearchScreenPreview() {
    CampusNestTheme {
        HostelSearchScreen(
            navController = rememberNavController(),
            onScroll ={}
        )
    }
}
