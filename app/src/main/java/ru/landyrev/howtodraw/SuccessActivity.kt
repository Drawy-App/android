package ru.landyrev.howtodraw

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_success.*
import ru.landyrev.howtodraw.util.Background

class SuccessActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_success)
        successLayout.background = Background.background

        successContinue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            this.startActivity(intent)
        }

        val difficulty = intent.getIntExtra("difficulty", 0)

        successRating.text = (1..difficulty).joinToString(" ") { "\uD83C\uDF1F" }

    }
}