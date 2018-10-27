package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.MD5Utils;
import cn.db.bean.OriginalStock;

public class OriginalStockDao extends OperationDao {

	/**
	 * 增加原始股票数据
	 * 
	 */
	public void addOriginalData(String sDate, String stockCodes, String changeRates, String turnoverRates) throws SQLException {

		PreparedStatement state = null;
		String sql = "insert into " + OriginalStock.TABLE_NAME + " (" + OriginalStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			Long maxNum = this.getMaxNumFromOriginalStock();
			state.setLong(1, ++maxNum);
			state.setDate(2, new java.sql.Date(DateUtils.stringToDate(sDate).getTime()));
			String[] codeArray = stockCodes.split(",");
			state.setInt(3, codeArray.length);
			state.setString(4, stockCodes);
			String stockCodesEncrypt = DESUtils.encryptToHex(stockCodes);
			state.setString(5, stockCodesEncrypt);
			String stockCodeMD5 = MD5Utils.getEncryptedPwd(stockCodes);
			state.setString(6, stockCodeMD5);
			state.setString(7, changeRates);
			String changeRatesEncrypt = DESUtils.encryptToHex(changeRates);
			state.setString(8, changeRatesEncrypt);
			String changeRatesMD5 = MD5Utils.getEncryptedPwd(changeRates);
			state.setString(9, changeRatesMD5);
			if (!CommonUtils.isBlank(turnoverRates)) {
				state.setString(10, turnoverRates);
				String turnoverRatesEncrypt = DESUtils.encryptToHex(turnoverRates);
				state.setString(11, turnoverRatesEncrypt);
				String turnoverRatesMD5 = MD5Utils.getEncryptedPwd(turnoverRates);
				state.setString(12, turnoverRatesMD5);
			} else {
				state.setString(10, null);
				state.setString(11, null);
				state.setString(12, null);
			}
			state.setTimestamp(13, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + sDate + "原始股票数据增加失败！");
			log.loger.error(sDate + " 原始股票数据增加失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
	}

	/**
	 * 更新原始股票数据
	 * 
	 */
	public void updateOriginalData(String sDate, String stockCodes, String changeRates, String turnoverRates) throws SQLException {

		PreparedStatement state = null;
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(OriginalStock.TABLE_NAME).append(" set ").append(OriginalStock.STOCK_NUMBER).append("=?, ")
				.append(OriginalStock.STOCK_CODES).append("=?, ").append(OriginalStock.STOCK_CODES_ENCRYPT).append("=?, ")
				.append(OriginalStock.STOCK_CODES_MD5).append("=?,").append(OriginalStock.CHANGE_RATES).append("=?, ")
				.append(OriginalStock.CHANGE_RATES_ENCRYPT).append("=?, ").append(OriginalStock.CHANGE_RATES_MD5).append("=?, ")
				.append(OriginalStock.TURNOVER_RATES).append("=?, ").append(OriginalStock.TURNOVER_RATES_ENCRYPT).append("=?,")
				.append(OriginalStock.TURNOVER_RATES_MD5).append("=?, ").append(OriginalStock.INPUT_TIME).append("=? where ")
				.append(OriginalStock.STOCK_DATE).append("=?");
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
			int stockNumber = stockCodes.split(",").length;
			state.setInt(1, stockNumber);
			state.setString(2, stockCodes);
			String stockCodesEncrypt = DESUtils.encryptToHex(stockCodes);
			state.setString(3, stockCodesEncrypt);
			String stockCodesMD5 = MD5Utils.getEncryptedPwd(stockCodes);
			state.setString(4, stockCodesMD5);
			state.setString(5, changeRates);
			String changeRatesEncrypt = DESUtils.encryptToHex(changeRates);
			state.setString(6, changeRatesEncrypt);
			String changeRatesMD5 = MD5Utils.getEncryptedPwd(changeRates);
			state.setString(7, changeRatesMD5);
			state.setString(8, turnoverRates);
			String turnoverRatesEncrypt = DESUtils.encryptToHex(turnoverRates);
			state.setString(9, turnoverRatesEncrypt);
			String turnoverRatesMD5 = MD5Utils.getEncryptedPwd(turnoverRates);
			state.setString(10, turnoverRatesMD5);
			state.setTimestamp(11, new java.sql.Timestamp((new Date()).getTime()));
			state.setDate(12, new java.sql.Date(DateUtils.stringToDate(sDate).getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + sDate + "原始股票数据(original_stock_)更新失败！");
			log.loger.error(sDate + " 原始股票数据(original_stock_)更新失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
	}

	/**
	 * 查询所有的原始股票数据
	 * 
	 */
	public List<OriginalStock> listOriginalData() throws SQLException {

		List<OriginalStock> dataList = new ArrayList<OriginalStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(OriginalStock.ALL_FIELDS).append(" from ").append(OriginalStock.TABLE_NAME).append(" order by ")
				.append(OriginalStock.STOCK_DATE);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			OriginalStock data = getOriginalStockFromResult(rs);
			dataList.add(data);
		}
		close(rs, state);
		return dataList;
	}
	
	/**
	 * 查询某一时间段内的原始股票数据
	 * 
	 */
	public List<OriginalStock> getOriginalStockByDateInterval(Date startDate, Date endDate) throws SQLException {

		List<OriginalStock> dataList = new ArrayList<OriginalStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(OriginalStock.ALL_FIELDS).append(" from ").append(OriginalStock.TABLE_NAME).append(" where ")
		   .append(OriginalStock.STOCK_DATE).append(" between ? and ? ").append(" order by ").append(OriginalStock.STOCK_DATE);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(startDate.getTime()));
		state.setDate(2, new java.sql.Date(endDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			OriginalStock data = getOriginalStockFromResult(rs);
			dataList.add(data);
		}
		close(rs, state);
		return dataList;
	}

	/**
	 * 查询原始股票数据最近日期
	 * 
	 */
	public Date getRecentStockDate() throws SQLException {

		Date recentDate = null;
		String sql = "select max(" + OriginalStock.STOCK_DATE + ") as recent_stock_date_ from " + OriginalStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			recentDate = rs.getDate("recent_stock_date_");
		}
		close(rs, state);
		return recentDate;
	}

	public int searchOriginalData(String sDate) throws SQLException {
		int result = 0;
		String sql = "select count(*) as count_ from " + OriginalStock.TABLE_NAME + " where " + OriginalStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(DateUtils.stringToDate(sDate).getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			result = rs.getInt("count_");
		}
		close(rs, state);
		return result;
	}

	public Long getMaxNumFromOriginalStock() throws SQLException {

		long maxNum = 0;
		String sql = "select max(" + OriginalStock.NUM + ") as num_ from " + OriginalStock.TABLE_NAME;
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getInt("num_");
		}
		close(rs, state);
		return maxNum;
	}

