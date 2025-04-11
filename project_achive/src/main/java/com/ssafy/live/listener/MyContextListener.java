package com.ssafy.live.listener;

import com.ssafy.live.util.DBUtil;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MyContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("context 초기화 중입니다. 전체 애플리케이션 초기화와 관련된 작업 진행");
        DBUtil.getUtil();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("context 종료합니다. 정리할 작업들 진행해주세요.");
    }
}
