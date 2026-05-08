package com.campusnest1.groupq.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.campusnest1.groupq.ui.theme.TealPrimary

@Composable
fun BottomNavBar(
    navController: NavHostController,
    isVisible: Boolean = true)
{
    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 150.dp,
        label = "nav_hide"
    )

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Hostels,
        BottomNavItem.Profile
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        modifier = Modifier
            .offset(y = offsetY)
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .height(72.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                
                val contentColor by animateColorAsState(
                    targetValue = if (selected) TealPrimary else Color.Gray,
                    label = "color"
                )
                
                val backgroundColor by animateColorAsState(
                    targetValue = if (selected) TealPrimary.copy(alpha = 0.1f) else Color.Transparent,
                    label = "bg"
                )

                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!selected) {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        if (selected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.label,
                                color = contentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
