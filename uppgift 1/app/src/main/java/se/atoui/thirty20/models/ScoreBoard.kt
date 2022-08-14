package se.atoui.thirty20.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
* Creates a collection of score categories and score
* Initializes the score as null
*/
@Parcelize
class ScoreBoard(var scoreBoard : MutableMap<String, Int?>): Parcelable {
    fun setValue(key: String, value: Int) = scoreBoard.set(key, value)
    val totalScore: Int
        get() = scoreBoard.values.filterNotNull().sum()
    val scoresLeft: Set<String>
        get() = scoreBoard.filter { entry -> entry.value == null }.keys
    val scoresDone: List<Pair<String, Int>>
        get() = scoreBoard.map { entry ->
            Pair(
                entry.key,
                if (entry.value != null) entry.value!! else 0
            )
        }
}