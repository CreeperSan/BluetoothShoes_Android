package creeper_san.myshoes.event


class MissingResultEvent(private val status:Boolean,private val isConnect:Boolean){

    public fun getStatus():Boolean{
        return status
    }

    public fun isConnected():Boolean{
        return isConnect
    }

}

