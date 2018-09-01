package com.llj.web.service;

import com.llj.web.domain.Demo;

import java.util.List;

/**
 * @Description:
 * @Author: lulongji
 * @Date: Created in 15:12 2018/8/27
 */
public interface DemoService {


    /**
     * 查询所有状态为1的数据
     *
     * @param demo
     * @return
     * @throws Exception
     */
    List<Demo> getDemoList(Demo demo) throws Exception;


    /**
     * 更新数据
     *
     * @param demo
     * @throws Exception
     */
    void updateDemo(Demo demo) throws Exception;
}
