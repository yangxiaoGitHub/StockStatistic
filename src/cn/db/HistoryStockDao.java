package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.HistoryStock;

import com.mysql.jdbc.PreparedStatement;

public class HistoryStockDao extends OperationDao {
	
	public boolean saveHistoryStock(HistoryStock historyStock) {
		
		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + HistoryStock.TABLE_NAME + " (" + HistoryStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?)";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			state.setLong(1, historyStock.getNum());
			state.setDate(2, new java.sql.Date(historyStock.getStockDate().getTime()));
			state.setString(3, historyStock.getStockCode());
			state.setDouble(4, historyStock.getOpenPrice());
			state.setDouble(5, historyStock.getHighPrice());
			state.setDouble(6, historyStock.getLowPrice());
			state.setDouble(7, historyStock.getClosePrice());
			state.setLong(8, historyStock.getTradedStockNumber());
			state.setFloat(9, historyStock.getTradedAmount());
			state.setTimestamp(10, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			//conn.commit();
			saveFlg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + " 历史股票信息表(history_stock_)中添加记录失败---股票代码：" + historyStock.getStockCode() + "(" + PropertiesUtils.getProperty(historyStock.getStockCode()) + ")");
			log.loger.error(" 历史股票信息表(history_stock_)中添加记录失败---股票代码：" + historyStock.getStockCode() + "(" + PropertiesUtils.getProperty(historyStock.getStockCode()) + ")");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return saveFlg;
	}

	public boolean isExistInHistoryStock(String stockCode, Date stockDate) throws SQLException {
		
		int count = 0;
		String sql = "select count(" + HistoryStock.NUM + ") as count_ from " + HistoryStock.TABLE_NAME + " where " 
				   + HistoryStock.STOCK_CODE + "=? and " + HistoryStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		return count == 0 ? false : true;
	}

	public Long getMaxNumFromHistoryStock() throws SQLException {
		
		long maxNum = 0;
		String sql = "select max(" + HistoryStock.NUM + ") as num_ from " + HistoryStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getInt("num_");
		}
		return maxNum;
	}

	public Date[] getMinMaxDate(String stockCode) throws SQLException {
		
		Date[] dateArray = new Date[2];
		String sql = "select min(" + HistoryStock.STOCK_DATE + ") as min_date_, max(" + HistoryStock.STOCK_DATE + ") as max_date_ from " 
				   + HistoryStock.TABLE_NAME + " where " + HistoryStock.STOCK_CODE + "=?";
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setString(1, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			dateArray[0] = rs.getDate("min_date_");
			dateArray[1] = rs.getDate("max_date_");
		}
		return dateArray;
	}

	public List<HistoryStock> getHistoryStockByStockDate(Date date) throws SQLException {

		List<HistoryStock> historyStockList = new ArrayList<HistoryStock>();
		String sql = "select " + HistoryStock.ALL_FIELDS + " from " + HistoryStock.TABLE_NAME + " where " + HistoryStock.STOCK_DATE
				+ "=? order by " + HistoryStock.INPUT_TIME;
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(date.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			HistoryStock historyStock = getHistoryStockFromResult(rs);
			historyStockList.add(historyStock);
		}
		return historyStockList;
	}
}
