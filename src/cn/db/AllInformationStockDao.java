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
import cn.db.bean.AllInformationStock;

public class AllInformationStockDao extends OperationDao {

	public AllInformationStock getAllInformationStockByKey(Date stockDate, String stockCode) throws SQLException {

		AllInformationStock infoStock = null;
		String sql = "select " + AllInformationStock.ALL_FIELDS + " from " + AllInformationStock.TABLE_NAME + " where "
				+ AllInformationStock.STOCK_CODE + "=? and " + AllInformationStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			infoStock = getAllInformationStockFromResult(rs);
		}
		close(rs, state);
		return infoStock;
	}

	public int saveOrUpdateAllInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {
		int saveUpdateFlg = 0; // 0:无效; 1:保存; 2:更新
		AllInformationStock infoStock = getAllInformationStockByKey(DateUtils.stringToDate(stockDate), stockCode);
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
		String sql = "update " + AllInformationStock.TABLE_NAME + " set " + AllInformationStock.STOCK_INFO + "=?, "
				+ AllInformationStock.STOCK_INFO_DES + "=?, " + AllInformationStock.STOCK_INFO_MD5 + "=?, " + AllInformationStock.INPUT_TIME
				+ "=? where " + AllInformationStock.STOCK_CODE + "=? and " + AllInformationStock.STOCK_DATE + "=?";
		try {
			beginTransaction();
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
			//connection.commit();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(
					DateUtils.dateTimeToString(new Date()) + "  " + stockDate + "所有股票信息表(all_information_stock_)更新股票信息(" + stockCode + ")失败！");
			log.loger.error(stockDate + "所有统计股票信息表(all_information_stock_)更新股票信息(" + stockCode + ")失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}

	private boolean saveAllInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + AllInformationStock.TABLE_NAME + " (" + AllInformationStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
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
			//super.connection.commit();
			commitTransaction();
			saveFlg = true;
		} catch (Exception e) {
			//super.connection.rollback();
			rollBackTransaction();
			System.out.println(
					DateUtils.dateTimeToString(new Date()) + "  " + stockDate + " 所有股票信息表(all_information_stock_)增加股票信息(" + stockCode + ")失败！");
			log.loger.error(stockDate + " 所有股票信息表(all_information_stock_)增加股票信息(" + stockCode + ")失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}

	private Long getMaxNumFromAllInformationStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(AllInformationStock.NUM).append(") as num_ from ").append(AllInformationStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		close(rs, state);
		return maxNum;
	}

	private AllInformationStock getAllInformationStockFromResult(ResultSet rs) throws SQLException {

		AllInformationStock data = new AllInformationStock();
		data.setNum(rs.getLong(AllInformationStock.NUM));
		data.setStockCode(rs.getString(AllInformationStock.STOCK_CODE));
		data.setStockDate(rs.getDate(AllInformationStock.STOCK_DATE));
		data.setStockInfo(rs.getString(AllInformationStock.STOCK_INFO));
		data.setStockInfoDES(rs.getString(AllInformationStock.STOCK_INFO_DES));
		data.setStockInfoMD5(rs.getString(AllInformationStock.STOCK_INFO_MD5));
		data.setInputTime(rs.getDate(AllInformationStock.INPUT_TIME));
		return data;
	}

	public List<AllInformationStock> listAllInformationStock() throws SQLException {

		List<AllInformationStock> allInformationStockList = new ArrayList<AllInformationStock>();
		String sql = "select " + AllInformationStock.ALL_FIELDS + " from " + AllInformationStock.TABLE_NAME + " order by "
				+ AllInformationStock.NUM;
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			AllInformationStock allInformationStock = getAllInformationStockFromResult(rs);
			allInformationStockList.add(allInformationStock);
		}
		close(rs, state);
		return allInformationStockList;
	}

	public List<AllInformationStock> getAllInformationStockByStockDate(Date date) throws SQLException {

		List<AllInformationStock> allInformationStockList = new ArrayList<AllInformationStock>();
		String sql = "select " + AllInformationStock.ALL_FIELDS + " from " + AllInformationStock.TABLE_NAME + " where "
				+ AllInformationStock.STOCK_DATE + "=? order by " + AllInformationStock.INPUT_TIME;
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(date.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			AllInformationStock allInformationStock = getAllInformationStockFromResult(rs);
			allInformationStockList.add(allInformationStock);
		}
		close(rs, state);
		return allInformationStockList;
	}

	public boolean updateNum(AllInformationStock allInformationStock, long num) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllInformationStock.TABLE_NAME + " set " + AllInformationStock.NUM + "=? where " + AllInformationStock.STOCK_CODE
				+ "=? and " + AllInformationStock.STOCK_DATE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) connection.prepareStatement(sql);
			state.setLong(1, num);
			state.setString(2, allInformationStock.getStockCode());
			state.setDate(3, new java.sql.Date(allInformationStock.getStockDate().getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(allInformationStock.getStockDate())
					+ "所有股票信息表(all_information_stock_)更新股票(" + allInformationStock.getStockCode() + ")信息num_字段失败！");
			log.loger.error(DateUtils.dateToString(allInformationStock.getStockDate()) + "所有股票信息表(all_information_stock_)更新股票("
					+ allInformationStock.getStockCode() + ")信息num_字段失败！");
			e.printStackTrace();
			log.loger.error(e);
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}

	public boolean isExistInAllInformationStock(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + AllInformationStock.NUM + ") as count_ from " + AllInformationStock.TABLE_NAME + " where "
				+ AllInformationStock.STOCK_CODE + "=? and " + AllInformationStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		close(rs, state);
		return count == 0 ? false : true;
	}
}
