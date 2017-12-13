package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.AllDetailStock;

import com.mysql.jdbc.PreparedStatement;

public class AllDetailStockDao extends OperationDao {

	public boolean saveOrUpdateAllDetailStock(AllDetailStock allDetailStock) throws Exception {
		
		boolean saveFlg = false;
		boolean isExist = isExistInAllDetailStock(allDetailStock.getStockCode(), allDetailStock.getStockDate());
		if (!isExist) {
			saveFlg = saveAllDetailStock(allDetailStock);
		}
		return saveFlg;
	}

	public boolean isExistInAllDetailStock(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + AllDetailStock.NUM + ") as count_ from " + AllDetailStock.TABLE_NAME + " where " + AllDetailStock.STOCK_CODE + "=? and "
				+ AllDetailStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		return count == 0 ? false : true;
	}
	
	public boolean saveAllDetailStock(AllDetailStock allDetailStock) {
		
		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + AllDetailStock.TABLE_NAME + " (" + AllDetailStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			Long maxNum = getMaxNumFromAllDetailStock();
			state.setLong(1, ++maxNum);
			state.setDate(2, new java.sql.Date(allDetailStock.getStockDate().getTime()));
			state.setString(3, allDetailStock.getStockCode());
			state.setString(4, allDetailStock.getStockCodeDES());
			state.setString(5, allDetailStock.getStockName());
			state.setDouble(6, allDetailStock.getTodayOpen());
			state.setDouble(7, allDetailStock.getYesterdayClose()!=null?allDetailStock.getYesterdayClose():0);
			state.setDouble(8, allDetailStock.getCurrent());
			state.setDouble(9, allDetailStock.getTodayHigh());
			state.setDouble(10, allDetailStock.getTodayLow());
			state.setLong(11, allDetailStock.getTradedStockNumber());
			state.setFloat(12, allDetailStock.getTradedAmount());
			state.setDouble(13, allDetailStock.getChangeRate()!=null?allDetailStock.getChangeRate():0);
			state.setString(14, allDetailStock.getChangeRateDES()!=null?allDetailStock.getChangeRateDES():"");
			state.setDouble(15, allDetailStock.getTurnoverRate()!=null?allDetailStock.getTurnoverRate():0);
			state.setString(16, allDetailStock.getTurnoverRateDES());
			Date tradedTime = allDetailStock.getTradedTime()==null?allDetailStock.getStockDate():allDetailStock.getTradedTime();
			state.setTimestamp(17, new java.sql.Timestamp(tradedTime.getTime()));
			state.setTimestamp(18, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			conn.commit();
			saveFlg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + DateUtils.Date2String(allDetailStock.getStockDate()) + " 所有股票详细信息表(all_detail_stock_)添加股票信息(" + allDetailStock.getStockCode() + ")失败！");
			log.loger.error(DateUtils.Date2String(allDetailStock.getStockDate()) + " 所有股票详细信息表(all_detail_stock_)添加股票信息(" + allDetailStock.getStockCode() + ")失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return saveFlg;
	}

	public Long getMaxNumFromAllDetailStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(AllDetailStock.NUM).append(") as num_ from ").append(AllDetailStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		return maxNum;
	}

	public AllDetailStock getAllDetailStockByKey(String sDate, String stockCode) throws SQLException {

		AllDetailStock data = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllDetailStock.ALL_FIELDS).append(" from ").append(AllDetailStock.TABLE_NAME).append(" where ")
				.append(AllDetailStock.STOCK_DATE).append("=? and ").append(AllDetailStock.STOCK_CODE).append("=?");
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(DateUtils.String2Date(sDate).getTime()));
		state.setString(2, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			data = getAllDetailStockFromResult(rs);
		}
		return data;
	}

	public List<AllDetailStock> getAllDetailStockByStockDate(Date date) throws SQLException {

		List<AllDetailStock> allDetailStockList = new ArrayList<AllDetailStock>();
		String sql = "select " + AllDetailStock.ALL_FIELDS + " from " + AllDetailStock.TABLE_NAME + " where " + AllDetailStock.STOCK_DATE
				+ "=? order by " + AllDetailStock.NUM + ", " + AllDetailStock.INPUT_TIME;
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(date.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			AllDetailStock allDetailStock = getAllDetailStockFromResult(rs);
			allDetailStockList.add(allDetailStock);
		}
		return allDetailStockList;
	}

	public boolean updateTurnoverRate(AllDetailStock allDetailStock, Double turnoverRate, String turnoverRateDES) {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllDetailStock.TABLE_NAME + " set " + AllDetailStock.TURNOVER_RATE + "=?, " + AllDetailStock.TURNOVER_RATE_DES 
				+ "=?, " + AllDetailStock.INPUT_TIME + "=?  where " + AllDetailStock.STOCK_CODE + "=? and " + AllDetailStock.STOCK_DATE + "=?";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			state.setDouble(1, turnoverRate);
			state.setString(2, turnoverRateDES);
			state.setTimestamp(3, new java.sql.Timestamp(allDetailStock.getStockDate().getTime()));
			state.setString(4, allDetailStock.getStockCode());
			state.setDate(5, new java.sql.Date(allDetailStock.getStockDate().getTime()));
			state.executeUpdate();
			conn.commit();
			updateFlg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + DateUtils.Date2String(allDetailStock.getStockDate()) + "所有股票详细信息表(all_detail_stock_)更新股票" + allDetailStock.getStockCode() + "(" + PropertiesUtils.getProperty(allDetailStock.getStockCode()) + ")的换手率失败！");
			log.loger.error(DateUtils.Date2String(allDetailStock.getStockDate()) + "所有股票详细信息表(all_detail_stock_)更新股票" + allDetailStock.getStockCode() + "(" + PropertiesUtils.getProperty(allDetailStock.getStockCode()) + ")的换手率失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return updateFlg;
	}
}
