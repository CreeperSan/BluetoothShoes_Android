package creeper_san.myshoes.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import creeper_san.myshoes.helper.DensityHelper

class RoundProgressBar : View {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var isInit = false
    var offset = 0f
    var canvasHeight = 0f
    var canvasWidth = 0f

    constructor(context:Context):super(context){
    }
    constructor(context:Context,attr:AttributeSet):super(context,attr){
    }
    constructor(context: Context,attr: AttributeSet,defStyle:Int):super(context,attr,defStyle){
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInit){
            isInit = true
            paint.strokeWidth = DensityHelper.dp2px(context,10f)
            offset = paint.strokeWidth/2
            canvasHeight = canvas.height.toFloat()
            canvasWidth = canvas.width.toFloat()
            paint.style = Paint.Style.STROKE
        }
        paint.color = Color.parseColor("#c0c0c0")
        canvas.drawArc(0f+offset,0f+offset,canvasWidth-offset,canvasHeight-offset,270f,360f,false,paint)
        paint.color = Color.parseColor("#ff4081")
        canvas.drawArc(0f+offset,0f+offset,canvasWidth-offset,canvasHeight-offset,270f,0f,false,paint)
    }

    fun log(string:String){
        Log.i("RoundProgressBar",string)
    }

}



