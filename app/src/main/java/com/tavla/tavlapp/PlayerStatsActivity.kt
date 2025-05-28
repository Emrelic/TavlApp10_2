package com.tavla.tavlapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

class PlayerStatsActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        // Intent'ten oyuncu ID'sini al (varsa)
        val playerId = intent.getLongExtra("player_id", -1)

        setContent {
            TavlaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerStatsScreen(
                        playerId = playerId,
                        dbHelper = dbHelper,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatsScreen(
    playerId: Long = -1,
    dbHelper: DatabaseHelper,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Oyuncu listesini al
    val playersList = remember { dbHelper.getAllPlayers() }

    // Seçili oyuncu için durum değişkeni
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }

    // Intent'ten gelen ID varsa oyuncuyu seç
    LaunchedEffect(playerId, playersList) {
        if (playerId != -1L) {
            selectedPlayer = playersList.find { it.id == playerId }
        }
    }

    // Dropdown menüsü için durum değişkeni
    var showPlayerMenu by remember { mutableStateOf(false) }

    // Oyuncunun istatistikleri ve maçları
    val playerStats = if (selectedPlayer != null) {
        dbHelper.getPlayerStats(selectedPlayer!!.id)
    } else null

    val playerMatches = if (selectedPlayer != null) {
        dbHelper.getPlayerMatches(selectedPlayer!!.id)
    } else emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oyuncu İstatistikleri") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Oyuncu seçimi
            Column {
                Text("Oyuncu:")
                Box {
                    OutlinedTextField(
                        value = selectedPlayer?.name ?: "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showPlayerMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = showPlayerMenu,
                        onDismissRequest = { showPlayerMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        playersList.forEach { player ->
                            DropdownMenuItem(
                                text = { Text(player.name) },
                                onClick = {
                                    selectedPlayer = player
                                    showPlayerMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // İstatistikler
            if (playerStats != null) {
                DetailedPlayerStatsCard(playerStats, selectedPlayer!!.name)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Maç listesi
            if (playerMatches.isNotEmpty()) {
                Text(
                    text = "Oynadığı Maçlar",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn {
                    items(playerMatches) { match ->
                        UIComponents.MatchListItem(match, playersList.associateBy { it.id }) { matchId ->
                            // Maç detayına git
                            val intent = Intent(context, MatchDetailActivity::class.java)
                            intent.putExtra("match_id", matchId)
                            context.startActivity(intent)
                        }
                    }
                }
            } else if (selectedPlayer != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bu oyuncu henüz bir maç oynamamış.",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedPlayerStatsCard(stats: PlayerStats, playerName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$playerName İstatistikleri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Genel istatistikler
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Toplam Maç: ${stats.totalMatches}")
                    Text("Kazanılan Maç: ${stats.matchesWon}")
                    Text("Kazanma Oranı: ${
                        if (stats.totalMatches > 0) {
                            String.format("%.1f%%", stats.matchesWon.toFloat() / stats.totalMatches * 100)
                        } else "0.0%"
                    }")
                }

                Column {
                    Text("Toplam El: ${stats.totalRounds}")
                    Text("Kazanılan El: ${stats.roundsWon}")
                    Text("Kazanma Oranı: ${
                        if (stats.totalRounds > 0) {
                            String.format("%.1f%%", stats.roundsWon.toFloat() / stats.totalRounds * 100)
                        } else "0.0%"
                    }")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Dinamik olarak tüm kazanım tiplerini ve puanlarını hesapla
            val context = LocalContext.current
            val dbHelper = DatabaseHelper(context)
            val playerMatches = dbHelper.getPlayerMatches(stats.playerId)

            // Bu oyuncunun tüm kazanım tiplerini ve puanlarını hesapla
            val winTypeMap = mutableMapOf<String, Int>()
            val winScoreMap = mutableMapOf<String, Int>()

            playerMatches.forEach { match ->
                val rounds = dbHelper.getMatchRounds(match.id)
                val playerRounds = rounds.filter { it.winnerId == stats.playerId }

                playerRounds.forEach { round ->
                    val winType = round.combinedWinType
                    winTypeMap[winType] = (winTypeMap[winType] ?: 0) + 1
                    winScoreMap[winType] = (winScoreMap[winType] ?: 0) + round.score
                }
            }

            // Kazanım tipi sıralaması için
            val sortedWinTypes = WinTypeUtils.sortWinTypes(winTypeMap.keys)

            if (sortedWinTypes.isNotEmpty()) {
                Text(
                    text = "Kazanılan El Türleri",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Scrollable tablo bölümü - maksimum yükseklik ile
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp) // Maksimum yükseklik
                        .verticalScroll(rememberScrollState()) // Dikey scroll
                ) {
                    // Tablo başlığı
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Tip",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(70.dp)
                        )
                        Text(
                            text = "Sayı (Puan)",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(110.dp)
                        )
                        Text(
                            text = "Açıklama",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Kazanım tipleri listesi
                    sortedWinTypes.forEach { type ->
                        val count = winTypeMap[type] ?: 0
                        val score = winScoreMap[type] ?: 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = type,
                                modifier = Modifier.width(70.dp),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$count ($score p)",
                                modifier = Modifier.width(110.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = WinTypeUtils.typeToDisplayText(type),
                                modifier = Modifier.weight(1f),
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Her satır arası ince çizgi
                        Divider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Toplam satırı
                    val totalWins = winTypeMap.values.sum()
                    val totalScore = winScoreMap.values.sum()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Toplam",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(70.dp),
                            fontSize = 15.sp
                        )
                        Text(
                            text = "$totalWins ($totalScore p)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                // Eğer hiç kazanım yoksa
                Text(
                    text = "Henüz kazanılan el bulunmamaktadır.",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
/*
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

 */