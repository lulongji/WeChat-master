package com.llj.web.domain.wechat;

import java.util.List;

/**
 * @Description:
 * @Author: lulongji
 * @Date: Created in 15:33 2018/8/25
 */
public class WeiXinUserData {

    private List<String> openid;

    public List<String> getOpenid() {
        return openid;
    }

    public void setOpenid(List<String> openid) {
        this.openid = openid;
    }
}
