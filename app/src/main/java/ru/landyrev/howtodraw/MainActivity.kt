package ru.landyrev.howtodraw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ru.landyrev.howtodraw.data.LevelsAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLayoutManager = LinearLayoutManager(this)
        viewAdapter = LevelsAdapter(this)

        mRecyclerView = findViewById<RecyclerView>(R.id.levelsList).apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = viewAdapter
        }

    }
}