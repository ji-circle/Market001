package com.example.markettask1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.markettask1.databinding.ActivityItemDetailBinding

class ItemDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityItemDetailBinding.inflate(layoutInflater) }
    private val myItemsList = MyItemsList.getItemsList()
    private lateinit var items : ItemInfo

    var detailIsHeart : Boolean = false
    var detailHeartNum : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        // Intent로부터 데이터 가져오기
        val selected = intent.getParcelableExtra<ItemInfo>("selected")!!

        Toast.makeText(this,"$selected 받음",Toast.LENGTH_SHORT).show()

        // 데이터를 뷰에 적용

        items = myItemsList.searchItemByName(selected.thisName)!!


        binding.ivDetailItemImage.setImageResource(selected.thisIcon ?: 0)
        binding.tvDetailSeller.text = selected.thisSeller ?: ""
        binding.tvDetailLocation.text = selected.thisLocation ?: ""
        binding.tvDetailTitle.text = selected.thisName ?: ""
        binding.tvDetailInfo.text = selected.thisInfo ?: ""
        binding.tvDetailPrice.text = selected.thisPrice ?: ""


        if(selected.isHeart){
            binding.ibBtnDetailHeart.setImageResource(R.drawable.filledheart)
        }else{
            binding.ibBtnDetailHeart.setImageResource(R.drawable.heart)
        }

        detailIsHeart = selected.isHeart
        detailHeartNum = selected.thisHeart

        binding.ivBtnBack.setOnClickListener {

            //returnIntent = Intent(this,MainActivity::class.java)

            finish()
        }

        binding.ibBtnDetailHeart.setOnClickListener {
            selected.isHeart = !selected.isHeart
            myItemsList.changeHeartStatus(items, selected.isHeart)
            binding.ibBtnDetailHeart.setImageResource(selected.changeHeart)
            //changeHeartNum(detailHeartNum, detailIsHeart)
//            Toast.makeText(this,"$detailHeartNum, ${selected.thisHeart},$detailIsHeart, ${selected.isHeart}", Toast.LENGTH_SHORT).show()
//            val myItemsList2 = MyItemsList.getItemsList()
//            val temp2 = myItemsList2.searchItemByName(selected.thisName)
//            Toast.makeText(this,"${temp2?.thisHeart}",Toast.LENGTH_SHORT).show()
        }



    }
    //하트가 눌러져있지 않던 상태에서 하트가 눌렸으면 +1, 눌러져 있었는데 다시 눌러 취소했다면 -1
//    val changeHeartNum get() = if(!isHeart) thisHeart + 1 else thisHeart -1
    fun changeHeartNum(_detailHeartNum: Int, _detailIsHeart:Boolean) {
        if (!_detailIsHeart) {
            detailHeartNum = _detailHeartNum + 1
            detailIsHeart = true
        } else {
            detailHeartNum = _detailHeartNum - 1
            detailIsHeart = false
        }
    }


}