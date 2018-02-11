package reflec.aop.test;

import reflec.aop.After;
import reflec.aop.Before;
import reflec.aop.Aspect;

/**
 * Created by szh on 2017/4/21.
 */
@Aspect
public class BeforeEat {
    @Before("reflec.aop.test.Eat.startEat()")
    public void beforeStart() {
        System.out.println("��ʼ�Է�ǰ��׼��");
    }

    @After("reflec.aop.test.Eat.startEat()")
    public void afterEat() {
        System.out.println("�Է�����Ժ�");
    }
}
