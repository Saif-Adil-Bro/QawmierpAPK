package com.qawmierp.apk.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Adaptive responsive grid matching QawmiERP-V2.0 Tailwind breakpoints:
 * - < 340.dp: 1 column (very narrow phones / large font scaling)
 * - 340.dp .. 599.dp: 2 columns (standard phones)
 * - >= 600.dp: 4 columns (tablets / landscape)
 */
@Composable
fun <T> ResponsiveGrid(
    items: List<T>,
    spacing: Dp = 10.dp,
    maxColumnsTablet: Int = 4,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T, Modifier) -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = when {
            maxWidth < 340.dp -> 1
            maxWidth < 600.dp -> 2
            else -> maxColumnsTablet
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            val rows = items.chunked(columns)
            for (rowItems in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    for (item in rowItems) {
                        itemContent(item, Modifier.weight(1f))
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
