package creeper_san.myshoes.event

import android.support.annotation.Nullable
import creeper_san.myshoes.json.NowJson

class WeatherResultEvent(private val isSuccess:Boolean){

    public fun isSuccess():Boolean{
        return isSuccess
    }

}
