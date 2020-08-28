package com.cloud.system.util;

import java.util.concurrent.TimeUnit;

/**
 * @auther cs
 * @date 2020/8/21 17:33
 * @description
 */
public class VolatileExample {
    private static volatile boolean flag = false;
    private static  int i = 0;
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                flag = true;
                System.out.println("flag 被修改成 true");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        while (!flag) {
            i++;
//            try {
//                TimeUnit.MILLISECONDS.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("flag表示=" + flag);
        }
        System.out.println("程序结束,i=" + i);
    }
}
