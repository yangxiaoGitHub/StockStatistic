package reflec.aop.test;


/**
 * Created by szh on 2017/4/21.
 */
public class Eat {
    public void startEat(){
        System.out.println("¿ªÊ¼³Ô·¹");
    }

    public static void main(String[] args) {
    	try {
    		Eat eat = new Eat();
    		eat.startEat();
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
