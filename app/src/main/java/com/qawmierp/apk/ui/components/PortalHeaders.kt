package com.qawmierp.apk.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.qawmierp.apk.core.i18n.translateRoleToBangla
import com.qawmierp.apk.ui.navigation.Routes

/**
 * Web-parity Admin Panel Header matching `app/dashboard/DashboardShell.tsx`.
 * White/Surface 64dp bar with bordered Hamburger menu, Madrasa title,
 * Refresh, 3-dot More menu, Notification Bell, and HeaderUserProfile pill.
 */
@Composable
fun AdminPanelHeader(
    madrasaName: String,
    userFullName: String,
    userRole: String,
    onRefresh: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var navMenuExpanded by remember { mutableStateOf(false) }
    var moreMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp)
        ) {
            val showProfileDetails = maxWidth >= 400.dp

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Bordered Hamburger Menu + Madrasa Title
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box {
                        Surface(
                            onClick = { navMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Box(
                                modifier = Modifier.size(38.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "মেনু",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = navMenuExpanded,
                            onDismissRequest = { navMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("শিক্ষার্থী ব্যবস্থাপনা") },
                                leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                                onClick = { navMenuExpanded = false; onNavigate(Routes.STUDENTS) }
                            )
                            DropdownMenuItem(
                                text = { Text("দৈনিক হাজিরা") },
                                leadingIcon = { Icon(Icons.Default.FactCheck, contentDescription = null) },
                                onClick = { navMenuExpanded = false; onNavigate(Routes.ATTENDANCE) }
                            )
                            DropdownMenuItem(
                                text = { Text("হিফজ ও কিতাব") },
                                leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                                onClick = { navMenuExpanded = false; onNavigate(Routes.HIFZ_KITAB) }
                            )
                            DropdownMenuItem(
                                text = { Text("পরীক্ষা ও ফলাফল") },
                                leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                                onClick = { navMenuExpanded = false; onNavigate(Routes.EXAMS) }
                            )
                            DropdownMenuItem(
                                text = { Text("আর্থিক ও যাকাত") },
                                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                                onClick = { navMenuExpanded = false; onNavigate(Routes.FINANCE) }
                            )
                            DropdownMenuItem(
                                text = { Text("ফি ও রসিদ") },
                                leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                                onClick = { navMenuExpanded = false; onNavigate(Routes.FEES) }
                            )
                            DropdownMenuItem(
                                text = { Text("অপারেশন ও বোর্ডিং") },
                                leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = null) },
                                onClick = { navMenuExpanded = false; onNavigate(Routes.OPERATIONS) }
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = madrasaName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "অ্যাডমিন প্যানেল",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF047857),
                            maxLines = 1
                        )
                    }
                }

                // Right: 3-Dot More Menu + Notification Bell + HeaderUserProfile Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 3-Dot More Menu (HeaderMoreMenuDropdown)
                    Box {
                        IconButton(
                            onClick = { moreMenuExpanded = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "আরো অপশন",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = moreMenuExpanded,
                            onDismissRequest = { moreMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("ডাটা রিফ্রেশ করুন") },
                                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                onClick = { moreMenuExpanded = false; onRefresh() }
                            )
                            DropdownMenuItem(
                                text = { Text("সেটিংস ও সার্ভার") },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                onClick = { moreMenuExpanded = false; onNavigate(Routes.SETTINGS) }
                            )
                        }
                    }

                    // GlobalNotificationBell
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onNavigate(Routes.NOTIFICATIONS) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "নোটিফিকেশন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 6.dp, end = 6.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }

                    // HeaderUserProfile Pill
                    Surface(
                        onClick = { onNavigate(Routes.SETTINGS) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF059669)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userFullName.trim().firstOrNull()?.toString() ?: "A",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            if (showProfileDetails) {
                                Column(modifier = Modifier.widthIn(max = 110.dp)) {
                                    Text(
                                        text = userFullName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = Color(0xFF047857),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = translateRoleToBangla(userRole),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF047857),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/**
 * Web-parity Parent / Student Portal Mobile Header matching `app/portal/PortalShell.tsx`.
 * Dark Slate-900 topbar with Menu drawer trigger, emerald BookOpen badge, "প্যারেন্ট পোর্টাল",
 * KeyRound quick action, Refresh, and circular Emerald-700 user avatar.
 */
@Composable
fun StudentPortalHeader(
    userFullName: String,
    photoUrl: String?,
    onRefresh: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Menu Button + Emerald BookOpen Icon + "প্যারেন্ট পোর্টাল"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { menuExpanded = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "মেনু খুলুন",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("হাজিরা বিবরণী") },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.ATTENDANCE) }
                        )
                        DropdownMenuItem(
                            text = { Text("হিফজ ও সবক") },
                            leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.HIFZ_KITAB) }
                        )
                        DropdownMenuItem(
                            text = { Text("পরীক্ষা ও ফলাফল") },
                            leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.EXAMS) }
                        )
                        DropdownMenuItem(
                            text = { Text("ফি ও রসিদ") },
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.FEES) }
                        )
                        DropdownMenuItem(
                            text = { Text("নোটিশ বোর্ড") },
                            leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.NOTIFICATIONS) }
                        )
                        DropdownMenuItem(
                            text = { Text("সেটিংস") },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.SETTINGS) }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF059669)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "প্যারেন্ট পোর্টাল",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right: Refresh + KeyRound button + Circular Emerald-700 Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "রিফ্রেশ",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF064E3B).copy(alpha = 0.6f))
                        .border(1.dp, Color(0xFF065F46), RoundedCornerShape(8.dp))
                        .clickable { onNavigate(Routes.SETTINGS) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "পাসওয়ার্ড ও সেটিংস",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF047857))
                        .clickable { onNavigate(Routes.SETTINGS) },
                    contentAlignment = Alignment.Center
                ) {
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = userFullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = userFullName.trim().firstOrNull()?.toString() ?: "প",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = Color(0xFF1E293B))
    }
}

