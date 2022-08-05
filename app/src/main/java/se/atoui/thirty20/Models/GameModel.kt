package se.atoui.thirty20.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// The states the game can be in
enum class States {
    Rolling,
    Scoring,
}

class GameModel : ViewModel() {
    // The current state of the game
    // Initializes as rolling
    private var _currentState = MutableLiveData(States.Rolling)
    val currentState: LiveData<States>
        get() = _currentState

    fun updateState(state: States) {
        _currentState.postValue(state)
    }

    // The current scoreboard of the game
    private val _scoreBoard = MutableLiveData(ScoreBoard())
    val scoreBoard: LiveData<ScoreBoard>
        get() = _scoreBoard

    // The current dices that are used for rolling
    private val _rollingDices = MutableLiveData(Dices(MutableList(6) {
        Dice(it, 6)
    }))
    val rollingDices: LiveData<Dices>
        get() = _rollingDices

    // Resets all rolling dices to be rollable
    private fun resetDices() {
        val dices = _rollingDices.value!!
        dices.dices.forEach { dice -> dice.canBeRolled = true }
        _rollingDices.postValue(dices)
    }

    // The current dices that are used for scoring
    // Copies the rolling dices fetched
    private val _scoringDices = MutableLiveData<Dices>().apply {
        this.postValue(copy(_rollingDices.value!!.dices));
    }
    val scoringDices: LiveData<Dices>
        get() = _scoringDices

    // The current dice combinations
    // The pair's first value indicates if the pair has been saved
    private val _pairs = MutableLiveData(mutableListOf<Pair<Boolean, Dices>>())
    val pairs: LiveData<MutableList<Pair<Boolean, Dices>>>
        get() = _pairs

    // Add the clicked dice to the last available score combination
    // If no combination exists then creates a new one.
    fun appendDiceToPair(diceId: Int) {
        val dice = _scoringDices.value!!.dices[diceId]
        val pairs = _pairs.value!!
        val filteredPairs = pairs.filter { pair -> !pair.first }
        var lastPair = filteredPairs.lastOrNull()
        pairs.remove(lastPair)

        if (lastPair == null) {
            lastPair = Pair(false, Dices(mutableListOf(dice)))
        } else {
            lastPair.second.dices.add(dice)
        }

        pairs.add(lastPair)
        _pairs.postValue(pairs)
        _scoringDices.postValue(_scoringDices.value!!)
    }

    // Verifies and saves the last dice combination
    fun addPair(): Boolean {
        var pair: Pair<Boolean, Dices> =
            _pairs.value!!.removeLastOrNull() ?: return false
        val satisfied: Boolean;
        // Converts the current score category to criteria and evaluates
        satisfied = when (_currentScoreCategory.value!!) {
            "LOW" -> verifySpecialPair(pair.second)
            "4" -> verifyPair(pair.second, 4)
            "5" -> verifyPair(pair.second, 5)
            "6" -> verifyPair(pair.second, 6)
            "7" -> verifyPair(pair.second, 7)
            "8" -> verifyPair(pair.second, 8)
            "9" -> verifyPair(pair.second, 9)
            "10" -> verifyPair(pair.second, 10)
            "11" -> verifyPair(pair.second, 11)
            "12" -> verifyPair(pair.second, 12)
            else -> false
        }
        // Adds the updated combination and triggers observers for pairs.
        val pairs = _pairs.value!!
        pair = pair.copy(first = satisfied)
        pairs.add(pair)

        _pairs.postValue(pairs)
        return satisfied
    }

    // Removes a dice combination
    fun removePair(index: Int) {
        val pairs = _pairs.value!!
        pairs.removeAt(index)
        _pairs.postValue(pairs)
        _scoringDices.postValue(_scoringDices.value!!)
    }

    // Validates that all combinations are valid.
    // If combinations are valid then save the total score of
    // each pair.
    fun saveScore(): Boolean {
        if (_pairs.value!!.any { pair -> !pair.first }) return false
        val scoreBoard = _scoreBoard.value!!
        scoreBoard.setValue(
            _currentScoreCategory.value!!,
            _pairs.value!!.sumOf { pair: Pair<Boolean, Dices> -> pair.second.getScore() })

        _pairs.postValue(mutableListOf())
        _scoreBoard.postValue(scoreBoard)
        _scoreIsSet.postValue(true)
        return true
    }

    // Verifies combination meets criteria
    // Works for each category except LOW
    private fun verifyPair(dices: Dices, criteria: Int): Boolean {
        return dices.getScore() == criteria
    }

    // Verifies combination meets the LOW criteria
    private fun verifySpecialPair(dices: Dices): Boolean {
        return !dices.dices.any { dice -> dice.currentSide!! > 3 }
    }

    // Validates if dice is in a pair
    fun diceInPair(diceId: Int): Boolean {
        return _pairs.value!!.any { pair -> pair.second.dices.any { dice -> dice.id == diceId } }
    }

    // The current score category
    private val _currentScoreCategory = MutableLiveData<String>()
    val currentScoreCategory: LiveData<String>
        get() = _currentScoreCategory

