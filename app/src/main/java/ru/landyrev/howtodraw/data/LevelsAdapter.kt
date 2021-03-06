package ru.landyrev.howtodraw.data

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ru.landyrev.howtodraw.DetailsActivity
import ru.landyrev.howtodraw.PaywallActivity
import ru.landyrev.howtodraw.R
import ru.landyrev.howtodraw.TryItActivity
import ru.landyrev.howtodraw.views.RatingView


class LevelsAdapter(private val context: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        val unlockButton: Button = itemView.findViewById(R.id.unlockButton)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ViewTypes.header -> {
                val linearView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.level_header, parent, false) as LinearLayout
                ViewHeaderHolder(linearView)
            }
            ViewTypes.divider -> {
                val linearView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.divider, parent, false) as LinearLayout
                ViewDividerHolder(linearView)
            }
            else -> {
                val linearView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.level_card, parent, false) as LinearLayout
                ViewBodyHolder(linearView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return LevelsData.viewByIndex(position)!!.type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = LevelsData.viewByIndex(holder.adapterPosition)!!

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
                val stageNumber = item.data as Int
                if (stageNumber == 1) {
                    (holder as ViewDividerHolder).title.text = context.getString(R.string.first_divider_text)
                } else {
                    (holder as ViewDividerHolder).title.text = context.getString(
                            R.string.divider_text,
                            LevelsData.ratingToNewLevel,
                            "\ud83c\udf1f"
                    )
                    holder.unlockButton.visibility = Button.VISIBLE
                    holder.unlockButton.setOnClickListener {
                        context.startActivityForResult(
                                Intent(context, PaywallActivity::class.java),
                                1
                        )
                    }
                }
            }
        }
    }

    private fun openTutorial(level: Level) {
        val intent = if (level.name == "generic_square") {
            Intent(context, TryItActivity::class.java)
        } else {
            Intent(context, DetailsActivity::class.java).apply {
                putExtra("name", level.name)
            }
        }
        context.startActivityForResult(intent, 1)
    }

}