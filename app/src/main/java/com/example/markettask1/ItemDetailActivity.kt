package com.example.markettask1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.markettask1.databinding.ActivityItemDetailBinding

class ItemDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityItemDetailBinding.inflate(layoutInflater) }
    private val myItemsList = MyItemsList.getItemsList()
    private lateinit var items: ItemInfo

    var detailIsHeart: Boolean = false
    var detailHeartNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Intent로부터 데이터 가져오기
        val selected = intent.getParcelableExtra<ItemInfo>("selected")!!

        Toast.makeText(this, "$selected 받음", Toast.LENGTH_SHORT).show()

        // 데이터를 뷰에 적용
        items = myItemsList.searchItemByName(selected.thisName)!!

        binding.ivDetailItemImage.setImageResource(selected.thisIcon)
        binding.tvDetailSeller.text = selected.thisSeller
        binding.tvDetailLocation.text = selected.thisLocation
        binding.tvDetailTitle.text = selected.thisName
        binding.tvDetailInfo.text = selected.thisInfo
        binding.tvDetailPrice.text = selected.thisPrice

        if (selected.isHeart) {
            binding.ibBtnDetailHeart.setImageResource(R.drawable.filledheart)
        } else {
            binding.ibBtnDetailHeart.setImageResource(R.drawable.heart)
        }

        detailIsHeart = selected.isHeart
        detailHeartNum = selected.thisHeart

        binding.ivBtnBack.setOnClickListener {
            finish()
        }

        binding.ibBtnDetailHeart.setOnClickListener {
            //현재 하트 상태를 반전시키고
            selected.isHeart = !selected.isHeart
            //원본 데이터를 수정하고
            myItemsList.changeHeartStatus(items, selected.isHeart)
            //하트 이미지를 업데이트
            binding.ibBtnDetailHeart.setImageResource(selected.changeHeart)
        }
    }
}