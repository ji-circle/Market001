package com.example.markettask1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.markettask1.databinding.ItemRecyclerviewBinding

//RecyclerView의 Adapter를 상속받음
class MyAdapter(var mItems: MutableList<ItemInfo>) : RecyclerView.Adapter<MyAdapter.Holder>() {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    interface HeartClick {
        fun onClick(view: View, position: Int)
    }

    var heartClick: HeartClick? = null

    interface ItemLongClick {
        fun onLongClick(view: View, position: Int): Boolean
    }

    var itemLongClick: ItemLongClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding =
            ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Holder에 집어넣어주기. Hodlder를 생성하는 것!
        return Holder(binding)
    }

    // holder: Holder 안에 차례대로 들어오는 것.
    // position: Int 는 0번째부터 들어오게 됨
    override fun onBindViewHolder(holder: Holder, position: Int) {

        //클릭이벤트 추가하는 부분
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }
        holder.heart.setOnClickListener {
            heartClick?.onClick(holder.heart, position)
        }
        holder.itemView.setOnLongClickListener {
            itemLongClick?.onLongClick(it, position) ?: false
        }

        holder.iconImageView.setImageResource(mItems[position].thisIcon.toInt())
        holder.title.text = mItems[position].thisName
        holder.location.text = mItems[position].thisLocation
        holder.price.text = mItems[position].thisPrice
        holder.chat.text = mItems[position].thisComment.toString()
        holder.like.text = mItems[position].thisHeart.toString()
        if (mItems[position].isHeart) {
            holder.heart.setImageResource(R.drawable.filledheart)
            holder.heart.clearColorFilter()
        } else {
            holder.heart.setImageResource(R.drawable.heart)
            val grayColor = holder.heart.context.resources.getColor(R.color.gray)
            //처음 색이 조금 튀어서... 회색으로 색을 바꿔줌
            // 근데 위의 아이콘은 붉은색이라 동적으로 바꿔주게 됨
            holder.heart.setColorFilter(grayColor)
        }
    }

    //오버라이드
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //오버라이드
    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getItem(position: Int): ItemInfo {
        return mItems[position]
    }

    // ItemRecyclerviewBinding 은 item_recyclerview.xml 에 연결됨
    inner class Holder(val binding: ItemRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val iconImageView = binding.iconItem
        val title = binding.tvTitle
        val location = binding.tvLocation
        val price = binding.tvPrice
        val chat = binding.tvChatnum
        val like = binding.tvHeartnum
        val heart = binding.ibBtnHeart
    }
}