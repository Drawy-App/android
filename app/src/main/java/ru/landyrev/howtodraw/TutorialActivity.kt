package ru.landyrev.howtodraw

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.tutorial_page.*
import ru.landyrev.howtodraw.util.Background
import ru.landyrev.howtodraw.util.UserData

class TutorialActivity: AppCompatActivity() {

    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        tutorialActivity.background = Background.background

        viewPager = tutorialPager
        viewPager.adapter = TutorialPagerAdapter(supportFragmentManager)

        nextButton.setOnClickListener {
            val nextPage = viewPager.currentItem + 1
            if (nextPage > 2) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                this.startActivity(intent)
                UserData(this).tutorialPassed = true
            } else {
                viewPager.setCurrentItem(nextPage, true)
            }
        }
    }
}

class TutorialPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    class TutorialFragment: Fragment() {
        private var animDelaysHandler: Handler? = null
        private var picDelaysHandler: Handler? = null
        var topAnimatePosition: Int = 220
        var bottomAnimatePosition: Int = 120
        var bottomImageStartPosition: Int = 0
        val startTimeout: Long = 1000
        lateinit var imageOne: ImageView
        lateinit var textLabelView: TextView
        var pageNumber = 0
        var textLabel = ""
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.tutorial_page, container, false)
        }

        override fun onStart() {
            super.onStart()
            val scale = context!!.resources.displayMetrics.density

            topAnimatePosition = (topAnimatePosition * scale).toInt()
            bottomAnimatePosition = (bottomAnimatePosition * scale).toInt()
            bottomImageStartPosition = context!!.resources.getDimension(R.dimen.tutorialDownSpace).toInt()
            bottomImageStartPosition = (bottomImageStartPosition * scale).toInt()


            imageOne = tutorialImageOne
            textLabelView = tutorialLabel

            tutorialImageOne.layoutParams = (tutorialImageOne.layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = if (pageNumber == 2) { bottomImageStartPosition } else { 0 }
            }

            when (pageNumber) {
                0 -> {
                    imageOne.setImageResource(R.drawable.persp_papers)
                    textLabel = getString(R.string.tutorialOne)
                    tutorialImageTwo.alpha = 0f
                }
                1 -> {
                    imageOne.setImageResource(R.drawable.drawn_page)
                    textLabel = getString(R.string.tutorialTwo)
                    tutorialImageTwo.alpha = 0f
                }
                2 -> {
                    textLabel = getString(R.string.tutorialThree)
                    tutorialImageTwo.alpha = 1f
                }
            }
            textLabelView.text = textLabel
        }

        override fun onDestroy() {
            super.onDestroy()
            this.resetAnimate()
        }

        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            when (pageNumber) {
                2 -> {
                    if (isVisibleToUser) {
                        startAnimate()
                    } else {
                        resetAnimate()
                    }
                }
            }
        }

        fun resetAnimate() {
            if (tutorialTwoImageContainer != null) {
                Handler(context!!.mainLooper).post {
                    if (picDelaysHandler != null) { picDelaysHandler!!.removeCallbacksAndMessages(null) }
                    if (animDelaysHandler != null) { animDelaysHandler!!.removeCallbacksAndMessages(null) }
                    tutorialTwoImageContainer.clearAnimation()
                    if (tutorialTwoImageContainer.animation != null) {
                        tutorialTwoImageContainer.animation.cancel()
                    }
                    tutorialTwoImageContainer.layoutParams = (tutorialTwoImageContainer.layoutParams as ConstraintLayout.LayoutParams).apply {
                        bottomMargin = topAnimatePosition
                    }
                    tutorialImageTwo.setImageResource(R.drawable.phone_empty)
                }
            }
        }

        private fun startAnimate() {
            Handler(context!!.mainLooper).postDelayed({
                if (context != null) {
                    animDelaysHandler = Handler(context!!.mainLooper)
                    picDelaysHandler = Handler(context!!.mainLooper)
                    animateBottom()
                }
            }, startTimeout)
        }

        fun animateTop() {
            if (tutorialTwoImageContainer == null) {
                return
            }
            tutorialTwoImageContainer.startAnimation(
                    ImageAnimation(tutorialTwoImageContainer, topAnimatePosition).apply {
                        duration = 700
                        setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationRepeat(animation: Animation?) {
                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                if (!isVisible) {
                                    return
                                }
                                animDelaysHandler!!.postDelayed({
                                    animateBottom()
                                }, 2000)
                                picDelaysHandler!!.postDelayed({
                                    if (tutorialImageTwo != null) {
                                        tutorialImageTwo.setImageResource(R.drawable.phone_empty)
                                    }
                                }, 500)
                            }

                            override fun onAnimationStart(animation: Animation?) {
                            }

                        })
                    }
            )
        }

        fun animateBottom() {
            if (tutorialTwoImageContainer == null) {
                return
            }
            tutorialTwoImageContainer.startAnimation(
                    ImageAnimation(tutorialTwoImageContainer, bottomAnimatePosition).apply {
                        duration = 500
                        setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationRepeat(animation: Animation?) {
                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                if (!isVisible) {
                                    return
                                }
                                animDelaysHandler!!.postDelayed({
                                    animateTop()
                                }, 2000)
                                picDelaysHandler!!.postDelayed({
                                    tutorialImageTwo.setImageResource(R.drawable.phone)
                                }, 500)
                            }

                            override fun onAnimationStart(animation: Animation?) {

                            }

                        })
                    }
            )
        }

        inner class ImageAnimation(val view: LinearLayout, val value: Int): Animation() {
            val originalValue = (view.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
            val diff = value - originalValue

            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (!isVisible) {
                    view.clearAnimation()
                    view.animate().cancel()
                    return
                }
                val params = view.layoutParams as ConstraintLayout.LayoutParams
                params.bottomMargin = (originalValue + diff * interpolatedTime).toInt()
                view.layoutParams = params
            }
        }

    }

    override fun getCount(): Int {
        return 3
    }
    override fun getItem(position: Int): Fragment {
        val fragment = TutorialFragment()
        fragment.pageNumber = position
        return fragment
    }
}