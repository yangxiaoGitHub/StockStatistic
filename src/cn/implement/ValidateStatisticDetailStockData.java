package cn.implement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DataUtils;
import cn.com.DateUtils;
import cn.com.JsonUtils;
import cn.com.PropertiesUtils;
import cn.com.StockUtils;
import cn.db.DailyStockDao;
import cn.db.OriginalStockDao;
import cn.db.StatisticDetailStockDao;
import cn.db.bean.DailyStock;
import cn.db.bean.OriginalStock;
import cn.db.bean.StatisticDetailStock;
import cn.log.Message;

public class ValidateStatisticDetailStockData extends OperationData {

	/**
	 * 验证统计股票详细表(statistic_detail_stock_)中的数据
	 * 
	 */
	public void validateStatisticDetailStockData() {
		dailyStockDao = new DailyStockDao();
		originalStockDao = new OriginalStockDao();
		statisticDetailStockDao = new StatisticDetailStockDao();
		try {
			Date[] minMaxDate = statisticDetailStockDao.getMinMaxDate();
			Date[] preOneMonth = DateUtils.getPreOneMonth(minMaxDate[1]);
			List<StatisticDetailStock> statisticDetailStockList = statisticDetailStockDao.listStatisticDetailStockByDate(preOneMonth);
			if (statisticDetailStockList.size() > 0) {
				// 验证stock_code_ DES加密
				boolean stockCodeFLg = validateStockCodeDESInStatisticDetailStock(statisticDetailStockList);
				// 验证stock_code_是否存在表daily_stock_中
				boolean stockCodeFirstDateFlg = validateStockCodeInDailyStock(statisticDetailStockList);
				// 验证表(statistic_stock_)中股票总涨跌次数(与表daily_stock_比较)
				boolean dailyUpDownNumberFlg = validateUpAndDownInDailyStock(statisticDetailStockList);
				// 验证表(statistic_stock_)中股票总涨跌次数(与表original_stock_比较)
				boolean originalUpDownNumberFlg = validateUpAndDownInOriginalStock(statisticDetailStockList);
				// 验证表(statistic_detail_stock_)中Json涨跌次数(与daily_stock_表比较)
				boolean dailyJsonFlg = validateUpAndDownJsonInDailyStock(statisticDetailStockList);
				// 验证表(statistic_detail_stock_)中股票Json涨跌次数(与original_stock_表比较)
				boolean originalJsonFlg = validateUpAndDownJsonInOriginalStock(statisticDetailStockList);
				if (stockCodeFLg && stockCodeFirstDateFlg && dailyUpDownNumberFlg && originalUpDownNumberFlg && dailyJsonFlg && originalJsonFlg) {
					System.out.println(DateUtils.dateTimeToString(new Date()) + "=====>表(statistic_detail_stock_)中的股票数据验证成功！" + CommonUtils.getLineBreak());
					log.loger.warn(DateUtils.dateTimeToString(new Date()) + "=====>表(statistic_detail_stock_)中的股票数据验证成功！" + CommonUtils.getLineBreak());
				}
			} else {
				System.out.println("表(statistic_detail_stock_)中没有股票数据！");
				log.loger.warn("表(statistic_detail_stock_)中没有股票数据！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		} finally {
			closeDao(dailyStockDao, originalStockDao, statisticDetailStockDao);
		}
	}
	
	/**
	 * 验证表(statistic_detail_stock_)中股票总涨跌次数(与表daily_stock_比较)
	 *
	 */
	private boolean validateUpAndDownInDailyStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中股票总涨跌次数(与daily_stock_表比较)验证开始...");
		Message.clear();
		// 根据statisticDetailStockList，统计daily_stock_表中股票总涨跌次数
		Map<String, StatisticDetailStock> upDownAndNumberMap = statisticUpAndDownInDailyStock(statisticDetailStockList);
	    // 验证统计涨跌次数和表(statistic_detail_stock_)中的涨跌次数
		List<StatisticDetailStock> invalidUpAndDownList = listErrorUpAndDownNumber(statisticDetailStockList, upDownAndNumberMap, DailyStock.TABLE_NAME);
		Message.printMethodExecuteMessage();
		if (invalidUpAndDownList.size() > 0) {
			flg = false;
			System.out.println("---------------验证统计股票信息表(statistic_detail_stock_)的涨跌次数无效----------------");
			log.loger.warn("---------------验证统计股票信息表(statistic_detail_stock_)的涨跌次数无效----------------");
			for (int index=0; index<invalidUpAndDownList.size(); index++) {
				StatisticDetailStock data = invalidUpAndDownList.get(index);
				System.out.println("验证涨跌次数无效---" + DataUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_detail_stock_)中的数据为: " + StatisticDetailStock.UP_DOWN_NUMBER + ":" 
								 + data.getErrorUpDownNumber() + "," + StatisticDetailStock.UP_NUMBER + ":" + data.getErrorUpNumber() + "," + StatisticDetailStock.DOWN_NUMBER + ":" + data.getErrorDownNumber() 
								 + "  在表(daily_stock_)中的统计数据为: " + StatisticDetailStock.UP_DOWN_NUMBER + ":" + data.getUpDownNumber() + "," + StatisticDetailStock.UP_NUMBER + ":" + data.getUpNumber() 
								 + "," + StatisticDetailStock.DOWN_NUMBER + ":" + data.getDownNumber());
				log.loger.warn("验证涨跌次数无效---" + DataUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_detail_stock_)中的数据为: " + StatisticDetailStock.UP_DOWN_NUMBER + ":" 
								 + data.getErrorUpDownNumber() + "," + StatisticDetailStock.UP_NUMBER + ":" + data.getErrorUpNumber() + "," + StatisticDetailStock.DOWN_NUMBER + ":" + data.getErrorDownNumber() 
								 + "  在表(daily_stock_)中的统计数据为: " + StatisticDetailStock.UP_DOWN_NUMBER + ":" + data.getUpDownNumber() + "," + StatisticDetailStock.UP_NUMBER + ":" + data.getUpNumber() 
								 + "," + StatisticDetailStock.DOWN_NUMBER + ":" + data.getDownNumber());
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中股票总涨跌次数(与表daily_stock_比较)验证成功！");
		}
		return flg;
	}
	
