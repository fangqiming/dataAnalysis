package com.i000.stock.user.web.thread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:25 2018/5/8
 * @Modified By:
 */
@Component
public class ReceiveRecommendThread {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void execute(Runnable task) {
        threadPoolTaskExecutor.execute(task);
    }
}
