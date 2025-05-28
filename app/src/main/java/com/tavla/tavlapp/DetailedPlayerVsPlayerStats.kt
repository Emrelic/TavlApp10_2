package com.tavla.tavlapp

data class DetailedPlayerVsPlayerStats(
    val player1Id: Long,
    val player2Id: Long,
    val totalMatches: Int,
    val player1Wins: Int,
    val player2Wins: Int,
    val player1SingleWins: Int = 0,
    val player1MarsWins: Int = 0,
    val player1BackgammonWins: Int = 0,
    val player1DoubleSingleWins: Int = 0,
    val player1DoubleMarsWins: Int = 0,
    val player1DoubleBackgammonWins: Int = 0,
    val player1QuadSingleWins: Int = 0,
    val player1QuadMarsWins: Int = 0,
    val player1QuadBackgammonWins: Int = 0,
    val player2SingleWins: Int = 0,
    val player2MarsWins: Int = 0,
    val player2BackgammonWins: Int = 0,
    val player2DoubleSingleWins: Int = 0,
    val player2DoubleMarsWins: Int = 0,
    val player2DoubleBackgammonWins: Int = 0,
    val player2QuadSingleWins: Int = 0,
    val player2QuadMarsWins: Int = 0,
    val player2QuadBackgammonWins: Int = 0
)