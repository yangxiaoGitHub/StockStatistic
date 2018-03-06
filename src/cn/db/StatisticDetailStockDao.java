package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;
import com.mysql.jdbc.PreparedStatement;

public class StatisticDetailStockDao extends OperationDao {

	/**
	 * 查询所有统计股票的信息
	 * 
	 */
	public List<StatisticDetailStock> listStatisticDetailStock() throws SQLException {

		List<StatisticDetailStock> statisticDetailStockList = new ArrayList<StatisticDetailStock>();
		String sql = "select " + StatisticDetailStock.ALL_FIELDS + " from " + StatisticDetailStock.TABLE_NAME + " order by " + StatisticDetailStock.UP_DOWN_NUMBER
				+ " desc, " + StatisticDetailStock.NUM;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = getStatisticDetailStockFromResult(rs);
			statisticDetailStockList.add(statisticDetailStock);
		}
		close(rs, state);
		return statisticDetailStockList;
	}
	
	/**
	 * 查询最近统计股票详细信息
	 *
	 */
	public List<StatisticDetailStock> listRecentStatisticDetailStock() throws SQLException {
		
		List<StatisticDetailStock> statisticDetailStockList = new ArrayList<StatisticDetailStock>();
		String sql = "select " + StatisticDetailStock.ALL_ALIAS_FIELDS + " from " + StatisticDetailStock.TABLE_NAME + " as "
				+ StatisticDetailStock.TABLE_ALIAS_NAME + " inner join (select " + StatisticDetailStock.STOCK_CODE + ", max("
				+ StatisticDetailStock.STOCK_DATE + ") as recent_date_ from " + StatisticDetailStock.TABLE_NAME + " group by "
				+ StatisticDetailStock.STOCK_CODE + ") as statistic on (" + StatisticDetailStock.TABLE_ALIAS_NAME + "."
				+ StatisticDetailStock.STOCK_CODE + " = statistic." + StatisticDetailStock.STOCK_CODE + " and "
				+ StatisticDetailStock.TABLE_ALIAS_NAME + "." + StatisticDetailStock.STOCK_DATE + " = statistic.recent_date_) order by "
				+ StatisticDetailStock.TABLE_ALIAS_NAME + "." + StatisticDetailStock.NUM;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = getStatisticDetailStockFromResult(rs);
			statisticDetailStockList.add(statisticDetailStock);
		}
		close(rs, state);
		return statisticDetailStockList;
	}

	/*public boolean updateUpDownNumber(String stockCode, StatisticDetailStock statisticDetailStock) throws SQLException {

		boolean flg = false;
		PreparedStatement state = null;
		String sql = "update " + StatisticDetailStock.TABLE_NAME + " set " + StatisticDetailStock.UP_DOWN_NUMBER + "=? where " + StatisticDetailStock.STOCK_CODE
				+ "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setInt(1, statisticDetailStock.getUpDownNumber());
			state.setString(2, stockCode);
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			flg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中股票" + stockCode + "("
					+ PropertiesUtils.getProperty(stockCode) + ")更新字段(up_down_number_)失败！");
			log.loger.error(
					"  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(up_down_number_)失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return flg;
	}*/

	/*public boolean updateUpNumber(String stockCode, StatisticDetailStock statisticDetailStock) throws SQLException {

		boolean flg = false;
		PreparedStatement state = null;
		String sql = "update " + StatisticDetailStock.TABLE_NAME + " set " + StatisticDetailStock.UP_NUMBER + "=? where " + StatisticDetailStock.STOCK_CODE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setInt(1, statisticDetailStock.getUpNumber());
			state.setString(2, stockCode);
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			flg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中股票" + stockCode + "("
					+ PropertiesUtils.getProperty(stockCode) + ")更新字段(up_number_)失败！");
			log.loger.error("  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(up_number_)失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return flg;
	}*/

	/*public boolean updateDownNumber(String stockCode, StatisticDetailStock statisticDetailStock) throws SQLException {

		boolean flg = false;
		PreparedStatement state = null;
		String sql = "update " + StatisticDetailStock.TABLE_NAME + " set " + StatisticDetailStock.DOWN_NUMBER + "=? where " + StatisticDetailStock.STOCK_CODE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setInt(1, statisticDetailStock.getDownNumber());
			state.setString(2, stockCode);
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			flg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  统计股票信息表(statistic_stock_)中股票" + stockCode + "("
					+ PropertiesUtils.getProperty(stockCode) + ")更新字段(down_number_)失败！");
			log.loger
					.error("  统计股票信息表(statistic_stock_)中股票" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")更新字段(down_number_)失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return flg;
	}*/

	public Long getMaxNumFromStatisticDetailStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(StatisticDetailStock.NUM).append(") as num_ from ").append(StatisticDetailStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		close(rs, state);
		return maxNum;
	}

	public boolean isExistInStatisticDetailStock(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + StatisticDetailStock.NUM + ") as count_ from " + StatisticDetailStock.TABLE_NAME 
				   + " where " + StatisticDetailStock.STOCK_CODE + "=? and " + StatisticDetailStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setTimestamp(2, new java.sql.Timestamp(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		close(rs, state);
		return count == 0 ? false : true;
	}

	public boolean updateStatisticDetailStock(StatisticDetailStock statistic) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update ").append(StatisticDetailStock.TABLE_NAME).append(" set ").append(StatisticDetailStock.STOCK_DATE).append("=?, ")
				.append(StatisticDetailStock.UP_DOWN_NUMBER).append("=?, ").append(StatisticDetailStock.UP_NUMBER).append("=?, ")
				.append(StatisticDetailStock.DOWN_NUMBER).append("=?, ").append(StatisticDetailStock.ONE_WEEK).append("=?, ")
				.append(StatisticDetailStock.HALF_MONTH).append("=?, ").append(StatisticDetailStock.ONE_MONTH).append("=?, ")
				.append(StatisticDetailStock.TWO_MONTH).append("=?, ").append(StatisticDetailStock.THREE_MONTH).append("=?, ")
				.append(StatisticDetailStock.HALF_YEAR).append("=?, ").append(StatisticDetailStock.ONE_YEAR).append("=?, ")
				.append(StatisticDetailStock.INPUT_TIME).append("=? where ").append(StatisticDetailStock.STOCK_CODE).append("=? ").append("and ")
				.append(StatisticDetailStock.STOCK_DATE).append("=?");
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sqlBuffer.toString());
			state.setDate(1, new java.sql.Date(statistic.getStockDate().getTime()));
			state.setInt(2, statistic.getUpDownNumber());
			state.setInt(3, statistic.getUpNumber());
			state.setInt(4, statistic.getDownNumber());
			state.setString(5, statistic.getOneWeek());
			state.setString(6, statistic.getHalfMonth());
			state.setString(7, statistic.getOneMonth());
			state.setString(8, statistic.getTwoMonth());
			state.setString(9, statistic.getThreeMonth());
			state.setString(10, statistic.getHalfYear());
			state.setString(11, statistic.getOneYear());
			state.setTimestamp(12, new java.sql.Timestamp(statistic.getInputTime().getTime()));
			state.setString(13, statistic.getStockCode());
			state.setDate(14, new java.sql.Date(statistic.getStockDate().getTime()));
			state.executeUpdate();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)更新记录失败，股票代码：" + statistic.getStockCode()
					+ "(" + PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			log.loger.error("----->表(statistic_detail_stock_)更新记录失败，股票代码：" + statistic.getStockCode() + "("
					+ PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}

	public boolean saveStatisticDetailStock(StatisticDetailStock statistic) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + StatisticDetailStock.TABLE_NAME + " (" + StatisticDetailStock.ALL_FIELDS
				+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setLong(1, statistic.getNum());
			state.setString(2, statistic.getStockCode());
			state.setDate(3, new java.sql.Date(statistic.getStockDate().getTime()));
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
			commitTransaction();
			saveFlg = true;
		} catch (Exception e) {
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + " 表(statistic_detail_stock_)中增加记录失败--->股票代码：" + statistic.getStockCode()
					+ "(" + PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			log.loger.error(" 表(statistic_stock_)中增加记录失败--->股票代码：" + statistic.getStockCode() + "("
					+ PropertiesUtils.getProperty(statistic.getStockCode()) + ")");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}

	/**
	 * 更新所有json字段数据
	 * 
	 */
	public boolean updateAllJsonRow(StatisticDetailStock statisticDetailStock) throws SQLException {

		boolean flg = false;
		PreparedStatement state = null;
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(StatisticDetailStock.TABLE_NAME)
				.append(StatisticDetailStock.ONE_WEEK).append("=?, ").append(StatisticDetailStock.HALF_MONTH).append("=?, ").append(StatisticDetailStock.ONE_MONTH)
				.append("=?, ").append(StatisticDetailStock.TWO_MONTH).append("=?, ").append(StatisticDetailStock.THREE_MONTH).append("=?, ")
				.append(StatisticDetailStock.HALF_YEAR).append("=?, ").append(StatisticDetailStock.ONE_YEAR).append("=?, ").append(StatisticDetailStock.INPUT_TIME)
				.append("=? where ").append(StatisticDetailStock.STOCK_CODE).append("=?");
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
			state.setString(1, statisticDetailStock.getOneWeek());
			state.setString(2, statisticDetailStock.getHalfMonth());
			state.setString(3, statisticDetailStock.getOneMonth());
			state.setString(4, statisticDetailStock.getTwoMonth());
			state.setString(5, statisticDetailStock.getThreeMonth());
			state.setString(6, statisticDetailStock.getHalfYear());
			state.setString(7, statisticDetailStock.getOneYear());
			state.setTimestamp(8, new java.sql.Timestamp(statisticDetailStock.getInputTime().getTime()));
			state.setString(9, statisticDetailStock.getStockCode());
			state.executeUpdate();
			commitTransaction();
			flg = true;
		} catch (Exception e) {
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  表(statistic_detail_stock_)中股票" + statisticDetailStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticDetailStock.getStockCode()) + ")更新Json字段数据失败！");
			log.loger.error("  表(statistic_detail_stock_)中股票" + statisticDetailStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticDetailStock.getStockCode()) + ")更新Json字段数据失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return flg;
	}

	/*public boolean saveOrUpdateStatisticDetailStock(StatisticDetailStock statisticDetailStock) throws SQLException {

		boolean saveUpdateFlg = false;
		if (!isExistInStatisticDetailStock(statisticDetailStock.getStockCode())) {
			saveStatisticDetailStock(statisticDetailStock);
			saveUpdateFlg = true;
		} else {
			updateStatisticDetailStock(statisticDetailStock);
			saveUpdateFlg = true;
		}
		return saveUpdateFlg;
	}*/

	public StatisticDetailStock getStatisticDetailStockByStockCode(String stockCode) throws SQLException {

		StatisticDetailStock statisticDetailStock = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(StatisticDetailStock.ALL_FIELDS).append(" from ").append(StatisticDetailStock.TABLE_NAME).append(" where ")
				.append(StatisticDetailStock.STOCK_CODE).append("=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			statisticDetailStock = getStatisticDetailStockFromResult(rs);
		}
		close(rs, state);
		return statisticDetailStock;
	}

	/**
	 * according to the key(stock_code_, stock_date_), get the records of the table statistic_detail_stock_
	 *
	 */
	public StatisticDetailStock getStatisticDetailStockByKey(String stockCode, Date stockDate) throws SQLException {

		StatisticDetailStock statisticDetailStock = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(StatisticDetailStock.ALL_FIELDS).append(" from ").append(StatisticDetailStock.TABLE_NAME).append(" where ")
				.append(StatisticDetailStock.STOCK_CODE).append("=? and " + StatisticDetailStock.STOCK_DATE + "=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			statisticDetailStock = getStatisticDetailStockFromResult(rs);
		}
		close(rs, state);
		return statisticDetailStock;
	}
	
	/**
	 * according to the time, get the records of the table statistic_detail_stock_
	 *
	 */
	public List<StatisticDetailStock> listStatisticDetailStockByDate(Date[] dateArray) throws SQLException {
		
		List<StatisticDetailStock> statisticDetailStockList = new ArrayList<StatisticDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(StatisticDetailStock.ALL_FIELDS).append(" from ").append(StatisticDetailStock.TABLE_NAME).append(" where ")
		   .append(StatisticDetailStock.STOCK_DATE).append(" between ? and ?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(dateArray[0].getTime()));
		state.setDate(2, new java.sql.Date(dateArray[1].getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = getStatisticDetailStockFromResult(rs);
			statisticDetailStockList.add(statisticDetailStock);
		}
		close(rs, state);
		return statisticDetailStockList;
	}
	
	/**
	 * 查询最大最小日期
	 * 
	 */
	public Date[] getMinMaxDate() throws SQLException {

		Date[] dateArray = new Date[2];
		String sql = "select min(" + StatisticDetailStock.STOCK_DATE + ") as min_date_, max(" + StatisticDetailStock.STOCK_DATE + ") as max_date_ from "
				+ StatisticDetailStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			dateArray[0] = rs.getDate("min_date_");
			dateArray[1] = rs.getDate("max_date_");
		}
		close(rs, state);
		return dateArray;
	}
}
