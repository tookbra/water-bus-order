package com.tookbra.water.order.utils;

import com.tookbra.water.order.enums.HttpMethodEnum;
import okhttp3.*;
import okhttp3.internal.http.HttpMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by tookbra on 2017/8/7.
 */
public class HttpUtil {

    private static volatile HttpUtil httpUtil;
    private OkHttpClient httpClient;

    private static final String BASE_URL = "http://www.ssky123.com:8081";


    public HttpUtil() {
        httpClient = new OkHttpClient()
                .newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .cookieJar(setCookieJar())
                .build();
    }

    private CookieJar setCookieJar() {
        return new CookieJar() {
            private final Map<String, List<Cookie>> cookieMap = new ConcurrentHashMap<>();
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                String host = httpUrl.host();
                List<Cookie> cookiesList = cookieMap.get(host);
                if (cookiesList != null){
                    cookieMap.remove(host);
                }
                cookieMap.put(host, list);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                List<Cookie> cookieList = cookieMap.get(httpUrl.host());
                return cookieList != null ? cookieList : Collections.EMPTY_LIST;
            }
        };
    }


    public static HttpUtil getInstance() {
        if(httpUtil == null) {
            synchronized (HttpUtil.class) {
                if(httpUtil == null) {
                    httpUtil = new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    public Response requeset(String url, Map<String, Object> params, HttpMethodEnum methodEnum) throws IOException {
        switch (methodEnum) {
            case GET:
                return getBySyn(url, params);
            case POST:
                return postByAsyn(url, params);
            default:
                return null;
        }

    }

    public Response requeset(String url, FormBody requestBody) throws IOException {
        String requestUrl = String.format("%s/%s", BASE_URL, url);
        Request request = new Request.Builder().url(requestUrl).post(requestBody).build();
        Response response = httpClient.newCall(request).execute();
        setCookie(request, response.headers());
        return response;

    }

    private Response getBySyn(String url, Map<String, Object> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        if(params != null) {
            for (String key : params.keySet()) {
                if (pos > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s", key, params.get(key)));
                pos++;
            }
        }
        String requestUrl = String.format("%s/%s?%s", BASE_URL, url, sb.toString());
        System.out.println(requestUrl);
        Request request = new Request.Builder().url(requestUrl).build();
        Response response = httpClient.newCall(request).execute();
        setCookie(request, response.headers());
        return response;
    }


    private Response postByAsyn(String url, Map<String, Object> params) throws IOException {
        FormBody.Builder requestBody = new FormBody.Builder();
        if(params != null) {
            for (String key : params.keySet()) {
                requestBody.add(key, params.get(key).toString());
            }
        }
        String requestUrl = String.format("%s/%s", BASE_URL, url);
        Request request = new Request.Builder().url(requestUrl).post(requestBody.build()).build();
        Response response = httpClient.newCall(request).execute();
        setCookie(request, response.headers());
        return response;
    }

    private void setCookie(Request request, Headers headers) {
        List<Cookie> cookieList = Cookie.parseAll(request.url(), headers);
        if (!cookieList.isEmpty()) {
            httpClient.cookieJar().saveFromResponse(request.url(), cookieList);
        }
    }
}
