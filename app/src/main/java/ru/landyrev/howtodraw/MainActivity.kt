package ru.landyrev.howtodraw

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import ru.landyrev.howtodraw.data.LevelsAdapter
import ru.landyrev.howtodraw.util.Background
import ru.landyrev.howtodraw.util.UserData

class MainActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityBackground.background = Background.background

        mLayoutManager = LinearLayoutManager(this)
        viewAdapter = LevelsAdapter(this)

        mRecyclerView = findViewById<RecyclerView>(R.id.levelsList).apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = viewAdapter
        }
        if (!UserData(this).tutorialPassed) {
            startTutorial()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 99) {
            this.mRecyclerView.adapter.notifyDataSetChanged()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startTutorial() {
        val intent = Intent(this, TutorialActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        this.startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        (viewAdapter as LevelsAdapter).updateData()
    }
}