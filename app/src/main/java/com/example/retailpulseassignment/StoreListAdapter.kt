package com.example.retailpulseassignment

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retailpulseassignment.databinding.ListviewSingleItemBinding
import com.example.retailpulseassignment.model.Store
import java.io.Serializable

private const val TAG = "HotGamesWinnerListAdapter"

class StoreListAdapter(private val storeList: MutableList<Store>, val context: Context?) :
    RecyclerView.Adapter<StoreListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListviewSingleItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListviewSingleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val store = storeList[position]

        holder.binding.name.text = store.name
        holder.binding.address.text = store.address
        holder.binding.area.text = store.area

        holder.itemView.setOnClickListener {

            val intent = Intent(context, StoreImageUploadActivity::class.java)
            intent.putExtra("store", store as Serializable)
            context?.startActivity(intent)

        }

    }

    override fun getItemCount() = storeList.size

}