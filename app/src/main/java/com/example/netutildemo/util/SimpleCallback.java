package com.example.netutildemo.util;

/**
 * 簡單的回調接口
 * @author zwm
 */
public interface SimpleCallback {
    /**
     * 選擇圖片
     */
    int ACTION_SELECT_IMAGE = 1;

    /**
     * 選擇取消訂單的原因
     */
    int ACTION_SELECT_CANCEL_ORDER_REASON = 2;

    /**
     * 關閉APP升級彈窗
     */
    int ACTION_CLOSE_APP_UPDATE_POPUP = 3;

    /**
     * 關閉廣告活動彈窗
     */
    int ACTION_CLOSE_ACTIVITY_POPUP = 4;


    void onSimpleCall(Object data);
}
