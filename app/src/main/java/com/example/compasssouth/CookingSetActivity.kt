package com.example.compasssouth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import com.example.compasssouth.MainActivity.Companion.cooklinglist


class CookingSetActivity : AppCompatActivity() {
    lateinit var listView: ListView
    var list: ArrayList<String> = ArrayList()
    lateinit var arrayAdapter: ArrayAdapter<String>

    lateinit var editText: EditText
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cooking_set)

        //檢查list
        /*val text = cooklinglist.toString()
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()*/

        listviewadpterclickchange()

        editText = findViewById(R.id.editText)
        button = findViewById(R.id.btnAdd)
        button.setOnClickListener {
            cooklinglist.add(editText.text.toString().trim())
            editText.setText("")
            arrayAdapter.notifyDataSetChanged()
            listView.adapter = arrayAdapter
        }
    }

    fun listviewadpterclickchange(){
        listView = findViewById(R.id.listView)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cooklinglist)
        arrayAdapter.notifyDataSetChanged()
        listView.adapter = arrayAdapter

        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to Delete "+ cooklinglist[position] +"?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        // Delete selected note from database
                        Toast.makeText(getApplicationContext(), "你刪除的是" + cooklinglist[position], Toast.LENGTH_SHORT).show();
                        cooklinglist.remove(cooklinglist[position])
                        arrayAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_compass -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("18key", "18value")
                }
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun savecookinglist(){
        val pref = getSharedPreferences("cookinglist", MODE_PRIVATE)
        pref.edit()
            .putStringSet("cookinglist", cooklinglist.toSet())
            .apply()
    }

    override fun onPause() {
        super.onPause()
        savecookinglist()
    }

}