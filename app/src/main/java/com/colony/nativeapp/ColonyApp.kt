package com.colony.nativeapp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class ColonyDestination(
    val label: String,
    val description: String
)

private val colonyDestinations = listOf(
    ColonyDestination("Home", "Your Colony dashboard"),
    ColonyDestination("Live", "Colony Live broadcasting"),
    ColonyDestination("Connections", "Your Colony connections"),
    ColonyDestination("Sites", "Published websites and directory"),
    ColonyDestination("Edit HTML", "HTML editor workspace"),
    ColonyDestination("Gallery", "Your media gallery"),
    ColonyDestination("Journal", "Your personal journal"),
    ColonyDestination("Portal", "Colony portal"),
    ColonyDestination("Profile", "Your personal profile"),
    ColonyDestination("Requests", "Connection requests"),
    ColonyDestination("Rewards", "ANAY rewards dashboard"),
    ColonyDestination("Settings", "Account and privacy settings"),
    ColonyDestination("Public Wall", "Community public wall")
)

@Composable
fun ColonyApp() {
    var selectedDestination by remember { mutableStateOf("Home") }

    ColonyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ColonyColors.Background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Colony header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COLONY",
                        color = ColonyColors.Accent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Native",
                        color = ColonyColors.Secondary,
                        fontSize = 11.sp
                    )
                }

                HorizontalDivider(color = ColonyColors.Border)

                // Original 13-section navigation structure
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    colonyDestinations.forEach { destination ->
                        val selected =
                            selectedDestination == destination.label

                        Button(
                            onClick = {
                                selectedDestination = destination.label
                            },
                            contentPadding = PaddingValues(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) {
                                    ColonyColors.Primary
                                } else {
                                    ColonyColors.Panel
                                },
                                contentColor = if (selected) {
                                    Color.White
                                } else {
                                    ColonyColors.Secondary
                                }
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = destination.label,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                HorizontalDivider(color = ColonyColors.Border)

                // Screen content area
                val destination = colonyDestinations.first {
                    it.label == selectedDestination
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (selectedDestination) {
                        "Home" -> ColonyHomeScreen()
                        "Rewards" -> ColonyRewardsScreen()
                        else -> ColonySectionScreen(destination)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColonyHomeScreen() {
    ColonyCard {
        Text(
            text = "Welcome to Colony",
            color = ColonyColors.Text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your decentralized social environment.",
            color = ColonyColors.Secondary,
            fontSize = 13.sp
        )
    }

    ColonyCard {
        Text(
            text = "Active Mesh Node Operator",
            color = ColonyColors.Accent,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Colony network status will appear here.",
            color = ColonyColors.Text,
            fontSize = 13.sp
        )
    }

    ColonyCard {
        Text(
            text = "Quick Mesh Summary",
            color = ColonyColors.Text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        SummaryRow("Connections", "—")
        SummaryRow("Requests", "—")
        SummaryRow("Published Sites", "—")
        SummaryRow("Incognito Mode", "—")
    }
}

@Composable
private fun ColonyRewardsScreen() {
    ColonyCard {
        Text(
            text = "ANAY REWARDS",
            color = ColonyColors.Accent,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your rewards dashboard",
            color = ColonyColors.Secondary,
            fontSize = 13.sp
        )
    }

    ColonyCard {
        Text(
            text = "Earnings Summary",
            color = ColonyColors.Text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        SummaryRow("Total Earned", "—")
        SummaryRow("Pending", "—")
        SummaryRow("Reserved", "—")
        SummaryRow("Available to Withdraw", "—")
        SummaryRow("Paid Rewards", "—")
        SummaryRow("Withdrawn", "—")
    }

    ColonyCard {
        Text(
            text = "Reward Records",
            color = ColonyColors.Text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Text(
            text = "Your reward history will appear here.",
            color = ColonyColors.Secondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ColonySectionScreen(destination: ColonyDestination) {
    ColonyCard {
        Text(
            text = destination.label,
            color = ColonyColors.Accent,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = destination.description,
            color = ColonyColors.Secondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ColonyCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = ColonyColors.Panel
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            ColonyColors.Border
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = ColonyColors.Secondary,
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = ColonyColors.Text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
