package creeper_san.myshoes.event

import creeper_san.myshoes.helper.NetworkHelper

class NetworkEvent(private val url:String,private val handler: NetworkHelper.NetworkHandler){

    public fun getUrl():String{
        return url
    }

    public fun getHandler():NetworkHelper.NetworkHandler{
        return handler
    }

}
