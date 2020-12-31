package com.example.netutildemo.api;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {
     final static MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

     final static MediaType FILE
            = MediaType.parse("application/octet-stream");

    /**
     * 表單上傳
     * @param url
     * @param jsonField
     * @param json
     * @param imageField
     * @param imageFile
     * @param imageFileType "image/png","image/jpg"
     * @return
     * @throws IOException
     */
    public static String postFormData(String url, String jsonField, String json, String imageField, File imageFile, String imageFileType) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(jsonField, json)
                .addFormDataPart(imageField, imageFile.getName(), RequestBody.create(MediaType.parse(imageFileType), imageFile))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * POST請求
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * POST請求
     * @param url
     * @return
     * @throws IOException
     */
    public static String post(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(new FormBody.Builder().build())
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * GET請求
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException{
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 文件上傳
     * @param url
     * @param file
     * @return
     * @throws IOException
     */
    public static String postFile(String url, File file) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(FILE, file);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
