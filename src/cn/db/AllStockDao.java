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
import cn.db.bean.AllStock;

public class AllStockDao extends OperationDao {
	//private static AllStockDao instance;

	public AllStockDao() {
		
	}
	
	/*private AllStockDao() {

	}*/

	/*public static AllStockDao getInstance() throws Exception {

		if (instance == null) {
			instance = new AllStockDao();
		} else if (instance.connectionIsNull()) {
			instance.openConnection();
		}
		return instance;
	}*/

	public AllStock getAllStockByStockCode(String stockCode) throws SQLException {

		AllStock stock = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select " + AllStock.ALL_FIELDS + " from " + AllStock.TABLE_NAME + " where " + AllStock.STOCK_CODE + "=?");
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		state.setString(1, stockCode);
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			stock = getAllStockFromResult(rs);
		}
		close(rs, state);
		return stock;
	}

	public boolean saveAllStock(AllStock stock) throws SQLException {

		boolean saveFlg = false;
		PreparedStatement state = null;
		String sql = "insert into " + AllStock.TABLE_NAME + " (" + AllStock.ALL_FIELDS + ") values (?,?,?,?,?,?,?,?)";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setLong(1, stock.getNum());
			state.setString(2, stock.getStockCode());
			state.setString(3, stock.getStockName());
			state.setLong(4, stock.getCirculationValue());
			state.setLong(5, stock.getCirculationStockComplex());
			state.setString(6, stock.getCirculationStockSimple());
			state.setString(7, DataUtils._BLANK);
			state.setTimestamp(8, new java.sql.Timestamp((new Date()).getTime()));
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			saveFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  所有股票表(all_stock_)中增加记录失败---股票代码：" + stock.getStockCode() + "("
					+ PropertiesUtils.getProperty(stock.getStockCode()) + ")");
			log.loger.error(
					"  所有股票表(all_stock_)中增加记录失败---股票代码：" + stock.getStockCode() + "(" + PropertiesUtils.getProperty(stock.getStockCode()) + ")");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return saveFlg;
	}

	/**
	 * 查询所有股票的信息
	 * 
	 */
	public List<AllStock> listAllStock() throws SQLException {

		List<AllStock> dataList = new ArrayList<AllStock>();
		StringBuffer sql = new StringBuffer();
		sql.append("select ").append(AllStock.ALL_FIELDS).append(" from ").append(AllStock.TABLE_NAME).append(" order by ").append(AllStock.NUM);
		PreparedStatement statement = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			AllStock data = getAllStockFromResult(rs);
			dataList.add(data);
		}
		close(rs, statement);
		return dataList;
	}

	/**
	 * 更新所有股票的流通信息
	 * 
	 */
	public boolean updateAllStockCirculation(AllStock stock) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllStock.TABLE_NAME + " set " + AllStock.CIRCULATION_VALUE + "=?, " + AllStock.CIRCULATION_STOCK_COMPLEX + "=?, "
				+ AllStock.CIRCULATION_STOCK_SIMPLE + "=?, " + AllStock.INPUT_TIME + "=? where " + AllStock.STOCK_CODE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setLong(1, stock.getCirculationValue());
			state.setLong(2, stock.getCirculationStockComplex());
			state.setString(3, stock.getCirculationStockSimple());
			state.setDate(4, new java.sql.Date(stock.getInputTime().getTime()));
			state.setString(5, stock.getStockCode());
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(stock.getInputTime())
					+ " 所有股票信息表(all_stock_)更新股票(" + stock.getStockCode() + ")流通值失败！");
			log.loger.error(DateUtils.dateToString(stock.getInputTime()) + " 所有股票信息表(all_stock_)更新股票(" + stock.getStockCode() + ")流通值失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}

	/**
	 * 更新所有股票表(all_stock_)中股票的名称
	 * 
	 */
	public boolean updateAllStockName(AllStock stock) throws SQLException {

		boolean updateFlg = false;
		PreparedStatement state = null;
		String sql = "update " + AllStock.TABLE_NAME + " set " + AllStock.STOCK_NAME + "=?, " + AllStock.INPUT_TIME + "=? where "
				+ AllStock.STOCK_CODE + "=?";
		try {
			beginTransaction();
			state = (PreparedStatement) super.connection.prepareStatement(sql);
			state.setString(1, stock.getStockName());
			state.setDate(2, new java.sql.Date(stock.getInputTime().getTime()));
			state.setString(3, stock.getStockCode());
			state.executeUpdate();
			//connection.commit();
			commitTransaction();
			updateFlg = true;
		} catch (Exception e) {
			//connection.rollback();
			rollBackTransaction();
			System.out.println(DateUtils.dateTimeToString(new Date()) + "  " + DateUtils.dateToString(stock.getInputTime())
					+ " 所有股票表(all_stock_)更新股票信息(" + stock.getStockCode() + ")失败！");
			log.loger.error(DateUtils.dateToString(stock.getInputTime()) + " 所有股票表(all_stock_)更新股票信息(" + stock.getStockCode() + ")失败！");
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			resetConnection();
			close(state);
		}
		return updateFlg;
	}

	public Long getMaxNumFromAllStock() throws SQLException {

		long maxNum = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("select max(").append(AllStock.NUM).append(") as num_ from ").append(AllStock.TABLE_NAME);
		PreparedStatement state = (PreparedStatement) super.connection.prepareStatement(sql.toString());
		ResultSet rs = state.executeQuery();
		while (rs.next()) {
			maxNum = rs.getLong("num_");
		}
		close(rs, state);
		return maxNum;
	}

	private AllStock getAllStockFromResult(ResultSet rs) throws SQLException {

		AllStock data = new AllStock();
		data.setNum(rs.getLong(AllStock.NUM));
		data.setStockCode(rs.getString(AllStock.STOCK_CODE));
		data.setStockName(rs.getString(AllStock.STOCK_NAME));
		data.setCirculationValue(rs.getLong(AllStock.CIRCULATION_VALUE));
		data.setCirculationStockComplex(rs.getLong(AllStock.CIRCULATION_STOCK_COMPLEX));
		data.setCirculationStockSimple(rs.getString(AllStock.CIRCULATION_STOCK_SIMPLE));
		data.setIndustry(rs.getString(AllStock.INDUSTRY));
		data.setInputTime(rs.getDate(AllStock.INPUT_TIME));
		return data;
	}
}