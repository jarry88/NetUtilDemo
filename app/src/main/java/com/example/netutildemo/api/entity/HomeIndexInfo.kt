package com.example.netutildemo.api.entity

import com.google.gson.annotations.SerializedName

data class HomeIndexInfo(
    val appIndexNavigationImage: String,
    val appIndexNavigationLinkType: String,
    val appIndexNavigationLinkValue: String,
    val appPopupAdImage: String,
    val appPopupAdLinkType: String,
    val appPopupAdLinkValue: String,
    val enableAppIndexNavigation: String,
    val enableAppPopupAd: String,
    val enableEveryTimeAppPopupAd: String,
    val goodsCommonCount: Int,
    val imSessionCount: Int,
    val memberCount: Int,
    val storeCount: Int,
    val wantPostCount: Int,
//    @SerializedName("webSliderItem",alternate = ["bannerList"])
//    val webSliderItem: List<WebSliderItem>
)