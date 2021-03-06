package com.example.ch13

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ch13.databinding.ItemRecyclerviewBinding

class MyViewHolder(val binding:ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){

}
class MyAdapter(val datas: MutableList<String>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun getItemCount(): Int {
        return datas?.size ?: 0 // datas.size가 0일때 0 반환
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false ))

    // 뷰 홀더와 데이터의 바인딩 과정
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        val binding = (holder as MyViewHolder).binding
        binding.itemTodo.text = datas!![position] //null 불허용
    }




}