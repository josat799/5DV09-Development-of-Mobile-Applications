package se.atoui.thirty20.Models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameModel : ViewModel() {
    private val _dices = MutableLiveData(Dices(MutableList(6) {
        Dice(it, 6)
    }))
    val dices: LiveData<Dices>
        get() = _dices

    private val _rollsLeft = MutableLiveData<Int>(3)
    val rollsLeft: LiveData<Int>
        get() = _rollsLeft

    private val _canRoll = MediatorLiveData<Boolean>().apply {
        addSource(rollsLeft) {
            rollToastMessage = "No More Rolls Left!"
            this.postValue(rollsLeft.value!! > 0)
        }
        addSource(dices) {
            rollToastMessage = "Every Dice is Saved!"
            this.postValue(dices.value!!.rollableDices.isNotEmpty())
        }
    }
    val canRoll: LiveData<Boolean>
        get() = _canRoll

    var rollToastMessage: String = "No More Rolls Left"

    fun rollDices() {
        val tmpDices = _dices.value!!
        tmpDices.rollDices()
        _dices.postValue(tmpDices)
        _rollsLeft.postValue(_rollsLeft.value!!.minus(1))

        Log.d(
            "dices",
            dices.value!!.dices.map { dice -> dice.currentSide.toString() }.joinToString { s -> s })
    }

    fun toggleRollable(diceId: Int) {
        val tmpDices = dices.value!!
        val dice = tmpDices.dices[diceId]
        dice.canBeRolled = !dice.canBeRolled
        _dices.postValue(tmpDices)
    }

    private val _currentRound = MutableLiveData<Int>(0)
    val currentRound: LiveData<Int>
        get() = _currentRound

    private val _scoreBoard = MutableLiveData<ScoreBoard>(ScoreBoard())
    val scoreBoard: LiveData<ScoreBoard>
        get() = _scoreBoard


}