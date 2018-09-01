package com.llj.web.domain.message;

import java.util.List;
import java.util.Map;

/**
 * @Description: 群发消息
 * @Author: lulongji
 * @Date: Created in 16:53 2018/8/25
 */

public class MassMessage {
    private List<String> touser;
    private String msgtype;
    private Map<String, Object> text;

    public List<String> getTouser() {
        return touser;
    }

    public void setTouser(List<String> touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Map<String, Object> getText() {
        return text;
    }

    public void setText(Map<String, Object> text) {
        this.text = text;
    }
}