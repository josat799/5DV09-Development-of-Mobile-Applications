package se.atoui.thirty20.Models

// Copies values from Dices object to a new instance of Dices
fun copy(dices: MutableList<Dice>) = Dices(dices)

/*
* Creates a collection of dices that can be rolled.
* After a roll the score can be computed.
*/
class Dices(_dices: List<Dice>) {
    var dices: MutableList<Dice> = mutableListOf()
    val rollableDices: List<Dice>
        get() = dices.filter { dice -> dice.canBeRolled }

    fun rollDices() {
        for (dice in rollableDices) {
            dice.rollDice()
        }
    }

    fun getScore(): Int {
        return dices.sumOf { dice: Dice -> dice.currentSide!! }
    }

    init {
        dices = _dices as MutableList<Dice>
    }
}
/*
* Dice object with n sides. The dice can be rolled creating a random number between 1..n
*/
class Dice(var id: Int, private var sides: Int) {

    var canBeRolled = true
    var currentSide: Int? = null

    fun rollDice() {
        currentSide = (1..sides).random()
    }

}