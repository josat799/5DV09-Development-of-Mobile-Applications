package se.atoui.thirty20

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import se.atoui.thirty20.models.GameModel
import se.atoui.thirty20.adapters.ScoreboardListAdapter
import se.atoui.thirty20.databinding.FragmentRollingBinding

class RollingFragment : Fragment() {

    private var _binding: FragmentRollingBinding? = null
    private val binding get() = _binding!!
    private val gameModel: GameModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRollingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sets the scoreboard adapter
        val scoreBoardListView : ListView = binding.scoreboardList
        val adapter = ScoreboardListAdapter(this)
        scoreBoardListView.adapter = adapter

        // Observes if the game is in a finished state and show the user a award image
        gameModel.scoreBoard.observe(viewLifecycleOwner) { scoreBoard ->
            if (scoreBoard.scoresLeft.isEmpty()) {
                binding.awardImageView.visibility = View.VISIBLE
                binding.scoreboardTextView.visibility = View.GONE
                binding.scoreboardList.visibility = View.GONE
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroy()
        _binding = null
    }

}