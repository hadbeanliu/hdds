package com.linghua.hdds.api.conf;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class QueryTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private TemporaryRecorder task1;

    @Autowired
    private ItemRecorder task2;

    @PostConstruct
    public void init(){
        logger.info("开启日志监听线程1：");
//        TemporaryRecorder recorder1=TemporaryRecorder.getInstance();
        Thread t1=new Thread(task1);
        t1.start();
        logger.info("开启日志监听线程1 成功[["+task1+"]]");
        logger.info("开启日志监听线程2：");
//        ItemRecorder recorder2=ItemRecorder.getInstance();
        Thread t2=new Thread(task2);
        t2.start();
        logger.info("开启日志监听线程2 成功[["+task2+"]]");
    }
}
