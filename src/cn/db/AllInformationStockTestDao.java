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
import cn.db.bean.AllInformationStockTest;

public class AllInformationStockTestDao extends OperationDao {

	public AllInformationStockTest getAllInformationStockByKey(Date stockDate, String stockCode) throws SQLException {

		AllInformationStockTest infoStock = null;
		String sql = "select " + AllInformationStockTest.ALL_FIELDS + " from " + AllInformationStockTest.TABLE_NAME + " where "
				+ AllInformationStockTest.STOCK_CODE + "=? and " + AllInformationStockTest.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet result = state.executeQuery();
		while (result.next()) {
			infoStock = getAllInformationStockFromResult(result);
		}
		this.close(result, state, null);
		return infoStock;
	}

	public int saveOrUpdateAllInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {
		int saveUpdateFlg = 0; // 0:无效; 1:保存; 2:更新
		AllInformationStockTest infoStock = getAllInformationStockByKey(DateUtils.stringToDate(stockDate), stockCode);
		if (infoStock == null) {
			boolean saveFlg = saveAllInformationStock(stockDate, stockCode, stockInfo);
			if (saveFlg)
				saveUpdateFlg = 1;
		} else if (CommonUtils.isBlank(infoStock.getStockInfo())) {
			boolean updateFlg = updateAllInformationStock(stockDate, stockCode, stockInfo);
			if (updateFlg)
				saveUpdateFlg = 2;
		}
		return saveUpdateFlg;
	}

	private boolean updateAllInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllInformationStockTest.TABLE_NAME + " set " + AllInformationStockTest.STOCK_INFO + "=?, "
				+ AllInformationStockTest.STOCK_INFO_DES + "=?, " + AllInformationStockTest.STOCK_INFO_MD5 + "=?, "
				+ AllInformationStockTest.INPUT_TIME + "=? where " + AllInformationStockTest.STOCK_CODE + "=? and "
				+ AllInformationStockTest.STOCK_DATE + "=?";
		try {
			state = (PreparedStatement) connection.prepareStatement(sql);
			state.setString(1, stockInfo);
			String stockInfoDES = DESUtils.encryptToHex(stockInfo);
			state.setString(2, stockInfoDES);
			String stockInfoMD5 = MD5Utils.getEncryptedPwd(stockInfo);
			state.setString(3, stockInfoMD5);
			state.setTimestamp(4, new java.sql.Timestamp((new Date()).getTime()));
			state.setString(5, stockCode);
			state.setDate(6, new java.sql.Date(DateUtils.stringToDate(stockDate).getTime()));
			state.executeUpdate();
			connection.commit();
			updateFlg = true;
		} catch (Exception e) {
			connection.rollback();
			System.out.println(
					DateUtils.dateTimeToString(new Date()) + "  " + stockDate + "所有股票信息表(all_information_stock_)更新股票信息(" + stockCode + ")失败！");
			log.loger.error(stockDate + "所有统计股票信息表(all_information_stock_)更新股票信息(" + stockCode + ")失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			close(state);
		}
		return updateFlg;
	}

	private boolean saveAllInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + AllInformationStockTest.TABLE_NAME + " (" + AllInformationStockTest.ALL_FIELDS + ") values (?,?,?,?,?,?,?)";
		try {
			state = (PreparedStatement) connection.prepareStatement(sql);
			Long maxNum = getMaxNumFromAllInformationStock();
			state.setLong(1, ++maxNum);
			state.setString(2, stockCode);
			state.setDate(3, new java.sql.Date(DateUtils.stringToDate(stockDate).getTime()));
			state.setString(4, stockInfo);
			String stockInfoDES = DESUtils.encryptToHex(stockInfo);
			state.setString(5, stockInfoDES);
			String stockInfoMD5 = MD5Utils.getEncryptedPwd(stockInfo);
			state.setString(6, stockInfoMD5);
			state.setTimestamp(7, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			connection.commit();
			saveFlg = true;
		} catch (Exception e) {
			connection.rollback();
			System.out.println(
					DateUtils.dateTimeToString(new Date()) + "  " + stockDate + " 所有股票信息表(all_information_stock_)增加股票信息(" + stockCode + ")失败！");
			log.loger.error(stockDate + " 所有股票信息表(all_information_stock_)增加股票信息(" + stockCode + ")失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			close(state);
		}
		return saveFlg;
	}

	private Long getMaxNumFromAllInformationStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(AllInformationStockTest.NUM).append(") as num_ from ").append(AllInformationStockTest.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql.toString());
		ResultSet result = state.executeQuery();
		while (result.next()) {
			maxNum = result.getLong("num_");
		}
		this.close(result, state, null);
		return maxNum;
	}

