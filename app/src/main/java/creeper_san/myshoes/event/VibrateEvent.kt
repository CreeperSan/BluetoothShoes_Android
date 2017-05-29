package creeper_san.myshoes.event

class VibrateEvent(private val level: Int,private val origin:Int) {

    public fun getLevel():Int{
        return level
    }

    public fun getOrigin():Int{
        return origin
    }

}


