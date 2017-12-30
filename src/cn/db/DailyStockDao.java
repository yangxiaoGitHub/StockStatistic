package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.DailyStock;
import cn.db.bean.StatisticDetailStock;
import cn.db.bean.StatisticStock;

public class DailyStockDao extends OperationDao {

	/**
	 * 根据stockCode统计出现的日期
	 * 
	 */
	public List<Date> analysisStockDatesByStockCode(String stockCode, String startDate, String endDate, String changeFlg) throws SQLException {

		List<Date> dateList = new ArrayList<Date>();
		String changeFlgSql = "";
		if (changeFlg.equals("0") || changeFlg.equals("1")) {
			changeFlgSql = " and " + DailyStock.CHANGE_FLG + "='" + changeFlg + "'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_DATE).append(" from ").append(DailyStock.TABLE_NAME).append(" where ")
				.append(DailyStock.STOCK_CODE).append("=? and ").append(DailyStock.STOCK_DATE).append(" between ? and ?").append(changeFlgSql)
				.append(" order by ").append(DailyStock.STOCK_DATE).append(" desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(DateUtils.stringToDate(startDate).getTime()));
		state.setDate(3, new java.sql.Date(DateUtils.stringToDate(endDate).getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			Date stockDate = rs.getDate(DailyStock.STOCK_DATE);
			dateList.add(stockDate);
		}
		close(rs, state);
		return dateList;
	}

	/**
	 * 根据时间段统计每日股票数据
	 * 
	 */
	public List<DailyStock> analysisStockByTime(Date startDate, Date endDate, String changeFlg) throws SQLException {

		List<DailyStock> dataList = new ArrayList<DailyStock>();
		String changeFlgSql = "";
		if (DailyStock.CHANGE_FLG_ZERO.equals(changeFlg) || DailyStock.CHANGE_FLG_ONE.equals(changeFlg)) {
			changeFlgSql = " and " + DailyStock.CHANGE_FLG + "='" + changeFlg + "'";
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as count_").append(" from ")
				.append(DailyStock.TABLE_NAME).append(" where ").append(DailyStock.STOCK_DATE).append(" between ? and ?").append(changeFlgSql)
				.append(" group by ").append(DailyStock.STOCK_CODE).append(" order by count_ desc, ").append(DailyStock.NUM).append(" asc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(startDate.getTime()));
		state.setDate(2, new java.sql.Date(endDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			DailyStock data = new DailyStock();
			data.setStockCode(rs.getString(DailyStock.STOCK_CODE));
			data.setCount(rs.getInt("count_"));
			dataList.add(data);
		}
		close(rs, state);
		return dataList;
	}

	/**
	 * 统计每日股票数据的总涨跌次数
	 * 
	 */
	public Map<String, StatisticDetailStock> statisticUpDownInDailyStock() throws SQLException {

		Map<String, StatisticDetailStock> dataMap = new HashMap<String, StatisticDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as up_down_number_ from ")
				.append(DailyStock.TABLE_NAME).append(" group by ").append(DailyStock.STOCK_CODE).append(" order by up_down_number_ desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock();
			String stockCode = rs.getString(DailyStock.STOCK_CODE);
			statisticDetailStock.setStockCode(stockCode);
			statisticDetailStock.setUpDownNumber(rs.getInt("up_down_number_"));
			dataMap.put(stockCode, statisticDetailStock);
		}
		close(rs, state);
		return dataMap;
	}

	/**
	 * 统计每日股票数据的涨次数
	 * 
	 */
	public Map<String, StatisticDetailStock> statisticUpInDailyStock() throws SQLException {

		Map<String, StatisticDetailStock> dataMap = new HashMap<String, StatisticDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as up_number_ from ")
				.append(DailyStock.TABLE_NAME).append(" where ").append(DailyStock.CHANGE_FLG).append("=1").append(" group by ")
				.append(DailyStock.STOCK_CODE).append(" order by up_number_ desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock();
			String stockCode = rs.getString(DailyStock.STOCK_CODE);
			statisticDetailStock.setStockCode(stockCode);
			statisticDetailStock.setUpNumber(rs.getInt("up_number_"));
			dataMap.put(stockCode, statisticDetailStock);
		}
		close(rs, state);
		return dataMap;
	}

	/**
	 * 统计每日股票数据的跌次数
	 * 
	 */
	public Map<String, StatisticDetailStock> statisticDownInDailyStock() throws SQLException {

		Map<String, StatisticDetailStock> dataMap = new HashMap<String, StatisticDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as down_number_ from ")
				.append(DailyStock.TABLE_NAME).append(" where ").append(DailyStock.CHANGE_FLG).append("='0' ").append(" group by ")
				.append(DailyStock.STOCK_CODE).append(" order by down_number_ desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock();
			String stockCode = rs.getString(DailyStock.STOCK_CODE);
			statisticDetailStock.setStockCode(stockCode);
			statisticDetailStock.setDownNumber(rs.getInt("down_number_"));
			dataMap.put(stockCode, statisticDetailStock);
		}
		close(rs, state);
		return dataMap;
	}

	/**
	 * 统计每日股票数据的涨跌次数
	 * 
	 */
	public Map<String, StatisticDetailStock> statisticUpDownInDailyStock(String stockCode, Date[] startEndDate) throws SQLException {

		Map<String, StatisticDetailStock> dataMap = new HashMap<String, StatisticDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as up_down_number_ from ")
				.append(DailyStock.TABLE_NAME).append(" where ").append(DailyStock.STOCK_CODE).append("=? and ").append(DailyStock.STOCK_DATE)
				.append(" between ? and ? group by ").append(DailyStock.STOCK_CODE).append(" order by up_down_number_ desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(startEndDate[0].getTime()));
		state.setDate(3, new java.sql.Date(startEndDate[1].getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock();
			statisticDetailStock.setStockCode(stockCode);
			statisticDetailStock.setUpDownNumber(rs.getInt("up_down_number_"));
			dataMap.put(stockCode, statisticDetailStock);
		}
		close(rs, state);
		return dataMap;
	}

	/**
	 * 统计每日股票数据的涨次数
	 * 
	 */
	public Map<String, StatisticDetailStock> statisticUpInDailyStock(String stockCode, Date[] startEndDate) throws SQLException {

		Map<String, StatisticDetailStock> dataMap = new HashMap<String, StatisticDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as up_number_ from ")
				.append(DailyStock.TABLE_NAME).append(" where ").append(DailyStock.STOCK_CODE).append("=? and ").append(DailyStock.CHANGE_FLG)
				.append("='1' and ").append(DailyStock.STOCK_DATE).append(" between ? and ?").append(" group by ").append(DailyStock.STOCK_CODE)
				.append(" order by up_number_ desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(startEndDate[0].getTime()));
		state.setDate(3, new java.sql.Date(startEndDate[1].getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock();
			statisticDetailStock.setStockCode(stockCode);
			statisticDetailStock.setUpNumber(rs.getInt("up_number_"));
			dataMap.put(stockCode, statisticDetailStock);
		}
		close(rs, state);
		return dataMap;
	}

	/**
	 * 统计每日股票数据的跌次数
	 * 
	 */
	public Map<String, StatisticDetailStock> statisticDownInDailyStock(String stockCode, Date[] startEndDate) throws SQLException {

		Map<String, StatisticDetailStock> dataMap = new HashMap<String, StatisticDetailStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as down_number_ from ")
				.append(DailyStock.TABLE_NAME).append(" where ").append(DailyStock.STOCK_CODE).append("=? and ").append(DailyStock.CHANGE_FLG)
				.append("='0' and ").append(DailyStock.STOCK_DATE).append(" between ? and ?").append(" group by ").append(DailyStock.STOCK_CODE)
				.append(" order by down_number_ desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(startEndDate[0].getTime()));
		state.setDate(3, new java.sql.Date(startEndDate[1].getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock();
			statisticDetailStock.setStockCode(stockCode);
			statisticDetailStock.setDownNumber(rs.getInt("down_number_"));
			dataMap.put(stockCode, statisticDetailStock);
		}
		close(rs, state);
		return dataMap;
	}

	/**
	 * 统计每日股票数据的股票代码
	 * 
	 */
	public List<DailyStock> statisticDailyStock() throws SQLException {

		List<DailyStock> dataList = new ArrayList<DailyStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_CODE).append(", count(").append(DailyStock.STOCK_CODE).append(") as count_, min(")
				.append(DailyStock.STOCK_DATE).append(") as min_date_, max(" + DailyStock.STOCK_DATE + ") as max_date_ from ")
				.append(DailyStock.TABLE_NAME).append(" group by ").append(DailyStock.STOCK_CODE).append(" order by min_date_");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			DailyStock stock = new DailyStock();
			stock.setStockCode(rs.getString(DailyStock.STOCK_CODE));
			stock.setCount(rs.getInt("count_"));
			stock.setStockDate(rs.getDate("min_date_"));
			stock.setRecentStockDate(rs.getDate("max_date_"));
			dataList.add(stock);
		}
		close(rs, state);
		return dataList;
	}

	/**
	 * 统计每日股票数据的数量
	 * 
	 */
	public List<DailyStock> statisticDailyData() throws SQLException {

		List<DailyStock> dataList = new ArrayList<DailyStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.STOCK_DATE).append(", count(").append(DailyStock.STOCK_DATE).append(") as count_ from ")
				.append(DailyStock.TABLE_NAME).append(" group by ").append(DailyStock.STOCK_DATE).append(" order by ")
				.append(DailyStock.STOCK_DATE);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			DailyStock data = new DailyStock();
			data.setStockDate(rs.getDate(DailyStock.STOCK_DATE));
			data.setCount(rs.getInt("count_"));
			dataList.add(data);
		}
		close(rs, state);
		return dataList;
	}

	/**
	 * 根据主键查询每日股票数据
	 * 
	 */
	public DailyStock getDailyStockByKey(Date stockDate, String stockCode) throws SQLException {

		DailyStock data = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.ALL_FIELDS).append(" from ").append(DailyStock.TABLE_NAME).append(" where ")
				.append(DailyStock.STOCK_DATE).append("=? and ").append(DailyStock.STOCK_CODE).append("=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(stockDate.getTime()));
		state.setString(2, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			data = getDailyStockFromResult(rs);
		}
		close(rs, state);
		return data;
	}

	/**
	 * 查询所有的每日股票数据
	 * 
	 */
	public List<DailyStock> listDailyData() throws SQLException {

		List<DailyStock> dataList = new ArrayList<DailyStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.ALL_FIELDS).append(" from ").append(DailyStock.TABLE_NAME).append(" order by ")
				.append(DailyStock.NUM);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			DailyStock data = getDailyStockFromResult(rs);
			dataList.add(data);
		}
		close(rs, state);
		return dataList;
	}

	public boolean isExistInDailyStock(DailyStock data) throws SQLException {

		int result = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) as count_ from ").append(DailyStock.TABLE_NAME).append(" where ").append(DailyStock.STOCK_CODE)
				.append("=? and ").append(DailyStock.STOCK_DATE).append("=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, data.getStockCode());
		state.setDate(2, new java.sql.Date(data.getStockDate().getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			result = rs.getInt("count_");
		}
		close(rs, state);
		return result == 0 ? false : true;
	}

	/**
	 * 增加每日股票数据
	 * 
	 */
	public boolean saveDailyStock(DailyStock data) throws SQLException {

		boolean flg = false;
		PreparedStatement state = null;
		String sql = "insert into " + DailyStock.TABLE_NAME + " (" + DailyStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			Long maxNum = this.getMaxNumFromDailyStock();
			state.setLong(1, ++maxNum);
			state.setDate(2, new java.sql.Date(data.getStockDate().getTime()));
			state.setString(3, data.getStockCode());
			String stockCodeEncrypt = DESUtils.encryptToHex(data.getStockCode());
			state.setString(4, stockCodeEncrypt);
			state.setDouble(5, data.getChangeRate());
			String changeRateEncrypt = DESUtils.encryptToHex(data.getChangeRate().toString());
			state.setString(6, changeRateEncrypt);
			if (!DataUtils.isZeroOrNull(data.getTurnoverRate())) {
				state.setDouble(7, data.getTurnoverRate());
				String turnoverRateEncrypt = DESUtils.encryptToHex(data.getTurnoverRate().toString());
				state.setString(8, turnoverRateEncrypt);
			} else {
				state.setDouble(7, 0.0);
				state.setString(8, null);
			}
			state.setString(9, data.getChangeFlg());
			state.setString(10, data.getNote());
			state.setTimestamp(11, new java.sql.Timestamp(data.getInputTime().getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			flg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(
					DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(data.getStockDate()) + "每日股票数据(daily_stock_)增加失败！");
			log.loger.error(DateUtils.dateToString(data.getStockDate()) + " 每日股票数据(daily_stock_)增加失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return flg;
	}

	/**
	 * 查询每日股票数据最近日期
	 * 
	 */
	public Date getRecentStockDate() throws SQLException {

		Date recentDate = null;
		String sql = "select max(" + DailyStock.STOCK_DATE + ") as recent_stock_date_ from " + DailyStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			recentDate = rs.getDate("recent_stock_date_");
		}
		close(rs, state);
		return recentDate;
	}

	public List<DailyStock> searchStockByTime(String stockCode, String startDate, String endDate, String changeFlg) throws SQLException {

		List<DailyStock> dataList = new ArrayList<DailyStock>();
		String changeFlgSql = "";
		if (changeFlg.equals("0") || changeFlg.equals("1")) {
			changeFlgSql = " and " + DailyStock.CHANGE_FLG + "='" + changeFlg + "'";
		}
		String dateSql = "";
		if (!startDate.equals(DataUtils.CONSTANT_BLANK) && !endDate.equals(DataUtils.CONSTANT_BLANK)) {
			dateSql = " and " + DailyStock.STOCK_DATE + "  between ? and ?";
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.ALL_FIELDS).append(" from ").append(DailyStock.TABLE_NAME).append(" where ")
				.append(DailyStock.STOCK_CODE).append("=? ").append(dateSql).append(changeFlgSql).append(" order by ")
				.append(DailyStock.STOCK_DATE).append(" desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		if (!dateSql.equals(DataUtils.CONSTANT_BLANK)) {
			state.setDate(2, new java.sql.Date(DateUtils.stringToDate(startDate).getTime()));
			state.setDate(3, new java.sql.Date(DateUtils.stringToDate(endDate).getTime()));
		}
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			DailyStock data = getDailyStockFromResult(rs);
			dataList.add(data);
		}
		close(rs, state);
		return dataList;
	}

	public List<DailyStock> listDailyStockByChangeRateOrTurnoverRate() throws SQLException {

		List<DailyStock> dataList = new ArrayList<DailyStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(DailyStock.ALL_FIELDS).append(" from ").append(DailyStock.TABLE_NAME).append(" where ")
				.append(DailyStock.CHANGE_RATE).append("=? or ").append(DailyStock.TURNOVER_RATE).append("=? ").append(" order by ")
				.append(DailyStock.STOCK_DATE).append(" desc");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDouble(1, DataUtils.CONSTANT_DOUBLE_ZERO);
		state.setDouble(2, DataUtils.CONSTANT_DOUBLE_ZERO);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			DailyStock data = getDailyStockFromResult(rs);
			dataList.add(data);
		}
		close(rs, state);
		return dataList;
	}

	public Integer getUpNumberByStockCode(String stockCode) throws SQLException {

		Map<String, StatisticDetailStock> upStatisticDetailStock = statisticUpInDailyStock();
		StatisticDetailStock statisticDetailStock = upStatisticDetailStock.get(stockCode) == null ? new StatisticDetailStock() : upStatisticDetailStock.get(stockCode);
		Integer upNumber = statisticDetailStock.getUpNumber();
		return upNumber!=null?upNumber:0;
	}

	public Integer getDownNumberByStockCode(String stockCode) throws SQLException {

		Map<String, StatisticDetailStock> downStatisticDetailStock = statisticDownInDailyStock();
		StatisticDetailStock statisticDetailStock = downStatisticDetailStock.get(stockCode) == null ? new StatisticDetailStock() : downStatisticDetailStock.get(stockCode);
		Integer downNumber = statisticDetailStock.getDownNumber();
		return downNumber!=null?downNumber:0;
	}

	public Integer getUpDownNumberByStockCode(String stockCode) throws SQLException {

		Map<String, StatisticDetailStock> upDownStatisticDetailStock = statisticUpDownInDailyStock();
		StatisticDetailStock statisticDetailStock = upDownStatisticDetailStock.get(stockCode) == null ? new StatisticDetailStock() : upDownStatisticDetailStock.get(stockCode);
		Integer upDownNumber = statisticDetailStock.getUpDownNumber();
		return upDownNumber!=null?upDownNumber:0;
	}

	public Integer getUpNumberByStockCode(String stockCode, Date[] startEndDate) throws SQLException {

		Map<String, StatisticDetailStock> upStatisticDetailStock = statisticUpInDailyStock(stockCode, startEndDate);
		StatisticDetailStock statisticDetailStock = upStatisticDetailStock.get(stockCode);
		return statisticDetailStock!=null?statisticDetailStock.getUpNumber():DataUtils.CONSTANT_INT_ZERO;
	}

	public Integer getDownNumberByStockCode(String stockCode, Date[] startEndDate) throws SQLException {

		Map<String, StatisticDetailStock> downStatisticDetailStock = statisticDownInDailyStock(stockCode, startEndDate);
		StatisticDetailStock statisticDetailStock = downStatisticDetailStock.get(stockCode);
		return statisticDetailStock!=null?statisticDetailStock.getDownNumber():DataUtils.CONSTANT_INT_ZERO;
	}

	public Integer getUpDownNumberByStockCode(String stockCode, Date[] startEndDate) throws SQLException {

		Map<String, StatisticDetailStock> upDownStatisticDetailStock = statisticUpDownInDailyStock(stockCode, startEndDate);
		StatisticDetailStock statisticDetailStock = upDownStatisticDetailStock.get(stockCode);
		return statisticDetailStock!=null?statisticDetailStock.getUpDownNumber():DataUtils.CONSTANT_INT_ZERO;
	}

	public Long getMaxNumFromDailyStock() throws SQLException {

		long maxNum = 0;
		String sql = "select max(" + DailyStock.NUM + ") as num_ from " + DailyStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		close(rs, state);
		return maxNum;
	}

	public Date[] getMinMaxDate() throws SQLException {

		Date[] dateArray = new Date[2];
		String sql = "select min(" + DailyStock.STOCK_DATE + ") as min_date_, max(" + DailyStock.STOCK_DATE + ") as max_date_ from "
				+ DailyStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			dateArray[0] = rs.getDate("min_date_");
			dateArray[1] = rs.getDate("max_date_");
		}
		close(rs, state);
		return dateArray;
	}

	public boolean updateChangeRateAndTurnoverRate(DailyStock dailyStock, int fieldFlg) throws SQLException {

		boolean flg = false;
		PreparedStatement state = null;
		String appixSql = "";
		switch (fieldFlg) {
		case 0:
			appixSql = DailyStock.CHANGE_RATE + "=?," + DailyStock.CHANGE_RATE_ENCRYPT + "=?, " + DailyStock.TURNOVER_RATE + "=?, "
					+ DailyStock.TURNOVER_RATE_ENCRYPT + "=?";
			break;
		case 1:
			appixSql = DailyStock.CHANGE_RATE + "=?," + DailyStock.CHANGE_RATE_ENCRYPT + "=?";
			break;
		case 2:
			appixSql = DailyStock.TURNOVER_RATE + "=?, " + DailyStock.TURNOVER_RATE_ENCRYPT + "=?";
			break;
		default:
			appixSql = DailyStock.CHANGE_RATE + "=?," + DailyStock.CHANGE_RATE_ENCRYPT + "=?, " + DailyStock.TURNOVER_RATE + "=?, "
					+ DailyStock.TURNOVER_RATE_ENCRYPT + "=?";
			break;
		}
		String sql = "update " + DailyStock.TABLE_NAME + " set " + appixSql + " where " + DailyStock.STOCK_CODE + "=? and " + DailyStock.STOCK_DATE
				+ "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			switch (fieldFlg) {
			case 0:
				state.setDouble(1, dailyStock.getChangeRate());
				state.setString(2, dailyStock.getEncryptChangeRate());
				state.setDouble(3, dailyStock.getTurnoverRate());
				state.setString(4, dailyStock.getEncryptTurnoverRate());
				state.setString(5, dailyStock.getStockCode());
				state.setDate(6, new java.sql.Date(dailyStock.getStockDate().getTime()));
				break;
			case 1:
				state.setDouble(1, dailyStock.getChangeRate());
				state.setString(2, dailyStock.getEncryptChangeRate());
				state.setString(3, dailyStock.getStockCode());
				state.setDate(4, new java.sql.Date(dailyStock.getStockDate().getTime()));
				break;
			case 2:
				state.setDouble(1, dailyStock.getTurnoverRate());
				state.setString(2, dailyStock.getEncryptTurnoverRate());
				state.setString(3, dailyStock.getStockCode());
				state.setDate(4, new java.sql.Date(dailyStock.getStockDate().getTime()));
				break;
			default:
				state.setDouble(1, dailyStock.getChangeRate());
				state.setString(2, dailyStock.getEncryptChangeRate());
				state.setDouble(3, dailyStock.getTurnoverRate());
				state.setString(4, dailyStock.getEncryptTurnoverRate());
				state.setString(5, dailyStock.getStockCode());
				state.setDate(6, new java.sql.Date(dailyStock.getStockDate().getTime()));
				break;
			}
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			flg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  每日股票信息表(daily_stock_)中股票" + dailyStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(dailyStock.getStockCode()) + ")更新涨跌幅和换手率失败！");
			log.loger.error("  每日股票信息表(daily_stock_)中股票" + dailyStock.getStockCode() + "(" + PropertiesUtils.getProperty(dailyStock.getStockCode())
					+ ")更新涨跌幅和换手率失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return flg;
	}
}
