package com.example.wordsearch

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var homeContainer: View
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
        }
        val startButton = MaterialButton(this).apply {
            text = "Start Game"
            setBackgroundColor(Color.parseColor("#02158C"))
            setStrokeColorResource(R.color.purple_700)
            strokeWidth = 2
            setTextColor(Color.WHITE)
            cornerRadius = 100

            id = View.generateViewId()

            // Set the button's layout parameters
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                horizontalBias = 0.5f
                verticalBias = 0.7f
            }

            (layoutParams as ConstraintLayout.LayoutParams).apply {
                setPadding(0,70,0,70)
                setMargins(50,0,50,0)
                gravity
            }
            setOnClickListener {
                val passableData = Intent(applicationContext, GameActivity::class.java).apply {
                    putExtra("message", "page loaded")
                }
                startActivity(passableData)
            }

        }

        val overlay = View(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#20000000")) // black color with 50% opacity
        }
        val previewImageView: ImageView = ImageView(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                500,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                horizontalBias = 0.5f
                verticalBias = 0.1f
//                bottomMargin = 400
            }
            setImageResource(R.drawable.preview)
        }
        val imgContainer = FrameLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                700,
                700,
            ).apply {
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                horizontalBias = 0.5f
                verticalBias = 0.25f
            }
            val layoutParams = FrameLayout.LayoutParams(
                400,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            previewImageView.layoutParams = layoutParams
            addView(previewImageView)
            val shapeDrawable = ShapeDrawable(OvalShape())
            shapeDrawable.paint.color = Color.parseColor("#75FFFFFF")
            background = shapeDrawable
        }
        homeContainer = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )

            // Set the background image resource
            setBackgroundResource(R.drawable.img)
            addView(overlay)
            addView(imgContainer)
            addView(startButton)

        }
        setContentView(homeContainer)
    }
}