package com.tavla.tavlapp

data class PlayerRoundStats(
    val totalWins: Int = 0,
    val winTypeMap: Map<String, Int> = mapOf(),
    val singleWins: Int = 0,
    val marsWins: Int = 0,
    val backgammonWins: Int = 0,
    val doubleSingleWins: Int = 0,
    val doubleMarsWins: Int = 0,
    val doubleBackgammonWins: Int = 0,
    val quadSingleWins: Int = 0,
    val quadMarsWins: Int = 0,
    val quadBackgammonWins: Int = 0

)