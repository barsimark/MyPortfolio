package myapps.myportfolio.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import myapps.myportfolio.data.Share

@Dao
interface AssetDao {
    @Query("SELECT * FROM Share")
    fun getAllShares(): List<Share>

    @Insert
    fun insertShare(share: Share): Long

    @Delete
    fun deleteShare(share: Share)
}