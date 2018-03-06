package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.AllDetailStock;
import cn.db.bean.DetailStock;

public class AllDetailStockDao extends OperationDao {

	//private static AllDetailStockDao instance;

	public AllDetailStockDao() {
		
	}

	/*private AllDetailStockDao() {

	}*/

	/*public static AllDetailStockDao getInstance() throws SQLException {

		if (instance == null) {
			instance = new AllDetailStockDao();
		} else if (instance.connectionIsNull()) {
			instance.openConnection();
		}
		return instance;
	}*/
	
	public boolean saveOrUpdateAllDetailStock(AllDetailStock allDetailStock) throws SQLException {

		boolean saveFlg = false;
		boolean isExist = isExistInAllDetailStock(allDetailStock.getStockCode(), allDetailStock.getStockDate());
		if (!isExist) {
			saveFlg = saveAllDetailStock(allDetailStock);
		}
		return saveFlg;
	}

	public boolean isExistInAllDetailStock(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + AllDetailStock.NUM + ") as count_ from " + AllDetailStock.TABLE_NAME + " where " + AllDetailStock.STOCK_CODE
				+ "=? and " + AllDetailStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		super.close(rs, state);
		return count == 0 ? false : true;
	}

	public boolean saveAllDetailStock(AllDetailStock allDetailStock) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + AllDetailStock.TABLE_NAME + " (" + AllDetailStock.ALL_FIELDS
				+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setLong(1, allDetailStock.getNum());
			state.setDate(2, new java.sql.Date(allDetailStock.getStockDate().getTime()));
			state.setString(3, allDetailStock.getStockCode());
			state.setString(4, allDetailStock.getStockCodeDES());
			state.setString(5, allDetailStock.getStockName());
			state.setDouble(6, allDetailStock.getTodayOpen());
			state.setDouble(7, allDetailStock.getYesterdayClose());
			state.setDouble(8, allDetailStock.getCurrent());
			state.setDouble(9, allDetailStock.getTodayHigh());
			state.setDouble(10, allDetailStock.getTodayLow());
			state.setLong(11, allDetailStock.getTradedStockNumber());
			state.setFloat(12, allDetailStock.getTradedAmount());
			state.setDouble(13, allDetailStock.getChangeRate());
			state.setString(14, allDetailStock.getChangeRateDES());
			state.setDouble(15, allDetailStock.getTurnoverRate());
			state.setString(16, allDetailStock.getTurnoverRateDES());
			Date tradedTime = allDetailStock.getTradedTime(); // == null ? allDetailStock.getStockDate() : allDetailStock.getTradedTime();
			state.setTimestamp(17, new java.sql.Timestamp(tradedTime.getTime()));
			state.setTimestamp(18, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			// super.connection.commit();
			commitTransaction();
			saveFlg = true;
		} catch (Exception e) {
			// super.connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(allDetailStock.getStockDate())
					+ " 所有股票详细信息表(all_detail_stock_)增加股票信息(" + allDetailStock.getStockCode() + ")失败！");
			log.loger.error(DateUtils.dateToString(allDetailStock.getStockDate()) + " 所有股票详细信息表(all_detail_stock_)增加股票信息("
					+ allDetailStock.getStockCode() + ")失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}
	
	public List<AllDetailStock> getAllDetailStockByCodesAndTime(String stockCodes, Date[] startEndDate) throws SQLException {

		List<AllDetailStock> allDetailStockList = new ArrayList<AllDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllDetailStock.ALL_FIELDS).append(" from ").append(AllDetailStock.TABLE_NAME).append(" where ")
		   .append(AllDetailStock.STOCK_CODE).append(" in (").append(stockCodes).append(") and ").append(AllDetailStock.STOCK_DATE)
		   .append(" between ? and ?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(startEndDate[0].getTime()));
		state.setDate(2, new java.sql.Date(startEndDate[1].getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			AllDetailStock data = getAllDetailStockFromResult(rs);
			allDetailStockList.add(data);
		}
		super.close(rs, state);
		return allDetailStockList;
	}

	public Long getMaxNumFromAllDetailStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(AllDetailStock.NUM).append(") as num_ from ").append(AllDetailStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		super.close(rs, state);
		return maxNum;
	}

	public AllDetailStock getAllDetailStockByKey(Date stockDate, String stockCode) throws SQLException {

		AllDetailStock data = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllDetailStock.ALL_FIELDS).append(" from ").append(AllDetailStock.TABLE_NAME).append(" where ")
				.append(AllDetailStock.STOCK_DATE).append("=? and ").append(AllDetailStock.STOCK_CODE).append("=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(stockDate.getTime()));
		state.setString(2, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			data = getAllDetailStockFromResult(rs);
		}
		super.close(rs, state);
		return data;
	}

	public List<AllDetailStock> getAllDetailStockByStockDate(Date date) throws SQLException {

		List<AllDetailStock> allDetailStockList = new ArrayList<AllDetailStock>();
		String sql = "select " + AllDetailStock.ALL_FIELDS + " from " + AllDetailStock.TABLE_NAME + " where " + AllDetailStock.STOCK_DATE
				+ "=? order by " + AllDetailStock.NUM + ", " + AllDetailStock.INPUT_TIME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(date.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			AllDetailStock allDetailStock = getAllDetailStockFromResult(rs);
			allDetailStockList.add(allDetailStock);
		}
		super.close(rs, state);
		return allDetailStockList;
	}

	public boolean updateTurnoverRate(AllDetailStock allDetailStock, Double turnoverRate, String turnoverRateDES) throws Exception {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllDetailStock.TABLE_NAME + " set " + AllDetailStock.TURNOVER_RATE + "=?, " + AllDetailStock.TURNOVER_RATE_DES
				+ "=?, " + AllDetailStock.INPUT_TIME + "=?  where " + AllDetailStock.STOCK_CODE + "=? and " + AllDetailStock.STOCK_DATE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setDouble(1, turnoverRate);
			state.setString(2, turnoverRateDES);
			state.setTimestamp(3, new java.sql.Timestamp(allDetailStock.getStockDate().getTime()));
			state.setString(4, allDetailStock.getStockCode());
			state.setDate(5, new java.sql.Date(allDetailStock.getStockDate().getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(allDetailStock.getStockDate())
					+ "所有股票详细信息表(all_detail_stock_)更新股票" + allDetailStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(allDetailStock.getStockCode()) + ")的换手率失败！");
			log.loger.error(DateUtils.dateToString(allDetailStock.getStockDate()) + "所有股票详细信息表(all_detail_stock_)更新股票"
					+ allDetailStock.getStockCode() + "(" + PropertiesUtils.getProperty(allDetailStock.getStockCode()) + ")的换手率失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
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
}
