package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.MD5Utils;
import cn.db.bean.InformationStock;

public class InformationStockDao extends OperationDao {

	public int saveOrUpdateInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {

		int saveUpdateFlg = 0; // 0:无效; 1:保存; 2:更新
		InformationStock infoStock = getInformationStockByKey(stockDate, stockCode);
		if (infoStock == null) {
			boolean saveFlg = saveInformationStock(stockDate, stockCode, stockInfo);
			if (saveFlg)
				saveUpdateFlg = 1;
		} else if (CommonUtils.isBlank(infoStock.getStockInfo())) {
			boolean updateFlg = updateInformationStock(stockDate, stockCode, stockInfo);
			if (updateFlg)
				saveUpdateFlg = 2;
		}
		return saveUpdateFlg;
	}

	private InformationStock getInformationStockByKey(String stockDate, String stockCode) throws SQLException {

		InformationStock infoStock = null;
		String sql = "select " + InformationStock.ALL_FIELDS + " from " + InformationStock.TABLE_NAME + " where " + InformationStock.STOCK_CODE
				+ "=? and " + InformationStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(DateUtils.stringToDate(stockDate).getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			infoStock = getInformationStockFromResult(rs);
		}
		close(rs, state);
		return infoStock;
	}

	public boolean isExistInInformationStock(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + InformationStock.NUM + ") as count_ from " + InformationStock.TABLE_NAME + " where "
				+ InformationStock.STOCK_CODE + "=? and " + InformationStock.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			count = rs.getInt("count_");
		}
		close(rs, state);
		return count == 0 ? false : true;
	}

	private boolean updateInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + InformationStock.TABLE_NAME + " set " + InformationStock.STOCK_INFO + "=?, " + InformationStock.STOCK_INFO_DES
				+ "=?, " + InformationStock.STOCK_INFO_MD5 + "=?, " + InformationStock.INPUT_TIME + "=? where " + InformationStock.STOCK_CODE
				+ "=? and " + InformationStock.STOCK_DATE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
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
					DateUtils.dateTimeToString(new Date()) + "  " + stockDate + "统计股票信息表(information_stock_)更新股票信息(" + stockCode + ")失败！");
			log.loger.error(stockDate + " 统计股票信息表(information_stock_)更新股票信息(" + stockCode + ")失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}

	private boolean saveInformationStock(String stockDate, String stockCode, String stockInfo) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + InformationStock.TABLE_NAME + " (" + InformationStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			Long maxNum = getMaxNumFromInformationStock();
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
			//connection.commit();
			commitTransaction();
			saveFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(
					DateUtils.dateTimeToString(new Date()) + "  " + stockDate + "统计股票信息表(information_stock_)增加股票信息(" + stockCode + ")失败！");
			log.loger.error(stockDate + " 统计股票信息表(information_stock_)增加股票信息(" + stockCode + ")失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}

	private Long getMaxNumFromInformationStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(InformationStock.NUM).append(") as num_ from ").append(InformationStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getInt("num_");
		}
		close(rs, state);
		return maxNum;
	}
}
