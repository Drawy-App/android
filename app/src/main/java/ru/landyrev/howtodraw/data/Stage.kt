package ru.landyrev.howtodraw.data

class Stage (val stageNumber: Int, val levels: List<Level>) {

    val unlocked: Boolean
        get() {
            return LevelsData.totalRating >= LevelsData.steps[stageNumber]!!
        }
}