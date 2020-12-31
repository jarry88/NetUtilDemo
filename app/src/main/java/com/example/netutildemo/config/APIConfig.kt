package com.example.netutildemo.config

enum class APIConfig(val baseUrl:String) {
    aF2("https://f2.twant.com"),//app 线上官网路径
    aF3("https://f3.twant.com"),
    aW3("https://www.twant.com");

    fun getApiUrl()=baseUrl.plus("/api")
    fun getWebUrl()=baseUrl.plus("/web")
    fun getProd()= this == aF2||this ==aF3||this ==aW3
    companion object{
        fun getOssBaseUrl( develop:Boolean)=if(develop) "https://ftofs-editor.oss-cn-shenzhen.aliyuncs.com"
        else "https://img.twant.com";
        fun getByOrdinal(ordinal:Int)=if(ordinal<0||ordinal>= values().size) aF2 else values()[ordinal]
    }
}