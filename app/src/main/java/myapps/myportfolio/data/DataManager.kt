package myapps.myportfolio.data

import myapps.myportfolio.network.WebShareMinimal

object DataManager {
    var shares = mutableListOf<Share>()

    fun addShare(share: Share): Share?{
        for (s in shares){
            if (s.name == share.name){
                s.number += share.number
                s.buyValue += share.number * share.price
                s.price = share.price
                return s
            }
        }
        shares.add(share)

        return null
    }

    fun updateSharePrice(webshare: WebShareMinimal): Share?{
        for (s in shares){
            if (s.name == webshare.symbol) {
                s.price = webshare.price
                return s
            }
        }
        return null
    }

    fun sellShare(share: Share): Share?{
        for (s in shares){
            if (s.name == share.name){
                s.number -= share.number
                s.buyValue -= (s.buyValue / s.number) * share.number
                s.price = share.price
                return s
            }
        }

        return null
    }
}