	private AllInformationStockTest getAllInformationStockFromResult(ResultSet rs) throws SQLException {

		AllInformationStockTest data = new AllInformationStockTest();
		data.setNum(rs.getLong(AllInformationStockTest.NUM));
		data.setStockCode(rs.getString(AllInformationStockTest.STOCK_CODE));
		data.setStockDate(rs.getDate(AllInformationStockTest.STOCK_DATE));
		data.setStockInfo(rs.getString(AllInformationStockTest.STOCK_INFO));
		data.setStockInfoDES(rs.getString(AllInformationStockTest.STOCK_INFO_DES));
		data.setStockInfoMD5(rs.getString(AllInformationStockTest.STOCK_INFO_MD5));
		data.setInputTime(rs.getDate(AllInformationStockTest.INPUT_TIME));
		return data;
	}

	public List<AllInformationStockTest> listAllInformationStock() throws SQLException {

		List<AllInformationStockTest> allInformationStockList = new ArrayList<AllInformationStockTest>();
		String sql = "select " + AllInformationStockTest.ALL_FIELDS + " from " + AllInformationStockTest.TABLE_NAME + " order by "
				+ AllInformationStockTest.NUM;
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		ResultSet result = state.executeQuery();
		while (result.next()) {
			AllInformationStockTest allInformationStockTest = getAllInformationStockFromResult(result);
			allInformationStockList.add(allInformationStockTest);
		}
		this.close(result, state, null);
		return allInformationStockList;
	}

	public List<AllInformationStockTest> getAllInformationStockByStockDate(Date date) throws SQLException {

		List<AllInformationStockTest> allInformationStockList = new ArrayList<AllInformationStockTest>();
		String sql = "select " + AllInformationStockTest.ALL_FIELDS + " from " + AllInformationStockTest.TABLE_NAME + " where "
				+ AllInformationStockTest.STOCK_DATE + "=? order by " + AllInformationStockTest.INPUT_TIME;
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(date.getTime()));
		ResultSet result = state.executeQuery();
		while (result.next()) {
			AllInformationStockTest AllInformationStockTest = getAllInformationStockFromResult(result);
			allInformationStockList.add(AllInformationStockTest);
		}
		this.close(result, state, null);
		return allInformationStockList;
	}

	public boolean updateNum(AllInformationStockTest allInformationStockTest, long num) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllInformationStockTest.TABLE_NAME + " set " + AllInformationStockTest.NUM + "=? where "
				+ AllInformationStockTest.STOCK_CODE + "=? and " + AllInformationStockTest.STOCK_DATE + "=?";
		try {
			state = (PreparedStatement) connection.prepareStatement(sql);
			state.setLong(1, num);
			state.setString(2, allInformationStockTest.getStockCode());
			state.setDate(3, new java.sql.Date(allInformationStockTest.getStockDate().getTime()));
			state.executeUpdate();
			connection.commit();
			updateFlg = true;
		} catch (Exception e) {
			connection.rollback();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(allInformationStockTest.getStockDate())
					+ "所有股票信息表(all_information_stock_)更新股票(" + allInformationStockTest.getStockCode() + ")信息num_字段失败！");
			log.loger.error(DateUtils.dateToString(allInformationStockTest.getStockDate()) + "所有股票信息表(all_information_stock_)更新股票("
					+ allInformationStockTest.getStockCode() + ")信息num_字段失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			close(state);
		}
		return updateFlg;
	}

	public boolean isExistInAllInformationStock(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + AllInformationStockTest.NUM + ") as count_ from " + AllInformationStockTest.TABLE_NAME + " where "
				+ AllInformationStockTest.STOCK_CODE + "=? and " + AllInformationStockTest.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet result = state.executeQuery();
		while (result.next()) {
			count = result.getInt("count_");
		}
		this.close(result, state, null);
		return count == 0 ? false : true;
	}
}
