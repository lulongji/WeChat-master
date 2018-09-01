package com.llj.web.schedule;

import com.llj.web.domain.Demo;
import com.llj.web.server.RuntimeData;
import com.llj.web.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;


/**
 * @Description:
 * @Author: lulongji
 * @Date: Created in 18:22 2018/8/27
 */
@Component
public class GlobalTask implements Runnable {

    private static final Logger logger = Logger.getLogger(GlobalTask.class.getName());

    @Autowired
    private RuntimeData runtimeData;
    @Autowired
    private DemoService demoService;

    /**
     * 每1分钟执行一次静态化任务
     */
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("" + e);
        }
    }

    /**
     * 执行内容
     *
     * @throws Exception
     */
    private void execute() throws Exception {
        Demo demo = new Demo();
        List<Demo> demoList = demoService.getDemoList(demo);
        for (Demo d : demoList) {
            demo.setId(d.getId());
            demo.setStatus(2);
            demoService.updateDemo(demo);
            runtimeData.newMsg(d);
            Thread.sleep(500);
        }
    }

}