package com.tavla.tavlapp

data class Round(
    val id: Long,
    val matchId: Long,
    val roundNumber: Int,
    val winnerId: Long,
    val winType: String,  // "SINGLE", "MARS", "BACKGAMMON"
    val isDouble: Boolean,
    val doubleValue: Int,  // 2, 4, 8, vb. küp değeri
    val score: Int,
    val date: String

){
    // Hesaplanan özellik
    val combinedWinType: String
        get() = if (isDouble && doubleValue > 1) {
            "$doubleValue${winType.first()}"
        } else {
            "${winType.first()}"
        }
}