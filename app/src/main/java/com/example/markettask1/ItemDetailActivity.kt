package com.example.markettask1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.markettask1.databinding.ActivityItemDetailBinding
import com.google.android.material.snackbar.Snackbar

class ItemDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityItemDetailBinding.inflate(layoutInflater) }
    private val myItemsList = MyItemsList.getItemsList()
    private lateinit var items: ItemInfo

    private var detailIsHeart: Boolean = false
    private var detailHeartNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Intent로부터 데이터 가져오기
        val selected = intent.getParcelableExtra<ItemInfo>("selected")!!

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

        //< 버튼 누른 경우
        binding.ivBtnBack.setOnClickListener {
            finish()
        }

        //하트 버튼 누른 경우
        binding.ibBtnDetailHeart.setOnClickListener {
            //현재 하트 상태를 반전시키고
            selected.isHeart = !selected.isHeart
            //원본 데이터를 수정하고
            myItemsList.changeHeartStatus(items, selected.isHeart)
            //하트 이미지를 업데이트
            binding.ibBtnDetailHeart.setImageResource(selected.changeHeart)
            showSnackbar(selected.isHeart)
        }
    }

    private fun showSnackbar(_isHeart: Boolean) {
        val rootView = binding.root
        if (_isHeart) {
            Snackbar.make(rootView, "관심 목록에 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
        }else{
            //
        }
    }
}