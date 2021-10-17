package myapps.myportfolio.data

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
