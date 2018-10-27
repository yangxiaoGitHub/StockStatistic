package cn.aop.cglib;

import cn.implement.ValidateStatisticDetailStockData;

public class CglibTest {

	public static void main(String args[]) throws ClassNotFoundException {
		try {
		    Music music = new Music();
			CGLIBProxy cglibProxy = new CGLIBProxy(music);
			((Music) cglibProxy.getProxy()).sing("ceshi de ");

			ValidateStatisticDetailStockData validateStatisticDetail = new ValidateStatisticDetailStockData();
			CGLIBProxy _cglibProxy = new CGLIBProxy(validateStatisticDetail);
			((ValidateStatisticDetailStockData) _cglibProxy.getProxy()).testLoop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
