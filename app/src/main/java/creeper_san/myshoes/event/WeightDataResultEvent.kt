package creeper_san.myshoes.event

import creeper_san.myshoes.MainWeightFragment

class WeightDataResultEvent(private val list:List<MainWeightFragment.Item>){

    public fun getList():List<MainWeightFragment.Item>{
        return list
    }

}
