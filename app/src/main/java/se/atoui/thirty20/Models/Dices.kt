package se.atoui.thirty20.Models

class Dices(_dices: List<Dice>) {

    var dices: MutableList<Dice> = mutableListOf()
    val rollableDices: List<Dice>
        get() = dices.filter { dice -> dice.canBeRolled }

    fun rollDices() {
        for (dice in rollableDices) {
            dice.rollDice()
        }
    }

    init {
        dices = _dices as MutableList<Dice>
    }
}

class Dice(var id: Int, private var sides: Int) {

    var canBeRolled = true
    var currentSide: Int? = null

    fun rollDice() {
        currentSide = (1..sides).random()
    }

}