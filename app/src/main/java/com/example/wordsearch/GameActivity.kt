package com.example.wordsearch

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class GameActivity : AppCompatActivity() {
    private val texts = "qwertyuiopasgdfhjklzcxvbcmcvbg"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val gameBoardView = LinearLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                    topToTop = ConstraintSet.PARENT_ID
                    bottomToBottom = ConstraintSet.PARENT_ID
                    horizontalBias = 0.5f
                    verticalBias = 0.5f
                setPadding(10, 10, 10, 10)
            }
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.LTGRAY)

            // Use a step of 5 to iterate over groups of 5 characters at a time
            for (i in 0..texts.length step 5) {
                val row = LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    )
                    orientation = LinearLayout.HORIZONTAL
                }

                // Iterate over the characters in the current group
                for (j in i until i + 5) {
                    if (j >= texts.length) break // Stop iterating if we reach the end of the string

                    val charBtn = Button(context).apply {
                        text = texts[j].uppercase()
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f,
                        )
                        setOnClickListener {
                            Log.d("Button", this.text.toString())
                        }
                        setBackgroundColor(Color.LTGRAY)

                    }
                    row.addView(charBtn)
                }
                addView(row)
            }
        }
        val mainView = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
            )
            setBackgroundColor(Color.WHITE)
            addView(gameBoardView)
        }
        setContentView(mainView)
    }
}