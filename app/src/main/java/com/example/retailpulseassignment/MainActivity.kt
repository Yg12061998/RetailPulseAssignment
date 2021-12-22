package com.example.retailpulseassignment

import android.R.id.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retailpulseassignment.model.Store
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {
    val TAG = "MAIN_ACTIVITY"
    val databaseURL = "https://retail-pulse-assignment-532d0-default-rtdb.firebaseio.com/"

    lateinit var sharedPref: SharedPreferences
    lateinit var userName: String
    lateinit var database: DatabaseReference
    lateinit var lvShops: RecyclerView
    lateinit var search: TextInputLayout
    lateinit var btnLogOut:Button

    var filteredList = mutableListOf<Store>()
    var layoutData = mutableListOf<Store>()
    var storesData = mutableListOf<Store>()
    lateinit var adapter : StoreListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "onCreate Started")
        database = Firebase.database(databaseURL).reference
        sharedPref = getSharedPreferences(getString(R.string.sharedPrefUserData), MODE_PRIVATE)
        userName = sharedPref.getString("user", "NEW USER").toString()


        search = findViewById(R.id.search)
        btnLogOut = findViewById(R.id.btnLogOut)
        lvShops = findViewById(R.id.lvShops)
        adapter = StoreListAdapter(filteredList, this@MainActivity)

        lvShops.adapter = adapter
        lvShops.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this@MainActivity)
        lvShops.layoutManager = layoutManager

        btnLogOut.setOnClickListener {
            val preferences = getSharedPreferences(getString(R.string.sharedPrefUserData), MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        getStoreIds()
        startFilter()


    }

    private fun startFilter() {


        search.editText?.doAfterTextChanged{ it ->

            if (it.toString().isNotEmpty()) {
                lvShops.scrollToPosition(0)
                filteredList.clear()

                val searched = convert(it.toString())

                for (store in storesData) {
                    val name = store.name.lowercase()

                    if (name.contains( searched, true)) {
                        Log.d(TAG, name + " : "+ searched)
                        filteredList.add(store)
                    }
                }
                adapter.notifyDataSetChanged()

            } else {
                lvShops.smoothScrollToPosition(0)
                filteredList.clear()
                filteredList.addAll(storesData)
                adapter.notifyDataSetChanged()
            }
        }

    }

    fun convert(s:CharSequence): CharSequence {
        return s
    }

    private fun getStoreIds() {
        database.child("users").orderByChild("name").equalTo(userName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Log.d(TAG, snapshot.toString())
                    val children = snapshot.children
                    val userAvailableStoreLists = mutableListOf<String>()

                    children.forEach {

                        val child = it.child("stores").children
                        for (store in child) {
                            val data = store.value
                            userAvailableStoreLists.add(data.toString())
                        }
                        getStoresData(userAvailableStoreLists)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message)
                }
            })
    }

    private fun getStoresData(list: MutableList<String>) {
        var count = 1
        for (storeId in list) {
            database.child("stores").child(storeId).get().addOnSuccessListener {
                count++;

                val area = it.child("area").value.toString()
                val address = it.child("address").value.toString()
                val route = it.child("route").value.toString()
                val name = it.child("name").value.toString()
                val type = it.child("type").value.toString()

                storesData.add(Store(storeId, address, area, name, route, type))


                if(count == list.size ){
                    storesData.sortBy { it.name }

                    filteredList.addAll(storesData)
                    adapter.notifyDataSetChanged()
                }
            }.addOnFailureListener {
                count++;

                if(count == list.size ){
                    storesData.sortBy { it.name }

                    filteredList.addAll(storesData)
                    adapter.notifyDataSetChanged()
                }

            }
        }



    }

}