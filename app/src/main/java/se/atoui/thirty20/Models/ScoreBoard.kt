package se.atoui.thirty20.Models

class ScoreBoard {
    val scoreBoard: MutableMap<String, Int?> = mutableMapOf<String, Int?>().apply {
        this["LOW"] = null; this["4"] = null; this["5"] = null; this["6"] = null; this["7"] = null
        this["8"] = null; this["9"] = null; this["10"] = null; this["11"] = null; this["12"] = null
    }
    fun setValue(key: String, value: Int) = scoreBoard.set(key, value)
    val totalScore: Int
        get() = scoreBoard.values.filterNotNull().sum()
    val scoresLeft: Set<String>
        get() = scoreBoard.filter { entry -> entry.value != null }.keys



    fun size() = scoreBoard.size

}