package cn.aop.cglib;

import reflec.aop.After;
import reflec.aop.Before;
import reflec.aop.Aspect;

@Aspect
public class Player {
    @Before("cn.aop.cglib.Music.sing()")
    public void beforeSing() {
        System.out.println("开始唱歌前的准备");
    }

    @After("cn.aop.cglib.Music.sing()")
    public void afterSing() {
        System.out.println("唱完之后开始评分");
    }
}
