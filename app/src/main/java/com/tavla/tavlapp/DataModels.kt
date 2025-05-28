package com.tavla.tavlapp

// Oyuncu verilerini tutacak data class
data class Player(val id: Long, val name: String)

// Maç detaylarını tutacak data class
data class Match(
    val id: Long,
    val player1Id: Long,
    val player2Id: Long,
    val player1Score: Int,
    val player2Score: Int,
    val gameType: String,
    val totalRounds: Int,
    val player1RoundsWon: Int,
    val player2RoundsWon: Int,
    val winnerId: Long,
    val date: String
)

// Oyuncu istatistiklerini tutacak data class
data class PlayerStats(
    val playerId: Long,
    val totalMatches: Int,
    val matchesWon: Int,
    val totalRounds: Int,
    val roundsWon: Int,
    val singleWins: Int,
    val marsWins: Int,
    val backgammonWins: Int,
    val doubleSingleWins: Int,
    val doubleMarsWins: Int,
    val doubleBackgammonWins: Int
)

// İki oyuncu arasındaki istatistikleri tutacak data class
data class PlayerVsPlayerStats(
    val player1Id: Long,
    val player2Id: Long,
    val totalMatches: Int,
    val player1Wins: Int,
    val player2Wins: Int
)