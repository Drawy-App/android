package ru.landyrev.howtodraw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ru.landyrev.howtodraw.data.LevelsAdapter
import ru.landyrev.howtodraw.data.LevelsData
import kotlinx.android.synthetic.main.activity_main.*
import ru.landyrev.howtodraw.util.Background

class MainActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LevelsData.loadImages(this)
        Background.load(this)
        setContentView(R.layout.activity_main)

        mainActivityBackground.background = Background.background

        mLayoutManager = LinearLayoutManager(this)
        viewAdapter = LevelsAdapter(this)

        mRecyclerView = findViewById<RecyclerView>(R.id.levelsList).apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = viewAdapter
        }

    }
}