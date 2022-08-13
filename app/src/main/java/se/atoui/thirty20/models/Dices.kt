package se.atoui.thirty20.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

// Copies values from Dices object to a new instance of Dices
fun copy(dices: MutableList<Dice>) = Dices(dices)

/*
* Creates a collection of dices that can be rolled.
* After a roll the score can be computed.
*/
@Parcelize
class Dices(val dices: MutableList<Dice>) : Parcelable {
    val rollableDices: List<Dice>
        get() = dices.filter { dice -> dice.canBeRolled }

    fun rollDices() {
        for (dice in rollableDices) {
            dice.rollDice()
        }
    }

    fun getScore(): Int {
        return dices.sumOf { dice: Dice -> dice.currentSide }
    }

    override fun toString(): String {
        var result = "${dices.count()} \n";
        dices.forEach { dice ->
            result += "$dice \n"
        }
        return result
    }
}

/*
* Dice object with n sides. The dice can be rolled creating a random number between 1..n
*/
@Parcelize
class Dice(
    var id: Int,
    private var sides: Int,
    var canBeRolled: Boolean,
    var currentSide: Int
) : Parcelable {

    fun rollDice() {
        currentSide = (1..sides).random()
    }

    override fun toString(): String {
        return "$id, $currentSide"
    }
}