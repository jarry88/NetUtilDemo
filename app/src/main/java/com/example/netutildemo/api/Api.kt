package com.example.netutildemo.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Pair
import com.example.netutildemo.User
import com.example.netutildemo.config.APIConfig
import com.example.netutildemo.util.SLog
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * Api接口
 * @author gzp
 */
object Api {
    val STREAM = "application/octet-stream".toMediaTypeOrNull() // （ 二进制流
    val JSON = "application/json; charset=utf-8".toMediaTypeOrNull() // JSON字符串
    const val BUFFER_SIZE = 4096
    var API_BASE_URL=APIConfig.aF2.getApiUrl()

    /**
     * 發送Http請求
     * 如果ioCallback和uiCallback同時為null，表示同步方式執行
     * @param method GET或者POST
     * @param path URL的路徑
     * @param params 請求參數(可以為null)
     * @param ioCallback 在IO線程中執行的回調(可以為null)
     * @param uiCallback 在UI線程中執行的回調(可以為null)
     * @return 如果以異步方式執行，固定返回null；如果以同步方式執行，返回結果字符串
     */
    private fun httpRequest(
        method: MethodType, path: String, params: JsonObject?,
        ioCallback: Callback?, uiCallback: UICallback?
    ): String? {
        val client = okHttpClient
        var url: String
        url =
            API_BASE_URL+ path
        var request: Request? = null
        var token: String = ""//User.getToken
        if (token == null) {
            token = ""
        }
        SLog.info("${method.name} url[$url], Authorization[$token]")
        when(method){
            MethodType.GET ->{
                // 如果有其他get参数，拼接到url中
                if (params != null) {
                    url += makeQueryString(params)
                }
                request = Request.Builder()
                    .url(url)
                    .header("Authorization", token)
                    .build()
            }
            MethodType.POST->{
                val builder = FormBody.Builder()

                // 如果有其他post参数，也拼装起来
                params?.entrySet()?.forEach {
                    builder.add(it.key, it.value.toString())
                }
                val formBody: RequestBody = builder.build()
                Request.Builder()
                        .url(url)
                        .post(formBody)
                        .header("Authorization", token)
                        .build()
            }
            MethodType.PUT->{
                val builder = FormBody.Builder()

                // 如果有其他post参数，也拼装起来
                params?.entrySet()?.forEach {
                    builder.add(it.key, it.value.toString())
                }
                val formBody: RequestBody = builder.build()
                Request.Builder()
                        .url(url)
                        .put(formBody)
                        .header("Authorization", token)
                        .build()
            }
            MethodType.DELETE->{
                val builder = FormBody.Builder()

                // 如果有其他post参数，也拼装起来
                params?.entrySet()?.forEach {
                    builder.add(it.key, it.value.toString())
                }
                val formBody: RequestBody = builder.build()
                Request.Builder()
                        .url(url)
                        .delete(formBody)
                        .header("Authorization", token)
                        .build()
            }

        }
        if (uiCallback != null) {
            // 在UI線程中執行回調
            client.newCall(request!!).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val handler = Handler()
                    uiCallback.setOnFailure(call, e)
                    handler.post(uiCallback)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val statusCode = response.code
                     SLog.info("statusCode[%d]", statusCode);
                    val handler = Handler(Looper.getMainLooper())
                    uiCallback.setOnResponse(call, response.body!!.string())
                    handler.post(uiCallback)
                }
            })
        } else if (ioCallback != null) {
            // 在IO線程中執行回調
            client.newCall(request!!).enqueue(ioCallback)
        } else {
            // 同步方式執行
            try {
                val response = client.newCall(request!!).execute()
                return response.body!!.string()
            } catch (e: Exception) {
                SLog.info("Error!message[%s], trace[%s]", e.message, Log.getStackTraceString(e))
            }
        }
        return null
    }

    private fun postInternal(
        path: String,
        params: JsonObject?,
        ioCallback: Callback?,
        uiCallback: UICallback?
    ): String? {
        return httpRequest(MethodType.POST, path, params, ioCallback, uiCallback)
    }

    /**
     * POST請求，回調在UI線程中執行
     * @param path
     * @param params
     * @param uiCallback
     */
    fun postUI(path: String, params: JsonObject?, uiCallback: UICallback?) {
        postInternal(path, params, null, uiCallback)
    }

    /**
     * POST請求，回調在IO線程中執行
     * @param path
     * @param params
     * @param ioCallback
     */
    fun postIO(path: String, params: JsonObject?, ioCallback: Callback?) {
        postInternal(path, params, ioCallback, null)
    }

    /**
     * 同步POST
     * @param path
     * @param params
     * @return
     */
    fun syncPost(path: String, params: JsonObject): String? {
        return postInternal(path, params, null, null)
    }

    private fun getInternal(
        path: String,
        params: JsonObject?,
        ioCallback: Callback?,
        uiCallback: UICallback?
    ): String? {
        return httpRequest(MethodType.GET, path, params, ioCallback, uiCallback)
    }

    /**
     * GET請求，回調在UI線程中執行
     * @param path
     * @param params
     * @param uiCallback
     */
    fun getUI(path: String, params: JsonObject?, uiCallback: UICallback?) {
        getInternal(path, params, null, uiCallback)
    }

    /**
     * GET請求，回調在IO線程中執行
     * @param path
     * @param params
     * @param ioCallback
     */
    fun getIO(path: String, params: JsonObject?, ioCallback: Callback?) {
        getInternal(path, params, ioCallback, null)
    }

    /**
     * 同步GET
     * @param path
     * @param params
     * @return
     */
    fun syncGet(path: String, params: JsonObject?): String? {
        return getInternal(path, params, null, null)
    }

    /**
     * PUT 方法的內部實現
     * @param path
     * @param params
     * @param ioCallback
     * @param uiCallback
     * @return
     */
    private fun putInternal(
        path: String,
        params: JsonObject,
        ioCallback: Callback,
        uiCallback: UICallback
    ): String? {
        return httpRequest(MethodType.PUT, path, params, ioCallback, uiCallback)
    }

    /**
     * 以PUT方式提交 JSON,回調在UI線程中執行
     * @param path
     * @param json
     * @param uiCallback
     */
    fun putJsonUi(path: String, json: String?, uiCallback: UICallback?) {
        jsonRequest(MethodType.PUT, path, json, null, uiCallback)
    }
    //todo 同步下載文件
    /**
     * DELETE 方法的內部實現
     * @param path
     * @param params
     * @param ioCallback
     * @param uiCallback
     * @return
     */
    private fun deleteInternal(
        path: String,
        params: JsonObject?,
        ioCallback: Callback?,
        uiCallback: UICallback
    ): String? {
        return httpRequest(MethodType.DELETE, path, params, ioCallback, uiCallback)
    }

    /**
     * DELETE請求，回調在UI線程中執行
     * @param path
     * @param params
     * @param uiCallback
     */
    fun deleteUI(path: String, params: JsonObject?, uiCallback: UICallback) {
        deleteInternal(path, params, null, uiCallback)
    }// 1. 首先獲取captcha key


    /**
     * 生成查詢參數字符串，例如:   ?param1=1111&param2=2222
     * @param params
     */
    fun makeQueryString(params: JsonObject?): String {
        val queryString = StringBuilder("?")
        // 是否為第1個參數對
        var isFirst = true
        params?.entrySet()?.forEach {
            if (!isFirst) {
                queryString.append("&")
            }
            queryString.append("${it.key}=${it.value}")
            isFirst = false
        }
        return queryString.toString()
    }


    /**
     * 同步POST JSON
     * @param path
     * @param json
     * @return
     * @throws IOException
     */
    fun syncPostJson(path: String, json: String?): String? {
        return jsonRequest(MethodType.POST, path, json, null, null)
    }

    /**
     * 以POST方式提交 JSON,回調在UI線程中執行
     * @param path
     * @param json
     * @param uiCallback
     */
    fun postJsonUi(path: String, json: String?, uiCallback: UICallback?) {
        jsonRequest(MethodType.POST, path, json, null, uiCallback)
    }

    /**
     * 以POST方式提交 JSON,回調在UI線程中執行
     * @param path
     * @param json
     * @param ioCallback
     */
    fun postJsonIo(path: String, json: String?, ioCallback: Callback?) {
        jsonRequest(MethodType.POST, path, json, ioCallback, null)
    }

    /**
     * 以【POST】方式提交JSON字符串
     * 如果ioCallback和uiCallback同時為null，表示同步方式執行
     * @param method
     * @param path URL的路徑
     * @param json 提交給服務器的json字符串
     * @param ioCallback 在IO線程中執行的回調(可以為null)
     * @param uiCallback 在UI線程中執行的回調(可以為null)
     * @return 如果以異步方式執行，固定返回null；如果以同步方式執行，返回結果字符串
     */
    fun jsonRequest(
        method: MethodType,
        path: String,
        json: String?,
        ioCallback: Callback?,
        uiCallback: UICallback?
    ): String? {
        val url: String
        url =  API_BASE_URL.toString() + path
        var token: String = User.getToken()
        if (token == null) {
            token = ""
        }
        val client = okHttpClient
        val body = RequestBody.create(JSON, json!!)
        val request=when(method){
            MethodType.POST->Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", token)
                .build()
            MethodType.PUT->Request.Builder()
                .url(url)
                .put(body)
                .header("Authorization", token)
                .build()
            MethodType.DELETE->Request.Builder()
                .url(url)
                .delete(body)
                .header("Authorization", token)
                .build()
            else->null
        }
       request?.let {
           if (uiCallback != null) {
               // 在UI線程中執行回調
               client.newCall(request).enqueue(object : Callback {
                   override fun onFailure(call: Call, e: IOException) {
                       val handler = Handler(Looper.getMainLooper())
                       uiCallback.setOnFailure(call, e)
                       handler.post(uiCallback)
                   }

                   @Throws(IOException::class)
                   override fun onResponse(call: Call, response: Response) {
                       val statusCode = response.code
                       SLog.info("statusCode[%d]", statusCode)
                       val handler = Handler(Looper.getMainLooper())
                       uiCallback.setOnResponse(call, response.body!!.string())
                       handler.post(uiCallback)
                   }
               })
           } else if (ioCallback != null) {
               // 在IO線程中執行回調
               client.newCall(request).enqueue(ioCallback)
           } else {
               // 同步方式執行
               try {
                   val response = client.newCall(request).execute()
                   return response.body!!.string()
               } catch (e: Exception) {
                   SLog.info("Error!message[%s], trace[%s]", e.message, Log.getStackTraceString(e))
               }
           }
       }
        return null
    }


    // 如果是生產模式，則使用正常的OkHttpClient
    // SLog.info("如果是開發模式，設置OkHttpClient忽略ssl驗證");
    // 如果是開發模式，設置OkHttpClient忽略ssl驗證
    val okHttpClient: OkHttpClient
        get() {
            // 如果是生產模式，則使用正常的OkHttpClient
//            if (!Config.DEVELOPER_MODE) {
//                return OkHttpClient()
//            }
//            // SLog.info("如果是開發模式，設置OkHttpClient忽略ssl驗證");
            // 如果是開發模式，設置OkHttpClient忽略ssl驗證
            val xtm: X509TrustManager = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return Array(0){null}
                }
            }
            var sslContext: SSLContext? = null
            try {
                sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, arrayOf<TrustManager>(xtm), SecureRandom())
            } catch (e: Exception) {
                SLog.info("Error!message[%s], trace[%s]", e.message, Log.getStackTraceString(e))
            }
            val DO_NOT_VERIFY =
                HostnameVerifier { hostname, session -> true }
            return OkHttpClient.Builder() // .addInterceptor(interceptor)
                .sslSocketFactory(sslContext!!.socketFactory, xtm)
                .hostnameVerifier(DO_NOT_VERIFY)
                .build()
        }

}

