package com.phone.pocket.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.phone.pocket.data.CategorySummary

@Composable
fun SpendChart(
    categorySummaries: List<CategorySummary>,
    modifier: Modifier = Modifier
) {
    if (categorySummaries.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No data to display",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Chart Title
        Text(
            text = "Spend by Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Simple Bar Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                val maxAmount = categorySummaries.maxOfOrNull { it.total } ?: 0.0
                val colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.error,
                    MaterialTheme.colorScheme.outline
                )
                
                // Chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val barWidth = size.width / categorySummaries.size
                    val maxHeight = size.height
                    
                    categorySummaries.forEachIndexed { index, summary ->
                        val barHeight = if (maxAmount > 0) {
                            (summary.total / maxAmount * maxHeight).toFloat()
                        } else 0f
                        
                        val color = colors[index % colors.size]
                        val x = index * barWidth
                        val y = maxHeight - barHeight
                        
                        drawRect(
                            color = color,
                            topLeft = Offset(x + 4, y),
                            size = Size(barWidth - 8, barHeight)
                        )
                    }
                }
                
                // Legend
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    categorySummaries.forEachIndexed { index, summary ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = colors[index % colors.size],
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = summary.category,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = "₹${summary.total}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (index < categorySummaries.size - 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
} 