	/**
	 * 验证表(statistic_detail_stock_)中的stock_code_ DES加密
	 *
	 */
	private boolean validateStockCodeDESInStatisticDetailStock(List<StatisticDetailStock> stockList) {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中字段(stock_code_)的 DES加密验证开始...");
		List<StatisticDetailStock> invalidList = CommonUtils.listErrorStockCodeDES(stockList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------DES验证统计股票信息表(statistic_detail_stock_)无效数据----------------");
			log.loger.warn("---------------DES验证统计股票信息表(statistic_detail_stock_)无效数据----------------");
			for (int index=0; index<invalidList.size(); index++) {
				StatisticDetailStock data = (StatisticDetailStock) invalidList.get(index);
				System.out.println("DES验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
														  + ") 未加密的stock_code_=" + data.getStockCode() + " 解密的stock_code=" + DESUtils.decryptHex(data.getStockCodeDES()));

				log.loger.warn("DES验证出的无效数据---" + (index+1) +": 股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) 
													  + ") 未加密的stock_code_=" + data.getStockCode() + " 解密的stock_code=" + DESUtils.decryptHex(data.getStockCodeDES()));
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中字段(stock_code_)的 DES加密验证成功！");
		}
		return flg;
	}
	
	/**
	 * 验证表(statistic_detail_stock_)中的stock_code_是否存在表daily_stock_中
	 *
	 */
	private boolean validateStockCodeInDailyStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中的字段(stock_code_)的正确性验证开始...");
		List<DailyStock> dailyStockList = dailyStockDao.statisticDailyStock();
		List<StatisticDetailStock> invalidList = listErrorStockCode(statisticDetailStockList, dailyStockList);
		if (invalidList.size() > 0) {
			flg = false;
			System.out.println("---------------验证表(statistic_detail_stock_)的字段(stock_code_)无效(与daily_stock_表比较)----------------");
			log.loger.warn("---------------验证表(statistic_detail_stock_)的字段(stock_code_)无效(与daily_stock_表比较)----------------");
			for (int index=0; index<invalidList.size(); index++) {
				StatisticDetailStock data = invalidList.get(index);
				System.out.println("验证出的无效数据----->" + "表(statistic_detail_stock_)中的股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")不存在表(daily_stock_)中！");
				log.loger.warn("验证出的无效数据----->" + "表(statistic_detail_stock_)中的股票" + data.getStockCode() + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")不存在表(daily_stock_)中！");
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中的字段(stock_code_)的正确性验证成功！");
		}
		return flg;
	}
	
	private List<StatisticDetailStock> listErrorStockCode(List<StatisticDetailStock> statisticDetailStockList, List<DailyStock> dailyStockList) throws SQLException {

		List<StatisticDetailStock> errorStatisticDetailStockList = new ArrayList<StatisticDetailStock>();
		try {
			for (StatisticDetailStock statisticDetailStock : statisticDetailStockList) {
				String stockCode = statisticDetailStock.getStockCode();
				// 比较stock_code_
				DailyStock dailyStock = CommonUtils.containStockCode(stockCode, dailyStockList);
				if (dailyStock == null) {
					errorStatisticDetailStockList.add(statisticDetailStock);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorStatisticDetailStockList;
	}
	
	/**
	 * 统计表(daily_stock_)中所有股票的总涨跌次数(不包括Json涨跌次数)
	 *
	 */
	private Map<String, StatisticDetailStock> statisticUpAndDownInDailyStock() throws SQLException {

		Map<String, StatisticDetailStock> statisticDetailStockMap = new HashMap<String, StatisticDetailStock>();
		Map<String, StatisticDetailStock> upDownNumberMap = dailyStockDao.statisticUpDownInDailyStock();
		Map<String, StatisticDetailStock> upNumberMap = dailyStockDao.statisticUpInDailyStock();
		Map<String, StatisticDetailStock> downNumberMap = dailyStockDao.statisticDownInDailyStock();
		statisticDetailStockMap = combineUpAndDownNumber(upDownNumberMap, upNumberMap, downNumberMap);
		return statisticDetailStockMap;
	}
	
	/**
	 * 根据statisticDetailStockList, 统计表(daily_stock_)中所有股票的总涨跌次数(不包括Json涨跌次数)
	 *
	 */
	private Map<String, StatisticDetailStock> statisticUpAndDownInDailyStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException {
		
		Map<String, StatisticDetailStock> statisticDetailStockMap = new HashMap<String, StatisticDetailStock>();
		Map<String, StatisticDetailStock> upDownNumberMap = new HashMap<String, StatisticDetailStock>();
		Map<String, StatisticDetailStock> upNumberMap = new HashMap<String, StatisticDetailStock>();
		Map<String, StatisticDetailStock> downNumberMap = new HashMap<String, StatisticDetailStock>();
		Date[] startEndDate = dailyStockDao.getMinMaxDate();
		for (StatisticDetailStock statisticDetailStock : statisticDetailStockList) {
			String stockCode = statisticDetailStock.getStockCode();
			startEndDate[1] = statisticDetailStock.getStockDate();
			Map<String, StatisticDetailStock> upDownMap = dailyStockDao.statisticUpDownInDailyStock(stockCode, startEndDate);
			Map<String, StatisticDetailStock> upMap = dailyStockDao.statisticUpInDailyStock(stockCode, startEndDate);
			Map<String, StatisticDetailStock> downMap = dailyStockDao.statisticDownInDailyStock(stockCode, startEndDate);
			upDownNumberMap.put(stockCode+DateUtils.dateToString(startEndDate[1]), upDownMap.get(stockCode));
			upNumberMap.put(stockCode+DateUtils.dateToString(startEndDate[1]), upMap.get(stockCode));
			downNumberMap.put(stockCode+DateUtils.dateToString(startEndDate[1]), downMap.get(stockCode));
		}
		statisticDetailStockMap = combineUpAndDownNumber(upDownNumberMap, upNumberMap, downNumberMap);
		return statisticDetailStockMap;
	}

	private Map<String, StatisticDetailStock> combineUpAndDownNumber(Map<String, StatisticDetailStock> upDownNumberMap, 
																	 Map<String, StatisticDetailStock> upNumberMap,
																	 Map<String, StatisticDetailStock> downNumberMap) {

		Map<String, StatisticDetailStock> statisticDetailStockMap = new HashMap<String, StatisticDetailStock>();
		//初始化涨和跌次数
		for (StatisticDetailStock statisticDetailStock : upDownNumberMap.values()) {
			statisticDetailStock.setUpNumber(DataUtils._INT_ZERO);
			statisticDetailStock.setDownNumber(DataUtils._INT_ZERO);
			//System.out.println("stockCode:" + statisticDetailStock.getStockCode() + "-----stockDate:" + statisticDetailStock.getStockDate());
			String mapKey = statisticDetailStock.getStockCode() + DateUtils.dateToString(statisticDetailStock.getStockDate());
			statisticDetailStockMap.put(mapKey, statisticDetailStock);
		}
		// 合并upNumberMap
		for (StatisticDetailStock statisticDetailStock : upNumberMap.values()) {
			String stockCode = statisticDetailStock.getStockCode();
			Integer upNumber = statisticDetailStock.getUpNumber();
			String mapKey = stockCode + DateUtils.dateToString(statisticDetailStock.getStockDate());
			if (statisticDetailStockMap.containsKey(mapKey)) {
				StatisticDetailStock stock = statisticDetailStockMap.get(mapKey);
				stock.setUpNumber(upNumber);
			} else {
				statisticDetailStockMap.put(mapKey, statisticDetailStock);
			}
		}
		// 合并downNumberMap
		for (StatisticDetailStock statisticDetailStock : downNumberMap.values()) {
			String stockCode = statisticDetailStock.getStockCode();
			Integer downNumber = statisticDetailStock.getDownNumber();
			String mapKey = stockCode + DateUtils.dateToString(statisticDetailStock.getStockDate());
			if (statisticDetailStockMap.containsKey(mapKey)) {
				StatisticDetailStock stock = statisticDetailStockMap.get(mapKey);
				stock.setDownNumber(downNumber);
			} else {
				statisticDetailStockMap.put(mapKey, statisticDetailStock);
			}
		}
		return statisticDetailStockMap;
	}
	
	/**
	 * 验证表statistic_detail_stock_总涨跌次数(与表original_stock_或daily_stock_)，返回错误总涨跌次数List
	 *
	 */
	private List<StatisticDetailStock> listErrorUpAndDownNumber(List<StatisticDetailStock> statisticDetailStockList, 
																Map<String, StatisticDetailStock> statisticUpAndDownMap,
																String tableFlg) {
		
		List<StatisticDetailStock> errorUpAndDownNumberList = new ArrayList<StatisticDetailStock>();
		for (StatisticDetailStock statisticDetailStock : statisticDetailStockList) {
			String stockCode = statisticDetailStock.getStockCode();
			String mapKey = stockCode + DateUtils.dateToString(statisticDetailStock.getStockDate());
			StatisticDetailStock statisticUpAndDownStock = statisticUpAndDownMap.get(mapKey);
			if (statisticUpAndDownStock == null) {
				System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在表(" + tableFlg + ")中！");
				log.loger.warn("----->表(statistic_detail_stock_)中" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在表(" + tableFlg + ")中！");
				continue;
			}
			//验证表statistic_detail_stock_的总涨跌次数
			StatisticDetailStock errorStatisticDetailStock = validateUpAndDownNumber(statisticUpAndDownStock, statisticDetailStock);
			if (errorStatisticDetailStock.getErrorUpDownFlg().compareTo(DataUtils._INT_ZERO) != 0)
				errorUpAndDownNumberList.add(errorStatisticDetailStock);
		}
		return errorUpAndDownNumberList;
	}
	
	//test the loop
	public void testLoop() {

		for (int index=0; index<10; index++) {
			testAop(String.valueOf(index));
		}
	}
	
	//test the function of AOP
	public void testAop(String param) {

		System.out.println("循环次数为：" + param);
	}
	
	/**
	 * 验证表(statistic_detail_stock_)中总涨跌次数
	 *
	 */
	private StatisticDetailStock validateUpAndDownNumber(StatisticDetailStock statisticJsonUpAndDownStock, StatisticDetailStock statisticDetailStock) {

		String stockCode = statisticDetailStock.getStockCode();
		Date stockDate = statisticDetailStock.getStockDate();
		StatisticDetailStock errorUpAndDownStock = new StatisticDetailStock(stockCode, stockDate);
		try {
			Message.addMethodExecuteNumber();
			Message.inputMethodExecuteMessage("验证表(statistic_detail_stock_)中总涨跌次数", "ValidateStatisticDetailStockData.validateUpAndDownNumber");
			//Message.inputMethodExecuteMessage(DateUtils.dateTimeToString(new Date()) + ">>>>>>验证表(statistic_detail_stock_)中总涨跌次数的方法(ValidateStatisticDetailStockData.validateUpAndDownNumber)被执行number次！");
			// 比较总涨跌次数
			Integer upDownNumber = statisticDetailStock.getUpDownNumber();
			Integer upNumber = statisticDetailStock.getUpNumber();
			Integer downNumber = statisticDetailStock.getDownNumber();
			Integer statisticUpDownNumber = statisticJsonUpAndDownStock.getUpDownNumber()==null?0:statisticJsonUpAndDownStock.getUpDownNumber();
			Integer statisticUpNumber = statisticJsonUpAndDownStock.getUpNumber()==null?0:statisticJsonUpAndDownStock.getUpNumber();
			Integer statisticDownNumber = statisticJsonUpAndDownStock.getDownNumber()==null?0:statisticJsonUpAndDownStock.getDownNumber();
			if (upDownNumber.compareTo(statisticUpDownNumber) != 0 
					|| upNumber.compareTo(statisticUpNumber) != 0
					|| downNumber.compareTo(statisticDownNumber) != 0) {
				errorUpAndDownStock.setErrorUpDownFlg(DataUtils._INT_ONE);
				errorUpAndDownStock.setUpDownNumber(statisticUpDownNumber);
				errorUpAndDownStock.setUpNumber(statisticUpNumber);
				errorUpAndDownStock.setDownNumber(statisticDownNumber);
				errorUpAndDownStock.setErrorUpDownNumber(upDownNumber);
				errorUpAndDownStock.setErrorUpNumber(upNumber);
				errorUpAndDownStock.setErrorDownNumber(downNumber);
			}
		} catch (Exception ex) {
			System.out.println("验证表(statistic_detail_stock_)中的股票" + statisticDetailStock.getStockCode() + "("
							   + PropertiesUtils.getProperty(statisticDetailStock.getStockCode()) + ")涨跌次数异常！");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorUpAndDownStock;
	}
	
	/**
	 * 验证表statistic_detail_stock_中股票总涨跌次数(与表original_stock_比较)
	 *
	 */
	private boolean validateUpAndDownInOriginalStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException, IOException {

		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中的总涨跌次数(与original_stock_表比较)验证开始...");
		Message.clear();
		List<OriginalStock> originalStockList = originalStockDao.listOriginalData();
		Map<String, StatisticDetailStock> originalUpAndDownNumberMap = StockUtils.statisticUpAndDownNumber(statisticDetailStockList, originalStockList, DataUtils._INT_ZERO); //总涨跌次数
	    // 验证合并涨跌次数
		List<StatisticDetailStock> invalidUpAndDownList = listErrorUpAndDownNumber(statisticDetailStockList, originalUpAndDownNumberMap, OriginalStock.TABLE_NAME);
		//if (!Message.methodExecuteMessageIsEmpty())
		Message.printMethodExecuteMessage();
		if (invalidUpAndDownList.size() > 0) {
			flg = false;
			System.out.println("---------------验证表(statistic_detail_stock_)的涨跌次数无效----------------");
			log.loger.warn("---------------验证表(statistic_detail_stock_)的涨跌次数无效----------------");
			for (int index=0; index<invalidUpAndDownList.size(); index++) {
				StatisticDetailStock data = invalidUpAndDownList.get(index);
				System.out.println("验证涨跌次数无效---" + DataUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_detail_stock_)中的错误数据为: " + getCorrectErrorMessages(data)[1] + "  统计的正确数据为: " + getCorrectErrorMessages(data)[0]);
				log.loger.warn("验证涨跌次数无效---" + DataUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
				 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_detail_stock_)中的错误数据为: " + getCorrectErrorMessages(data)[1] + "  统计的正确数据为: " + getCorrectErrorMessages(data)[0]);
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中的总涨跌次数(与表original_stock_比较)验证成功！");
		}
		return flg;
	}

	/**
	 * 验证表(statistic_detail_stock_)中股票Json涨跌次数(与表original_stock_比较)
	 *
	 */
	private boolean validateUpAndDownJsonInOriginalStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException, IOException {
		
		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中股票Json涨跌次数(与表original_stock_比较)验证开始...");
		Message.clear();
		// 统计表original_stock_中所有股票总涨跌次数和Json涨跌次数
		Map<String, StatisticDetailStock> upAndDownNumberJsonMap = statisticUpDownNumberInOriginalStock(statisticDetailStockList);
	    // 验证Json涨跌次数
		List<StatisticDetailStock> invalidUpAndDownList = listErrorJsonUpAndDown(statisticDetailStockList, upAndDownNumberJsonMap, OriginalStock.TABLE_NAME);
		Message.printMethodExecuteMessage();
		if (invalidUpAndDownList.size() > 0) {
			flg = false;
			System.out.println("---------------验证统计股票信息表(statistic_stock_)的涨跌次数无效----------------");
			log.loger.warn("---------------验证统计股票信息表(statistic_stock_)的涨跌次数无效----------------");
			for (int index=0; index<invalidUpAndDownList.size(); index++) {
				StatisticDetailStock data = invalidUpAndDownList.get(index);
				System.out.println("验证涨跌次数无效---" + DataUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
								 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_stock_)中的错误数据为: " + getCorrectErrorMessages(data)[1] + "  统计的正确数据为: " + getCorrectErrorMessages(data)[0]);
				log.loger.warn("验证涨跌次数无效---" + DataUtils.supplyNumber(index+1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() 
				 + "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_stock_)中的错误数据为: " + getCorrectErrorMessages(data)[1] + "  统计的正确数据为: " + getCorrectErrorMessages(data)[0]);
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_stock_)中的总涨跌次数(与original_stock_表比较)验证成功！");
		}
		return flg;
	}
	
	/**
	 * 验证Json涨跌次数，返回错误Json涨跌次数List
	 *
	 */
	private List<StatisticDetailStock> listErrorJsonUpAndDown(List<StatisticDetailStock> statisticDetailStockList, 
															  Map<String, StatisticDetailStock> statisticJsonUpAndDownMap, 
															  String tableFlg) {

		List<StatisticDetailStock> errorJsonUpAndDownNumberList = new ArrayList<StatisticDetailStock>();

		for (StatisticDetailStock statisticDetailStock : statisticDetailStockList) {
			String stockCode = statisticDetailStock.getStockCode();
			String mapKey = stockCode + DateUtils.dateToString(statisticDetailStock.getStockDate());
			StatisticDetailStock statisticJsonUpAndDownStock = statisticJsonUpAndDownMap.get(mapKey);
			if (statisticJsonUpAndDownStock == null) {
				System.out.println("表(statistic_detail_stock_)中" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在表(" + tableFlg + ")中！");
				log.loger.warn("表(statistic_detail_stock_)中" + stockCode + "(" + PropertiesUtils.getProperty(stockCode) + ")不存在表(" + tableFlg + ")中！");
				continue;
			}
			//验证表(statistic_detail_stock_)中的Json涨跌次数
			StatisticDetailStock errorJsonStatisticDetailStock = validateJsonUpAndDownNumber(statisticJsonUpAndDownStock, statisticDetailStock);
			if (errorJsonStatisticDetailStock.getErrorUpDownFlg().compareTo(DataUtils._INT_ZERO) != 0 &&
				errorJsonStatisticDetailStock.getErrorUpDownFlg().compareTo(DataUtils._INT_ONE) != 0)
				errorJsonUpAndDownNumberList.add(errorJsonStatisticDetailStock);
		}
		return errorJsonUpAndDownNumberList;
	}
	
	/**
	 * 验证表(statistic_detail_stock_)中的Json涨跌次数(与表daily_stock_比较)
	 *
	 */
	private boolean validateUpAndDownJsonInDailyStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException, IOException {

		boolean flg = true;
		System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中股票的Json涨跌次数(与daily_stock_表比较)验证开始...");
		Message.clear();
		// 根据statisticDetailStockList，统计daily_stock_表中所有股票Json涨跌次数
		Map<String, StatisticDetailStock> jsonUpAndDownMap = statisticJsonUpAndDownInDailyStock(statisticDetailStockList);
	    // 验证Json涨跌次数(与daily_stock_表比较)
		List<StatisticDetailStock> invalidUpAndDownList = listErrorJsonUpAndDown(statisticDetailStockList, jsonUpAndDownMap, DailyStock.TABLE_NAME);
		//if (!Message.methodExecuteMessageIsEmpty())
		Message.printMethodExecuteMessage();
		if (invalidUpAndDownList.size() > 0) {
			flg = false;
			System.out.println("---------------验证表(statistic_detail_stock_)的Json涨跌次数无效----------------");
			log.loger.warn("---------------验证表(statistic_detail_stock_)的Json涨跌次数无效----------------");
			for (int index = 0; index < invalidUpAndDownList.size(); index++) {
				StatisticDetailStock data = invalidUpAndDownList.get(index);
				System.out.println("验证涨跌次数无效---" + DataUtils.supplyNumber(index + 1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode()
						+ "(" + PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_detail_stock_)中的错误数据为: "
						+ getCorrectErrorMessages(data)[1] + "  统计的正确数据为: " + getCorrectErrorMessages(data)[0]);
				log.loger.warn("验证涨跌次数无效---" + DataUtils.supplyNumber(index + 1, invalidUpAndDownList.size()) + ": 股票" + data.getStockCode() + "("
						+ PropertiesUtils.getProperty(data.getStockCode()) + ")在表(statistic_detail_stock_)中的错误数据为: " + getCorrectErrorMessages(data)[1]
						+ "  统计的正确数据为: " + getCorrectErrorMessages(data)[0]);
			}
		} else {
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(statistic_detail_stock_)中股票的Json涨跌次数(与daily_stock_表比较)验证成功！");
		}
		return flg;
	}
	
	/**
	 * 根据statisticDetailStockList，统计daily_stock_表中所有股票Json涨跌次数
	 *
	 */
	private Map<String, StatisticDetailStock> statisticJsonUpAndDownInDailyStock(List<StatisticDetailStock> statisticDetailStockList) throws SQLException {
		
		Map<String, StatisticDetailStock> jsonUpAndDownStatisticMap = new HashMap<String, StatisticDetailStock>();
		for (StatisticDetailStock statisticDetailStock : statisticDetailStockList) {
			String stockCode = statisticDetailStock.getStockCode();
			Date stockDate = statisticDetailStock.getStockDate();
			String mapKey = stockCode + DateUtils.dateToString(stockDate);
			StatisticDetailStock newStatisticDetailStock = new StatisticDetailStock(stockCode, stockDate);
			statisticUpAndDownJsonInDailyStock(newStatisticDetailStock); // 根据统计日期，统计表(statistic_detail_stock_)中Json涨跌次数
			jsonUpAndDownStatisticMap.put(mapKey, newStatisticDetailStock);
		}
		return jsonUpAndDownStatisticMap;
	}
	
	/**
	 * 统计daily_stock_表中所有股票的Json涨跌次数
	 *
	 */
	private Map<String, StatisticDetailStock> statisticJsonUpAndDownInDailyStock() throws SQLException {
		
		Map<String, StatisticDetailStock> jsonUpAndDownStatisticMap = new HashMap<String, StatisticDetailStock>();
		List<DailyStock> dailyStockList = dailyStockDao.statisticDailyStock();
		for (DailyStock dailyStock : dailyStockList) {
			String stockCode = dailyStock.getStockCode();
			Date stockDate = dailyStock.getStockDate();
			StatisticDetailStock statisticDetailStock = new StatisticDetailStock(stockCode, stockDate);
			statisticUpAndDownJsonInDailyStock(statisticDetailStock); // 统计表(statistic_detail_stock_)中Json涨跌次数
			jsonUpAndDownStatisticMap.put(stockCode, statisticDetailStock);
		}
		return jsonUpAndDownStatisticMap;
	}

	private String[] getCorrectErrorMessages(StatisticDetailStock data) throws IOException, SQLException {
		
		String correctMessage = "";
		String errorMessage = "";
		Date[] minMaxDate = dailyStockDao.getMinMaxDate();
		Integer errorUpDownFlg = data.getErrorUpDownFlg();
		switch (errorUpDownFlg) {
		case DataUtils._INT_EIGHT:
			String oneWeekJson = data.getOneWeek();
			String errorOneWeekJson = data.getErrorOneWeek();
			if (!CommonUtils.isJsonBlank(oneWeekJson) && !CommonUtils.isJsonBlank(errorOneWeekJson)) {
				String[] oneWeekMessages = getCorrectErrorMessagesByJson(oneWeekJson, errorOneWeekJson, StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM);
				String oneWeekTitle = "一周(" + DateUtils.getTimeInterval(DateUtils.getPreOneWeek(minMaxDate[1])) + ")";
				correctMessage = oneWeekTitle + oneWeekMessages[0];
				errorMessage = oneWeekTitle + oneWeekMessages[1];
			}

		case DataUtils._INT_SEVEN:
			String halfMonthJson = data.getHalfMonth();
			String errorHalfMonthJson = data.getErrorHalfMonth();
			if (!CommonUtils.isJsonBlank(halfMonthJson) && !CommonUtils.isJsonBlank(errorHalfMonthJson)) {
				String[] halfMonthMessages = getCorrectErrorMessagesByJson(halfMonthJson, errorHalfMonthJson, StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM);
				String halfMonthTitle = "半月(" + DateUtils.getTimeInterval(DateUtils.getPreHalfMonth(minMaxDate[1])) + ")";
				correctMessage += " " + halfMonthTitle + halfMonthMessages[0];
				errorMessage += " " + halfMonthTitle + halfMonthMessages[1];
			}
			
		case DataUtils._INT_SIX:
			String oneMonthJson = data.getOneMonth();
			String errorOneMonthJson = data.getErrorOneMonth();
			if (!CommonUtils.isJsonBlank(oneMonthJson) && !CommonUtils.isJsonBlank(errorOneMonthJson)) {
				String[] oneMonthMessages = getCorrectErrorMessagesByJson(oneMonthJson, errorOneMonthJson, StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM);
				String oneMonthTitle = "一月(" + DateUtils.getTimeInterval(DateUtils.getPreOneMonth(minMaxDate[1])) + ")";
				correctMessage += " " + oneMonthTitle + oneMonthMessages[0];
				errorMessage += " " + oneMonthTitle + oneMonthMessages[1];
			}

		case DataUtils._INT_FIVE:
			String twoMonthJson = data.getTwoMonth();
			String errorTwoMonthJson = data.getErrorTwoMonth();
			if (!CommonUtils.isJsonBlank(twoMonthJson) && !CommonUtils.isJsonBlank(errorTwoMonthJson)) {
				String[] twoMonthMessages = getCorrectErrorMessagesByJson(twoMonthJson, errorTwoMonthJson, StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM);
				String twoMonthTitle = "二月(" + DateUtils.getTimeInterval(DateUtils.getPreTwoMonth(minMaxDate[1])) + ")";
				correctMessage += " " + twoMonthTitle + twoMonthMessages[0];
				errorMessage += " " + twoMonthTitle + twoMonthMessages[1];
			}

		case DataUtils._INT_FOUR:
			String threeMonthJson = data.getThreeMonth();
			String errorThreeMonthJson = data.getErrorThreeMonth();
			if (!CommonUtils.isJsonBlank(threeMonthJson) && !CommonUtils.isJsonBlank(errorThreeMonthJson)) {
				String[] threeMonthMessages = getCorrectErrorMessagesByJson(threeMonthJson, errorThreeMonthJson, StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM);
				String threeMonthTitle = "三月(" + DateUtils.getTimeInterval(DateUtils.getPreThreeMonth(minMaxDate[1])) + ")";
				correctMessage += " " + threeMonthTitle + threeMonthMessages[0];
				errorMessage += " " + threeMonthTitle + threeMonthMessages[1];
			}
						
		case DataUtils._INT_THREE:
			String halfYearJson = data.getHalfYear();
			String errorHalfYearJson = data.getErrorHalfMonth();
			if (!CommonUtils.isJsonBlank(halfYearJson) && !CommonUtils.isJsonBlank(errorHalfYearJson)) {
				String[] halfYearMessages = getCorrectErrorMessagesByJson(halfYearJson, errorHalfYearJson, StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM);
				String halfYearTitle = "半年(" + DateUtils.getTimeInterval(DateUtils.getPreHalfYear(minMaxDate[1])) + ")";
				correctMessage += " " + halfYearTitle + halfYearMessages[0];
				errorMessage += " " + halfYearTitle + halfYearMessages[1];
			}
			
		case DataUtils._INT_TWO:
			String oneYearJson = data.getOneYear();
			String errorOneYearJson = data.getErrorOneYear();
			if (!CommonUtils.isJsonBlank(oneYearJson) && !CommonUtils.isJsonBlank(errorOneYearJson)) {
				String[] oneYearMessages = getCorrectErrorMessagesByJson(oneYearJson, errorOneYearJson, StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM);
				String oneYearTitle = "一年(" + DateUtils.getTimeInterval(DateUtils.getPreOneYear(minMaxDate[1])) + ")";
				correctMessage += " " + oneYearTitle + oneYearMessages[0];
				errorMessage += " " + oneYearTitle + oneYearMessages[1];
			}
		case DataUtils._INT_ONE:
			Integer upDownNumber = data.getUpDownNumber();
			Integer upNumber = data.getUpNumber();
			Integer downNumber = data.getDownNumber();
			Integer errorUpDownNumber = data.getErrorUpDownNumber();
			Integer errorUpNumber = data.getErrorUpNumber();
			Integer errorDownNumber = data.getDownNumber();
			if (upDownNumber!=null && upNumber!=null && downNumber!=null 
				&& errorUpDownNumber!=null && errorUpNumber!=null && errorDownNumber!=null) {
				String totalTitle = "时间(" + DateUtils.getTimeInterval(minMaxDate) + ")";
				correctMessage += " " + totalTitle + StatisticDetailStock.CH_ALL_UP_DOWN + ":" + upDownNumber + StatisticDetailStock.CH_UP + ":" + upNumber + StatisticDetailStock.CH_DOWN + ":" + downNumber;
				errorMessage += " " + totalTitle + StatisticDetailStock.CH_ALL_UP_DOWN + ":" + errorUpDownNumber + StatisticDetailStock.CH_UP + ":" + errorUpNumber + StatisticDetailStock.CH_DOWN + ":" + errorDownNumber;
			}
			break;
		default:
			IOException ioException = new IOException("周期标识(errorUpDownFlg)不正确: " + errorUpDownFlg);
			throw ioException;
		}
		String[] correctErrorArray = {correctMessage, errorMessage};
		return correctErrorArray;
	}
	
	/**
	 * 验证statistic_detail_stock_表中Json涨跌次数
	 *
	 */
	private StatisticDetailStock validateJsonUpAndDownNumber(StatisticDetailStock statisticJsonUpAndDownStock, 
															 StatisticDetailStock statisticDetailStock) {

		String stockCode = statisticDetailStock.getStockCode();
		Date stockDate = statisticDetailStock.getStockDate();
		StatisticDetailStock errorJsonStatisticDetailStock = new StatisticDetailStock(stockCode, stockDate);
		try {
			// 比较总涨跌次数
			/*Integer upDownNumber = statisticDetailStock.getUpDownNumber();
			Integer upNumber = statisticDetailStock.getUpNumber();
			Integer downNumber = statisticDetailStock.getDownNumber();
			Integer statisticUpDownNumber = statisticJsonUpAndDownStock.getUpDownNumber();
			Integer statisticUpNumber = statisticJsonUpAndDownStock.getUpNumber();
			Integer statisticDownNumber = statisticJsonUpAndDownStock.getDownNumber();
			if (upDownNumber.compareTo(statisticUpDownNumber) != 0 
					|| upNumber.compareTo(statisticUpNumber) != 0 
					|| downNumber.compareTo(statisticDownNumber) != 0) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils.CONSTANT_INTEGER_ONE);
				errorJsonStatisticDetailStock.setUpDownNumber(statisticUpDownNumber);
				errorJsonStatisticDetailStock.setUpNumber(statisticUpNumber);
				errorJsonStatisticDetailStock.setDownNumber(statisticDownNumber);
				errorJsonStatisticDetailStock.setErrorUpDownNumber(upDownNumber);
				errorJsonStatisticDetailStock.setErrorUpNumber(upNumber);
				errorJsonStatisticDetailStock.setErrorDownNumber(downNumber);
			}*/

			Message.addMethodExecuteNumber();
			Message.inputMethodExecuteMessage("验证表(statistic_stock_)中Json涨跌次数", "ValidateStatisticDetailStockData.validateJsonUpAndDownNumber");
			//Message.inputMethodExecuteMessage(DateUtils.dateTimeToString(new Date()) + ">>>>>>验证statistic_stock_表中Json涨跌次数的方法(validateJsonUpAndDownNumber)被执行number次！");
			// 比较一年涨跌次数
			String oneYearJson = statisticDetailStock.getOneYear();
			String oneYearStatisticJson = statisticJsonUpAndDownStock.getOneYear();
			if (!DataUtils.validateJsonUpAndDownNumber(oneYearJson, oneYearStatisticJson, StatisticDetailStock.PRE_ONE_YEAR_UP_DOWN_NUM)) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils._INT_TWO);
				errorJsonStatisticDetailStock.setOneYear(oneYearStatisticJson);
				errorJsonStatisticDetailStock.setErrorOneYear(oneYearJson);
			}
			// 比较半年涨跌次数
			String halfYearJson = statisticDetailStock.getHalfYear();
			String halfYearStatisticJson = statisticJsonUpAndDownStock.getHalfYear();
			if (!DataUtils.validateJsonUpAndDownNumber(halfYearJson, halfYearStatisticJson, StatisticDetailStock.PRE_HALF_YEAR_UP_DOWN_NUM)) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils._INT_THREE);
				errorJsonStatisticDetailStock.setHalfYear(halfYearStatisticJson);
				errorJsonStatisticDetailStock.setErrorHalfYear(halfYearJson);
			}
			// 比较三月涨跌次数
			String threeMonthJson = statisticDetailStock.getThreeMonth();
			String threeMonthStatisticJson = statisticJsonUpAndDownStock.getThreeMonth();
			if (!DataUtils.validateJsonUpAndDownNumber(threeMonthJson, threeMonthStatisticJson, StatisticDetailStock.PRE_THREE_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils._INT_FOUR);
				errorJsonStatisticDetailStock.setThreeMonth(threeMonthStatisticJson);
				errorJsonStatisticDetailStock.setErrorThreeMonth(threeMonthJson);
			}
			// 比较二月涨跌次数
			String twoMonthJson = statisticDetailStock.getTwoMonth();
			String twoMonthStatisticJson = statisticJsonUpAndDownStock.getTwoMonth();
			if (!DataUtils.validateJsonUpAndDownNumber(twoMonthJson, twoMonthStatisticJson, StatisticDetailStock.PRE_TWO_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils._INT_FIVE);
				errorJsonStatisticDetailStock.setTwoMonth(twoMonthStatisticJson);
				errorJsonStatisticDetailStock.setErrorTwoMonth(twoMonthJson);
			}
			// 比较一月涨跌次数
			String oneMonthJson = statisticDetailStock.getOneMonth();
			String oneMonthStatisticJson = statisticJsonUpAndDownStock.getOneMonth();
			if (!DataUtils.validateJsonUpAndDownNumber(oneMonthJson, oneMonthStatisticJson, StatisticDetailStock.PRE_ONE_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils._INT_SIX);
				errorJsonStatisticDetailStock.setOneMonth(oneMonthStatisticJson);
				errorJsonStatisticDetailStock.setErrorOneMonth(oneMonthJson);
			}
			// 比较半月涨跌次数
			String halfMonthJson = statisticDetailStock.getHalfMonth();
			String halfMonthStatisticJson = statisticJsonUpAndDownStock.getHalfMonth();
			if (!DataUtils.validateJsonUpAndDownNumber(halfMonthJson, halfMonthStatisticJson, StatisticDetailStock.PRE_HALF_MONTH_UP_DOWN_NUM)) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils._INT_SEVEN);
				errorJsonStatisticDetailStock.setHalfMonth(halfMonthStatisticJson);
				errorJsonStatisticDetailStock.setErrorHalfMonth(halfMonthJson);
			}
			// 比较一周涨跌次数
			String oneWeekJson = statisticDetailStock.getOneWeek();
			String oneWeekStatisticJson = statisticJsonUpAndDownStock.getOneWeek();
			if (!DataUtils.validateJsonUpAndDownNumber(oneWeekJson, oneWeekStatisticJson, StatisticDetailStock.PRE_ONE_WEEK_UP_DOWN_NUM)) {
				errorJsonStatisticDetailStock.setErrorUpDownFlg(DataUtils._INT_EIGHT);
				errorJsonStatisticDetailStock.setOneWeek(oneWeekStatisticJson);
				errorJsonStatisticDetailStock.setErrorOneWeek(oneWeekJson);
			}
		} catch (Exception ex) {
			System.out.println(DateUtils.dateToString(new Date()) + " 验证表(statistic_detail_stock_)中的股票" + statisticDetailStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticDetailStock.getStockCode()) + ")涨跌次数报异常错误！");
			log.loger.error(" 验证表(statistic_detail_stock_)中的股票" + statisticDetailStock.getStockCode() + "("
					+ PropertiesUtils.getProperty(statisticDetailStock.getStockCode()) + ")涨跌次数报异常错误！");
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return errorJsonStatisticDetailStock;
	}
	
	private String[] getCorrectErrorMessagesByJson(String correctUpAndDownJson, String errorUpAndDownJson, String periodFlg) throws IOException {
		
		String[] upAndDownNumberKeys = StockUtils.getUpAndDownNumberKeysByFlg(periodFlg);
		String upDownNumberKey = upAndDownNumberKeys[0];
		String upNumberKey = upAndDownNumberKeys[1];
		String downNumberKey = upAndDownNumberKeys[2];
		Map<String, Integer> correctUpAndDownMap = JsonUtils.getMapByJson(correctUpAndDownJson);
		Map<String, Integer> errorUpAndDownMap = JsonUtils.getMapByJson(errorUpAndDownJson);
		Integer correctUpDownNumber = correctUpAndDownMap.get(upDownNumberKey);
		Integer correctUpNumber = correctUpAndDownMap.get(upNumberKey);
		Integer correctDownNumber = correctUpAndDownMap.get(downNumberKey);
		Integer errorUpDownNumber = errorUpAndDownMap.get(upDownNumberKey);
		Integer errorUpNumber = errorUpAndDownMap.get(upNumberKey);
		Integer errorDownNumber = errorUpAndDownMap.get(downNumberKey);
		String correctMessage = StatisticDetailStock.CH_UP_DOWN + ":" + correctUpDownNumber + StatisticDetailStock.CH_UP + ":" + correctUpNumber + StatisticDetailStock.CH_DOWN + ":" + correctDownNumber;
		String errorMessage = StatisticDetailStock.CH_UP_DOWN + ":" + errorUpDownNumber + StatisticDetailStock.CH_UP + ":" + errorUpNumber + StatisticDetailStock.CH_DOWN + ":" + errorDownNumber;
		String[] messages = {correctMessage, errorMessage};
		return messages;
	}
}
