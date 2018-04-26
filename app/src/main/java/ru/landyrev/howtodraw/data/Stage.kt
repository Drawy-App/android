package ru.landyrev.howtodraw.data

class Stage (val stageNumber: Int, val levels: List<Level>) {
    val title: String
        get() = "$stageNumber уровень"
}