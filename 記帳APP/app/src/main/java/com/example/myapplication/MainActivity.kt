package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.Adapter
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.TransactionDao
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var DB: AppDatabase// Room 資料庫總管
    lateinit var Dao: TransactionDao// 操作資料庫的 DAO 介面

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //呼叫我們在 AppDatabase.kt 裡面寫好的 getDatabase(this) 方法
        DB=AppDatabase.getDatabase(this)
        //從打開的資料庫裡，拿出來那把操作資料的鑰匙（Dao）
        Dao=DB.transactionDao()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonadd.setOnClickListener{
            // 建立 Intent，功能為「首頁」跳轉到「第二頁」
            val intent=Intent(this,MainActivity2::class.java)
            startActivity(intent)//執行Intent
        }
    }
    override fun onResume(){
        super.onResume()
        refreshList()// 每次回到首頁，自動重新整理，確保看到最新帳目
    }
    fun refreshList() {
        var totalexp=0
        var totalinc=0
        var bal=0
        //Coroutine，在後台撈資料，避免爆錯
        CoroutineScope(Dispatchers.Default).launch {
            val list=Dao.getAllItems()
            val dateArray = list.map { item ->
                com.example.myapplication.data.dateholder(//dateholder來自Adapter
                    view_type = item.type,//item來自56行,代表來自,type來自tr..ion.kt
                    view_time = item.time,//view_time來自holder
                    view_money = item.money,
                    view_note = item.note
                )
            }.toTypedArray()
            for (item in list) {    //逐一檢查撈出來的資料，計算總支出與總收入
                if (item.type == "支出") {
                    totalexp += item.money.toString().toInt()
                } else if (item.type == "收入") {
                    totalinc += item.money.toString().toInt()
                }
            bal=totalinc-totalexp
            }
            runOnUiThread { //規定只能在主程式改xml畫面，runOnUiThread回主程式
                // 實例化 Adapter，把剛剛57行打包好的 dateArray 陣列餵給它
                val adapter = Adapter(this@MainActivity, dateArray)
                // 把 Adapter 裝給 ListView，清單畫面就會自動渲染並顯示出來
                binding.ListView.adapter = adapter
                binding.textViewexp.text= totalexp.toString()
                binding.textViewinc.text=totalinc.toString()
                binding.textViewbal.text=bal.toString()
            }
        }
    }
}