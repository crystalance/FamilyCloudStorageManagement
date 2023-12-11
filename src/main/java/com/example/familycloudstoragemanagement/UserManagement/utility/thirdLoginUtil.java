package com.example.familycloudstoragemanagement.UserManagement.utility;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class thirdLoginUtil {
    static String urlID;

    public static JSONObject getUser(String code, int type) {
        if (type == 1) {
            String ClientId = "4425599850986348843";
            String ClientSecret = "5jahjpdnuHxm2v07wSjsOsfqA39WNSqp0dYfoefefsPAuCT0l5oy52KgeeKNB3rt";
            String redirectUri = "http%3A%2F%2F120.25.123.85%3A8081%2Falicallback";
            String urlToken = "https://oauth.aliyun.com/v1/token?grant_type=authorization_code" +
                    "&code=" + code +
                    "&client_id=" + ClientId +
                    "&redirect_uri=" + redirectUri +
                    "&grant_type=authorization_code";
            String token = getToken(urlToken, ClientSecret);
            urlID = "https://oauth.aliyun.com/v1/userinfo?access_token=" + token;
            return JSONObject.parseObject(HttpUtil.get(urlID));
        } else {
            String ClientId = "f782fff375dacc13d09cd0e1356560382fe1d74277dd62a4b7b0269f4002b789";
            String ClientSecret = "9893cfd20a5e7cd9c3a1330491ef9db0953b4fa8926245b72ee79debaf1fd025";
            String redirectUri = "http://120.25.123.85:8081/user/giteecallback";
            String urlToken = "https://gitee.com/oauth/token?grant_type=authorization_code" +
                    "&code=" + code +
                    "&client_id=" + ClientId +
                    "&redirect_uri=" + redirectUri;
            String token = getToken(urlToken, ClientSecret);
            urlID = "https://gitee.com/api/v5/user?access_token=" + token;
            return JSONObject.parseObject(HttpUtil.get(urlID));
        }
    }


    public static String getToken(String url, String ClientSecret) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("client_secret", ClientSecret);
        JSONObject jsonResponse = JSONObject.parseObject(HttpUtil.post(url, param));
        return jsonResponse.getString("access_token");
    }
}
