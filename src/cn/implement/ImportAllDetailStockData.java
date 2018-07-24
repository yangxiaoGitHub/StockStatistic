package cn.implement;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.com.CommonUtils;
import cn.com.DESUtils;
import cn.com.DateUtils;
import cn.com.ObjectUtils;
import cn.com.POIUtils;
import cn.com.PropertiesUtils;
import cn.db.AllImportStockDao;
import cn.db.bean.AllImportStock;

public class ImportAllDetailStockData extends OperationData {

	public void importAllStockDetail(String sDate) {

		int saveNum = 0;
		allImportStockDao = new AllImportStockDao();
		try {
			String path = PropertiesUtils.getProperty("excelPath") + sDate;
			String xlsFilePath = path + POIUtils.XLS;
			String xlsxFilePath = path + POIUtils.XLSX;
			String xlsPath = ObjectUtils.checkFile(xlsFilePath);
			String xlsxPath = ObjectUtils.checkFile(xlsxFilePath);
			String filePath = (xlsPath==null?xlsxPath:xlsPath);
			if (filePath == null) {
				Exception exception = new IOException("Excel文件(" + (sDate + POIUtils.XLS) + "和" + (sDate + POIUtils.XLSX) + ")不存在！");
				throw exception;
			}
			long maxNum = allImportStockDao.getMaxNum(AllImportStock.TABLE_NAME);
			List<AllImportStock> allImportStockList = POIUtils.readExcel(filePath);
			// 导入所有股票每日详细信息(all_import_stock_)
			for (int index = 0; index < allImportStockList.size(); index++) {
				AllImportStock allImportStock = allImportStockList.get(index);
				allImportStock.setNum(++maxNum);
				String stockCode = allImportStock.getStockCode();
				Date stockDate = DateUtils.stringToDate(sDate);
				allImportStock.setStockDate(stockDate);
				allImportStock.setStockCodeDES(DESUtils.encryptToHex(stockCode));
				Double changeRate = allImportStock.getChangeRate();
				allImportStock.setChangeRateDes(DESUtils.encryptToHex(changeRate.toString()));
				Double turnoverRate = allImportStock.getTurnoverRate();
				allImportStock.setTurnoverRateDes(DESUtils.encryptToHex(turnoverRate.toString()));
				allImportStock.setInputTime(new Date());
				boolean isExist = allImportStockDao.isExistInAllImportStock(stockCode, stockDate);
				if (isExist) continue;
				boolean saveFlg = allImportStockDao.saveAllImportStock(allImportStock);
				if (saveFlg) {
					saveNum++;
					System.out.println(DateUtils.dateTimeToString(new Date()) + "----->表(all_import_stock_)增加了股票  " + saveNum + "："
							+ allImportStock.getStockCode() + "(" + PropertiesUtils.getProperty(allImportStock.getStockCode()) + ")");
					log.loger.warn("----->表(all_import_stock_)增加了股票  " + saveNum + "：" + allImportStock.getStockCode() + "(" 
							+ PropertiesUtils.getProperty(allImportStock.getStockCode()) + ")");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		} finally {
			closeDao(allImportStockDao);
			System.out.println(DateUtils.dateTimeToString(new Date()) + "----->" + sDate + " 表(all_import_stock_)共增加了" + saveNum + "条记录！");
			log.loger.warn("----->" + sDate + " 表(all_import_stock_)共增加了" + saveNum + "条记录！");
		}
	}
}
