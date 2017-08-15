package com.tookbra.water.order.service;

import com.tookbra.water.order.enums.HttpMethodEnum;
import com.tookbra.water.order.utils.HttpUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tookbra on 2017/8/7.
 */
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public static LoginService loginService = new LoginService();

    public boolean login(String name, String password) {
        logger.info("开始登陆");
        Map<String, Object > paramMap = new HashMap<>();
        paramMap.put("name", name);
        paramMap.put("password", password);
        try {
            Response response = HttpUtil.getInstance().requeset("login", paramMap, HttpMethodEnum.POST);
            if(response.code() == 200) {
                logger.info("开始成功");
                return true;
            } else {
                logger.info("开始失败");
                return false;
            }
        } catch (IOException e) {
            logger.error("login:{}", e.getMessage());
            return false;
        }
    }
}
