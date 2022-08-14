package se.atoui.thirty20


import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import se.atoui.thirty20.models.Dice
import se.atoui.thirty20.models.GameModel
import se.atoui.thirty20.models.States
import se.atoui.thirty20.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var gameModel: GameModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameModel = ViewModelProvider(this)[GameModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Adds a onClick listener to navigation buttons
        binding.toggleModeButtons.addOnButtonCheckedListener { buttons, _, isChecked ->
            if (isChecked) {
                when (buttons.checkedButtonId) {
                    R.id.modeRollingButton -> {
                        // Navigates to the rolling fragment if it is not already there
                        if (navController.currentDestination!!.id != R.id.rollingFragment) {
                            gameModel.updateState(States.Rolling)
                            navController.navigate(R.id.action_scoring_to_rollingNavigation)
                        }
                    }
                    R.id.modeScoringButton -> {
                        // Navigates to the scoring fragment if it is not already there
                        if (navController.currentDestination!!.id != R.id.scoringFragment) {
                            gameModel.updateState(States.Scoring)
                            navController.navigate(R.id.action_rolling_to_scoringNavigation)
                        }
                    }
                }
            }
        }
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setCurrentRound()
        setCurrentScore()

        // Observes the state of the game and updates buttons
        // depending on state
        gameModel.currentState.observe(this) {
            if (it == States.Rolling) {
                setRollDiceFab()
                setRollingDices()
                setNextRoundFab()
            } else if (it == States.Scoring) {
                setSavePairFab()
                setSaveScoreFab()
                setScoringDices()
            }
        }

        navController = findNavController(R.id.nav_host_fragment_content_main)

        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.rollingNavigation, R.id.scoringNavigation))
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    // Setup for the rolling dices
    private fun setRollingDices() {
        gameModel.rollingDices.observe(this) { dices ->
            // Find each imageView correlating to the dices
            // The dice id corresponds the index of the imageView
            dices.dices.forEach { dice ->
                val diceView: ImageView =
                    binding.content.dices.diceContainer[dice.id] as ImageView
                // Updates the image
                setDiceViewImage(diceView, dice)
                diceView.visibility = View.VISIBLE

                // Adds green background if the dice is not rollable
                if (!dice.canBeRolled) {
                    diceView.setBackgroundColor(Color.GREEN)
                } else {
                    diceView.setBackgroundColor(Color.TRANSPARENT)
                }

                gameModel.canClickDices.observe(this) { canClick ->
                    diceView.setOnClickListener {
                        if (canClick) {
                            gameModel.toggleRollable(dice.id)
                        }
                    }
                }
            }
        }
    }

    // Sets the score label
    private fun setCurrentScore() {
        gameModel.scoreBoard.observe(this) {
            binding.content.scoreTitle.text = getString(R.string.current_score, it.totalScore)
        }
    }

    // Sets the round label
    private fun setCurrentRound() {
        gameModel.currentRound.observe(this) {
            binding.content.roundTitle.text = getString(R.string.current_round, it)
        }
    }

    // Setup the toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Setup for the roll dices floating action button
    private fun setRollDiceFab() {
        binding.secondAction.icon = getDrawable(R.drawable.dice_solid)
        // Updates the amount of rolls left
        gameModel.rollsLeft.observe(this) {
            binding.secondAction.text = getString(R.string.roll_dice_hint, it)
        }
        gameModel.canSaveScore.removeObservers(this)
        gameModel.canRoll.observe(this) { canRoll ->
            // Enables/Disables the button if the user is in valid state of rolling
            binding.secondAction.isEnabled = canRoll
        }
        binding.secondAction.setOnClickListener {
            gameModel.rollDices()
        }
    }

    // Setup for the next round floating action button
    private fun setNextRoundFab() {
        binding.mainAction.text = getString(R.string.next_round)
        binding.mainAction.icon = getDrawable(R.drawable.ic_baseline_next_plan_24)
        gameModel.canUpdateRound.observe(this) { canUpdate ->
            // Enables/Disables the button if the user is in valid state of updating the round
            binding.mainAction.isEnabled = canUpdate
        }
        binding.mainAction.setOnClickListener {
            gameModel.updateRound()
        }
    }

    // Setup for the save pair floating action button
    private fun setSavePairFab() {
        binding.mainAction.text = getString(R.string.savePair)
        binding.mainAction.icon = getDrawable(R.drawable.add_48px)
        gameModel.canSavePair.observe(this) { canSavePair ->
            // Enables/Disables the button if the user is in valid state of saving a pair
            binding.mainAction.isEnabled = canSavePair
        }
        binding.mainAction.setOnClickListener {
            gameModel.addPair()
        }
    }

    // Setup for the save score floating action button
    private fun setSaveScoreFab() {
        binding.secondAction.text = getString(R.string.save_score_button)
        binding.secondAction.icon = getDrawable(android.R.drawable.ic_menu_save)
        gameModel.canSaveScore.observe(this) { canSaveScore ->
            // Enables/Disables the button if the user is in valid state of saving the score
            binding.secondAction.isEnabled = canSaveScore
        }
        binding.secondAction.setOnClickListener {
            if(gameModel.saveScore()) {
                // Navigates the user to rolling view if score has been saved
                binding.toggleModeButtons.check(R.id.modeRollingButton)
            }
        }
    }

    // Sets the imageviews image resource depending on the dice current side
    private fun setDiceViewImage(imageView: ImageView, dice: Dice) {
        when (dice.currentSide) {
            1 -> imageView.setImageResource(R.drawable.grey1)
            2 -> imageView.setImageResource(R.drawable.grey2)
            3 -> imageView.setImageResource(R.drawable.grey3)
            4 -> imageView.setImageResource(R.drawable.grey4)
            5 -> imageView.setImageResource(R.drawable.grey5)
            6 -> imageView.setImageResource(R.drawable.grey6)
        }
    }

    // setup the scoring dices imageViews
    private fun setScoringDices() {
        gameModel.scoringDices.observe(this) { dices ->
            dices.dices.forEach { dice ->
                val diceView: ImageView =
                    binding.content.dices.diceContainer[dice.id] as ImageView
                setDiceViewImage(diceView, dice)
                diceView.setBackgroundColor(Color.TRANSPARENT)

                // If a dice is already in a combination then hide the view
                if (gameModel.diceInPair(dice.id)) {
                    diceView.visibility = View.GONE
                } else {
                    diceView.visibility = View.VISIBLE
                }
                gameModel.canClickDices.observe(this) { canClick ->
                    diceView.setOnClickListener {
                        if (canClick) {
                            // If dice is not in a combination then add it
                            if (!gameModel.diceInPair(dice.id)) {
                                gameModel.appendDiceToPair(dice.id)
                            }
                        }
                    }
                }
            }
        }
    }

}