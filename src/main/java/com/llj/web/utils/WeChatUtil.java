package com.llj.web.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.llj.framework.cache.redis.springTemplate.RedisTemplateUtil;
import com.llj.framework.utils.date.DateUtil;
import com.llj.web.constants.RedisKeyConstants;
import com.llj.web.constants.WechatConstants;
import com.llj.web.domain.message.MassMessage;
import com.llj.web.domain.wechat.Token;
import com.llj.web.domain.wechat.UserInfo;
import com.llj.web.domain.wechat.WeChatOauth2Token;
import com.llj.web.domain.wechat.WeiXinUserList;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lu on 2017/12/13. 类名: WeChatUtil </br>
 * 描述: 微信通用工具类 </br>
 * 发布版本：V1.0 </br>
 */
public class WeChatUtil {

    private static Logger log = LoggerFactory.getLogger(WeChatUtil.class);

    // 凭证获取（GET）
    public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 获取accessToken
     *
     * @return
     * @throws Exception
     */
    public static String getAccessToken() {
        Object accessToken = null;
        try {
            accessToken = RedisTemplateUtil.get(RedisKeyConstants.WECHAT_ACCESS_TOKEN);
            if (null == accessToken) {
                accessToken = getToken().getAccessToken();
                RedisTemplateUtil.set(RedisKeyConstants.WECHAT_ACCESS_TOKEN, accessToken, 70, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken.toString();
    }

    /**
     * 获取接口访问凭证
     *
     * @return
     */
    public static Token getToken() {
        Token token = null;
        String requestUrl = token_url.replace("APPID", WechatConstants.WECHAT_APPID).replace("APPSECRET", WechatConstants.WECHAT_APPSECRET);
        // 发起GET请求获取凭证
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                token = new Token();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getInteger("expires_in"));
            } catch (JSONException e) {
                token = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInteger("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return token;
    }

    /**
     * 获取用户信息
     *
     * @param accessToken 接口访问凭证
     * @param openId      用户标识
     * @return UserInfo
     */
    public static UserInfo getUserInfo(String accessToken, String openId) {
        UserInfo UserInfo = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // 获取用户信息
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                UserInfo = new UserInfo();
                // 用户的标识
                UserInfo.setOpenId(jsonObject.getString("openid"));
                // 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
                UserInfo.setSubscribe(jsonObject.getInteger("subscribe"));
                // 用户关注时间
                UserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
                // 昵称
                UserInfo.setNickname(jsonObject.getString("nickname"));
                // 用户的性别（1是男性，2是女性，0是未知）
                UserInfo.setSex(jsonObject.getInteger("sex"));
                // 用户所在国家
                UserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                UserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                UserInfo.setCity(jsonObject.getString("city"));
                // 用户的语言，简体中文为zh_CN
                UserInfo.setLanguage(jsonObject.getString("language"));
                // 用户头像
                UserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
            } catch (Exception e) {
                if (0 == UserInfo.getSubscribe()) {
                    log.error("用户{}已取消关注", UserInfo.getOpenId());
                } else {
                    int errorCode = jsonObject.getInteger("errcode");
                    String errorMsg = jsonObject.getString("errmsg");
                    log.error("获取用户信息失败 errcode:{" + errorCode + "} errmsg:{" + errorMsg + "}");
                }
            }
        }
        return UserInfo;
    }


    /**
     * 获取微信授权信息
     *
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    public static WeChatOauth2Token getOauth2AccessToken(String appId, String appSecret, String code) {
        WeChatOauth2Token wat = new WeChatOauth2Token();
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&code=" + code
                + "&grant_type=authorization_code";
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        if (null != jsonObject) {
            try {
                wat = new WeChatOauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getInteger("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            } catch (Exception e) {
                wat = null;
                String errorCode = jsonObject.getString("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode{},errMsg", errorCode, errorMsg);
            }

        }
        return wat;
    }

    /**
     * 将请求参数转换为xml格式的string
     *
     * @param parameters 请求参数
     * @return
     * @Description：将请求参数转换为xml格式的string
     */
    @SuppressWarnings("rawtypes")
    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 返回给微信的参数
     *
     * @param return_code 返回编码
     * @param return_msg  返回信息
     * @return
     * @Description：返回给微信的参数
     */
    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
    }

    /**
     * url编码
     *
     * @param source
     * @return
     */
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 网页授权通过code 获取用户信息
     *
     * @param code
     * @param appid
     * @param secret
     * @return
     * @throws Exception
     */
    public static UserInfo oauth(String code, String appid, String secret) throws Exception {
        log.info("用户同意授权后的code: " + code);
        WeChatOauth2Token wt = getOauth2AccessToken(appid, secret, code);
        UserInfo u = getUserInfo(wt.getAccessToken(), wt.getOpenId());
        return u;
    }

