package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.DetailStock;

import com.mysql.jdbc.PreparedStatement;

public class DetailStockDao extends OperationDao {

	public Long getMaxNumFromDetailStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(DetailStock.NUM).append(") as num_ from ").append(DetailStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		return maxNum;
	}
	
	public boolean saveOrUpdateDetailStock(DetailStock detailStock) throws Exception {
		
		boolean saveFlg = false;
		boolean isExist = isExistInDetailStock(detailStock.getStockCode(), detailStock.getStockDate());
		if (!isExist) {
			saveFlg = saveDetailStock(detailStock);
		}
		return saveFlg;
	}
	
	private boolean isExistInDetailStock(String stockCode, Date stockDate) throws SQLException {
		
		int count = 0;
		String sql = "select count(" + DetailStock.NUM + ") as count_ from " + DetailStock.TABLE_NAME 
				   + " where " + DetailStock.STOCK_CODE + "=? and " + DetailStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		return count == 0 ? false : true;
	}

	private boolean isExistInAllDetailStock(String stockCode, Date stockDate) throws SQLException {
		
		int count = 0;
		String sql = "select count(" + DetailStock.NUM + ") as count_ from " + DetailStock.TABLE_NAME 
				   + " where " + DetailStock.STOCK_CODE + "=? and " + DetailStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		return count == 0 ? false : true;
	}
	
	@SuppressWarnings("unused")
	private DetailStock getDetailStockByKey(String stockCode, Date stockDate) throws SQLException {
		
		DetailStock detailStock = null;
		String sql = "select " + DetailStock.ALL_FIELDS + " from " + DetailStock.TABLE_NAME + " where " + DetailStock.STOCK_CODE + "=? and "
				+ DetailStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			detailStock = getDetailStockFromResult(rs);
		}
		return detailStock;
	}
	
	private boolean saveDetailStock(DetailStock detailStock) {
		
		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + DetailStock.TABLE_NAME + " (" + DetailStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			Long maxNum = getMaxNumFromDetailStock();
			state.setLong(1, ++maxNum);
			state.setDate(2, new java.sql.Date(detailStock.getStockDate().getTime()));
			state.setString(3, detailStock.getStockCode());
			state.setString(4, detailStock.getStockCodeDES());
			state.setString(5, detailStock.getStockName());
			state.setDouble(6, detailStock.getTodayOpen());
			state.setDouble(7, detailStock.getYesterdayClose());
			state.setDouble(8, detailStock.getCurrent());
			state.setDouble(9, detailStock.getTodayHigh());
			state.setDouble(10, detailStock.getTodayLow());
			state.setLong(11, detailStock.getTradedStockNumber());
			state.setFloat(12, detailStock.getTradedAmount());
			state.setDouble(13, detailStock.getChangeRate());
			state.setString(14, detailStock.getChangeRateDES());
			state.setDouble(15, detailStock.getTurnoverRate()==null?0.0:detailStock.getTurnoverRate());
			state.setString(16, detailStock.getTurnoverRateDES());
			state.setTimestamp(17, new java.sql.Timestamp(detailStock.getTradedTime().getTime()));
			state.setTimestamp(18, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			//conn.commit();
			saveFlg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + DateUtils.Date2String(detailStock.getStockDate()) + "股票详细信息表(detail_stock_)添加股票信息(" + detailStock.getStockCode() + ")失败！");
			log.loger.error(DateUtils.Date2String(detailStock.getStockDate()) + " 股票详细信息表(detail_stock_)添加股票信息(" + detailStock.getStockCode() + ")失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return saveFlg;
	}

	public List<DetailStock> getDetailStockByStockDate(Date startDate) throws SQLException {
		
		List<DetailStock> detailStockList = new ArrayList<DetailStock>();
		String sql = "select " + DetailStock.ALL_FIELDS + " from " + DetailStock.TABLE_NAME + " where " + DetailStock.STOCK_DATE
				   + "=? order by " + DetailStock.NUM + ", " + DetailStock.INPUT_TIME;
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(startDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			DetailStock detailStock = getDetailStockFromResult(rs);
			detailStockList.add(detailStock);
		}
		return detailStockList;
	}

	public boolean updateTurnoverRate(DetailStock detailStock, Double turnoverRate, String turnoverRateDES) {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + DetailStock.TABLE_NAME + " set " + DetailStock.TURNOVER_RATE + "=?, " + DetailStock.TURNOVER_RATE_DES 
				   + "=?, " + DetailStock.INPUT_TIME + "=? where " + DetailStock.STOCK_CODE + "=? and " + DetailStock.STOCK_DATE + "=?";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			state.setDouble(1, turnoverRate);
			state.setString(2, turnoverRateDES);
			state.setTimestamp(3, new java.sql.Timestamp((new Date()).getTime()));
			state.setString(4, detailStock.getStockCode());
			state.setDate(5, new java.sql.Date(detailStock.getStockDate().getTime()));
			state.executeUpdate();
			//conn.commit();
			updateFlg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  " + DateUtils.Date2String(detailStock.getStockDate()) + " 每日选择股票详细信息表(detail_stock_)更新股票" + detailStock.getStockCode() + "(" + PropertiesUtils.getProperty(detailStock.getStockCode()) + ")的换手率失败！");
			log.loger.error(DateUtils.Date2String(detailStock.getStockDate()) + " 每日选择股票详细信息表(all_detail_stock_)更新股票" + detailStock.getStockCode() + "(" + PropertiesUtils.getProperty(detailStock.getStockCode()) + ")的换手率失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return updateFlg;
	}
}
