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
        get() = data.flatMap { level -> level.tutorials }.size
    val levelsCount: Int
        get() = data.size

    private fun loadImages(context: Context): List<Stage> {
        val yaml = Yaml()
        val data = yaml.load(context.assets.open("Images.bundle/levels.yaml"))
                as List<LinkedHashMap<String, Any>>
        val allLevels: List<Level> = data.map { item ->
            Level(
                    item["difficulty"] as String,
                    item["level"] as String,
                    item["name"] as String,
                    item["title_en"] as String,
                    item["title_ru"] as String,
                    item["tutorials"] as List<String>
                    )
        }
        allLevels.reduce { acc: Dictionary<String, MutableList<Level>>, level: Level ->
            acc.add()
        }
        return levels
    }

    class ViewData(val type: Int, val data: Any)

    fun viewByIndex(index: Int): ViewData? {
        var i = 0

        data.forEach { level: Level ->
            if (i == index) {
                return ViewData(ViewTypes.HEADER, level)
            }
            i++
            level.tutorials.forEach { tutorial: String ->
                if (i == index) {
                    return ViewData(ViewTypes.BODY, tutorial)
                }
                i++
            }
        }
        return null
    }
}