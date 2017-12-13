package cn.db.bean;

public class AllInformationStock extends BaseStock {
	private static final long serialVersionUID = -5097561405866238523L;

	private String stockInfo;
	private String stockInfoDES;
	private String stockInfoMD5;

	public final static String TABLE_NAME = "ALL_INFORMATION_STOCK_";
	
	public final static String STOCK_INFO = "STOCK_INFO_";
	public final static String STOCK_INFO_DES = "STOCK_INFO_DES_";
	public final static String STOCK_INFO_MD5 = "STOCK_INFO_MD5_";
	public final static String ALL_FIELDS = NUM + "," + STOCK_CODE + "," + STOCK_DATE + "," + STOCK_INFO + "," + STOCK_INFO_DES + "," + STOCK_INFO_MD5 + "," + INPUT_TIME;

	public String getStockInfo() {
		return stockInfo;
	}
	public void setStockInfo(String stockInfo) {
		this.stockInfo = stockInfo;
	}
	public String getStockInfoDES() {
		return stockInfoDES;
	}
	public void setStockInfoDES(String stockInfoDES) {
		this.stockInfoDES = stockInfoDES;
	}
	public String getStockInfoMD5() {
		return stockInfoMD5;
	}
	public void setStockInfoMD5(String stockInfoMD5) {
		this.stockInfoMD5 = stockInfoMD5;
	}
}