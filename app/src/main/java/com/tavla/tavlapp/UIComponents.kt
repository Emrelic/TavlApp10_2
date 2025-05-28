package com.tavla.tavlapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Ortak UI bileşenleri için yardımcı sınıf
 */
object UIComponents {

    @Composable
    fun MatchListItem(match: Match, players: Map<Long, Player>, onItemClick: (Long) -> Unit) {
        val player1Name = players[match.player1Id]?.name ?: "Bilinmeyen"
        val player2Name = players[match.player2Id]?.name ?: "Bilinmeyen"
        val winnerName = players[match.winnerId]?.name ?: "Bilinmeyen"

        // Tarih formatını düzenle
        val date = try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val parsedDate = inputFormat.parse(match.date)
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            match.date
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onItemClick(match.id) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Maç başlığı ve tarih
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Maç #${match.id}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Oyuncu isimleri ve skorları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = player1Name,
                            fontWeight = if (match.winnerId == match.player1Id) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(text = "${match.player1Score} puan")
                        Text(text = "${match.player1RoundsWon} el")
                    }

                    Text(
                        text = "vs",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = player2Name,
                            fontWeight = if (match.winnerId == match.player2Id) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(text = "${match.player2Score} puan")
                        Text(text = "${match.player2RoundsWon} el")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Oyun tipi ve kazanan bilgisi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${match.gameType} Tavla")
                    Text(
                        text = "Kazanan: $winnerName",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    @Composable
    fun MatchListItemWithDelete(
        match: Match,
        players: Map<Long, Player>,
        isDeleteMode: Boolean,
        isSelected: Boolean,
        onItemClick: (Long) -> Unit,
        onDeleteClick: (Long) -> Unit
    ) {
        val player1Name = players[match.player1Id]?.name ?: "Bilinmeyen"
        val player2Name = players[match.player2Id]?.name ?: "Bilinmeyen"
        val winnerName = players[match.winnerId]?.name ?: "Bilinmeyen"

        // Tarih formatını düzenle
        val date = try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val parsedDate = inputFormat.parse(match.date)
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            match.date
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onItemClick(match.id) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(if (isSelected && isDeleteMode) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Silme modu aktifse checkbox göster
                if (isDeleteMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onItemClick(match.id) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                // Maç bilgileri
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    // Maç başlığı ve tarih
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Maç #${match.id}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Oyuncu isimleri ve skorları
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = player1Name,
                                fontWeight = if (match.winnerId == match.player1Id) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(text = "${match.player1Score} puan")
                            Text(text = "${match.player1RoundsWon} el")
                        }

                        Text(
                            text = "vs",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = player2Name,
                                fontWeight = if (match.winnerId == match.player2Id) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(text = "${match.player2Score} puan")
                            Text(text = "${match.player2RoundsWon} el")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Oyun tipi ve kazanan bilgisi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${match.gameType} Tavla")
                        Text(
                            text = "Kazanan: $winnerName",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Normal modda ise silme butonu göster
                if (!isDeleteMode) {
                    IconButton(
                        onClick = { onDeleteClick(match.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Sil",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PlayerListItem(player: Player, onItemClick: (Long) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onItemClick(player.id) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Görüntüle",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}