    // Updates the selected score category
    fun updateCurrentScoreCategory(category: String) {
        _currentScoreCategory.postValue(category)
    }

    // Validation if user has already set score during the round
    private val _scoreIsSet = MutableLiveData<Boolean>(false)
    private val scoreIsSet: LiveData<Boolean>
        get() = _scoreIsSet

    // The amount roll left during the round
    private val _rollsLeft = MutableLiveData<Int>(3)
    val rollsLeft: LiveData<Int>
        get() = _rollsLeft

    // Validates if the user can roll
    // The validation occurs on changes to rollsLeft, RollingDices and scoreIsSet
    private val _canRoll = MediatorLiveData<Boolean>().apply {
        addSource(rollsLeft) {
            this.postValue(validateCanRoll())
        }
        addSource(rollingDices) {
            this.postValue(validateCanRoll())
        }
        addSource(scoreIsSet) {
            this.postValue(validateCanRoll())
        }
    }

    // Validation method for can roll
    // To be able to roll the user must have at least 1 roll left
    // and at least 1 available dice
    // and the player has not yet set a score
    private fun validateCanRoll(): Boolean {
        return rollsLeft.value!! > 0
                && rollingDices.value!!.rollableDices.isNotEmpty()
                && !scoreIsSet.value!!
    }
    val canRoll: LiveData<Boolean>
        get() = _canRoll

    // Validates if the user can save a dice combination
    // The validation occurs on changes to rollsLeft and pairs
    private val _canSavePair = MediatorLiveData<Boolean>().apply {
        addSource(_rollsLeft) {
            this.postValue(validateCanSavePair())
        }
        addSource(_pairs) {
            this.postValue(validateCanSavePair())
        }
    }
    val canSavePair: LiveData<Boolean>
        get() = _canSavePair

    // Validation method for adding dice combination
    // To be able to save an combination, it must exists at least one pair
    // and at least one pair which is not already saved
    // the user must have rolled at least once
    private fun validateCanSavePair(): Boolean {
        return _pairs.value!!.isNotEmpty()
                && _pairs.value!!.any { pair -> !pair.first }
                && _rollsLeft.value!! < 3
    }

    // Validates if the user can save score
    // The validation occurs on changes to pairs
    private val _canSaveScore = MediatorLiveData<Boolean>().apply {
        addSource(_pairs) {
            // The user can only save the score if
            // the is at least one combination
            // and all combinations are valid
            this.postValue(_pairs.value!!.isNotEmpty() && _pairs.value!!.all { pair -> pair.first })
        }
    }
    val canSaveScore: LiveData<Boolean>
        get() = _canSaveScore

    // Roll the rolling dices
    fun rollDices() {
        val dices = _rollingDices.value!!
        dices.rollDices()
        _rollingDices.postValue(dices)
        _rollsLeft.postValue(_rollsLeft.value!!.minus(1))
        _pairs.postValue(mutableListOf())
    }

    // Toggles a rolling dice between rollable and not
    fun toggleRollable(diceId: Int) {
        val dices = rollingDices.value!!
        val dice = dices.dices[diceId]
        dice.canBeRolled = !dice.canBeRolled
        _rollingDices.postValue(dices)
    }

    // The current round of the game
    private val _currentRound = MutableLiveData<Int>(0)
    val currentRound: LiveData<Int>
        get() = _currentRound

    // Validates if the user can update round
    // The validation occurs on changes to scoreIsSet and scoreBoard
    private var _canUpdateRound = MediatorLiveData<Boolean>().apply {
        addSource(_scoreIsSet) {
            this.postValue(validateCanUpdateRound())
        }
        addSource(_scoreBoard) {
            this.postValue(validateCanUpdateRound())
        }
    }
    val canUpdateRound: LiveData<Boolean>
        get() = _canUpdateRound

    // Validation method for updating the round
    // To be able to update a round the
    // score must be set and if scoreboard is not finished
    private fun validateCanUpdateRound(): Boolean {
        return _scoreIsSet.value!! && _scoreBoard.value!!.scoresLeft.isNotEmpty()
    }

    // Validates of the dices can be clicked
    // The validation occurs on changes to rollsLeft and scoreIsSet
    private val _canClickDices = MediatorLiveData<Boolean>().apply {
        addSource(rollsLeft) {
            this.postValue(validateCanClickDices())
        }
        addSource(scoreIsSet) {
            this.postValue(validateCanClickDices())
        }
    }
    val canClickDices: LiveData<Boolean>
        get() = _canClickDices

    // Validation method for clicking dice
    // To be able to click on a dice,
    // the user most have rolled at least once
    // and score is not set
    private fun validateCanClickDices(): Boolean {
        return _rollsLeft.value!! < 3 && !_scoreIsSet.value!!;
    }

    // Updates the round
    fun updateRound() {
        _currentRound.postValue(_currentRound.value!!.plus(1))
        _rollsLeft.postValue(3)
        _scoreIsSet.postValue(false)
        resetDices()
    }
}