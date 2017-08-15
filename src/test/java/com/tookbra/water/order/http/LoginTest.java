package com.tookbra.water.order.http;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by tookbra on 2017/8/7.
 */
public class LoginTest {

    @Test
    public void login() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody.Builder requestBody = new FormBody.Builder();
        requestBody.add("name", "");
        requestBody.add("password", "");
        Request request = new Request.Builder().url("http://www.ssky123.com:8081/login").post(requestBody.build()).build();
        Response response = httpClient.newCall(request).execute();
        Assert.assertEquals(200, response.code());
    }
}
