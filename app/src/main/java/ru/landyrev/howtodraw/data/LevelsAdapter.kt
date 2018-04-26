package ru.landyrev.howtodraw.data

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.w3c.dom.Text
import ru.landyrev.howtodraw.R

class LevelsAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val levelsData = LevelsData(context)

    public object ViewTypes {
        val header: Int = 0
        val body: Int = 1
    }

    override fun getItemCount(): Int {
        return levelsData.totalLength
//        return levelsData.flatMap { level -> level.tutorials }.size + levelsData.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.tutorialCardView) as CardView
        val titleView: TextView = itemView.findViewById(R.id.tutorialCardViewTitle) as TextView

    }

    class ViewHeaderHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.levelSectionHeaderTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            ViewTypes.header -> {
                val linearView = LayoutInflater.from(parent!!.context)
                        .inflate(R.layout.level_header, parent, false) as LinearLayout
                return ViewHeaderHolder(linearView)
            }
        }

        val linearView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.level_card, parent, false) as LinearLayout
        return ViewHolder(linearView)
    }

    override fun getItemViewType(position: Int): Int {
        return levelsData.viewByIndex(position)!!.type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val item = levelsData.viewByIndex(position)!!

        when(item.type) {
            ViewTypes.body -> {
                (holder as ViewHolder).titleView.text = (item.data as Level).title_ru
            }
            ViewTypes.header -> {
                (holder as ViewHeaderHolder).titleView.text = (item.data as Stage).title
            }
        }
    }

}