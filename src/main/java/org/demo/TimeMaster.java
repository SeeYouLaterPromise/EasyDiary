package org.demo;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeMaster {

    public static void main(String[] args) {
        // 获取本地时区
        ZoneId zoneId = ZoneId.systemDefault();

        // 获取当前时间
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // 设置目标时间为今天的 23:59:59
        ZonedDateTime targetTime = now.withHour(14).withMinute(22).withSecond(0).withNano(0);

        // 如果目标时间已经过去，设置目标时间为明天的 23:59:59
        if (now.isAfter(targetTime)) {
            targetTime = targetTime.plusDays(1);
        }

        // 计算从当前时间到目标时间的延迟（毫秒）
        long delay = Duration.between(now, targetTime).toMillis();

        // 创建调度服务
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 创建定时任务
        Runnable task = () -> {
            System.out.println("Task executed at: " + ZonedDateTime.now(zoneId));
        };

        // 安排任务在指定时间执行
        scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);

        // 倒计时查看
        Timer timer = new Timer(true); // 使用守护线程来定时检查
        ZonedDateTime finalTargetTime = targetTime;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long delta = Duration.between(ZonedDateTime.now(zoneId), finalTargetTime).toMillis() / 1000;
                System.out.println(delta);
            }
        }, 0, 1000); // 每 1 秒检查一次剪切板
    }
}
