package creeper_san.myshoes.event


class MissingEvent(private var isCommand:Boolean,private var newStatus:Boolean){

    public fun isCommand():Boolean{
        return isCommand
    }

    public fun newStatus():Boolean{
        return newStatus
    }

}

