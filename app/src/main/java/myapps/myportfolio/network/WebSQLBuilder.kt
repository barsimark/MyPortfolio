package myapps.myportfolio.network

import myapps.myportfolio.data.DataManager
import myapps.myportfolio.data.Share

class WebSQLBuilder {
    fun SQLNamePriceQuery(): String {
        val ownedShares = GetOwnedShareNames()
        return "\"SELECT symbol, price " +
                "FROM stocks " +
                "WHERE symbol in ($ownedShares)\""
    }

    fun SQLNamePriceQuery(shares: List<Share>): String{
        val ownedShares = BuildShareNames(shares)
        return "\"SELECT symbol, price " +
                "FROM stocks " +
                "WHERE symbol in ($ownedShares)\""
    }

    private fun GetOwnedShareNames(): String{
        val shares = DataManager.shares
        return BuildShareNames(shares)
    }

    private fun BuildShareNames(shares: List<Share>): String{
        var shareNames = ""
        for (i in 0..shares.size - 2) {
            val currentName = shares[i].name
            shareNames += "'$currentName', "
        }
        val currentName = shares[shares.size - 1].name
        shareNames += "'$currentName'"
        return shareNames
    }
}