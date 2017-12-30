package cn.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllDetailStockTest;
import cn.db.bean.DailyStock;
import cn.db.bean.DetailStock;
import cn.db.bean.HistoryStock;
import cn.db.bean.InformationStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;

public class OperationDao extends BaseDao {
	protected Connection connection = null;

	public OperationDao() {
		//openConnection();
		this.connection = getConnection();
	}
	
	/*protected void openConnection() {
		try {
			if (this.connection == null || this.connection.isClosed()) {
				this.connection = getConnection();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/
	
	/*public boolean connectionIsNull() throws Exception {

		if (this.connection == null || this.connection.isClosed()) return true;
		else return false;
	}*/

	protected HistoryStock getHistoryStockFromResult(ResultSet rs) throws SQLException {

		HistoryStock historyStock = new HistoryStock();
		historyStock.setNum(rs.getLong(HistoryStock.NUM));
		historyStock.setStockDate(rs.getDate(HistoryStock.STOCK_DATE));
		historyStock.setStockCode(rs.getString(HistoryStock.STOCK_CODE));
		historyStock.setOpenPrice(rs.getDouble(HistoryStock.OPEN_PRICE));
		historyStock.setHighPrice(rs.getDouble(HistoryStock.HIGH_PRICE));
		historyStock.setLowPrice(rs.getDouble(HistoryStock.LOW_PRICE));
		historyStock.setClosePrice(rs.getDouble(HistoryStock.CLOSE_PRICE));
		historyStock.setTradedStockNumber(rs.getLong(HistoryStock.TRADED_STOCK_NUMBER));
		historyStock.setTradedAmount(rs.getFloat(HistoryStock.TRADED_AMOUNT));
		historyStock.setInputTime(rs.getDate(HistoryStock.INPUT_TIME));
		return historyStock;
	}

	protected DailyStock getDailyStockFromResult(ResultSet rs) throws SQLException {
		DailyStock data = new DailyStock();
		data.setNum(rs.getLong(DailyStock.NUM));
		data.setStockDate(rs.getDate(DailyStock.STOCK_DATE));
		data.setStockCode(rs.getString(DailyStock.STOCK_CODE));
		data.setStockCodeDES(rs.getString(DailyStock.STOCK_CODE_DES));
		data.setChangeRate(rs.getDouble(DailyStock.CHANGE_RATE));
		data.setEncryptChangeRate(rs.getString(DailyStock.CHANGE_RATE_ENCRYPT));
		data.setChangeFlg(rs.getString(DailyStock.CHANGE_FLG));
		data.setNote(rs.getString(DailyStock.NOTE));
		data.setInputTime(rs.getDate(DailyStock.INPUT_TIME));
		return data;
	}

	protected OriginalStock getOriginalStockFromResult(ResultSet rs) throws SQLException {

		OriginalStock data = new OriginalStock();
		data.setNum(rs.getLong(OriginalStock.NUM));
		data.setStockDate(rs.getDate(OriginalStock.STOCK_DATE));
		data.setStockNumber(rs.getInt(OriginalStock.STOCK_NUMBER));
		data.setStockCodes(rs.getString(OriginalStock.STOCK_CODES));
		data.setStockCodesEncrypt(rs.getString(OriginalStock.STOCK_CODES_ENCRYPT));
		data.setStockCodesMD5(rs.getString(OriginalStock.STOCK_CODES_MD5));
		data.setChangeRates(rs.getString(OriginalStock.CHANGE_RATES));
		data.setChangeRatesEncrypt(rs.getString(OriginalStock.CHANGE_RATES_ENCRYPT));
		data.setChangeRatesMD5(rs.getString(OriginalStock.CHANGE_RATES_MD5));
		data.setTurnoverRates(rs.getString(OriginalStock.TURNOVER_RATES));
		data.setTurnoverRatesEncrypt(rs.getString(OriginalStock.TURNOVER_RATES_ENCRYPT));
		data.setTurnoverRatesMD5(rs.getString(OriginalStock.TURNOVER_RATES_MD5));
		data.setInputTime(rs.getDate(OriginalStock.INPUT_TIME));
		return data;
	}

