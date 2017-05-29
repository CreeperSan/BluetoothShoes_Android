package creeper_san.myshoes.event

class LedEvent(private var status:Int){

    public fun  getStatus():Int{
        return status;
    }

    companion object{
        public val STATUS_OFF : Int = 0
        public val STATUS_ON : Int = 1
        public val STATUS_TWINKLE : Int = 2
    }

}


