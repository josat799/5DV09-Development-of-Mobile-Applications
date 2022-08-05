package se.atoui.thirty20.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.atoui.thirty20.Models.GameModel
import se.atoui.thirty20.R

/*
* Array adapter for showing scoreboard items.
*/
class ScoreboardListAdapter(private val fragment: Fragment) :
    ArrayAdapter<String>(fragment.requireContext(), R.layout.score_board_item) {

    private val gameModel: GameModel by fragment.activityViewModels()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val scorePair = gameModel.scoreBoard.value!!.scoresDone[position]
        val view: FrameLayout

        if (convertView == null) {
            val inflater = fragment.layoutInflater
            view = inflater.inflate(
                R.layout.score_board_item, null
            ) as FrameLayout
        } else {
            view = convertView as FrameLayout
        }

        view.findViewById<TextView>(R.id.scoreBoardItemKey).text = scorePair.first
        view.findViewById<TextView>(R.id.scoreBoardItemValue).text = scorePair.second.toString()

        return view
    }
    override fun getCount(): Int {
        return gameModel.scoreBoard.value!!.scoresDone.size
    }
}