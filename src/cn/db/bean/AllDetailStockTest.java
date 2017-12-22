package cn.db.bean;

import java.util.Date;

public class AllDetailStockTest extends DetailStock {
	private static final long serialVersionUID = -6391050262913913268L;

	public AllDetailStockTest() {

	}

	public AllDetailStockTest(String stockCode, Date stockDate) {
		super.setStockCode(stockCode);
		super.setStockDate(stockDate);
	}

	public final static String TABLE_NAME = "ALL_DETAIL_STOCK_TEST_";
}
