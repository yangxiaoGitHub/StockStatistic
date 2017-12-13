package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.StatisticStock;

import com.mysql.jdbc.PreparedStatement;

public class StatisticStockDao extends OperationDao {

	/**
	 * 查询所有统计股票的信息
	 * 
	 */
	public List<StatisticStock> listStatisticStock() throws SQLException {

		List<StatisticStock> statisticStockList = new ArrayList<StatisticStock>();
		String sql = "select " + StatisticStock.ALL_FIELDS + " from " + StatisticStock.TABLE_NAME 
				   + " order by " + StatisticStock.UP_DOWN_NUMBER + " desc, " + StatisticStock.NUM;
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticStock statisticStock = getStatisticStockFromResult(rs);
			statisticStockList.add(statisticStock);
		}
		return statisticStockList;
	}

	public boolean updateUpDownNumber(String stockCode, StatisticStock statisticStock) {

		boolean flg = false;
		PreparedStatement state = null;
		String sql = "update " + StatisticStock.TABLE_NAME + " set " + StatisticStock.UP_DOWN_NUMBER + "=? where " + StatisticStock.STOCK_CODE + "=?";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			state.setInt(1, statisticStock.getUpDownNumber());
			state.setString(2, stockCode);
			state.executeUpdate();
			//conn.commit();
			flg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(up_down_number_)失败！");
			log.loger.error("  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(up_down_number_)失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return flg;
	}
	
	public boolean updateUpNumber(String stockCode, StatisticStock statisticStock) {

		boolean flg = false;
		PreparedStatement state = null;
		String sql = "update " + StatisticStock.TABLE_NAME + " set " + StatisticStock.UP_NUMBER + "=? where " + StatisticStock.STOCK_CODE + "=?";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			state.setInt(1, statisticStock.getUpNumber());
			state.setString(2, stockCode);
			state.executeUpdate();
			//conn.commit();
			flg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(up_number_)失败！");
			log.loger.error("  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(up_number_)失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return flg;
	}

	public boolean updateDownNumber(String stockCode, StatisticStock statisticStock) {

		boolean flg = false;
		PreparedStatement state = null;
		String sql = "update " + StatisticStock.TABLE_NAME + " set " + StatisticStock.DOWN_NUMBER + "=? where " + StatisticStock.STOCK_CODE + "=?";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			state.setInt(1, statisticStock.getDownNumber());
			state.setString(2, stockCode);
			state.executeUpdate();
			//conn.commit();
			flg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(down_number_)失败！");
			log.loger.error("  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(down_number_)失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return flg;
	}

	public Long getMaxNumFromStatisticStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(StatisticStock.NUM).append(") as num_ from ").append(StatisticStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		return maxNum;
	}
	
	public boolean isExistInStatisticStock(String stockCode) throws SQLException {

		int count = 0;
		String sql = "select count(" + StatisticStock.NUM + ") as count_ from " + StatisticStock.TABLE_NAME + " where " + StatisticStock.STOCK_CODE + "=?";
		PreparedStatement state = (PreparedStatement) conn.prepareStatement(sql);
		state.setString(1, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		return count == 0 ? false : true;
	}
	
	public boolean updateStatisticStock(StatisticStock statistic) {
		
		boolean updateFlg = false;
		PreparedStatement state = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update ").append(StatisticStock.TABLE_NAME).append(" set ").append(StatisticStock.UP_DOWN_NUMBER)
				 .append("=?, ").append(StatisticStock.UP_NUMBER).append("=?, ").append(StatisticStock.DOWN_NUMBER).append("=?, ")
				 .append(StatisticStock.ONE_WEEK).append("=?, ").append(StatisticStock.HALF_MONTH).append("=?, ").append(StatisticStock.ONE_MONTH)
				 .append("=?, ").append(StatisticStock.TWO_MONTH).append("=?, ").append(StatisticStock.THREE_MONTH).append("=?, ")
				 .append(StatisticStock.HALF_YEAR).append("=?, ").append(StatisticStock.ONE_YEAR).append("=?, ").append(StatisticStock.INPUT_TIME)
				 .append("=? where ").append(StatisticStock.STOCK_CODE).append("=?");
		try {
			state = (PreparedStatement) conn.prepareStatement(sqlBuffer.toString());
			state.setInt(1, statistic.getUpDownNumber());
			state.setInt(2, statistic.getUpNumber());
			state.setInt(3, statistic.getDownNumber());
			state.setString(4, statistic.getOneWeek());
			state.setString(5, statistic.getHalfMonth());
			state.setString(6, statistic.getOneMonth());
			state.setString(7, statistic.getTwoMonth());
			state.setString(8, statistic.getThreeMonth());
			state.setString(9, statistic.getHalfYear());
			state.setString(10, statistic.getOneYear());
			state.setTimestamp(11, new java.sql.Timestamp((new Date()).getTime()));
			state.setString(12, statistic.getStockCode());
			state.executeUpdate();
			//conn.commit();
			updateFlg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中更新记录失败---股票代码：" + statistic.getStockCode() + "(" + PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			log.loger.error("  统计股票信息表(statistic_stock_)中更新记录失败---股票代码：" + statistic.getStockCode() + "(" + PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return updateFlg;
	}
	
	public boolean saveStatisticStock(StatisticStock statistic) {
		
		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + StatisticStock.TABLE_NAME + " (" + StatisticStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			state = (PreparedStatement) conn.prepareStatement(sql);
			state.setLong(1, statistic.getNum());
			state.setString(2, statistic.getStockCode());
			state.setDate(3, new java.sql.Date(statistic.getFirstDate().getTime()));
			state.setInt(4, statistic.getUpDownNumber());
			state.setInt(5, statistic.getUpNumber());
			state.setInt(6, statistic.getDownNumber());
			state.setString(7, statistic.getOneWeek());
			state.setString(8, statistic.getHalfMonth());
			state.setString(9, statistic.getOneMonth());
			state.setString(10, statistic.getTwoMonth());
			state.setString(11, statistic.getThreeMonth());
			state.setString(12, statistic.getHalfYear());
			state.setString(13, statistic.getOneYear());
			state.setString(14, statistic.getStockCodeDES());
			state.setTimestamp(15, new java.sql.Timestamp((new Date()).getTime()));
			state.setString(16, statistic.getNote());
			state.executeUpdate();
			//conn.commit();
			saveFlg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中添加记录失败---股票代码：" + statistic.getStockCode() + "(" + PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			log.loger.error("  统计股票信息表(statistic_stock_)中添加记录失败---股票代码：" + statistic.getStockCode() + "(" + PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return saveFlg;
	}
	
	/**
	 * 更新所有json字段数据
	 * 
	 */
	public boolean updateAllJsonRow(StatisticStock statisticStock) {
		
		boolean flg = false;
		PreparedStatement state = null;
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(StatisticStock.TABLE_NAME).append(" set ").append(StatisticStock.ONE_WEEK).append("=?, ")
		   .append(StatisticStock.HALF_MONTH).append("=?, ").append(StatisticStock.ONE_MONTH).append("=?, ").append(StatisticStock.TWO_MONTH)
		   .append("=?, ").append(StatisticStock.THREE_MONTH).append("=?, ").append(StatisticStock.HALF_YEAR).append("=?, ")
		   .append(StatisticStock.ONE_YEAR).append("=?, ").append(StatisticStock.INPUT_TIME).append("=? where ").append(StatisticStock.STOCK_CODE)
		   .append("=?");
		try {
			state = (PreparedStatement) conn.prepareStatement(sql.toString());
			state.setString(1, statisticStock.getOneWeek());
			state.setString(2, statisticStock.getHalfMonth());
			state.setString(3, statisticStock.getOneMonth());
			state.setString(4, statisticStock.getTwoMonth());
			state.setString(5, statisticStock.getThreeMonth());
			state.setString(6, statisticStock.getHalfYear());
			state.setString(7, statisticStock.getOneYear());
			state.setString(8, statisticStock.getStockCode());
			state.executeUpdate();
			//conn.commit();
			flg = true;
		} catch (Exception e) {
			System.out.println(DateUtils.DateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中股票" + statisticStock.getStockCode() + "(" + PropertiesUtils.getProperty(statisticStock.getStockCode()) + ")更新Json字段数据失败！");
			log.loger.error("  统计股票信息表(statistic_stock_)中股票" + statisticStock.getStockCode() + "(" + PropertiesUtils.getProperty(statisticStock.getStockCode()) + ")更新Json字段数据失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			close(state);
		}
		return flg;
	}

	public boolean saveOrUpdateStatisticStock(StatisticStock statisticStock) throws SQLException {

		boolean saveUpdateFlg = false;
		if (!isExistInStatisticStock(statisticStock.getStockCode())) {
			saveStatisticStock(statisticStock);
			saveUpdateFlg = true;
		} else {
			updateStatisticStock(statisticStock);
			saveUpdateFlg = true;
		}
		return saveUpdateFlg;
	}
}
