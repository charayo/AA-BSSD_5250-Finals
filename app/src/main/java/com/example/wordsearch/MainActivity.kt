package com.example.wordsearch

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var homeContainer: View
    // Inside your activity or fragment
    private val mainScope = MainScope()
    private var response: String = ""
    private var musicState: String = "ON"
    private var mediaPlayer: MediaPlayer? = null
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        musicHandler(true)
        val soundSwitch = SwitchCompat(this).apply {
            text = "Music: $musicState"
            setOnClickListener{
                if (musicState == "ON"){
                    musicHandler(false)
                    musicState = "OFF"
                    this.text = "Music: $musicState"
                }else{
                    musicHandler(true)
                    musicState = "ON"
                    this.text = "Music: $musicState"
                }
            }

        }.apply {
            setPadding(30,10,10,10)
            textSize = 20f
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
                loadResult()
//                Log.d("Response 1", response)
                mainScope.launch {
                    delay(1000)
                    val passableData = Intent(applicationContext, GameActivity::class.java).apply {
                        putExtra("API Response", response)
                    }
                    startActivity(passableData)
                }



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
            addView(soundSwitch)
            addView(imgContainer)
            addView(startButton)

        }
        setContentView(homeContainer)

    }
    private fun musicHandler(state: Boolean){
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        val path = "android.resource://" + this.packageName + "/raw/game_of_thrones"
        val uri = Uri.parse(path)
        try {
            mediaPlayer!!.setDataSource(applicationContext, uri)
            mediaPlayer!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (state){
            mediaPlayer!!.start()
        }else{
            mediaPlayer!!.stop()
        }

    }
    private fun fetch():String {

        val inputStream: InputStream
        var result: String = ""
        try {
            // Create URL
            val url = URL("https://api.justdevay.live/api")
            // Create HttpURLConnection
            val conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection
            // Launch GET request
            conn.connect()
            // Receive response as inputStream
            inputStream = conn.inputStream

            result = if (inputStream != null)
            // Convert input stream to string
                inputStream.bufferedReader().use(BufferedReader::readText)
            else
                "error: inputStream is nULl"
        } catch (err: Error) {
            print("Error when executing get request:" + err.localizedMessage)

        }
        return result
    }
    private fun loadResult(){
        thread(true){
            response = fetch()
//            Log.d("Response 2: ", response)
        }
    }
}