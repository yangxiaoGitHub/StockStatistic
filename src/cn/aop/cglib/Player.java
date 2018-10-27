package cn.aop.cglib;

import reflec.aop.After;
import reflec.aop.Before;
import reflec.aop.Aspect;

@Aspect
public class Player {
    @Before("cn.aop.cglib.Music.sing()")
    public void beforeSing() {
        System.out.println("��ʼ����ǰ��׼��");
    }

    @After("cn.aop.cglib.Music.sing()")
    public void afterSing() {
        System.out.println("����֮��ʼ����");
    }
}
