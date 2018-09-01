package com.llj.web.server;

import com.alibaba.fastjson.JSONObject;
import com.llj.web.constants.CommonConstants;
import com.llj.web.domain.Demo;
import com.llj.web.service.DemoService;
import com.llj.web.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 内存数据管理
 */
@Service
public class RuntimeData {

    private static final Logger log = Logger.getLogger(RuntimeData.class.getName());


    @Autowired
    private DemoService demoService;


    private static List<Demo> demoListData;


    /**
     * 初始化 内存数据
     */
    RuntimeData() {
        log.info("RuntimeData().start!");
        demoListData = new Vector<>();
        startPush();
    }

    /**
     * 启动
     */
    private void startPush() {
        Thread t = new Thread("msg  getMsg thread!") {
            public void run() {
                doMsg();
            }
        };
        t.start();
    }


    public void newMsg(Demo data) {
        synchronized (demoListData) {
            demoListData.add(data);
            log.info("wechatTo3rdParty queue size:" + demoListData.size());
            demoListData.notifyAll();
        }
    }

    /**
     * 处理数据
     */
    protected void doMsg() {
        while (true) {
            try {
                Demo data = null;
                synchronized (demoListData) {
                    if (demoListData.size() == 0) {
                        demoListData.wait();
                    } else {
                        data = demoListData.get(0);
                    }
                }
                if (data != null) {
                    demoListData.remove(0);
                    if (push(data)) {
                        log.info("push msg to the 3rd party ：num=1");
                        System.out.println("----------------------");
                    } else {
                        Thread.sleep(2000);
                        if (push(data)) {
                            log.info("push msg to the 3rd party：num=2");
                        } else {
                            Thread.sleep(2000);
                            if (push(data)) {
                                log.info("push msg to the 3rd party：num=3");
                            } else {
                                log.info("push 3 times,all failed,over.");
                                //发送三次失败 状态更新回1
                                Demo demo = new Demo();
                                demo.setStatus(1);
                                demo.setId(data.getId());
                                demoService.updateDemo(demo);
                            }
                        }
                    }
                } else {
                    Thread.sleep(2000);
                }

            } catch (Exception e) {
                log.info("" + e);
            }

        }
    }


    /**
     * 向第三方推送消息
     *
     * @param data
     * @return
     */
    private boolean push(Demo data) {
        log.log(Level.INFO, "push wechat msg to the 3rd party server：" + CommonConstants.SEND_MSG_URL);
        String sendData = "";
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("msg", data.getWechatmsg());
            jsonData.put("openid", data.getWechatnum());
            sendData = jsonData.toString();
        } catch (Exception e) {
            log.log(Level.WARNING, "", e);
            return true;
        }
        try {
            String result = HttpClientUtils.post(CommonConstants.SEND_MSG_URL, sendData);
            if (result != null) {
                HashMap<String, Object> resultMap = JSONObject.parseObject(result, HashMap.class);
                if ((Boolean) resultMap.get("success")) {
                    log.info("push wechat msg to the 3rd party server success,response content:" + result);
                    return true;
                } else {
                    log.info("push wechat msg to the 3rd party server failed,failed content：" + resultMap.get("message"));
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.WARNING, "", e);
            return false;
        }
    }

    /**
     * 发送微信数据
     *
     * @param data
     * @return
     */
//    private boolean push(Demo data) {
//        return WeChatUtil.sendTextMessageToUser(data.getWechatmsg(), data.getWechatnum());
//    }

}
