package com.example.wordsearch

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import org.json.JSONArray
import org.json.JSONObject

class GameActivity : AppCompatActivity() {
    private lateinit var frameLayout: FrameLayout
    private lateinit var mainView: ConstraintLayout
    private var texts = ""
     private var word: String = ""
     private var data: String = ""
    private lateinit var answers:JSONArray
    private  var clickedBtnIds: Array<Int> = arrayOf()
    private var buttonsBg:Boolean = true
    var hexColor = ""

    private var gameIndex = 0
    private var score = 0
    lateinit var arrayData:JSONArray
    private lateinit var  linearLayout: LinearLayout
    private lateinit var  gameBoardView: LinearLayout
    private lateinit var firstItem: JSONObject
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val canvasView = CanvasView(this)
        val dataSent = intent
        data = dataSent.getStringExtra("API Response").toString()
        arrayData = JSONArray(data)
        firstItem = arrayData.getJSONObject(gameIndex)
        texts = firstItem.getString("word_string")
        answers = JSONArray(firstItem.getString("answer").split(","))
        Log.d("Data: ", arrayData.toString())
        frameLayout = FrameLayout(this).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT
            )
            addView(canvasView)
        }

        gameBoardView = LinearLayout(this).apply {
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

        }
        gameViewMaker()
        linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
         mainView = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
            )

            setBackgroundColor(Color.WHITE)
            addView(linearLayout)
            addView(gameBoardView)

        }


        setContentView(mainView)
    }

    private fun touchMove() {
        Log.d("TouchMove", "touch move")
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val motionTouchEventX = event.x
        val motionTouchEventY = event.y
        when(event.action){
            MotionEvent.ACTION_MOVE -> touchMove()

        }
        return true
    }
    fun gameViewMaker(){
        firstItem = arrayData.getJSONObject(gameIndex)
        texts = firstItem.getString("word_string")
        answers = JSONArray(firstItem.getString("answer").split(","))
        var count = 0
        // Use a step of 5 to iterate over groups of 5 characters at a time
        for (i in 0..texts.length step 5) {
            val row = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                orientation = LinearLayout.HORIZONTAL
            }

            // Iterate over the characters in the current group
            for (j in i until i + 5) {
                if (j >= texts.length) break // Stop iterating if we reach the end of the string

                val charBtn = Button(this).apply {
                    text = texts[j].uppercase()
                    id = count
                    count ++
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f,
                    ).apply {
//                            setMargins(10, 10, 10, 10)

                    }
                    setOnLongClickListener { view ->
                        // Create a ClipData object that contains the text of the button
                        val dragData = ClipData.newPlainText("buttonText", this.text)

                        // Create a DragShadowBuilder that provides a visual representation of the dragged item
                        val shadowBuilder = View.DragShadowBuilder(view)

                        // Start the drag and drop operation
                        view.startDragAndDrop(dragData, shadowBuilder, view, 0)

                        true
                    }

                    setBackgroundColor(Color.TRANSPARENT)

                }
                charBtn.setOnDragListener { view, event ->
                    when (event.action) {
//                            Log.d("Action", event.action.toString()),
                        DragEvent.ACTION_DRAG_STARTED -> {
//                                choosenText.text = ""
                            word = ""
                            hexColor = getRandomHexColor()
                            true

                        }
                        DragEvent.ACTION_DRAG_ENTERED -> {
                            word += charBtn.text
                            charBtn.setBackgroundColor(Color.CYAN)

                            clickedBtnIds = clickedBtnIds.plus(charBtn.id)
                            Log.d("clbtn: ", clickedBtnIds.contentToString())
                            val screenX = view.left + event.x
                            val screenY = view.top + event.y
                            Log.d("Button", "Drag Entered: ${charBtn.text} screenX=$screenX screenY=$screenY")
//                                canvasView.pathMove(screenX, screenY, event.x, event.y)

                            true
                        }
                        DragEvent.ACTION_DRAG_EXITED -> {
                            true
                        }
                        DragEvent.ACTION_DROP -> {

                            validateAnswer(word)
                            clickedBtnIds.forEach { btnid->
                                findViewById<Button>(btnid).apply {
                                    if(buttonsBg){
                                        Log.d("frm drop: ", buttonsBg.toString())

                                        setBackgroundColor(Color.parseColor(hexColor))
                                        setTextColor(Color.WHITE)

                                    }else{
                                        setBackgroundColor(Color.TRANSPARENT)
                                        setTextColor(Color.BLACK)

                                    }
                                }
                            }
                            clickedBtnIds = arrayOf()


                            true
                        }
                        else -> false
                    }
                }
                row.addView(charBtn)
            }

            gameBoardView.addView(row)
        }
    }
    private fun getRandomHexColor(): String {
        val hexDigits = "0123456789ABCDEF"
        var color = "#"
        for (i in 0..5) {
            color += hexDigits.random()
        }
        return color
    }
    private fun validateAnswer(choice: String){
        for(i in 0 until answers.length()){

            if(choice == answers.getString(i)){
                score ++
                val chosenText: TextView = TextView(this).apply {
                    text = choice
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }.apply {
                    setPadding(15,5,15,5)
                    setBackgroundColor(Color.parseColor(hexColor))
                    setTextColor(Color.WHITE)
                    textSize = 26f
                    gravity = Gravity.CENTER
                    width = 2000
                }
                if(score <= 4) {
                    linearLayout.addView(chosenText)
                    Log.d("Score: ", score.toString())
                }
                if(score == 4){
                    Log.d("Game State: ", "Won!")
                    AlertDialog.Builder(this).apply {
                        setTitle("Game State")
                        setMessage("Winner!!!")
                        setPositiveButton("Next") { _, _ ->
                            linearLayout.removeAllViews()
                            gameBoardView.removeAllViews()
                            gameIndex++
                            // Check to see if I have exhausted the searchwords, then reset
                            if (gameIndex == 2){
                                gameIndex = 0
                            }
                            score = 0
                            gameViewMaker()
                        }
                        create()
                        show()
                    }
                }
                buttonsBg = true
                Log.d("CompareP: ", buttonsBg.toString())
                break
            }else{
                buttonsBg = false
                Log.d("CompareN: ", buttonsBg.toString())
            }
        }
    }
}