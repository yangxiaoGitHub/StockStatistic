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
import cn.db.bean.StatisticStock;

public class StatisticStockDao extends OperationDao {

	/**
	 * 查询所有统计股票的信息
	 * 
	 */
	public List<StatisticStock> listStatisticStock() throws SQLException {

		List<StatisticStock> statisticStockList = new ArrayList<StatisticStock>();
		String sql = "select " + StatisticStock.ALL_FIELDS + " from " + StatisticStock.TABLE_NAME + " order by " + StatisticStock.NUM;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticStock statisticStock = getStatisticStockFromResult(rs);
			statisticStockList.add(statisticStock);
		}
		close(rs, state);
		return statisticStockList;
	}

	public Long getMaxNumFromStatisticStock() throws SQLException {

		long maxNum = 0;
		String sql = "select max(" + (StatisticStock.NUM) + ") as num_ from " + StatisticStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		close(rs, state);
		return maxNum;
	}

	public boolean isExistInStatisticStock(String stockCode) throws SQLException {

		int count = 0;
		String sql = "select count(" + StatisticStock.NUM + ") as count_ from " + StatisticStock.TABLE_NAME + " where " + StatisticStock.STOCK_CODE
				+ "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setString(1, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		close(rs, state);
		return count == 0 ? false : true;
	}
	
	public boolean updateFirstDate(String stockCode, Date firstDate) {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + StatisticStock.TABLE_NAME + " set " + StatisticStock.FIRST_DATE + "=?" + " where " + StatisticStock.STOCK_CODE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setDate(1, new java.sql.Date(firstDate.getTime()));
			state.setString(2, stockCode);
			state.executeUpdate();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + " 表(statistic_stock_)更新股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的字段(first_date_)失败！");
			log.loger.error(" 表(statistic_stock_)更新股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")的字段(first_date_)失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}

	public boolean saveStatisticStock(StatisticStock statistic) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + StatisticStock.TABLE_NAME + " (" + StatisticStock.ALL_FIELDS + ") values (?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setLong(1, statistic.getNum());
			state.setString(2, statistic.getStockCode());
			state.setString(3, statistic.getStockCodeDES());
			state.setDate(4, new java.sql.Date(statistic.getFirstDate().getTime()));
			state.setTimestamp(5, new java.sql.Timestamp(statistic.getInputTime().getTime()));
			state.setString(6, statistic.getNote());
			state.executeUpdate();
			commitTransaction();
			saveFlg = true;
		} catch (Exception e) {
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中增加记录失败--->股票代码：" + statistic.getStockCode()
					+ "(" + PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			log.loger.error("  统计股票信息表(statistic_stock_)中增加记录失败--->股票代码：" + statistic.getStockCode() + "("
					+ PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}

	public StatisticStock getStatisticStockByStockCode(String stockCode) throws SQLException {

		StatisticStock statisticStock = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(StatisticStock.ALL_FIELDS).append(" from ").append(StatisticStock.TABLE_NAME).append(" where ")
				.append(StatisticStock.STOCK_CODE).append("=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			statisticStock = getStatisticStockFromResult(rs);
		}
		close(rs, state);
		return statisticStock;
	}
	
	private StatisticStock getStatisticStockFromResult(ResultSet rs) throws SQLException {
		StatisticStock data = new StatisticStock();
		data.setNum(rs.getLong(StatisticStock.NUM));
		data.setStockCode(rs.getString(StatisticStock.STOCK_CODE));
		data.setFirstDate(rs.getDate(StatisticStock.FIRST_DATE));
		data.setStockCodeDES(rs.getString(StatisticStock.STOCK_CODE_DES));
		data.setInputTime(rs.getDate(StatisticStock.INPUT_TIME));
		data.setNote(rs.getString(StatisticStock.NOTE));
		return data;
	}
}
