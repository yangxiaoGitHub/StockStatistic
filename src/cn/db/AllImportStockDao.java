package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.com.DateUtils;
import cn.db.bean.AllDetailStock;
import cn.db.bean.AllImportStock;
import cn.db.bean.OriginalStock;

public class AllImportStockDao extends OperationDao {

	public boolean saveAllImportStock(AllImportStock allImportStock) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + allImportStock.TABLE_NAME + " (" + allImportStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setLong(1, allImportStock.getNum());
			state.setDate(2, new java.sql.Date(allImportStock.getStockDate().getTime()));
			state.setString(3, allImportStock.getStockCode());
			state.setString(4, allImportStock.getStockCodeDES());
			state.setDouble(5, allImportStock.getChangeRate());
			state.setString(6, allImportStock.getChangeRateDes());
			state.setDouble(7, allImportStock.getCurrent());
			state.setLong(8, allImportStock.getTotalHands());
			state.setDouble(9, allImportStock.getYesterdayClose());
			state.setDouble(10, allImportStock.getTodayOpen());
			state.setDouble(11, allImportStock.getTodayHigh());
			state.setDouble(12, allImportStock.getTodayLow());
			state.setDouble(13, allImportStock.getQuantityRatio());
			state.setString(14, allImportStock.getIndustry());
			state.setDouble(15, allImportStock.getPeRatio());
			state.setDouble(16, allImportStock.getPbRatio());
			state.setDouble(17, allImportStock.getAmplitude());
			state.setDouble(18, allImportStock.getTurnoverRate());
			state.setString(19, allImportStock.getTurnoverRateDes());
			state.setDouble(20, allImportStock.getUpDown());
			state.setDouble(21, allImportStock.getAmount());
			state.setLong(22, allImportStock.getOutside());
			state.setLong(23, allImportStock.getInside());
			state.setLong(24, allImportStock.getMarketCap());
			state.setLong(25, allImportStock.getCirculateValue());
			state.setTimestamp(26, new java.sql.Timestamp(allImportStock.getInputTime().getTime()));
			state.executeUpdate();
			commitTransaction();
			saveFlg = true;
		} catch (Exception ex) {
			rollBackTransaction();
			System.out.println(
					DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateTimeToString(allImportStock.getStockDate()) + " 所有股票详细信息导入表(all_import_stock_)增加股票信息(" + allImportStock.getStockCode() + ")失败！");
			log.loger.error(DateUtils.dateTimeToString(allImportStock.getStockDate()) + " 所有股票详细信息导入表(all_import_stock_)增加股票信息(" + allImportStock.getStockCode() + ")失败！");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}
	
	public boolean isExistInAllImportStock(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + AllImportStock.NUM + ") as count_ from " + AllImportStock.TABLE_NAME 
					+ " where " + AllImportStock.STOCK_CODE + "=? and " + AllImportStock.STOCK_DATE + "=?";
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

	public AllImportStock getAllImportStockByKey(Date stockDate, String stockCode) throws SQLException {

		AllImportStock data = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllImportStock.ALL_FIELDS).append(" from ").append(AllImportStock.TABLE_NAME)
		   .append(" where ").append(AllImportStock.STOCK_DATE).append("=? and ").append(AllImportStock.STOCK_CODE).append("=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(stockDate.getTime()));
		state.setString(2, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			data = getAllImportStockFromResult(rs);
		}
		super.close(rs, state);
		return data;
	}
	
	public List<AllImportStock> getAllImportStockByStockDate(Date[] startEndDate) throws SQLException {

		List<AllImportStock> allImportStockList = new ArrayList<AllImportStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllImportStock.ALL_FIELDS).append(" from ").append(AllImportStock.TABLE_NAME)
		   .append(" where ").append(AllImportStock.STOCK_DATE).append(" between ? and ?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(startEndDate[0].getTime()));
		state.setDate(2, new java.sql.Date(startEndDate[1].getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			AllImportStock allImportStock = getAllImportStockFromResult(rs);
			allImportStockList.add(allImportStock);
		}
		super.close(rs, state);
		return allImportStockList;
	}

	private AllImportStock getAllImportStockFromResult(ResultSet rs) throws SQLException {

		AllImportStock data = new AllImportStock();
		data.setNum(rs.getLong(AllImportStock.NUM));
		data.setStockDate(rs.getDate(AllImportStock.STOCK_DATE));
		data.setStockCode(rs.getString(AllImportStock.STOCK_CODE));
		data.setStockCodeDES(rs.getString(AllImportStock.STOCK_CODE_DES));
		data.setChangeRate(rs.getDouble(AllImportStock.CHANGE_RATE));
		data.setChangeRateDes(rs.getString(AllImportStock.CHANGE_RATE_DES));
		data.setCurrent(rs.getDouble(AllImportStock.CURRENT));
		data.setTotalHands(rs.getLong(AllImportStock.TOTAL_HANDS));
		data.setYesterdayClose(rs.getDouble(AllImportStock.YESTERDAY_CLOSE));
		data.setTodayOpen(rs.getDouble(AllImportStock.TODAY_OPEN));
		data.setTodayHigh(rs.getDouble(AllImportStock.TODAY_HIGH));
		data.setTodayLow(rs.getDouble(AllImportStock.TODAY_LOW));
		data.setQuantityRatio(rs.getDouble(AllImportStock.QUANTITY_RATIO));
		data.setIndustry(rs.getString(AllImportStock.INDUSTRY));
		data.setPeRatio(rs.getDouble(AllImportStock.PE_RATIO));
		data.setPbRatio(rs.getDouble(AllImportStock.PB_RATIO));
		data.setAmplitude(rs.getDouble(AllImportStock.AMPLITUDE));
		data.setTurnoverRate(rs.getDouble(AllImportStock.TURNOVER_RATE));
		data.setTurnoverRateDes(rs.getString(AllImportStock.TURNOVER_RATE_DES));
		data.setUpDown(rs.getDouble(AllImportStock.UP_DOWN));
		data.setAmount(rs.getLong(AllImportStock.AMOUNT));
		data.setOutside(rs.getLong(AllImportStock.OUTSIDE));
		data.setInside(rs.getLong(AllImportStock.INSIDE));
		data.setMarketCap(rs.getLong(AllImportStock.MARKET_CAP));
		data.setCirculateValue(rs.getLong(AllImportStock.CIRCULATE_VALUE));
		data.setInputTime(rs.getDate(OriginalStock.INPUT_TIME));
		return data;
	}

	public List<AllImportStock> getAllImportStockByCodesAndDate(String stockCodes, Date[] startEndDate) throws SQLException {

		List<AllImportStock> allImportStockList = new ArrayList<AllImportStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllImportStock.ALL_FIELDS).append(" from ").append(AllImportStock.TABLE_NAME).append(" where ")
		   .append(AllImportStock.STOCK_CODE).append(" in (").append(stockCodes).append(") and ").append(AllImportStock.STOCK_DATE)
		   .append(" between ? and ?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(startEndDate[0].getTime()));
		state.setDate(2, new java.sql.Date(startEndDate[1].getTime()));
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			AllImportStock data = getAllImportStockFromResult(rs);
			allImportStockList.add(data);
		}
		super.close(rs, state);
		return allImportStockList;
	}

}