/**
 * Web-parity Teacher Portal Mobile Header matching `app/teacher-portal/TeacherShell.tsx`.
 * Dark Slate-900 topbar with Menu drawer trigger, emerald GraduationCap badge, "শিক্ষক পোর্টাল",
 * Notification Bell, Refresh, and circular Emerald-700 user avatar.
 */
@Composable
fun TeacherPortalHeader(
    teacherName: String,
    onRefresh: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Menu Button + Emerald GraduationCap Icon + "শিক্ষক পোর্টাল"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { menuExpanded = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "মেনু খুলুন",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("হাজিরা গ্রহণ") },
                            leadingIcon = { Icon(Icons.Default.FactCheck, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.ATTENDANCE) }
                        )
                        DropdownMenuItem(
                            text = { Text("সবক ও কিতাব") },
                            leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.HIFZ_KITAB) }
                        )
                        DropdownMenuItem(
                            text = { Text("পরীক্ষা ও নম্বর এন্ট্রি") },
                            leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.EXAMS) }
                        )
                        DropdownMenuItem(
                            text = { Text("নোটিশ বোর্ড") },
                            leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.NOTIFICATIONS) }
                        )
                        DropdownMenuItem(
                            text = { Text("সেটিংস") },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            onClick = { menuExpanded = false; onNavigate(Routes.SETTINGS) }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF059669)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "শিক্ষক পোর্টাল",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right: Refresh + Notification Bell + Circular Emerald-700 Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "রিফ্রেশ",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigate(Routes.NOTIFICATIONS) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "নোটিফিকেশন",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(19.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 5.dp, end = 5.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF047857))
                        .clickable { onNavigate(Routes.SETTINGS) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = teacherName.trim().firstOrNull()?.toString() ?: "উ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
        HorizontalDivider(color = Color(0xFF1E293B))
    }
}
