package com.example.markettask1

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemInfo(
    val thisNum: Int,
    val thisIcon: Int,
    val thisName: String,
    val thisInfo: String,
    val thisSeller: String,
    val thisPrice: String,
    val thisLocation: String,
    var thisHeart: Int,
    val thisComment: Int,
    var isHeart: Boolean = false
) : Parcelable {

    val changeHeart get() = if (isHeart) R.drawable.filledheart else R.drawable.heart

}