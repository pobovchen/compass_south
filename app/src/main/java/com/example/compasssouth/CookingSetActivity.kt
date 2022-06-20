package com.example.compasssouth

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compasssouth.MainActivity.Companion.cooklinglist


class CookingSetActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView

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
        }
    }

    fun listviewadpterclickchange(){
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = GridLayoutManager(this,3)
        var list = MainActivity.readcookingUtils.readcookinglist(this)
        mRecyclerView.adapter = MainAdapter(list, this)



/*        listView.onItemClickListener =
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
            }*/
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

class MainAdapter(val items : MutableList<String>, var context: Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emoji_string: String = context.getResources().getString(R.string.emoji_wastebasket)
        holder.textView.text = emoji_string+items[position]
        holder.textView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to Delete "+ items[position] +"?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    // Delete selected note from database
                    Toast.makeText(context, "你刪除的是" + items[position], Toast.LENGTH_SHORT).show();
                    cooklinglist.remove(items[position])
                    notifyDataSetChanged()
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    class ViewHolder(val view: View) :  RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.item_title)
    }
}
