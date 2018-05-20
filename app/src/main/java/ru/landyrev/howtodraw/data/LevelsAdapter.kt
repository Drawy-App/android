package ru.landyrev.howtodraw.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.*
import ru.landyrev.howtodraw.DetailsActivity
import ru.landyrev.howtodraw.R
import ru.landyrev.howtodraw.views.RatingView


class LevelsAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var openedItem: Int? = null

    object ViewTypes {
        const val header: Int = 0
        const val body: Int = 1
        const val divider: Int = 2
    }

    fun updateData() {
        if (openedItem != null) {
            this.notifyItemChanged(openedItem!!)
        }
    }


    override fun getItemCount(): Int {
        var itemsCount = LevelsData.totalLength + LevelsData.stagesCount
        if (LevelsData.hasLockedLevels) { itemsCount += 1 }
        return itemsCount
    }

    class ViewBodyHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.tutorialCardView)
        val titleView: TextView = itemView.findViewById(R.id.tutorialCardViewTitle)
        val previewView: ImageView = itemView.findViewById(R.id.cardPreviewImage)
        val cardRatingView: RatingView = itemView.findViewById(R.id.cardRatingView)
    }

    class ViewHeaderHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.levelSectionHeaderTitle)
    }

    class ViewDividerHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.dividerTitle)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ViewTypes.header -> {
                val linearView = LayoutInflater.from(parent!!.context)
                        .inflate(R.layout.level_header, parent, false) as LinearLayout
                ViewHeaderHolder(linearView)
            }
            ViewTypes.divider -> {
                val linearView = LayoutInflater.from(parent!!.context)
                        .inflate(R.layout.divider, parent, false) as LinearLayout
                ViewDividerHolder(linearView)
            }
            else -> {
                val linearView = LayoutInflater.from(parent!!.context)
                        .inflate(R.layout.level_card, parent, false) as LinearLayout
                ViewBodyHolder(linearView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return LevelsData.viewByIndex(position)!!.type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = LevelsData.viewByIndex(holder!!.adapterPosition)!!

        when(item.type) {
            ViewTypes.body -> {
                val level = LevelsData.levelBy(item.data as String)
                (holder as ViewBodyHolder).titleView.text = level.title
                holder.previewView.setImageBitmap(level.getPreview(context))
                val solved = level.rating > 0
                holder.cardRatingView.rating = level.difficulty
                holder.cardRatingView.solved = solved

                if (!item.isUnlocked) {
                    holder.cardView.alpha = 0.3f
                    holder.cardView.setOnClickListener(null)
                    holder.cardView.isClickable = false
                } else {
                    holder.cardView.isClickable = true
                    holder.cardView.alpha = 1.0f

                    holder.cardView.setOnClickListener({
                        Handler().postDelayed({
                            openedItem = holder.adapterPosition
                            openTutorial(level)
                        }, 50)
                    })
                }

                holder.cardRatingView.redraw()
            }
            ViewTypes.header -> {
                (holder as ViewHeaderHolder).titleView.text = context.getString(
                        R.string.level,
                        (item.data as Stage).stageNumber
                )
                if (!item.isUnlocked) {
                    holder.titleView.alpha = 0.3f
                } else {
                    holder.titleView.alpha = 1.0f
                }
            }
            ViewTypes.divider -> {
                (holder as ViewDividerHolder).title.text = context.getString(
                        R.string.divider_text,
                        LevelsData.ratingToNewLevel
                )
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