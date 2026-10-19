package com.example.myapplication.data
import android.widget.Button
import android.widget.TextView

class dateholder (
    var view_type: String,
    var view_time:Int,         //* 作用：它純粹是一個「用來存資料的容器」。
    var view_money:Int,        //從資料庫撈出 Transaction 後，把裡面的文字或數字在這裡打包。
    var view_note:String,      //這裡的變數名稱跟 XML 的 ID 完全無關，自由命名。
    var buttomdelete: android.widget.Button?=null
)
class viewholder(){
    var view_type: TextView?=null
    var view_time: TextView? =null
    var view_money: TextView? =null
    var view_note: TextView? =null
    var buttomdelete:Button?=null
}