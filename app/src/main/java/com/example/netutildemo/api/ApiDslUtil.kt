@file:Suppress("NAME_SHADOWING")

package com.example.netutildemo.api

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.netutildemo.api.entity.TwantResponse
import com.example.netutildemo.util.SLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.lxj.xpopup.core.BasePopupView
import okhttp3.Call
import java.io.IOException

/**
 * 请求接口数据的样板代码
 */
inline fun <reified T> View.requestData(path: String = "", params: Any? = null, loading: BasePopupView? = null, type: MethodType = MethodType.GET, crossinline onFailure: (IOException?) -> Unit = {}, crossinline handleResponseStr: (String) -> Unit = {}, crossinline succss: (T) -> Unit){
    api(context, path, params, loading, type,  onFailure, handleResponseStr, succss)
}
inline fun <reified T> Fragment.requestData(path: String = "", params: Any? = null, loading: BasePopupView? = null, type: MethodType = MethodType.GET, crossinline onFailure: (IOException?) -> Unit = {}, crossinline handleResponseStr: (String) -> Unit = {}, crossinline succss: (T) -> Unit){
    api(requireContext(), path, params, loading, type,  onFailure, handleResponseStr, succss)
}
inline fun <reified T> api(context: Context, path: String = "", params: Any? = null, loading: BasePopupView? = null, type: MethodType = MethodType.GET, crossinline onFailure: (IOException?) -> Unit = {}, crossinline handleResponseStr: (String) -> Unit = {}, crossinline succss: (T) -> Unit){
    SLog.info( params?.toString())
    val uiCallback=object : UICallback(){
        override fun onFailure(call: Call?, e: IOException?) {
//            ToastUtil.showNetworkError(context, e)
            loading?.dismiss()
            onFailure(e)
        }

        override fun onResponse(call: Call?, responseStr: String?) {
            loading?.dismiss()
            SLog.info("responseStr[%s]", responseStr)
            val responseObj = Gson().toJson(responseStr)
            //检查是否401 token失效 退出
//            if (ToastUtil.checkError(context, responseObj)) {
//                return
//            }
            responseStr?.let {
                //直接解析字符串数据
                handleResponseStr(it)


                getTwantData<T?>(it)?.apply {
                    data?.let {
                        succss(it)
                    } ?: SLog.info("$type --> 获取数据为空")
                }
            }
        }
    }

    loading?.show()
    try {
        when(type){
            MethodType.GET -> Api.getUI(path, params as? JsonObject, uiCallback)
            MethodType.POST -> Api.postUI(path, params as? JsonObject, uiCallback)
            MethodType.PUT -> Api.putJsonUi(path, params as? String, uiCallback)
            MethodType.DELETE -> Api.deleteUI(path, params as? JsonObject, uiCallback)
        }
    }catch (e: Exception){
        loading?.dismiss()
        SLog.info("Error!message[%s], trace[%s]", e.message, Log.getStackTraceString(e));
    }

}
inline fun <reified T> genericType() = object: TypeToken< T >() {}.type
inline fun <reified T> getTwantData(json:String) = Gson().fromJson<TwantResponse<T>>(json, genericType<TwantResponse<T>>())