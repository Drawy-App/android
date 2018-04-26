package ru.landyrev.howtodraw.data

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ru.landyrev.howtodraw.R

class LevelsAdapter(val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val levelsData = LevelsData(context)

    object ViewTypes {
        const val header: Int = 0
        const val body: Int = 1
    }

    override fun getItemCount(): Int {
        return levelsData.totalLength
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.tutorialCardViewTitle) as TextView
        val previewView: ImageView = itemView.findViewById(R.id.cardPreviewImage)
        val starView0: ImageView = itemView.findViewById(R.id.cardStar0)
        val starView1: ImageView = itemView.findViewById(R.id.cardStar1)
        val starView2: ImageView = itemView.findViewById(R.id.cardStar2)
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
                val level = item.data as Level
                (holder as ViewHolder).titleView.text = level.title_ru
                holder.previewView.setImageBitmap(level.getPreview(context))
                holder.starView0.setImageResource(R.drawable.star)
                holder.starView1.setImageResource(R.drawable.star_empty)
            }
            ViewTypes.header -> {
                (holder as ViewHeaderHolder).titleView.text = (item.data as Stage).title
            }
        }
    }

}