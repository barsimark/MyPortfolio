package myapps.myportfolio.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Share")
data class Share (
    @PrimaryKey(autoGenerate = true)
    var uid: Long? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "number")
    var number: Double,
    @ColumnInfo(name = "value")
    var value: Double,
    @ColumnInfo(name = "buyprice")
    var buyprice: Double
) : Serializable
