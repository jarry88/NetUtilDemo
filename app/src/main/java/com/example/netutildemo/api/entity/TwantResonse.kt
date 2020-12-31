package com.example.netutildemo.api.entity

import com.google.gson.annotations.SerializedName


data class TwantResponse<out T>(
    val code: Int, val errorMsg: String,
//        val datas: T?=null,
    @SerializedName("data",alternate = ["datas"])
    val data:T?=null,
    @SerializedName("msg",alternate = ["message"])
    val msg:String?=null)