	protected StatisticDetailStock getStatisticDetailStockFromResult(ResultSet rs) throws SQLException {
		StatisticDetailStock data = new StatisticDetailStock();
		data.setNum(rs.getLong(StatisticDetailStock.NUM));
		data.setStockCode(rs.getString(StatisticDetailStock.STOCK_CODE));
		data.setStockDate(rs.getDate(StatisticDetailStock.STOCK_DATE));
		data.setStockCodeDES(rs.getString(StatisticDetailStock.STOCK_CODE_DES));
		data.setUpDownNumber(rs.getInt(StatisticDetailStock.UP_DOWN_NUMBER));
		data.setUpNumber(rs.getInt(StatisticDetailStock.UP_NUMBER));
		data.setDownNumber(rs.getInt(StatisticDetailStock.DOWN_NUMBER));
		data.setOneWeek(rs.getString(StatisticDetailStock.ONE_WEEK));
		data.setHalfMonth(rs.getString(StatisticDetailStock.HALF_MONTH));
		data.setOneMonth(rs.getString(StatisticDetailStock.ONE_MONTH));
		data.setTwoMonth(rs.getString(StatisticDetailStock.TWO_MONTH));
		data.setThreeMonth(rs.getString(StatisticDetailStock.THREE_MONTH));
		data.setHalfYear(rs.getString(StatisticDetailStock.HALF_YEAR));
		data.setOneYear(rs.getString(StatisticDetailStock.ONE_YEAR));
		data.setInputTime(rs.getDate(StatisticDetailStock.INPUT_TIME));
		data.setNote(rs.getString(StatisticDetailStock.NOTE));
		return data;
	}

	protected InformationStock getInformationStockFromResult(ResultSet rs) throws SQLException {

		InformationStock data = new InformationStock();
		data.setNum(rs.getLong(InformationStock.NUM));
		data.setStockCode(rs.getString(InformationStock.STOCK_CODE));
		data.setStockDate(rs.getDate(InformationStock.STOCK_DATE));
		data.setStockInfo(rs.getString(InformationStock.STOCK_INFO));
		data.setStockInfoDES(rs.getString(InformationStock.STOCK_INFO_DES));
		data.setStockInfoMD5(rs.getString(InformationStock.STOCK_INFO_MD5));
		data.setInputTime(rs.getDate(InformationStock.INPUT_TIME));
		return data;
	}

	protected DetailStock getDetailStockFromResult(ResultSet rs) throws SQLException {

		DetailStock data = new DetailStock();
		data.setNum(rs.getLong(DetailStock.NUM));
		data.setStockDate(rs.getDate(DetailStock.STOCK_DATE));
		data.setStockCode(rs.getString(DetailStock.STOCK_CODE));
		data.setStockCodeDES(rs.getString(DetailStock.STOCK_CODE_DES));
		data.setStockName(rs.getString(DetailStock.STOCK_NAME));
		data.setTodayOpen(rs.getDouble(DetailStock.TODAY_OPEN));
		data.setYesterdayClose(rs.getDouble(DetailStock.YESTERDAY_CLOSE));
		data.setCurrent(rs.getDouble(DetailStock.CURRENT));
		data.setTodayHigh(rs.getDouble(DetailStock.TODAY_HIGH));
		data.setTodayLow(rs.getDouble(DetailStock.TODAY_LOW));
		data.setTradedStockNumber(rs.getLong(DetailStock.TRADED_STOCK_NUMBER));
		data.setTradedAmount(rs.getFloat(DetailStock.TRADED_AMOUNT));
		data.setChangeRate(rs.getDouble(DetailStock.CHANGE_RATE));
		data.setChangeRateDES(rs.getString(DetailStock.CHANGE_RATE_DES));
		data.setTurnoverRate(rs.getDouble(DetailStock.TURNOVER_RATE));
		data.setTurnoverRateDES(rs.getString(DetailStock.TURNOVER_RATE_DES));
		data.setTradedTime(rs.getTimestamp(DetailStock.TRADED_TIME));
		data.setInputTime(rs.getTimestamp(DetailStock.INPUT_TIME));
		return data;
	}

