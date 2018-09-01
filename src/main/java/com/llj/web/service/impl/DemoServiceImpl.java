package com.llj.web.service.impl;

import com.llj.web.dao.DemoDAO;
import com.llj.web.domain.Demo;
import com.llj.web.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: lulongji
 * @Date: Created in 15:12 2018/8/27
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoDAO demoDAO;

    @Override
    public List<Demo> getDemoList(Demo demo) throws Exception {
        return demoDAO.getDemoList(demo);
    }

    @Override
    public void updateDemo(Demo demo) throws Exception {
        demoDAO.updateDemo(demo);
    }
}
