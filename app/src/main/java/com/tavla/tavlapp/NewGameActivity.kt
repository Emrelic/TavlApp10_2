package com.tavla.tavlapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

// Yeni oyun ayarları ekranı aktivitesi
class NewGameActivity : ComponentActivity() {
    // Veritabanı yardımcısını tanımla
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Veritabanı yardımcısını başlat
        dbHelper = DatabaseHelper(this)

        setContent {
            TavlaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewGameScreen(dbHelper)
                }
            }
        }
    }
}

// Yeni Oyun ekranının UI tasarımı
@OptIn(ExperimentalMaterial3Api::class) // Henüz deneysel olan Material3 API'larını kullanıyoruz

@Composable
fun NewGameScreen(dbHelper: DatabaseHelper) {
    val context = LocalContext.current

    // Oyuncu listesini veritabanından yükle
    val playersList = remember {
        mutableStateOf(dbHelper.getAllPlayers())
    }

    // Yeni oyuncu adı için durum
    var newPlayerName by remember { mutableStateOf("") }

    // Seçilen oyuncular için durum - başlangıçta null
    var selectedPlayer1 by remember { mutableStateOf<Player?>(null) }
    var selectedPlayer2 by remember { mutableStateOf<Player?>(null) }

    // Son maçtaki oyuncuları varsayılan olarak seç
    LaunchedEffect(playersList.value) {
        if (playersList.value.isNotEmpty() && selectedPlayer1 == null && selectedPlayer2 == null) {
            // En son oynanan maçı al
            val lastMatches = dbHelper.getAllMatches()
            if (lastMatches.isNotEmpty()) {
                val lastMatch = lastMatches.first() // İlk eleman en son maç (DESC sıralı)

                // Son maçtaki oyuncuları bul
                val lastPlayer1 = playersList.value.find { it.id == lastMatch.player1Id }
                val lastPlayer2 = playersList.value.find { it.id == lastMatch.player2Id }

                // Oyuncular bulunduysa seç
                if (lastPlayer1 != null && lastPlayer2 != null) {
                    selectedPlayer1 = lastPlayer1
                    selectedPlayer2 = lastPlayer2
                }
            } else {
                // Eğer hiç maç yoksa, ilk iki oyuncuyu seç (mevcut davranış)
                if (playersList.value.isNotEmpty()) {
                    selectedPlayer1 = playersList.value[0]
                }
                if (playersList.value.size > 1) {
                    selectedPlayer2 = playersList.value[1]
                }
            }
        }
    }

    // Oyun türü ve el sayısı için durum
    var selectedGameType by remember { mutableStateOf("Modern") }

    var selectedRounds by remember { mutableStateOf("11") }
    var showRoundsMenu by remember { mutableStateOf(false) }

    // Skor giriş modu için durum (Otomatik=true, Manuel=false)
    var isScoreAutomatic by remember { mutableStateOf(true) }

    // El sayısı seçenekleri
    val roundsOptions = listOf("3", "5", "7", "9","11", "15", "17", "21")

    // Oyuncu 1 ve 2 dropdown için durum
    var showPlayer1Menu by remember { mutableStateOf(false) }
    var showPlayer2Menu by remember { mutableStateOf(false) }

    // Tavla türü değiştiğinde el sayısını güncelle
    LaunchedEffect(selectedGameType) {
        if (selectedGameType == "Geleneksel") {
            selectedRounds = "5"
        }
    }

    // Ana düzen - dikey bir sütun oluşturuyoruz
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Kaydırılabilir ekran
        verticalArrangement = Arrangement.spacedBy(16.dp) // Öğeler arasında 16dp boşluk
    ) {
        // Başlık
        Text(
            text = "Yeni Oyun Ayarları",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Oyuncu 1 Seçimi
        Text(text = "Oyuncu 1:")
        Box {
            // Oyuncu seçimi için özel bir TextField
            OutlinedTextField(
                value = selectedPlayer1?.name ?: "",
                onValueChange = { }, // Değişikliği doğrudan önlemek için boş bırakıyoruz
                modifier = Modifier.fillMaxWidth(),
                readOnly = true, // Salt okunur, kullanıcı yazamaz
                trailingIcon = {
                    IconButton(onClick = { showPlayer1Menu = true }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    }
                }
            )

            // Dropdown menü
            DropdownMenu(
                expanded = showPlayer1Menu,
                onDismissRequest = { showPlayer1Menu = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                playersList.value.forEach { player ->
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

        // Oyuncu 2 Seçimi (Oyuncu 1 ile aynı mantık)
        Text(text = "Oyuncu 2:")
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
                playersList.value.forEach { player ->
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

        // Yeni Oyuncu Ekleme
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Yeni Oyuncu:", modifier = Modifier.padding(end = 8.dp))
            TextField(
                value = newPlayerName,
                onValueChange = { newPlayerName = it }, // Metin değiştiğinde değeri güncelle
                modifier = Modifier.weight(1f), // Boş alanı doldurma oranı
                placeholder = { Text("Oyuncu adı girin") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val name = newPlayerName.trim()
                    if (name.isNotEmpty()) {
                        // Veritabanına ekle
                        val id = dbHelper.addPlayer(name)

                        if (id != -1L) {
                            // Oyuncu başarıyla eklendi
                            val newPlayer = Player(id, name)

                            // Oyuncu listesini güncelle
                            val updatedList = playersList.value.toMutableList()
                            updatedList.add(newPlayer)
                            playersList.value = updatedList

                            // Eğer henüz bir oyuncu seçilmemişse, yeni oyuncuyu seç
                            if (selectedPlayer1 == null) {
                                selectedPlayer1 = newPlayer
                            } else if (selectedPlayer2 == null) {
                                selectedPlayer2 = newPlayer
                            }

                            // Metin kutusunu temizle
                            newPlayerName = ""

                            Toast.makeText(context, "Oyuncu eklendi", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Bu isimde bir oyuncu zaten var", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Lütfen bir oyuncu adı girin", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Ekle")
            }
        }

        // Tavla Türü Seçimi
        Text(text = "Tavla Türü:")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedGameType == "Geleneksel",
                onClick = {
                    selectedGameType = "Geleneksel"
                    // Geleneksel seçildiğinde el sayısını otomatik olarak 5'e güncelle
                    selectedRounds = "5"
                }
            )
            Text(
                text = "Geleneksel",
                modifier = Modifier
                    .clickable {
                        selectedGameType = "Geleneksel"
                        // Geleneksel seçildiğinde el sayısını otomatik olarak 5'e güncelle
                        selectedRounds = "5"
                    }
                    .padding(start = 4.dp, end = 16.dp)
            )

            RadioButton(
                selected = selectedGameType == "Modern",
                onClick = { selectedGameType = "Modern" }
            )
            Text(
                text = "Modern",
                modifier = Modifier
                    .clickable { selectedGameType = "Modern" }
                    .padding(start = 4.dp)
            )
        }

        // El Sayısı Seçimi
        Text(text = "El Sayısı:")
        Box {
            OutlinedTextField(
                value = selectedRounds,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showRoundsMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    }
                }
            )

            DropdownMenu(
                expanded = showRoundsMenu,
                onDismissRequest = { showRoundsMenu = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                roundsOptions.forEach { rounds ->
                    DropdownMenuItem(
                        text = { Text(rounds) },
                        onClick = {
                            selectedRounds = rounds
                            showRoundsMenu = false
                        }
                    )
                }
            }
        }

        // Skor Giriş Modu Seçimi
        Text(text = "Skor Giriş Modu:")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Manuel",
                modifier = Modifier.padding(end = 8.dp)
            )

            Switch(
                checked = isScoreAutomatic,
                onCheckedChange = { isScoreAutomatic = it }
            )

            Text(
                text = "Otomatik (T/M/B)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Boş alan ekleyerek butonları ekranın alt kısmına yakın konumlandırıyoruz
        Spacer(modifier = Modifier.weight(1f))

        // İşlem Butonları
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // Gerekli kontroller
                    if (selectedPlayer1 == null || selectedPlayer2 == null) {
                        Toast.makeText(context, "Lütfen iki oyuncu seçin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (selectedPlayer1?.id == selectedPlayer2?.id) {
                        Toast.makeText(context, "Lütfen iki farklı oyuncu seçin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Oyun bilgilerini al
                    val player1Id = selectedPlayer1!!.id
                    val player2Id = selectedPlayer2!!.id
                    val player1Name = selectedPlayer1!!.name
                    val player2Name = selectedPlayer2!!.name
                    val rounds = selectedRounds.toInt()

                    // Skor ekranını başlat
                    val intent = Intent(context, GameScoreActivity::class.java).apply {
                        putExtra("player1_name", player1Name)
                        putExtra("player2_name", player2Name)
                        putExtra("game_type", selectedGameType)
                        putExtra("rounds", rounds)
                        putExtra("player1_id", player1Id)
                        putExtra("player2_id", player2Id)
                        putExtra("is_score_automatic", isScoreAutomatic)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Oyunu Başlat")
            }

            Button(
                onClick = {
                    // İptal et
                    (context as ComponentActivity).finish()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("İptal")
            }
        }
    }
}