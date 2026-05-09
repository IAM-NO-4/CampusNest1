package com.campusnest1.groupq.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.campusnest1.groupq.model.Notification
import com.campusnest1.groupq.ui.theme.BackgroundLight
import com.campusnest1.groupq.ui.theme.CampusNestTheme
import com.campusnest1.groupq.ui.theme.LightGray
import com.campusnest1.groupq.ui.theme.SurfaceWhite
import com.campusnest1.groupq.ui.theme.TealPrimary
import com.campusnest1.groupq.ui.theme.TextDark
import com.campusnest1.groupq.ui.theme.TextGrey
import com.campusnest1.groupq.utils.formatTimeStamp
import com.campusnest1.groupq.utils.getNotificationColors
import com.campusnest1.groupq.utils.getNotificationIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    navController: NavController?,
    notifications: List<Notification>,
    onDismiss: () -> Unit,
    onDelete: (Notification) -> Unit,
    onNotificationClick: (Notification) -> Unit
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)// Allows sheet to open up to full height showing entire sheet

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundLight,
        dragHandle = { BottomSheetDefaults.DragHandle()}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ){
           Row(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(horizontal = 12.dp, vertical = 8.dp),
               verticalAlignment = Alignment.CenterVertically
           ){
               Text(
                   text = "Notifications",
                   style = MaterialTheme.typography.headlineSmall,
                   fontWeight = FontWeight.Bold
               )
           }

            if(notifications.isEmpty()){
                EmptyNotificationState()
            }else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(items = notifications, key = { it.notificationId }
                    ){ notification ->

                        //Swipe to Dismiss
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if(it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd){
                                    onDelete(notification)
                                    true
                                }else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue){
                                        SwipeToDismissBoxValue.Settled -> Color.Transparent
                                        else -> Color.Red.copy(alpha = 0.8f)
                                    }, label = "ColorAnimation"
                                )
                                Box(
                                    Modifier.fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ){
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = SurfaceWhite
                                    )
                                }
                            },
                            content = {
                                NotificationItem(
                                    notification = notification,
                                    onClick = { onNotificationClick(notification) },
                                )
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 0.5.dp,
                            color = LightGray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 40.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            tint = TextGrey.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "No Notifications yet",
            style = MaterialTheme.typography.titleMedium,
            color = TextGrey
        )
        Text(
            text = "We will notify you when something happens",
            style = MaterialTheme.typography.bodySmall,
            color = TextGrey.copy(alpha = 0.7f)
        )
    }

}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
) {
    val icon = getNotificationIcons(notification.category)
    val (iconColor, iconBackgroundColor) = getNotificationColors(notification.category)

    Surface(
        color = if (notification.isRead) SurfaceWhite else TealPrimary.copy(0.03f),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = iconBackgroundColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextDark
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.padding(horizontal = 4.dp)){
                Text(
                    text = formatTimeStamp(notification.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGrey.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = CircleShape,
                    color = if (notification.isRead) Color.Transparent else TealPrimary,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp).size(8.dp)) { }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationSheetPreview(){
    CampusNestTheme {
        NotificationsSheet(
            navController = null,
            notifications = MockData.mockNotification,
            onDismiss = {},
            onDelete = {},
            onNotificationClick = {}
        )
    }
}