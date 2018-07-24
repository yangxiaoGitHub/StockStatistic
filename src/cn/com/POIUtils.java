package cn.com;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.db.bean.AllImportStock;
import cn.log.Log;

public class POIUtils {
	static Log log = Log.getLoger();
    public final static String XLS = ".xls";
    public final static String XLSX = ".xlsx";

	public static List<AllImportStock> readExcel(String filePath) {

		List<AllImportStock> allImportStockList = new ArrayList<AllImportStock>();
		try {
			Workbook workBook = getWorkBook(filePath);
			Sheet sheet = workBook.getSheetAt(0);
			int firstRowNum = sheet.getFirstRowNum();
			int lastRowNum = sheet.getLastRowNum();
			for (int rowNum = (firstRowNum + 1); rowNum <= lastRowNum; rowNum++) {
				Row row = sheet.getRow(rowNum);
				int firstCellNum = row.getFirstCellNum();
				int lastCellNum = row.getLastCellNum();
				AllImportStock allImportStock = new AllImportStock();
				for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
					Cell cell = row.getCell(cellNum);
					setCellValue(allImportStock, cell, cellNum);
				}
				allImportStockList.add(allImportStock);
				ObjectUtils.printProperties(allImportStock);
			}
			workBook.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
		return allImportStockList;
	}
    
    private static void setCellValue(AllImportStock allImportStock, Cell cell, int cellNum) {
    	
    	String cellValue = getCellValue(cell);
    	List<Integer> cellNumList = Arrays.asList(2,3,4,6,7,8,9,13,15,16,21,22,23,24,28,29,30,31);
    	if (cellValue.equals(DataUtils._BLANK) && (cellNumList.contains(cellNum))) {
    		cellValue = DataUtils._ZERO;
    	}
    	switch(cellNum) {
    	case 0: //代码
    		String stockCode = cellValue;
    		stockCode = StockUtils.getStockCodeByAliasCode(stockCode);
    		allImportStock.setStockCode(stockCode);
    		break;
    	case 2: //涨幅
    		Double changeRate = Double.valueOf(cellValue);
    		allImportStock.setChangeRate(changeRate);
    		break;
    	case 3: //现价
    		Double currentPrice = Double.valueOf(cellValue);
    		allImportStock.setCurrent(currentPrice);
    		break;
    	case 4: //总手(成交量-股)
    		Long totalHands = Long.valueOf(cellValue);
    		allImportStock.setTotalHands(totalHands);
    		break;
    	case 6: //昨收
    		Double yesterdayClose = Double.valueOf(cellValue);
    		allImportStock.setYesterdayClose(yesterdayClose);
    		break;
    	case 7: //开盘
    		Double todayOpen = Double.valueOf(cellValue);
    		allImportStock.setTodayOpen(todayOpen);
    		break;
    	case 8: //最高
    		Double todayHigh = Double.valueOf(cellValue);
    		allImportStock.setTodayHigh(todayHigh);
    		break;
    	case 9: //最低
    		Double todayLow = Double.valueOf(cellValue);
    		allImportStock.setTodayLow(todayLow);
    		break;
    	case 13: //量比
    		Double quantityRatio = Double.valueOf(cellValue);
    		allImportStock.setQuantityRatio(quantityRatio);
    		break;
    	case 14: //所属行业
    		String industry = cellValue;
    		allImportStock.setIndustry(industry);
    		break;
    	case 15: //市盈(动)
    		Double peRatio = Double.valueOf(cellValue);
    		allImportStock.setPeRatio(peRatio);
    		break;
    	case 16: //市净率
    		Double pbRatio = Double.valueOf(cellValue);
    		allImportStock.setPbRatio(pbRatio);
    		break;
    	case 21: //振幅
    		Double amplitude = Double.valueOf(cellValue);
    		allImportStock.setAmplitude(amplitude);
    		break;
    	case 22: //换手
    		Double turnoverRate = Double.valueOf(cellValue);
    		allImportStock.setTurnoverRate(turnoverRate);
    		break;
    	case 23: //涨跌
    		Double upDown = Double.valueOf(cellValue);
    		allImportStock.setUpDown(upDown);
    		break;
    	case 24: //金额(成交量-元)
    		Long amount = Long.valueOf(cellValue);
    		allImportStock.setAmount(amount);
    		break;
    	case 28: //外盘
    		Long outside = Long.valueOf(cellValue);
    		allImportStock.setOutside(outside);
    		break;
    	case 29: //内盘
    		Long inside = Long.valueOf(cellValue);
    		allImportStock.setInside(inside);
    		break;
    	case 30: //总市值
    		Long marketCap = Long.valueOf(cellValue);
    		allImportStock.setMarketCap(marketCap);
    		break;
    	case 31: //流通值
    		Long circulateValue = Long.valueOf(cellValue);
    		allImportStock.setCirculateValue(circulateValue);
    		break;
    	}
    }
    
    private static String getCellValue(Cell cell) {

    	   String cellValue = DataUtils._BLANK;
    	   if (cell==null) return cellValue;

    	   //设置cell格式为String，避免1变成1.0
    	   if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
    	      cell.setCellType(Cell.CELL_TYPE_STRING);
    	   }
    	   //判断数据的类型
    	   switch(cell.getCellType()) {
    	      case Cell.CELL_TYPE_NUMERIC: //数字
    	         cellValue = String.valueOf(cell.getNumericCellValue());
    	         break;
    	      case Cell.CELL_TYPE_STRING: //字符串
    	         cellValue = String.valueOf(cell.getStringCellValue());
    	         break;
    	      case Cell.CELL_TYPE_BOOLEAN: //Boolean
    	         cellValue = String.valueOf(cell.getBooleanCellValue());
    	         break;
    	      case Cell.CELL_TYPE_FORMULA: //公式
    	         cellValue = String.valueOf(cell.getCellFormula());
    	         break;
    	      case Cell.CELL_TYPE_BLANK: //空值
    	         cellValue = DataUtils._BLANK;
    	         break;
    	      default:
    	         cellValue = DataUtils._BLANK;
    	         break;
    	   }
    	   return cellValue.contains(DataUtils._DASH)?DataUtils._BLANK:cellValue;
    	}

	private static Workbook getWorkBook(String filePath) throws EncryptedDocumentException, InvalidFormatException {

		Workbook workBook = null;
		try {
			InputStream is = new FileInputStream(filePath);
			if (filePath.endsWith(XLSX)) {
				workBook = (XSSFWorkbook) WorkbookFactory.create(is);
			} else if (filePath.endsWith(XLS)) {
				POIFSFileSystem poiFile = new POIFSFileSystem(is);
				workBook = new HSSFWorkbook(poiFile);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return workBook;
	}
}
