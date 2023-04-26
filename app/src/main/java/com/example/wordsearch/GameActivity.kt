package com.example.wordsearch

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class GameActivity : AppCompatActivity() {
    private val texts = "qwertyuiopasgdfhjklzcxvbcmcvbg"
     var word: String = ""
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var choosenText = TextView(this).apply {
            text = word
        }


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
//
                        setOnLongClickListener { view ->
                            // Create a ClipData object that contains the text of the button
                            val dragData = ClipData.newPlainText("buttonText", this.text)

                            // Create a DragShadowBuilder that provides a visual representation of the dragged item
                            val shadowBuilder = View.DragShadowBuilder(view)

                            // Start the drag and drop operation
                            view.startDragAndDrop(dragData, shadowBuilder, view, 0)

                            true
                        }

                        setBackgroundColor(Color.LTGRAY)

                    }
                    charBtn.setOnDragListener { _, event ->
                        when (event.action) {
                            DragEvent.ACTION_DRAG_STARTED -> {
                                true
                            }
                            DragEvent.ACTION_DRAG_ENTERED -> {
                                Log.d("Button", "Button entered: ${charBtn.text}")
                                word += charBtn.text

                                true
                            }
                            DragEvent.ACTION_DRAG_EXITED -> {
                                true
                            }
                            DragEvent.ACTION_DROP -> {
                                choosenText.text = word
                                true
                            }
                            else -> false
                        }
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
            addView(choosenText)
            addView(gameBoardView)
        }
        setContentView(mainView)
    }
}