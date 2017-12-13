package cn.db.bean;

import java.util.Date;

public class AllDetailStock extends DetailStock {
	private static final long serialVersionUID = 3289317631102871983L;
	
	public AllDetailStock() {
		
	}
	
	public AllDetailStock(String stockCode, Date stockDate) {
		this.setStockCode(stockCode);
		this.setStockDate(stockDate);
	}
	
	public final static String TABLE_NAME = "ALL_DETAIL_STOCK_";
}