	public void updateCodesRatesMD5(Date stockDate, String stockCodesMD5, String changeRatesMD5) throws SQLException {

		PreparedStatement state = null;
		String sql = "update " + OriginalStock.TABLE_NAME + " set " + OriginalStock.STOCK_CODES_MD5 + "=?, " + OriginalStock.CHANGE_RATES_MD5
				+ "=?, " + OriginalStock.INPUT_TIME + "=? where " + OriginalStock.STOCK_DATE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setString(1, stockCodesMD5);
			state.setString(2, changeRatesMD5);
			state.setTimestamp(3, new java.sql.Timestamp((new Date()).getTime()));
			state.setDate(4, new java.sql.Date(stockDate.getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(stockDate) + "股票数据更新失败！");
			log.loger.error(DateUtils.dateToString(stockDate) + " 股票数据更新失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
	}

	public void updateStockCodesMD5(Date stockDate, String stockCodesMD5) throws SQLException {

		PreparedStatement state = null;
		String sql = "update " + OriginalStock.TABLE_NAME + " set " + OriginalStock.STOCK_CODES_MD5 + "=? where " + OriginalStock.STOCK_DATE
				+ "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setString(1, stockCodesMD5);
			state.setDate(2, new java.sql.Date(stockDate.getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(stockDate) + "股票数据更新失败！");
			log.loger.error(DateUtils.dateToString(stockDate) + " 股票数据更新失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
	}

	public void updateChangeRatesMD5(Date stockDate, String changeRatesMD5) throws SQLException {

		PreparedStatement state = null;
		String sql = "update " + OriginalStock.TABLE_NAME + " set " + OriginalStock.CHANGE_RATES_MD5 + "=? where " + OriginalStock.STOCK_DATE
				+ "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setString(1, changeRatesMD5);
			state.setDate(2, new java.sql.Date(stockDate.getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(stockDate) + "股票数据更新失败！");
			log.loger.error(DateUtils.dateToString(stockDate) + " 股票数据更新失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
	}

	public double getAvgNumberBySockDate(Date firstDateOfMonth, Date lastDateOfMonth) throws SQLException {
		
		double avgNumber = 0;
		String sql = "select avg(" + OriginalStock.STOCK_NUMBER + ") as avg_num_ from " + OriginalStock.TABLE_NAME + " where " + OriginalStock.STOCK_DATE + " between ? and ?";
		PreparedStatement state = (PreparedStatement)super.connection.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(firstDateOfMonth.getTime()));
		state.setDate(2, new java.sql.Date(lastDateOfMonth.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
		   avgNumber = rs.getDouble("avg_num_");
		}
		close(rs, state);
		return avgNumber;
	}

	public OriginalStock getOriginalStockByKey(Date recentDate) throws SQLException {
		
		OriginalStock originalStock = null;
		String sql = "select " + OriginalStock.ALL_FIELDS + " from " + OriginalStock.TABLE_NAME + " where " + OriginalStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(recentDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			originalStock = getOriginalStockFromResult(rs);
		}
		close(rs, state);
		return originalStock;
	}
}
