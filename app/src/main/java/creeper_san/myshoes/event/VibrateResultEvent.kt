package creeper_san.myshoes.event

class VibrateResultEvent{

    private var origin:Int = 0;
    private var result:Boolean = false

    constructor(result:Boolean){
        this.result = result
    }

    constructor(result:Boolean,origin:Int){
        this.result = result
        this.origin = origin
    }

    fun isSuccess():Boolean{
        return  result
    }
    fun getOrigin():Int{
        return origin
    }

}

