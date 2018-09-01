package com.llj.web.dao;

import com.llj.web.domain.Demo;

import java.util.List;

/**
 * @Description:
 * @Author: lulongji
 * @Date: Created in 15:12 2018/8/27
 */
public interface DemoDAO {


    List<Demo> getDemoList(Demo demo) throws Exception;

    void updateDemo(Demo demo) throws Exception;
}
