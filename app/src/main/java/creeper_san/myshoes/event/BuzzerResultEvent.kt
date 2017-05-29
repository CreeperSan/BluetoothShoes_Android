package creeper_san.myshoes.event

class BuzzerResultEvent(private var result:Boolean,private var newState:Boolean){

    public fun getResult():Boolean{
        return result
    }

    public fun getNewStatus():Boolean{
        return newState
    }

}

