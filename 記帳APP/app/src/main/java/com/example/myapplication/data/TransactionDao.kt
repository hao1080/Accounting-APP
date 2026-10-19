package com.example.myapplication.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Dao
interface TransactionDao {
    // 查詢所有資料，並依照日期由新到舊排序 (DESC)
    @Query("SELECT * FROM 'transaction' ORDER BY time DESC")
    suspend fun getAllItems(): List<Transaction>
    @Insert(onConflict = OnConflictStrategy.IGNORE)// 新增一筆紀錄
    suspend fun insert(item: Transaction)
    @Update// 更新資料
    suspend fun update(item: Transaction)
    @Delete// 刪除資料
    suspend fun delete(item: Transaction)
}