package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Transaction
import com.example.myapplication.data.TransactionDao
import com.example.myapplication.databinding.ActivityMain2Binding
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    var usedate=true
    var expenditure=true
    lateinit var DB:AppDatabase//只用在134行
    lateinit var Dao:TransactionDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.checkBox.setOnClickListener{//改支出收入
            binding.checkBox.isChecked=true
            binding.checkBox2.isChecked=false
            binding.main.setBackgroundColor(Color.parseColor("#22E97171"))
            expenditure=true
        }
        binding.checkBox2.setOnClickListener{
            binding.checkBox2.isChecked=true
            binding.checkBox.isChecked=false
            binding.main.setBackgroundColor(Color.parseColor("#32359EB5"))
            expenditure=false
        }//改支出收入
        binding.editTextText.showSoftInputOnFocus=false//欄位點擊時不跳出系統鍵盤
        binding.editTextDate.showSoftInputOnFocus=false
        // 監聽焦點切換：點擊金額欄位時，把 usedate 設為 false (代表現在鍵盤要打金額)
        binding.editTextText.setOnFocusChangeListener { v, hasFocus ->  if (hasFocus){ usedate = false }}
        // 監聽焦點切換：點擊日期欄位時，把 usedate 設為 true (代表現在鍵盤要打日期)
        binding.editTextDate.setOnFocusChangeListener { v, hasFocus -> if (hasFocus){ usedate = true } }
        var a=false//加上/，57~75不太重要
        binding.editTextDate.doOnTextChanged { text, start, before, count ->
            val Text = text.toString()
            if ((Text.length==5||Text.length==8)&&(a==false)){a=true}
            if ((Text.length == 4 ||Text.length==7)&&(a==true)) {
                val resultText = binding.editTextDate.text.toString().dropLast(1)
                binding.editTextDate.setText(resultText)
                binding.editTextDate.setSelection(resultText.length)
            }
            else if ((Text.length == 4 ||Text.length==7)&&(a==false)) {
                val resultText = binding.editTextDate.text.toString() + "/"
                binding.editTextDate.setText(resultText)
                binding.editTextDate.setSelection(resultText.length)
            }
            if(Text.length == 3 ||Text.length==6){
                a=false
            }
            binding.editTextDate.showSoftInputOnFocus=false
        }

        binding.button0.setOnClickListener{nownum("0")}//對照155行
        binding.button1.setOnClickListener{nownum("1")}
        binding.button2.setOnClickListener{nownum("2")}
        binding.button3.setOnClickListener{nownum("3")}
        binding.button4.setOnClickListener{nownum("4")}
        binding.button5.setOnClickListener{nownum("5")}
        binding.button6.setOnClickListener{nownum("6")}
        binding.button7.setOnClickListener{nownum("7")}
        binding.button8.setOnClickListener{nownum("8")}
        binding.button9.setOnClickListener{nownum("9")}
        binding.button.setOnClickListener{  //倒退鍵不太重要
            if(usedate==true){
                if(binding.editTextDate.text.toString()!="0"){
                    binding.editTextDate.setText(binding.editTextDate.text.toString().dropLast(1))
                    if(binding.editTextDate.text.toString()=="")
                        binding.editTextDate.setText("0")
                }
            }
            else
            {
                if(binding.editTextText.text.toString()!="0"){
                    binding.editTextText.setText(binding.editTextText.text.toString().dropLast(1))
                    if(binding.editTextText.text.toString()=="")
                        binding.editTextText.setText("0")
                }
            }
        }
        binding.buttondelete.setOnClickListener{
            finish()// 點擊「取消」按鈕，直接關閉這一頁，沒有儲存
        }
        binding.buttonOK.setOnClickListener{  //按下OK，108~127是防呆檢查，剩下才是重點寫入資料庫
            if(binding.editTextText.text.toString()==""){binding.editTextText.setText("0")}
            if(binding.editTextText.text.toString().toIntOrNull()==0)
            {
                Toast.makeText(this,"請輸入金額",Toast.LENGTH_LONG).show()
                binding.editTextText.error="請輸入金額"
                return@setOnClickListener// 因為有錯所以必須提前中斷
            }
            if(binding.editTextText2.text.toString()=="")
            {
                Toast.makeText(this,"請輸入備註",Toast.LENGTH_LONG).show()
                binding.editTextText2.error="請輸入備註"
                return@setOnClickListener
            }
            binding.editTextDate.setText(binding.editTextDate.text.toString().replace("/",""))
            if(binding.editTextDate.length()!=8)
            {
                Toast.makeText(this,"請輸入正確日期(YYYY/MM/DD)",Toast.LENGTH_LONG).show()
                binding.editTextDate.error="請輸入正確日期(YYYY/MM/DD)"
                return@setOnClickListener
            }
            val newTransaction = Transaction(//將空白資料打包成資料庫結構定義的Transaction物件
                type = if (expenditure == true) "支出" else "收入",
                time = binding.editTextDate.text.toString().toInt(),
                money = binding.editTextText.text.toString().toInt(),
                note = binding.editTextText2.text.toString()
            )
            //開啟資料庫總管，.apply大括號裡面是跟 DB 相關的操作
            DB = AppDatabase.getDatabase(this).apply {//DB、Dao看26、27
                //.apply裡，this就代表DB。這行代表從DB裡面取得操作資料的Dao
                Dao = this.transactionDao()
                //Coroutine，在後台撈資料，避免爆錯
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        Dao.insert(newTransaction)//把資料塞進資料庫
                        runOnUiThread{finish()}//規定只能在主程式改xml畫面，這段回主程式
                    } catch (e: Exception/*如果有例外的錯誤*/) {
                        showMsg("出錯了：${e.message}")//呼叫下方的自訂函式彈出提示
                    }
                }
            }
        }
    }
    fun showMsg(msg:String){//18-20
        runOnUiThread{// 安全地在前台把黑底白字的小提示（Toast）畫在螢幕上給使用者看
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
        }
    }
    fun nownum(num:String) {
        val noweditText=if(usedate==true) binding.editTextDate else binding.editTextText
        var textnum=noweditText.text.toString()
        if (textnum == "0") {
            noweditText.setText(num)
        } else {
            noweditText.setText(noweditText.text.toString()+num)
        }
    }

}