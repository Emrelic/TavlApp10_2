package com.tavla.tavlapp

// Kazanım tipleri için yardımcı fonksiyonlar
object WinTypeUtils {
    // Round'dan display tipini al
    fun getDisplayType(round: Round): String {
        val prefix = if (round.isDouble) "${round.doubleValue}" else ""
        val suffix = when (round.winType) {
            "SINGLE" -> "T"
            "MARS" -> "M"
            "BACKGAMMON" -> "B"
            else -> "?"
        }
        return "$prefix$suffix"
    }

    // Tip metnini daha okunabilir hale getir
    fun typeToDisplayText(type: String): String {
        val number = type.filter { it.isDigit() }
        val letter = type.filter { !it.isDigit() }

        val typeText = when (letter) {
            "T" -> "Tekli"
            "M" -> "Mars"
            "B" -> "Backgammon"
            else -> letter
        }

        return if (number.isNotEmpty()) {
            "$number× $typeText"
        } else {
            typeText
        }
    }

    // Kazanım tiplerini doğru sıralamak için
    fun sortWinTypes(types: Collection<String>): List<String> {
        return types.sortedWith(
            compareBy(
            { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 1 },
            { it.filter { c -> !c.isDigit() } }
        ))
    }

    // PlayerStats'ı winTypeMap'e dönüştür
    fun statsToWinTypeMap(stats: PlayerStats): Map<String, Int> {
        val result = mutableMapOf<String, Int>()

        // Normal kazanımlar
        if (stats.singleWins > 0) result["T"] = stats.singleWins
        if (stats.marsWins > 0) result["M"] = stats.marsWins
        if (stats.backgammonWins > 0) result["B"] = stats.backgammonWins

        // 2x küplü kazanımlar
        if (stats.doubleSingleWins > 0) result["2T"] = stats.doubleSingleWins
        if (stats.doubleMarsWins > 0) result["2M"] = stats.doubleMarsWins
        if (stats.doubleBackgammonWins > 0) result["2B"] = stats.doubleBackgammonWins

        return result
    }

    // PlayerRoundStats'ı winTypeMap'e dönüştür - Düzeltilmiş versiyon
    fun roundStatsToWinTypeMap(stats: PlayerRoundStats): Map<String, Int> {
        // Önce winTypeMap'den al (64T, 32M gibi yüksek değerler burada)
        val result = stats.winTypeMap.toMutableMap()

        // Eğer winTypeMap boşsa veya eksik değerler varsa, ayrı alanlardan topla
        if (result.isEmpty()) {
            // Normal kazanımlar
            if (stats.singleWins > 0) result["T"] = stats.singleWins
            if (stats.marsWins > 0) result["M"] = stats.marsWins
            if (stats.backgammonWins > 0) result["B"] = stats.backgammonWins

            // 2x küplü kazanımlar
            if (stats.doubleSingleWins > 0) result["2T"] = stats.doubleSingleWins
            if (stats.doubleMarsWins > 0) result["2M"] = stats.doubleMarsWins
            if (stats.doubleBackgammonWins > 0) result["2B"] = stats.doubleBackgammonWins

            // 4x+ küplü kazanımlar (bu aslında 4x, 8x, 16x, 32x, 64x toplamı)
            if (stats.quadSingleWins > 0) result["4+T"] = stats.quadSingleWins
            if (stats.quadMarsWins > 0) result["4+M"] = stats.quadMarsWins
            if (stats.quadBackgammonWins > 0) result["4+B"] = stats.quadBackgammonWins
        }

        return result
    }
}