package ru.landyrev.howtodraw.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.*
import ru.landyrev.howtodraw.DetailsActivity
import ru.landyrev.howtodraw.R


class LevelsAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    object ViewTypes {
        const val header: Int = 0
        const val body: Int = 1
    }


    override fun getItemCount(): Int {
        return LevelsData.totalLength
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.tutorialCardView)
        val titleView: TextView = itemView.findViewById(R.id.tutorialCardViewTitle)
        val previewView: ImageView = itemView.findViewById(R.id.cardPreviewImage)
        val starViews: List<ImageView> = listOf(
                itemView.findViewById(R.id.cardStar0),
                itemView.findViewById(R.id.cardStar1),
                itemView.findViewById(R.id.cardStar2)
        )
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
        return LevelsData.viewByIndex(position)!!.type
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val item = LevelsData.viewByIndex(position)!!

        when(item.type) {
            ViewTypes.body -> {
                val level = item.data as Level
                (holder as ViewHolder).titleView.text = level.title_ru
                holder.previewView.setImageBitmap(level.getPreview(context))
                (0 until level.difficulty).forEach { i ->
                    holder.starViews[i].setImageResource(R.drawable.star_empty)
                }
                holder.cardView.setOnClickListener({
                    Handler().postDelayed({
                        openTutorial(level)
                    }, 50)
                })
            }
            ViewTypes.header -> {
                (holder as ViewHeaderHolder).titleView.text = (item.data as Stage).title
            }
        }
    }

    private fun openTutorial(level: Level) {
        val intent = Intent(context, DetailsActivity::class.java).apply {
            putExtra("name", level.name)
        }
        context.startActivity(intent)
    }

}