    /**
     * 获取openId
     *
     * @param code
     * @param appid
     * @param secret
     * @return
     * @throws Exception
     */
    public static String getOpenId(String code, String appid, String secret) throws Exception {
        log.info("用户同意授权后的code: " + code);
        WeChatOauth2Token wt = getOauth2AccessToken(appid, secret, code);
        String openId = wt.getOpenId();
        return openId;
    }

    /**
     * 生成用于获取access_token的Code的Url
     *
     * @param redirectUrl
     * @return
     */
    public static String getRequestCodeUrl(String redirectUrl, String scope, String state) {
        return String.format(
                "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                WechatConstants.WECHAT_APPID, redirectUrl, scope, state);
    }

    /**
     * 创建菜单
     *
     * @param jsonMenu
     * @return
     */
    public static int createMenu(String jsonMenu) {
        int status = 0;
        String accessoken = WeChatUtil.getAccessToken();
        String path = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessoken;
        try {
            URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(jsonMenu.getBytes("UTF-8"));
            os.close();

            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] bt = new byte[size];
            is.read(bt);
            String message = new String(bt, "UTF-8");
            JSONObject jsonMsg = JSONObject.parseObject(message);
            status = Integer.parseInt(jsonMsg.getString("errcode"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }


    /**
     * 获取关注列表用户openid
     *
     * @param openidList
     * @param accessToken
     * @param nextOpenid  第一个拉取的OPENID，不填默认从头开始拉取
     * @return
     */
    public static List<String> findWeiXinUserList(List<String> openidList, String accessToken, String nextOpenid) {
        String path = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
        path = path.replace("ACCESS_TOKEN", accessToken).replace("NEXT_OPENID", nextOpenid);
        log.info("请求关注列表地址path：" + path);

        WeiXinUserList weixinUserList;
        Map<String, String> map = new TreeMap<String, String>();
        map.put("access_token", accessToken);
        if (StringUtils.isNotBlank(nextOpenid)) {
            map.put("next_openid", nextOpenid);
        }
        String result = HttpClientUtils.get(path);
        if (result != null) {
            try {
                weixinUserList = new Gson().fromJson(result, WeiXinUserList.class);
                //openidList = new ArrayList<>();
                if (weixinUserList.getCount() <= 10000 && weixinUserList.getCount() > 0) {
                    openidList.addAll(weixinUserList.getData().getOpenid());
                } else {
                    //如果大于10000的,继续查询
                    findWeiXinUserList(openidList, accessToken, weixinUserList.getNext_openid());
                }
            } catch (JsonSyntaxException e) {
                openidList = null;
            }
        }
        return openidList;
    }


    /**
     * 群发消息
     *
     * @param messageContent
     * @return
     */
    public static JSONObject sendMassMessage(String messageContent, List<String> openIdList) {
        JSONObject jso = null;
        try {
            String URL = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN";
            //获取token
            String accessToken = getAccessToken();
            String url = URL.replace("ACCESS_TOKEN", accessToken);

            MassMessage massMessage = new MassMessage();
            massMessage.setTouser(openIdList);
            massMessage.setMsgtype("text");
            Map<String, Object> map = new HashMap<>();
            if (messageContent == null || "".equals(messageContent)) {
                map.put("content", "hello，欢迎来到容联，现在的时间是：" + DateUtil.getCurrentTime());
            } else {
                map.put("content", messageContent);
            }
            massMessage.setText(map);
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            String str = gson.toJson(massMessage);
            System.out.println("str:" + str);
            jso = CommonUtil.httpsRequest(url, "post", str);
            System.out.println(jso.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jso;
    }


    /**
     * 普通文本消息，需用户在48h与公共帐号有互动
     * 微信公共账号发送给账号
     *
     * @param content        文本内容
     * @param toUser(OPENID) 微信用户
     * @return
     */
    public static boolean sendTextMessageToUser(String content, String toUser) {
        boolean flag = true;

        String json = "{\"touser\": \"" + toUser + "\",\"msgtype\": \"text\", \"text\": {\"content\": \"" + content + "\"}}";

        //获取access_token
        String accessToken = getAccessToken();

        //发送模版消息给指定用户
        String action = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;
        System.out.println("json:" + json);
        try {
            String result = HttpClientUtils.sendPost(action, json);
            System.out.println(result);
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

}
