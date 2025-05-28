package com.tavla.tavlapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

class PlayerVsPlayerActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        // Intent'ten oyuncu ID'lerini al (varsa)
        val player1Id = intent.getLongExtra("player1_id", -1)
        val player2Id = intent.getLongExtra("player2_id", -1)

        setContent {
            TavlaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerVsPlayerScreen(
                        player1Id = player1Id,
                        player2Id = player2Id,
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
fun PlayerVsPlayerScreen(
    player1Id: Long = -1,
    player2Id: Long = -1,
    dbHelper: DatabaseHelper,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Oyuncu listesini al
    val playersList = remember { dbHelper.getAllPlayers() }

    // Seçili oyuncular için durum değişkenleri
    var selectedPlayer1 by remember { mutableStateOf<Player?>(null) }
    var selectedPlayer2 by remember { mutableStateOf<Player?>(null) }

    // Intent'ten gelen ID'ler varsa oyuncuları seç
    LaunchedEffect(player1Id, player2Id, playersList) {
        if (player1Id != -1L) {
            selectedPlayer1 = playersList.find { it.id == player1Id }
        }
        if (player2Id != -1L) {
            selectedPlayer2 = playersList.find { it.id == player2Id }
        }
    }

    // Dropdown menüleri için durum değişkenleri
    var showPlayer1Menu by remember { mutableStateOf(false) }
    var showPlayer2Menu by remember { mutableStateOf(false) }

    // İki oyuncu arasındaki istatistikler ve maçlar
    val stats = if (selectedPlayer1 != null && selectedPlayer2 != null) {
        dbHelper.getDetailedPlayerVsPlayerStats(selectedPlayer1!!.id, selectedPlayer2!!.id)
    } else null

    val matches = if (selectedPlayer1 != null && selectedPlayer2 != null) {
        dbHelper.getMatchesBetweenPlayers(selectedPlayer1!!.id, selectedPlayer2!!.id)
    } else emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("İkili Karşılaşmalar") },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Oyuncu 1 seçimi
                Column(modifier = Modifier.weight(1f)) {
                    Text("1. Oyuncu:")
                    Box {
                        OutlinedTextField(
                            value = selectedPlayer1?.name ?: "",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showPlayer1Menu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown"
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = showPlayer1Menu,
                            onDismissRequest = { showPlayer1Menu = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            playersList.forEach { player ->
                                DropdownMenuItem(
                                    text = { Text(player.name) },
                                    onClick = {
                                        selectedPlayer1 = player
                                        showPlayer1Menu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Oyuncu 2 seçimi
                Column(modifier = Modifier.weight(1f)) {
                    Text("2. Oyuncu:")
                    Box {
                        OutlinedTextField(
                            value = selectedPlayer2?.name ?: "",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showPlayer2Menu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown"
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = showPlayer2Menu,
                            onDismissRequest = { showPlayer2Menu = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            playersList.forEach { player ->
                                DropdownMenuItem(
                                    text = { Text(player.name) },
                                    onClick = {
                                        selectedPlayer2 = player
                                        showPlayer2Menu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // İstatistikleri sorgula butonu
            Button(
                onClick = {
                    if (selectedPlayer1 == null || selectedPlayer2 == null) {
                        Toast.makeText(context, "Lütfen iki oyuncu seçin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (selectedPlayer1!!.id == selectedPlayer2!!.id) {
                        Toast.makeText(context, "Lütfen farklı oyuncular seçin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("İstatistikleri Göster")
            }

            // İstatistikler
            if (stats != null) {
                PlayerVsPlayerStatsCard(stats, selectedPlayer1!!.name, selectedPlayer2!!.name)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Maç listesi
            if (matches.isNotEmpty()) {
                Text(
                    text = "Karşılaşmalar",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn {
                    items(matches) { match ->
                        UIComponents.MatchListItem(match, playersList.associateBy { it.id }) { matchId ->
                            // Maç detayına git
                            val intent = Intent(context, MatchDetailActivity::class.java)
                            intent.putExtra("match_id", matchId)
                            context.startActivity(intent)
                        }
                    }
                }
            } else if (selectedPlayer1 != null && selectedPlayer2 != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bu oyuncular arasında henüz bir maç oynanmamış.",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerVsPlayerStatsCard(stats: DetailedPlayerVsPlayerStats, player1Name: String, player2Name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "İkili Karşılaşma İstatistikleri",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Genel istatistikler
            Text("Toplam Oynanan Maç: ${stats.totalMatches}")

            // Kazanan dağılımı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$player1Name: ${stats.player1Wins} maç")
                Text("$player2Name: ${stats.player2Wins} maç")
            }

            // Detaylı istatistikler
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Dinamik olarak tüm kazanım tiplerini ve puanlarını hesapla
            val player1WinTypes = mutableMapOf<String, Int>()
            val player1WinScores = mutableMapOf<String, Int>()
            val player2WinTypes = mutableMapOf<String, Int>()
            val player2WinScores = mutableMapOf<String, Int>()

            // İki oyuncu arasındaki tüm maçları al ve round'ları analiz et
            val context = LocalContext.current
            val dbHelper = DatabaseHelper(context)
            val matches = dbHelper.getMatchesBetweenPlayers(stats.player1Id, stats.player2Id)

            // Her maçın round'larını al ve kazanım tiplerini + puanlarını hesapla
            matches.forEach { match ->
                val rounds = dbHelper.getMatchRounds(match.id)
                rounds.forEach { round ->
                    val winType = round.combinedWinType
                    when (round.winnerId) {
                        stats.player1Id -> {
                            player1WinTypes[winType] = (player1WinTypes[winType] ?: 0) + 1
                            player1WinScores[winType] = (player1WinScores[winType] ?: 0) + round.score
                        }
                        stats.player2Id -> {
                            player2WinTypes[winType] = (player2WinTypes[winType] ?: 0) + 1
                            player2WinScores[winType] = (player2WinScores[winType] ?: 0) + round.score
                        }
                    }
                }
            }

            // Tüm kazanım tiplerinin birleşik listesi
            val allWinTypes = WinTypeUtils.sortWinTypes((player1WinTypes.keys + player2WinTypes.keys).toSet())

            if (allWinTypes.isNotEmpty()) {
                // Scrollable tablo bölümü - maksimum yükseklik ile
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp) // Maksimum yükseklik
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
                            modifier = Modifier.width(60.dp)
                        )
                        Text(
                            text = player1Name,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = player2Name,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Kazanım satırları
                    allWinTypes.forEach { winType ->
                        val player1Count = player1WinTypes[winType] ?: 0
                        val player1Score = player1WinScores[winType] ?: 0
                        val player2Count = player2WinTypes[winType] ?: 0
                        val player2Score = player2WinScores[winType] ?: 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = winType,
                                modifier = Modifier.width(60.dp),
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (player1Count > 0) "$player1Count ($player1Score p)" else "0",
                                modifier = Modifier.weight(1f),
                                fontWeight = if (player1Count > 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (player2Count > 0) "$player2Count ($player2Score p)" else "0",
                                modifier = Modifier.weight(1f),
                                fontWeight = if (player2Count > 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Her satır arası ince çizgi
                        Divider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
                    }

                    // Her oyuncunun özet bilgileri
                    val player1TotalWins = player1WinTypes.values.sum()
                    val player1TotalScore = player1WinScores.values.sum()
                    val player2TotalWins = player2WinTypes.values.sum()
                    val player2TotalScore = player2WinScores.values.sum()

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "",
                            modifier = Modifier.width(60.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$player1TotalWins el",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "$player1TotalScore puan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$player2TotalWins el",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "$player2TotalScore puan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Tek satır toplam özeti
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Toplam",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(60.dp),
                            fontSize = 15.sp
                        )
                        Text(
                            text = "$player1TotalWins ($player1TotalScore p)",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$player2TotalWins ($player2TotalScore p)",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dinamik açıklama satırları - ayrı scrollable bölüm
                Text(
                    text = "Tip Açıklamaları:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp) // Açıklamalar için maksimum yükseklik
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    // Açıklamaları dinamik olarak oluştur
                    val explanations = allWinTypes.map { type ->
                        "$type: ${WinTypeUtils.typeToDisplayText(type)}"
                    }

                    // Açıklamaları tek sütunda göster
                    explanations.forEach { explanation ->
                        Text(
                            text = explanation,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            } else {
                // Eğer hiç kazanım yoksa
                Text(
                    text = "Bu oyuncular arasında henüz oynanmış bir el bulunmamaktadır.",
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