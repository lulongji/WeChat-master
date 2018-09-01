package com.llj.web.domain;

/**
 * @Description:
 * @Author: lulongji
 * @Date: Created in 15:49 2018/8/31
 */
public class Msg {

    private String msg;
    private String openid;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "msg='" + msg + '\'' +
                ", openid='" + openid + '\'' +
                '}';
    }
}