	protected AllDetailStock getAllDetailStockFromResult(ResultSet rs) throws SQLException {

		AllDetailStock data = new AllDetailStock();
		data.setNum(rs.getLong(DetailStock.NUM));
		data.setStockDate(rs.getDate(DetailStock.STOCK_DATE));
		data.setStockCode(rs.getString(DetailStock.STOCK_CODE));
		data.setStockCodeDES(rs.getString(DetailStock.STOCK_CODE_DES));
		data.setStockName(rs.getString(DetailStock.STOCK_NAME));
		data.setTodayOpen(rs.getDouble(DetailStock.TODAY_OPEN));
		data.setYesterdayClose(rs.getDouble(DetailStock.YESTERDAY_CLOSE));
		data.setCurrent(rs.getDouble(DetailStock.CHANGE_RATE));
		data.setTodayHigh(rs.getDouble(DetailStock.TODAY_HIGH));
		data.setTodayLow(rs.getDouble(DetailStock.TODAY_LOW));
		data.setTradedStockNumber(rs.getLong(DetailStock.TRADED_STOCK_NUMBER));
		data.setTradedAmount(rs.getFloat(DetailStock.TRADED_AMOUNT));
		data.setChangeRate(rs.getDouble(DetailStock.CHANGE_RATE));
		data.setChangeRateDES(rs.getString(DetailStock.CHANGE_RATE_DES));
		data.setTurnoverRate(rs.getDouble(DetailStock.TURNOVER_RATE));
		data.setTurnoverRateDES(rs.getString(DetailStock.TURNOVER_RATE_DES));
		data.setTradedTime(rs.getTimestamp(DetailStock.TRADED_TIME));
		data.setInputTime(rs.getTimestamp(DetailStock.INPUT_TIME));
		return data;
	}

	protected AllDetailStockTest getAllDetailStockTestFromResult(ResultSet rs) throws SQLException {

		AllDetailStockTest data = new AllDetailStockTest();
		data.setNum(rs.getLong(DetailStock.NUM));
		data.setStockDate(rs.getDate(DetailStock.STOCK_DATE));
		data.setStockCode(rs.getString(DetailStock.STOCK_CODE));
		data.setStockCodeDES(rs.getString(DetailStock.STOCK_CODE_DES));
		data.setStockName(rs.getString(DetailStock.STOCK_NAME));
		data.setTodayOpen(rs.getDouble(DetailStock.TODAY_OPEN));
		data.setYesterdayClose(rs.getDouble(DetailStock.YESTERDAY_CLOSE));
		data.setCurrent(rs.getDouble(DetailStock.CHANGE_RATE));
		data.setTodayHigh(rs.getDouble(DetailStock.TODAY_HIGH));
		data.setTodayLow(rs.getDouble(DetailStock.TODAY_LOW));
		data.setTradedStockNumber(rs.getLong(DetailStock.TRADED_STOCK_NUMBER));
		data.setTradedAmount(rs.getFloat(DetailStock.TRADED_AMOUNT));
		data.setChangeRate(rs.getDouble(DetailStock.CHANGE_RATE));
		data.setChangeRateDES(rs.getString(DetailStock.CHANGE_RATE_DES));
		data.setTurnoverRate(rs.getDouble(DetailStock.TURNOVER_RATE));
		data.setTurnoverRateDES(rs.getString(DetailStock.TURNOVER_RATE_DES));
		data.setTradedTime(rs.getTimestamp(DetailStock.TRADED_TIME));
		data.setInputTime(rs.getTimestamp(DetailStock.INPUT_TIME));
		return data;
	}
	
	/**
	 * 开始事务
	 *
	 */
	@Override
	protected void beginTransaction() {
		if (this.connection != null) {
			try {
				if (this.connection.getAutoCommit()) {
					this.connection.setAutoCommit(false);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
	}

	/**
	 * 提交事务
	 *
	 */
	@Override
	protected void commitTransaction() {
		if (this.connection != null) {
			try {
				if (!this.connection.getAutoCommit()) {
					this.connection.commit();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
	}
		
	/**
	 * 回滚事务
	 *
	 */
	@Override
	protected void rollBackTransaction() {
		if (this.connection != null) {
			try {
				if (!this.connection.getAutoCommit()) {
					this.connection.rollback();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
	}
		
	/**
	 * 还原连接状态
	 * 
	 */
	@Override
	protected void resetConnection() {
		if (this.connection != null) {
			try {
				if (!this.connection.getAutoCommit()) {
					this.connection.setAutoCommit(true);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
	}

	@Override
	public void close() {
		this.close(null, null, this.connection);
		super.closeConnection();
	}

	@Override
	public void close(PreparedStatement state) {
		close(null, state, null);
	}

	@Override
	protected void close(ResultSet result, PreparedStatement statement) {
		close(result, statement, null);
	}

	@Override
	public void close(ResultSet rs, PreparedStatement ps, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException ex) {
				System.out.println("关闭ResultSet失败！");
				log.loger.error("关闭ResultSet失败！");
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
		if (ps != null) {
			try {
				ps.close();
				ps = null;
			} catch (SQLException ex) {
				System.out.println("关闭PreparedStatement失败！");
				log.loger.error("关闭PreparedStatement失败！");
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException ex) {
				System.out.println("关闭Connection失败！");
				log.loger.error("关闭Connection失败！");
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
	}
}