package com.example.wordsearch

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration


private const val STROKE_WIDTH = 12f
private var drawColor: String = "#550000FF"
class CanvasView (context: Context): View(context){

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private val backgroundColor = Color.parseColor("#00FF9922") //clear

    //    Set up the paint with which to draw
    val paint = Paint().apply {
        color = Color.parseColor(drawColor)
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are dow-sampled.
        isDither = true
        style = Paint.Style.STROKE //default: Fill
        strokeJoin = Paint.Join.ROUND // default Miter
        strokeCap = Paint.Cap.ROUND // default BUTT
        strokeWidth = STROKE_WIDTH // default Miter
        blendMode = BlendMode.XOR
    }
    private var path = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y
        when(event.action){
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()

        }
        return true
    }

    private fun touchStart(){
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove(){
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance){
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX)/2,
                (motionTouchEventY + currentY)/2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            //Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }
    public fun pathMove(currentXP: Float, currentYP: Float, motionTouchEventXP :Float, motionTouchEventYP :Float){
            currentX = currentXP
            currentY = currentYP
            motionTouchEventX = motionTouchEventXP
            motionTouchEventY = motionTouchEventYP
            val dx = Math.abs(motionTouchEventX - currentX)
            val dy = Math.abs(motionTouchEventY - currentY)
            if (dx >= touchTolerance || dy >= touchTolerance){
                path.quadTo(currentX, currentY, (motionTouchEventX + currentX)/2,
                    (motionTouchEventY + currentY)/2)
                currentX = motionTouchEventX
                currentY = motionTouchEventY
                //Draw the path in the extra bitmap to cache it.
                extraCanvas.drawPath(path, paint)
            }
            invalidate()
        }

    private fun touchUp(){
        //Reset the path so it doesn't get drawn again
        path.reset()
    }

    public fun clearAll(){
       //recycle old bitmap
        extraBitmap.recycle()
        //start over with a new Bitmap
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        invalidate()
    }
    public fun setDrawColor(color: String, appMode: String){

        if(appMode == "text"){
            drawColor = "#55$color"
        }
        else{
            drawColor = "#$color"
        }
        paint.color = Color.parseColor(drawColor)
    }
}