package creeper_san.myshoes.event

class LedResultEvent(private var result: Boolean,private var newStatus:Int){

    private fun getResult():Boolean{
        return result
    }

    private fun getNewStatus():Int{
        return newStatus
    }

}
