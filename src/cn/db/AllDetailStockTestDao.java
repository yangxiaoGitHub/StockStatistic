package cn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.PropertiesUtils;
import cn.db.bean.AllDetailStockTest;

public class AllDetailStockTestDao extends OperationDao {

	public boolean saveOrUpdateAllDetailStockTest(AllDetailStockTest allDetailStockTest) throws SQLException {

		boolean saveFlg = false;
		boolean isExist = isExistInAllDetailStockTest(allDetailStockTest.getStockCode(), allDetailStockTest.getStockDate());
		if (!isExist) {
			saveFlg = saveAllDetailStockTest(allDetailStockTest);
		}
		return saveFlg;
	}

	public boolean isExistInAllDetailStockTest(String stockCode, Date stockDate) throws SQLException {

		int count = 0;
		String sql = "select count(" + AllDetailStockTest.NUM + ") as count_ from " + AllDetailStockTest.TABLE_NAME + " where "
				+ AllDetailStockTest.STOCK_CODE + "=? and " + AllDetailStockTest.STOCK_DATE + "=?";
		PreparedStatement state = (PreparedStatement) this.connection.prepareStatement(sql);
		state.setString(1, stockCode);
		state.setDate(2, new java.sql.Date(stockDate.getTime()));
		ResultSet result = state.executeQuery();
		while (result.next()) {
			count = result.getInt("count_");
		}
		close(result, state);
		return count == 0 ? false : true;
	}

	public boolean saveAllDetailStockTest(AllDetailStockTest allDetailStockTest) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + AllDetailStockTest.TABLE_NAME + " (" + AllDetailStockTest.ALL_FIELDS
				+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			Long maxNum = getMaxNumFromAllDetailStockTest();
			state.setLong(1, ++maxNum);
			state.setDate(2, new java.sql.Date(allDetailStockTest.getStockDate().getTime()));
			state.setString(3, allDetailStockTest.getStockCode());
			state.setString(4, allDetailStockTest.getStockCodeDES());
			state.setString(5, allDetailStockTest.getStockName());
			state.setDouble(6, allDetailStockTest.getTodayOpen());
			state.setDouble(7, allDetailStockTest.getYesterdayClose() != null ? allDetailStockTest.getYesterdayClose() : 0);
			state.setDouble(8, allDetailStockTest.getCurrent());
			state.setDouble(9, allDetailStockTest.getTodayHigh());
			state.setDouble(10, allDetailStockTest.getTodayLow());
			state.setLong(11, allDetailStockTest.getTradedStockNumber());
			state.setFloat(12, allDetailStockTest.getTradedAmount());
			state.setDouble(13, allDetailStockTest.getChangeRate() != null ? allDetailStockTest.getChangeRate() : 0);
			state.setString(14, allDetailStockTest.getChangeRateDES() != null ? allDetailStockTest.getChangeRateDES() : DataUtils.CONSTANT_BLANK);
			state.setDouble(15, allDetailStockTest.getTurnoverRate() != null ? allDetailStockTest.getTurnoverRate() : 0);
			state.setString(16, allDetailStockTest.getTurnoverRateDES());
			Date tradedTime = allDetailStockTest.getTradedTime() == null ? allDetailStockTest.getStockDate() : allDetailStockTest.getTradedTime();
			state.setTimestamp(17, new java.sql.Timestamp(tradedTime.getTime()));
			state.setTimestamp(18, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			saveFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(allDetailStockTest.getStockDate())
					+ " 所有股票详细信息表(all_detail_stock_)增加股票信息(" + allDetailStockTest.getStockCode() + ")失败！");
			log.loger.error(DateUtils.dateToString(allDetailStockTest.getStockDate()) + " 所有股票详细信息表(all_detail_stock_)增加股票信息("
					+ allDetailStockTest.getStockCode() + ")失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}

	public Long getMaxNumFromAllDetailStockTest() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(AllDetailStockTest.NUM).append(") as num_ from ").append(AllDetailStockTest.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql.toString());
		ResultSet result = state.executeQuery();
		while (result.next()) {
			maxNum = result.getLong("num_");
		}
		close(result, state);
		return maxNum;
	}

	public AllDetailStockTest getAllDetailStockTestByKey(Date stockDate, String stockCode) throws SQLException {

		AllDetailStockTest data = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllDetailStockTest.ALL_FIELDS).append(" from ").append(AllDetailStockTest.TABLE_NAME).append(" where ")
				.append(AllDetailStockTest.STOCK_DATE).append("=? and ").append(AllDetailStockTest.STOCK_CODE).append("=?");
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql.toString());
		state.setDate(1, new java.sql.Date(stockDate.getTime()));
		state.setString(2, stockCode);
		ResultSet result = state.executeQuery();
		while (result.next()) {
			data = getAllDetailStockTestFromResult(result);
		}
		close(result, state);
		return data;
	}

	public List<AllDetailStockTest> getAllDetailStockTestByStockDate(Date date) throws SQLException {

		List<AllDetailStockTest> allDetailStockTestList = new ArrayList<AllDetailStockTest>();
		String sql = "select " + AllDetailStockTest.ALL_FIELDS + " from " + AllDetailStockTest.TABLE_NAME + " where "
				+ AllDetailStockTest.STOCK_DATE + "=? order by " + AllDetailStockTest.NUM + ", " + AllDetailStockTest.INPUT_TIME;
		PreparedStatement state = (PreparedStatement) connection.prepareStatement(sql);
		state.setDate(1, new java.sql.Date(date.getTime()));
		ResultSet result = state.executeQuery();
		while (result.next()) {
			AllDetailStockTest allDetailStockTest = getAllDetailStockTestFromResult(result);
			allDetailStockTestList.add(allDetailStockTest);
		}
		close(result, state);
		return allDetailStockTestList;
	}

	public boolean updateTurnoverRate(AllDetailStockTest allDetailStockTest, Double turnoverRate, String turnoverRateDES) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllDetailStockTest.TABLE_NAME + " set " + AllDetailStockTest.TURNOVER_RATE + "=?, "
				+ AllDetailStockTest.TURNOVER_RATE_DES + "=?, " + AllDetailStockTest.INPUT_TIME + "=?  where " + AllDetailStockTest.STOCK_CODE
				+ "=? and " + AllDetailStockTest.STOCK_DATE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) connection.prepareStatement(sql);
			state.setDouble(1, turnoverRate);
			state.setString(2, turnoverRateDES);
			state.setTimestamp(3, new java.sql.Timestamp(allDetailStockTest.getStockDate().getTime()));
			state.setString(4, allDetailStockTest.getStockCode());
			state.setDate(5, new java.sql.Date(allDetailStockTest.getStockDate().getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(allDetailStockTest.getStockDate())
					+ "所有股票详细信息表(all_detail_stock_)更新股票" + allDetailStockTest.getStockCode() + "("
					+ PropertiesUtils.getProperty(allDetailStockTest.getStockCode()) + ")的换手率失败！");
			log.loger.error(DateUtils.dateToString(allDetailStockTest.getStockDate()) + "所有股票详细信息表(all_detail_stock_)更新股票"
					+ allDetailStockTest.getStockCode() + "(" + PropertiesUtils.getProperty(allDetailStockTest.getStockCode()) + ")的换手率失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}
}
