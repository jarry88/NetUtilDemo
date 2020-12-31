package com.example.netutildemo.api;

import android.util.Log;


import com.example.netutildemo.util.SLog;

import java.io.IOException;

import okhttp3.Call;


/**
 * 獲取OkHttp的異步結果后，切換到UI線程中執行的回調
 * @author zwm
 */
public abstract class UICallback implements Runnable {
    /**
     * 需要實現的失敗處理方法
     * @param call
     * @param e
     */
    public abstract void onFailure(Call call, IOException e);

    /**
     * 需要實現的成功處理方法
     * @param call
     * @param responseStr
     * @throws IOException
     */
    public abstract void onResponse(Call call, String responseStr) throws IOException;


    /**
     * OkHttp的執行結果的常量定義
     */
    public static final int RESULT_UNKNOWN = 0;
    public static final int RESULT_FAILURE = 1;
    public static final int RESULT_RESPONSE = 2;

    /**
     * OkHttp的執行結果
     */
    private int result = RESULT_UNKNOWN;

    private Call call;
    private IOException ioException;
    private String responseStr;

    /**
     * OkHttp失敗時調用
     * @param call
     * @param ioException
     */
    public void setOnFailure(Call call, IOException ioException) {
        result = RESULT_FAILURE;
        this.call = call;
        this.ioException = ioException;
    }

    /**
     * OkHttp成功時調用
     * @param call
     * @param responseStr
     */
    public void setOnResponse(Call call, String responseStr) {
        result = RESULT_RESPONSE;
        this.call = call;
        this.responseStr = responseStr;
    }

    @Override
    public void run() {
        if (result == RESULT_FAILURE) {
            // 失敗時回調onFailure
            onFailure(call, ioException);
        } else if (result == RESULT_RESPONSE) {
            try {
                // 成功時回調onResponse
                onResponse(call, responseStr);
            } catch (Exception e) {
                System.out.println();
                SLog.INSTANCE.info("Error!message[%s], trace[%s]", e.getMessage(), Log.getStackTraceString(e));
            }
        }
    }
}
