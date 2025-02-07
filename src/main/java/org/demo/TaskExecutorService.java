package org.demo;

import java.util.concurrent.*;

public class TaskExecutorService {

    public static void main(String[] args) {
        // 创建一个固定大小的线程池，大小为 3
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 提交多个任务
        executorService.submit(new Task("Task 1"));
        executorService.submit(new Task("Task 2"));
        executorService.submit(new Task("Task 3"));

        // 提交一个返回值的任务（可以使用 Callable）
        Future<String> resultFuture = executorService.submit(new CallableTask());

        try {
            // 获取返回的结果
            String result = resultFuture.get();
            System.out.println("Result from callable task: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // 关闭线程池，防止新的任务被提交
        executorService.shutdown();
    }
}

// 定义一个普通的任务（实现 Runnable）
class Task implements Runnable {
    private String taskName;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println(taskName + " is being executed by " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000); // 模拟任务执行的耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// 定义一个返回值的任务（实现 Callable）
class CallableTask implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("Callable task is being executed by " + Thread.currentThread().getName());
        Thread.sleep(1000); // 模拟任务执行的耗时
        return "Callable Task Completed!";
    }
}

