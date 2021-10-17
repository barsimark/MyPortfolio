package myapps.myportfolio.database

import androidx.room.*
import myapps.myportfolio.data.Share

@Dao
interface AssetDao {
    @Query("SELECT * FROM Share")
    fun getAllShares(): List<Share>

    @Insert
    fun insertShare(share: Share): Long

    @Update
    fun updateShare(share: Share)

    @Delete
    fun deleteShare(share: Share)
}