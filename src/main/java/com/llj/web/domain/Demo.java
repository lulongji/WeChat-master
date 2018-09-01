package com.llj.web.domain;

import java.io.Serializable;

/**
 * @Description: demo
 * @Author: lulongji
 * @Date: Created in 14:57 2018/8/27
 */
public class Demo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer status;
    private String wechatnum;
    private String wechatmsg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWechatnum() {
        return wechatnum;
    }

    public void setWechatnum(String wechatnum) {
        this.wechatnum = wechatnum;
    }

    public String getWechatmsg() {
        return wechatmsg;
    }

    public void setWechatmsg(String wechatmsg) {
        this.wechatmsg = wechatmsg;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "id=" + id +
                ", status=" + status +
                ", wechatnum='" + wechatnum + '\'' +
                ", wechatmsg='" + wechatmsg + '\'' +
                '}';
    }
}
