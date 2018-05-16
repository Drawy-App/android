package ru.landyrev.howtodraw.data

import android.content.Context
import org.yaml.snakeyaml.Yaml
import ru.landyrev.howtodraw.R
import java.util.*
import kotlin.collections.HashMap

object LevelsData {
    var data: List<Stage>? = null

    object ViewTypes {
        const val HEADER: Int = 0
        const val BODY: Int = 1
        const val DIVIDER: Int = 2
    }

    val totalLength: Int
        get() = data!!.map { stage -> stage.levels.size }.sum()

    val ratingToNewLevel: Int
        get() {
            return steps[unlockedStagesCount + 1]!! - totalRating
        }

    val stagesCount: Int
        get() = data!!.size

    private val unlockedStagesCount: Int
        get() = data!!.filter { stage -> stage.unlocked }.size

    val hasLockedLevels: Boolean
        get() = stagesCount > unlockedStagesCount

    val totalRating: Int
        get() = data!!.flatMap { stage -> stage.levels}.map { level -> level.rating }.sum()

    fun levelBy(name: String): Level {
        return data!!.flatMap(Stage::levels).filter { level -> level.name == name }[0]

    }

    fun loadImages(context: Context) {
        val yaml = Yaml()
        val data = yaml.load(context.assets.open("Images.bundle/levels.yaml"))
                as List<LinkedHashMap<String, Any>>
        val allLevels: List<Level> = data.map { item ->
            Level(
                    (item["difficulty"] as String).toInt(),
                    (item["level"] as String).toInt(),
                    item["name"] as String,
                    item["title_" + context.getString(R.string.postfix)] as String,
                    (item["tutorials"] as List<String>).map {tutorial -> "Images.bundle/$tutorial"},
                    context
            )

        }
        val foldedLevels = allLevels.fold(initial = hashMapOf()) { acc: HashMap<Int, MutableList<Level>>, level: Level ->
            if (acc[level.level] == null) acc[level.level] = mutableListOf()
            acc[level.level]!!.add(level)
            return@fold acc
        }

        this.data = foldedLevels.keys.map { stageName ->
            Stage(stageName, foldedLevels[stageName]!!.toList())
        }.sortedBy(Stage::stageNumber)
    }

    class ViewData(val type: Int, val data: Any, val isUnlocked: Boolean)

    fun viewByIndex(index: Int): ViewData? {
        var i = 0

        data!!.forEach { stage ->
            if (stage.stageNumber - 1 == LevelsData.unlockedStagesCount) {
                if (i == index) {
                    return ViewData(ViewTypes.DIVIDER, 0, stage.unlocked)
                }
                i++
            }
            if (i == index) {
                return ViewData(ViewTypes.HEADER, stage, stage.unlocked)
            }
            stage.levels.forEach { level ->
                i++
                if (i == index) {
                    return ViewData(ViewTypes.BODY, level.name, stage.unlocked)
                }
            }
            i++
        }

        return null
    }

    val steps: HashMap<Int, Int> = hashMapOf(
            1 to 0,
            2 to 3,
            3 to 7,
            4 to 12,
            5 to 14,
            6 to 17,
            7 to 20,
            8 to 24,
            9 to 29,
            10 to 34,
            11 to 40,
            12 to 46,
            13 to 53
    )
}