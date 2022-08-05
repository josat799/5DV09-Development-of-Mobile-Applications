package se.atoui.thirty20.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.atoui.thirty20.Models.Dices
import se.atoui.thirty20.Models.GameModel
import se.atoui.thirty20.R

/*
* Array adapter for selected dice combinations.
*/
class ScorePairCategoryListAdapter(
    private val fragment: Fragment,
    private var data: MutableList<Pair<Boolean, Dices>> = mutableListOf()
) :
    ArrayAdapter<String>(fragment.context!!, R.layout.pair_item) {

    private val gameModel: GameModel by fragment.activityViewModels()

    override fun getCount(): Int {

        return data.size
    }

    fun setData(newData: MutableList<Pair<Boolean, Dices>>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = fragment.layoutInflater
        val pairItem: Pair<Boolean, Dices> = data[position]

        val pairItemView = inflater.inflate(R.layout.pair_item, null, true)

        //Set the background to green on saved pairs
        if (pairItem.first) {
            pairItemView.setBackgroundColor(Color.GREEN)
        }
        val innerLayout = pairItemView.findViewById<LinearLayout>(R.id.pairedDices)
        pairItem.second.dices.forEach { dice ->
            val imageView = ImageView(fragment.requireContext())
            when (dice.currentSide) {
                1 -> imageView.setImageResource(R.drawable.grey1)
                2 -> imageView.setImageResource(R.drawable.grey2)
                3 -> imageView.setImageResource(R.drawable.grey3)
                4 -> imageView.setImageResource(R.drawable.grey4)
                5 -> imageView.setImageResource(R.drawable.grey5)
                6 -> imageView.setImageResource(R.drawable.grey6)
            }
            innerLayout.addView(imageView)
        }
        pairItemView.findViewById<TextView>(R.id.combinedScore).text =
            fragment.requireContext().getString(R.string.pairScore, pairItem.second.getScore())

        pairItemView.findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
            gameModel.removePair(position)
        }

        return pairItemView
    }
}