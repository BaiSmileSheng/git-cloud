package com.cloud.common.utils.okhttp;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpClients {

    /**
     * get请求
     */
    public static <T> OkhttpResult<T> get(OkHttpParam restParam, Class<T> tClass) throws Exception {
        String url = restParam.getApiUrl();

        if (restParam.getApiPath() != null && !"".equalsIgnoreCase(restParam.getApiPath())) {
            url = url+restParam.getApiPath();
        }
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return exec(restParam, request, tClass);
    }

    /**
     * POST请求json数据
     */
    public static <T> OkhttpResult<T> post(OkHttpParam restParam, String reqJsonData, Class<T> tClass) throws Exception {
        String url = restParam.getApiUrl();

        if (restParam.getApiPath() != null && !"".equalsIgnoreCase(restParam.getApiPath())) {
            url = url+restParam.getApiPath();
        }
        RequestBody body = RequestBody.create(restParam.getMediaType(), reqJsonData);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return exec(restParam, request, tClass);
    }

    /**
     * POST请求map数据
     */
    public static <T> OkhttpResult<T> post(OkHttpParam restParam, Map<String, String> parms, Class<T> tClass) throws Exception {
        String url = restParam.getApiUrl();

        if (restParam.getApiPath() != null && !"".equalsIgnoreCase(restParam.getApiPath())) {
            url = url+restParam.getApiPath();
        }
        FormBody.Builder builder = new FormBody.Builder();

        if (parms != null) {
            for (Map.Entry<String, String> entry : parms.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }

        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return exec(restParam, request, tClass);
    }

    /**
     * POST请求map数据
     */
    public static <T> OkhttpResult<T> postWithAuthorize(OkHttpParam restParam, Map<String, String> parms, Class<T> tClass,String accessToken) throws Exception {
        String url = restParam.getApiUrl();

        if (restParam.getApiPath() != null && !"".equalsIgnoreCase(restParam.getApiPath())) {
            url = url+restParam.getApiPath();
        }
        FormBody.Builder builder = new FormBody.Builder();

        if (parms != null) {
            for (Map.Entry<String, String> entry : parms.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }

        FormBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization","Bearer "+accessToken)
                .build();
        return exec(restParam, request, tClass);
    }

    /**
     * 返回值封装成对象
     */
    private static <T> OkhttpResult<T> exec(
            OkHttpParam restParam,
            Request request,
            Class<T> tClass) throws Exception {

        OkhttpResult clientResult = exec(restParam, request);
        String result = clientResult.getResult();
        int status = clientResult.getStatus();

        T t = null;
        if (status == 200) {
            if (result != null && "".equalsIgnoreCase(result)) {
                t = JSON.parseObject(result, tClass);
            }
        } else {
            try {
                result = JSON.parseObject(result, String.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new OkhttpResult<>(clientResult.getStatus(), result, t);
    }

    /**
     * 执行方法
     */
    private static OkhttpResult exec(
            OkHttpParam restParam,
            Request request) throws Exception {

        OkhttpResult result = null;
        okhttp3.OkHttpClient client = null;
        ResponseBody responseBody = null;
        try {
            client = new okhttp3.OkHttpClient();

            client.newBuilder()
                    .connectTimeout(restParam.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(restParam.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(restParam.getWriteTimeout(), TimeUnit.MILLISECONDS)
                    .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())    //忽略https
                    .hostnameVerifier(SSLSocketClient.getHostnameVerifier()).build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                responseBody = response.body();
                if (responseBody != null) {
                    String responseString = responseBody.string();

                    result = new OkhttpResult<>(response.code(), responseString, null);
                }
            } else {
                throw new Exception(response.message());
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
            if (client != null) {
                client.dispatcher().executorService().shutdown();   //清除并关闭线程池
                client.connectionPool().evictAll();                 //清除并关闭连接池
                try {
                    if (client.cache() != null) {
                        client.cache().close();                         //清除cache
                    }
                } catch (IOException e) {
                    throw new Exception(e.getMessage());
                }
            }
        }
        return result;
    }

}
