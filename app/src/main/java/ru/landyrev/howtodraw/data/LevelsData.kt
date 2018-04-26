package ru.landyrev.howtodraw.data

import android.content.Context
import org.yaml.snakeyaml.Yaml
import java.util.*

class LevelsData(context: Context) {
    public object ViewTypes {
        const val HEADER: Int = 0
        const val BODY: Int = 1
    }

    val data = loadImages(context)
    val totalLength: Int
        get() = data.fold(initial = 0) {acc, stage ->
            acc + stage.levels.size
        }
    val levelsCount: Int
        get() = data.size

    private fun loadImages(context: Context): List<Stage> {
        val yaml = Yaml()
        val data = yaml.load(context.assets.open("Images.bundle/levels.yaml"))
                as List<LinkedHashMap<String, Any>>
        val allLevels: List<Level> = data.map { item ->
            return@map Level(
                    item["difficulty"] as String,
                    item["level"] as String,
                    item["name"] as String,
                    item["title_en"] as String,
                    item["title_ru"] as String,
                    item["tutorials"] as List<String>
            )

        }
        val foldedLevels = allLevels.fold(initial = hashMapOf()) { acc: HashMap<String, MutableList<Level>>, level: Level ->
            if (acc[level.level] == null) acc[level.level] = mutableListOf()
            acc[level.level]!!.add(level)
            return@fold acc
        }
        return foldedLevels.keys.map { stageName ->
            Stage(stageName.toInt(), foldedLevels[stageName]!!.toList())
        }
    }

    class ViewData(val type: Int, val data: Any)

    fun viewByIndex(index: Int): ViewData? {
        var i = 0

        data.forEach { stage ->
            if (i == index) {
                return ViewData(ViewTypes.HEADER, stage)
            }
            stage.levels.forEach { level ->
                i++
                if (i == index) {
                    return ViewData(ViewTypes.BODY, level)
                }
            }
        }

        return null
    }
}