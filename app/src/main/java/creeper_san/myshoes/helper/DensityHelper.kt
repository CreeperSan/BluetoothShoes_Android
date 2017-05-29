package creeper_san.myshoes.helper

import android.content.Context

object DensityHelper {

    fun dp2px(context:Context,value:Float):Float{
        val scale =  context.resources.displayMetrics.density
        return value*scale*0.5f
    }

    fun px2dp(context:Context,value:Float):Float{
        val scale =  context.resources.displayMetrics.density
        return value/scale*0.5f
    }

}
