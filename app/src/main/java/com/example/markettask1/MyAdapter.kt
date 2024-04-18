package com.example.markettask1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.markettask1.databinding.ItemRecyclerviewBinding

//RecyclerView의 Adapter를 상속받음
//인자로 ItemInfo들의 List를 넣어줌(샘플데이터의 리스트들)
class MyAdapter(var mItems: MutableList<ItemInfo>) : RecyclerView.Adapter<MyAdapter.Holder>() {

    // 클릭이벤트를 MainActivity 에 넘기는 부분
    //ItemClick이라는 인터페이스 만들기
    // 실제로 함수는 onClick, 매개변수로 view랑 position이 들어가게...
    // 그리고 메인액티비티로 이동
    interface ItemClick {
        fun onClick(view : View, position : Int)
    }
    var itemClick : ItemClick? = null

    interface HeartClick{
        fun onClick(view:View, position:Int)
    }
    var heartClick: HeartClick?= null

    interface ItemLongClick{
        fun onLongClick(view: View, position: Int): Boolean
    }
    var itemLongClick: ItemLongClick ?= null

    //어댑터가 만들어지면서 viewHolder도 만들어짐
    // 홀더는 이런 식으로 만들거야 st... 홀더가 어떤 식으로 되어있는지는 아래 이너클래스에.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //Holder 만들어주기!!
        // 바인딩하고 inflate해서...
        val binding = ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Holder에 집어넣어주기. Hodlder를 생성하는 것!
        return Holder(binding)
    }

    //onBindViewHolder가 아이템 개수만큼 돌면서 mItems에 있던 샘플데이터가 하나하나 실행됨, 실행 결과에 보여줌
    // 화면에 보이는 부분까지만 실행됨! 나머지는 스크롤해서 보여질 때 보이게 됨

    // holder: Holder 안에 차례대로 들어오는 것.
    // position: Int 는 0번째부터 들어오게 됨
    override fun onBindViewHolder(holder: Holder, position: Int) {

        //클릭이벤트 추가하는 부분
        // 1. 이 안에서 인텐트로 다른 액티비티를 호출하는 방법
        // 2. 이 MyAdapter에서 클릭 이벤트를 받고, 이 이벤트 받은 것을 메인액티비티로 넘겨서
        //        실제로 클릭에 대한 이벤트 처리를 메인에서 하게 함
        //    그러려면 MyAdapter과 MainActivity 사이에 통신할 수 있는 인터페이스가 필요함
        //    -> 윗부분에 interface ItemClick 부분 보기

        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position)
        }
        holder.heart.setOnClickListener {
            heartClick?.onClick(holder.heart,position)
        }
        holder.itemView.setOnLongClickListener {
            itemLongClick?.onLongClick(it,position)?: false
        }

        holder.iconImageView.setImageResource(mItems[position].thisIcon.toInt())
        holder.title.text = mItems[position].thisName
        holder.location.text = mItems[position].thisLocation
        holder.price.text = mItems[position].thisPrice
        holder.chat.text = mItems[position].thisComment.toString()
        holder.like.text = mItems[position].thisHeart.toString()
        if(mItems[position].isHeart){
            holder.heart.setImageResource(R.drawable.filledheart)
        }else{
            holder.heart.setImageResource(R.drawable.heart)
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

    fun getItem(position: Int): ItemInfo{
        return mItems[position]
    }

    // ItemRecyclerviewBinding 은 item_recyclerview.xml 에 연결됨
    inner class Holder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val iconImageView = binding.iconItem
        val title = binding.tvTitle
        val location = binding.tvLocation
        val price = binding.tvPrice
        val chat = binding.tvChatnum
        val like = binding.tvHeartnum
        val heart = binding.ibBtnHeart

    }
}
