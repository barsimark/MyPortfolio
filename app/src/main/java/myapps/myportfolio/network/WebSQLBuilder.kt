package myapps.myportfolio.network

import android.util.Log
import myapps.myportfolio.data.DataManager

class WebSQLBuilder {
    fun SQLNamePriceQuery(): String {
        val ownedShares = GetOwnedShareNames()
        return "\"SELECT symbol, price " +
                "FROM stocks " +
                "WHERE symbol in ($ownedShares)\""
    }

    private fun GetOwnedShareNames(): String{
        val shares = DataManager.shares
        var shareNames = ""
        for (i in 0..shares.size - 2) {
            val currentName = shares[i].name
            shareNames += "'$currentName', "
            Log.d("websql", "here")
        }
        val currentName = shares[shares.size - 1].name
        shareNames += "'$currentName'"
        return shareNames
    }
}