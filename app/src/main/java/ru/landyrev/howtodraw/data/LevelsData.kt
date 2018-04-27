package ru.landyrev.howtodraw.data

import android.content.Context
import org.yaml.snakeyaml.Yaml
import java.util.*

object LevelsData {
    var data: List<Stage>? = null

    object ViewTypes {
        const val HEADER: Int = 0
        const val BODY: Int = 1
    }

    val totalLength: Int
        get() = data!!.fold(initial = 0) {acc, stage ->
            acc + stage.levels.size
        }
    val levelsCount: Int
        get() = data!!.size

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
                    item["title_en"] as String,
                    item["title_ru"] as String,
                    item["tutorials"] as List<String>,
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

    class ViewData(val type: Int, val data: Any)

    fun viewByIndex(index: Int): ViewData? {
        var i = 0

        data!!.forEach { stage ->
            if (i == index) {
                return ViewData(ViewTypes.HEADER, stage)
            }
            stage.levels.forEach { level ->
                i++
                if (i == index) {
                    return ViewData(ViewTypes.BODY, level)
                }
            }
            i++
        }

        return null
    }
}