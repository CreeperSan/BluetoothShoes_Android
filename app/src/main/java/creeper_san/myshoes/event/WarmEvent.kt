package creeper_san.myshoes.event

class WarmEvent(private val status:Boolean,private val temp:Int){

    public fun getStatus():Boolean{
        return status
    }

    public fun getTemperature():Int{
        return temp
    }

}

