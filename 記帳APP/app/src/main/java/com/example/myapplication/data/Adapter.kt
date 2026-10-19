package com.example.myapplication.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Adapter (ct: Context,data:Array<dateholder>):
        ArrayAdapter<dateholder>(ct, R.layout.list_item,data) {
    val dataArray = data
    val Ct = ct

    override fun getCount(): Int {
        return dataArray.size
    }

    override fun getItem(position: Int): dateholder? {
        return dataArray[position]
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        var item = dataArray[position]
        var holder = viewholder()

        if (convertView == null) {
            view = LayoutInflater.from(Ct).inflate(R.layout.list_item, parent, false)
            holder.view_type = view.findViewById<TextView>(R.id.texttype)
            holder.view_time = view.findViewById<TextView>(R.id.texttime)
            holder.view_money = view.findViewById<TextView>(R.id.textmoney)
            holder.view_note = view.findViewById<TextView>(R.id.textnote)
            holder.buttomdelete=view.findViewById<Button>(R.id.btdelete)
            view.tag = holder
        } else
            holder = view?.tag as viewholder


        var stringtime=item.view_time.toString().take(4)+"/"+item.view_time.toString().drop(4).dropLast(2)+"/"+item.view_time.toString().takeLast(2)
        holder.view_time?.text = stringtime
        holder.view_type?.text = item.view_type
        holder.view_money?.text =
            if (item.view_type == "支出") "$-" + item.view_money.toString() else "$" + item.view_money.toString()
        holder.view_note?.text = item.view_note
        holder.buttomdelete?.setOnClickListener{
            val mainActivity= Ct as MainActivity
            CoroutineScope(Dispatchers.Default).launch {
                val deletelist=mainActivity.Dao.getAllItems()
                val listitem=deletelist[position]
                mainActivity.Dao.delete(listitem)
                mainActivity.runOnUiThread{
                    mainActivity.refreshList()
                }
            }
        }
        return view!!


    }
    }
