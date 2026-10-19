package com.example.myapplication.data
    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.PrimaryKey
    @Entity(tableName = "transaction")
    data class Transaction(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        @ColumnInfo(name="type") //支出或收入
        val type:String,
        @ColumnInfo(name="time")//存時間
        val time:Int,
        @ColumnInfo(name="money")//金額
        val money:Int,
        @ColumnInfo(name="note")//備註
        val note:String,
    )
