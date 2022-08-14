package se.atoui.thirty20

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.atoui.thirty20.models.GameModel
import se.atoui.thirty20.adapters.ScorePairCategoryListAdapter
import se.atoui.thirty20.databinding.FragmentScoringBinding

// Implements the AdapterView.OnItemSelectedListener to for the score category spinner
class ScoringFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentScoringBinding? = null
    private val gameModel: GameModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoringBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Finds the score category spinner and adds an arrayAdapter
        val scoreCategorySpinner: Spinner = binding.scoreCategoriesSpinner
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            gameModel.scoreBoard.value!!.scoresLeft.toMutableList()
        )

        // Uses the android dropdown view
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        scoreCategorySpinner.adapter = spinnerAdapter
        scoreCategorySpinner.onItemSelectedListener = this

        // Finds the dice combinations pairs and adds the ScorePairCategoryListAdapter
        val scorePairs: ListView = binding.pairList
        val scorePairListAdapter = ScorePairCategoryListAdapter(this)
        scorePairs.adapter = scorePairListAdapter
        gameModel.pairs.observe(viewLifecycleOwner) {
            scorePairListAdapter.setData(it)
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // Updates the selected score category
        val item  = gameModel.scoreBoard.value!!.scoresLeft.toList()[p2]
        gameModel.updateCurrentScoreCategory(item)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}