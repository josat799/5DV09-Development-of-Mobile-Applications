package se.atoui.thirty20

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import se.atoui.thirty20.Models.Dice
import se.atoui.thirty20.Models.GameModel
import se.atoui.thirty20.databinding.FragmentGameBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val gameModel: GameModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val diceContainer = binding.dices.diceContainer

        gameModel.currentRound.observe(viewLifecycleOwner) {
            binding.roundTitle.text = getString(R.string.current_round, it)
        }

        gameModel.scoreBoard.observe(viewLifecycleOwner) {
            binding.scoreTitle.text = getString(R.string.current_score, it.totalScore)
        }

        gameModel.dices.observe(viewLifecycleOwner) { dices ->
            dices.dices.forEach { dice ->
                val diceView: ImageView = diceContainer[dice.id] as ImageView
                setDiceViewImage(diceView, dice)
                setDiceViewListener(diceView, dice.id)
            }
        }

        gameModel.rollsLeft.observe(viewLifecycleOwner) {
            binding.fab.text = getString(R.string.roll_dice_hint, it)
        }

        gameModel.canRoll.observe(viewLifecycleOwner) { canRoll ->
            binding.fab.setOnClickListener {
                if (canRoll) {
                    gameModel.rollDices()
                } else {
                    Toast.makeText(context, gameModel.rollToastMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDiceViewImage(imageView: ImageView, dice: Dice) {
        when (dice.currentSide) {
            1 -> imageView.setImageResource(R.drawable.grey1)
            2 -> imageView.setImageResource(R.drawable.grey2)
            3 -> imageView.setImageResource(R.drawable.grey3)
            4 -> imageView.setImageResource(R.drawable.grey4)
            5 -> imageView.setImageResource(R.drawable.grey5)
            6 -> imageView.setImageResource(R.drawable.grey6)
        }
        if (!dice.canBeRolled) {
            val border = GradientDrawable()
            border.setColor(Color.GREEN)
            border.setStroke(1, Color.BLACK)
            imageView.background = border
        } else {
            imageView.background = null
        }
    }

    private fun setDiceViewListener(diceView: ImageView, diceId: Int) {
        diceView.setOnClickListener {
            Log.d("dices", "toggleDice: $diceId")
            gameModel.toggleRollable(diceId)
        }
    }
}