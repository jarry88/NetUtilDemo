package com.example.netutildemo.util

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.internal.trimSubstring
import java.text.SimpleDateFormat
import java.util.*
/**
 * 在控制台中输出日志
 * 使用方法:
 * SLog.info("n = %d", 666);
 * 输出类似如下内容:
 * [2019-04-26 16:28:39.772][RxJavaDemo.java][00052]n = 666
 * @author zwm
 */
object SLog {
    @SuppressLint("SimpleDateFormat")
    fun info(format: String?, vararg args: Any?) {
        try {
            val traceArray = Thread.currentThread().stackTrace

            // 在Android中固定为3
            val trace = traceArray[3]
            val content: String? = format

            // 生成时间戳
            // 设置日期格式： 2019-04-26 16:28:39.772
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            // new Date()为获取当前系统时间
            val timestamp = sdf.format(Date())
            val fileName = trace.fileName
            val lineNumber: String = trace.lineNumber.toString()
            var logContent= "[$timestamp][$fileName][$lineNumber]$content"
            // 由于Logcat每条日志最大的长度是4096个字符，对于超出部分会进行截断处理，因此需要对于超长的日志进行分段处理
            var first = true
            val limit = 3900
            while (true) {
                var msg: String
                if (logContent.length > limit) {
                    msg = logContent.trimSubstring(0,limit)
                } else {
                    msg = logContent
                }
                if (!first) {
                    msg = "------>[$msg]"
                }
                Log.println(Log.ASSERT, "SLog", msg)
                if (logContent.length <= limit) {
                    break
                }
                logContent = logContent.trimSubstring(limit)
                first = false
            }
        } catch (e: Exception) {
            info("Error!message[%s], trace[%s]", e.message, Log.getStackTraceString(e))
        }
    }

    /**
     * 打印当前调用栈
     */
    fun bt() {
        // 生成时间戳
        // 设置日期格式： 2019-04-26 16:28:39.772
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        // new Date()为获取当前系统时间
        val timestamp = sdf.format(Date())
        val sb = StringBuilder("\n****************TRACE****************")
        val elements = Thread.currentThread().stackTrace
        for (i in 3 .. elements.size) {
            val element = elements[i]
            sb.append("\n\tat ")
            sb.append(element)
        }
        sb.append("\n################TRACE################")
        val traceArray = Thread.currentThread().stackTrace
        val trace = traceArray[3]
        val content =
            "[$timestamp][$trace.fileName][$sb]\n"
        Log.println(Log.ASSERT, "SLog", content)
    }
}