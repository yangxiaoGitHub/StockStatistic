package cn.com;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import cn.db.bean.DailyStock;

public class PictureUtils {
	
	public static final String FILE_SUFFIX = ".jpg";

	/*
	public static void main(String[] args) {
		try {
			Date nowDate = new Date();
			DailyStock stockOne = new DailyStock("600771", nowDate);
			stockOne.setChangeRate(1.12);
			stockOne.setTurnoverRate(0.23);
			DailyStock stockTwo = new DailyStock("600754", nowDate);
			stockTwo.setChangeRate(2.12);
			stockTwo.setTurnoverRate(1.57);
			DailyStock stockThree = new DailyStock("600741", nowDate);
			stockThree.setChangeRate(3.21);
			stockThree.setTurnoverRate(2.15);
			DailyStock stockFour = new DailyStock("600566", nowDate);
			stockFour.setChangeRate(1.56);
			stockFour.setTurnoverRate(2.31);
			DailyStock stockFive = new DailyStock("600521", nowDate);
			stockFive.setChangeRate(0.25);
			stockFive.setTurnoverRate(0.15);
			
			List<DailyStock> dailyStockList = new ArrayList<DailyStock>();
			dailyStockList.add(stockOne);
			dailyStockList.add(stockTwo);
			dailyStockList.add(stockThree);
			dailyStockList.add(stockFour);
			dailyStockList.add(stockFive);

			generateDailyStockPic(DateUtils.stringToDate("2018-03-16"), dailyStockList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	*/

	public static void generateDailyStockPic(String stockDate, List<DailyStock> dailyStockList) {

		try {
			String title = stockDate + " 选择的股票";
			String[] headCells = { "序号", "股票代码", "股票名称", "涨跌幅", "换手率" };
			String filePath = PropertiesUtils.getProperty("picturePath");
			String filePathName = filePath + stockDate + FILE_SUFFIX;
			Date nowDate = new Date();
			String notes = "创建图片的时间 " + DateUtils.dateTimeMsecToString(nowDate);
			String text = String.valueOf(nowDate.getTime());

			List<String[]> dataList = new ArrayList<String[]>();
			for (int index = 0; index < dailyStockList.size(); index++) {
				DailyStock dailyStock = dailyStockList.get(index);
				String stockCode = dailyStock.getStockCode();
				String stockName = PropertiesUtils.getProperty(stockCode);
				Double changeRate = dailyStock.getChangeRate();
				Double turnoverRate = dailyStock.getTurnoverRate();
				String[] dataArray = { (index + 1) + "", stockCode, stockName, changeRate + "%", turnoverRate + "%" };
				dataList.add(dataArray);
			}
			generateTablePic(filePathName, title, headCells, dataList, notes);
			markImageBySingleText(filePathName, filePath, stockDate, FILE_SUFFIX, Color.gray, text, 12);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//生成一个表格
	private static void generateTablePic(String filePathName, String title, String[] headCells, List<String[]> cellList, String notes) throws Exception {

		int totalrow = cellList.size() + 1;
		int totalcol = headCells.length;
		int titleWidth = 230;
		int imageWidth = DataUtils._INT_ONE_KB;
		int rowheight = DataUtils._INT_FORTY;
		int startHeight = DataUtils._INT_TEN;
		int startWidth = DataUtils._INT_TEN;;
		int offset = DataUtils._INT_FIFTEEN;
		int imageHeight = startHeight + rowheight * (totalrow + 2);
		int colwidth = ((imageWidth - 20) / totalcol);

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, imageWidth, imageHeight);
		graphics.setColor(new Color(220, 240, 240));
		//画横线
		for (int j = 0; j < totalrow + 1; j++) {
			graphics.setColor(Color.black);
			graphics.drawLine(startWidth, startHeight + (j + 1) * rowheight, startWidth + totalcol*colwidth, startHeight + (j + 1) * rowheight);
		}
		//画竖线
		for (int k = 0; k < totalcol + 1; k++) {
			graphics.setColor(Color.black);
			graphics.drawLine(startWidth + k * colwidth, startHeight + rowheight, startWidth + k * colwidth, startHeight + rowheight*(totalrow+1));
		}
		//设置字体
		Font font = new Font("华文楷体", Font.BOLD, 22);
		graphics.setFont(font);
		//写标题
		graphics.drawString(title, imageWidth/2 - titleWidth/2, startHeight+rowheight-offset);
		font = new Font("华文楷体", Font.BOLD, 18);
		graphics.setFont(font);

		//写入表头
		for (int m = 0; m < headCells.length; m++) {
			graphics.drawString(headCells[m].toString(), startWidth + colwidth * m + 5, startHeight + rowheight * 2 - offset);
		}

		//设置字体
		font = new Font("华文楷体", Font.PLAIN, 16);
		graphics.setFont(font);

		//写入内容
		for (int n=0; n<cellList.size(); n++) {
			String[] cellValue = cellList.get(n);
			for (int i=0; i<cellValue.length; i++) {
				graphics.drawString(cellValue[i].toString(), startWidth+colwidth*i+5, startHeight+rowheight*(n+3) - offset);
			}
		}

		font = new Font("华文楷体", Font.BOLD, 18);
		graphics.setFont(font);
		graphics.setColor(Color.blue);
		//写备注
		String remark = "备注：" + notes;
		graphics.drawString(remark, startWidth, imageHeight - offset);
		createImage(filePathName, image);
	}

	private static void createImage(String fileLocation, BufferedImage image) {
		try {
			FileOutputStream fos = new FileOutputStream(fileLocation);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			encoder.encode(image);
			bos.close();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	  * 给图片不同位置添加多个图片水印，可设置水印图片旋转角度
	  * @param icon 水印图片路径(如：F:/images/icon.png)
	  * @param source 没有加水印的图片路径(如：F:/images/source.jpg)
	  * @param output 加水印后的图片路径(如：F:/images/)
	  * @param imageName 图片名称(如：imageName)
	  * @param imageType 图片类型(如：jpg)
	  * @param degree 水印图片旋转角度，为null表示不旋转
	  */
	private static String markImageByMoreIcon(String icon, String source, String output, String imageName, String imageType, Integer degree) {

		String result = "添加图片水印出错";
		try {
			File file = new File(source);
			File ficon = new File(icon);
			if (!file.isFile()) {
				return source + " 不是一个图片文件！";
			}
			//将icon加载到内存中
			Image ic = ImageIO.read(ficon);
			//icon高度
			int icheight = ic.getHeight(null);
			//将源图片读到内存中
			Image img = ImageIO.read(file);
			//图片宽
			int width = img.getWidth(null);
			//图片高
			int height = img.getHeight(null);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//创建一个指定BufferedImage的Graphics2D对象
			Graphics2D g = bi.createGraphics();
			//x,y轴默认是从0坐标开始
			int x = 0;
			int y = 0;
			//默认两张水印图片的间隔高度是水印图片的1/3
			int temp = icheight / 3;
			int space = 1;
			if (height >= 2) {
				space = height / icheight;
				if (space >= 2) {
					temp = y = icheight / 2;
					if (space == 1 || space == 0) {
						x = 0;
						y = 0;
					}
				}
			} else {
				x = 0;
				y = 0;
			}
			//设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			//呈现一个图像，在绘制前进行从图像空间到用户空间的转换
			g.drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			for (int i = 0; i < space; i++) {
				if (null != degree) {
					//设置水印旋转
					g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
				}
				//水印图像的路径，水印一般为gif或者png的，这样可设置透明度
				ImageIcon imgIcon = new ImageIcon(icon);
				//得到Image对象
				Image con = imgIcon.getImage();
				//透明度，最小值为0，最大值为1
				float clarity = 0.6f;
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, clarity));
				//表示水印图片的坐标位置(x,y)
				//g.drawImage(con, 300, 220, null);
				g.drawImage(con, x, y, null);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
				y += (icheight + temp);
			}
			g.dispose();
			File sf = new File(output, imageName + "." + imageType);
			ImageIO.write(bi, imageType, sf); //保存图片
			result = "图片完成添加Icon水印";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	  * 给图片添加单个图片水印，可设置水印图片旋转角度
	  * @param icon 水印图片路径(如：F:/images/icon.png)
	  * @param source 没有加水印的图片路径(如：F:/images/source.jpg)
	  * @param output 加水印后的图片路径(如：F:/images/)
	  * @param imageName 图片名称(如：imageName)
	  * @param degree 水印图片旋转角度，null表示不旋转
	  */
	private static String markImageBySingleIcon(String icon, String source, String output, String imageName, String imageType, Integer degree) {

		String result = "添加图片水印出错";
		try {
			File file = new File(source);
			File ficon = new File(icon);
			if (!file.isFile()) {
				return source + " 不是一个图片文件！";
			}
			//将icon加载到内存中
			Image ic = ImageIO.read(ficon);
			//icon高度
			int icheight = ic.getHeight(null);
			//将源图片读到内存中
			Image img = ImageIO.read(file);
			//图片宽
			int width = img.getWidth(null);
			//图片高
			int height = img.getHeight(null);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//创建一个指定BufferedImage的Graphics2D对象
			Graphics2D g = bi.createGraphics();
			//x,y轴默认是从0坐标开始
			int x = 0;
			int y = (height / 2) - (icheight / 2);
			//设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			//呈现一个图像，在绘制前从图像空间到用户空间的转换
			g.drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			if (null != degree) {
				//设置水印旋转
				g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
			}
			//水印图像的路径，水印一般为gif或者png的，这样可设置透明度
			ImageIcon imgIcon = new ImageIcon(icon);
			//得到Image对象
			Image con = imgIcon.getImage();
			//透明度，最小值为0，最大值为1
			float clarity = 0.6f;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, clarity));
			//表示水印图片的坐标位置(x,y)
			g.drawImage(con, x, y, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g.dispose();
			File sf = new File(output, imageName + "." + imageType);
			//保存图片
			ImageIO.write(bi, imageType, sf);
			result = "图片完成添加Icon水印";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	  * 给图片添加多个文字水印，可设置水印文字旋转角度
	  * @param source 需要添加水印的图片路径(如：F:/images/source.jpg)
	  * @param outPut 添加水印后图片输出路径(如：F:/images/)
	  * @param imageName 图片名称(如：imageName)
	  * @param imageType 图片类型(如：jpg)
	  * @param word 水印文字
	  * @param degree 水印文字旋转角度，null表示不旋转
	  */
	private static void markImageByMoreText(String source, String output, String imageName, String imageType, 
			                               Color color, String word, Integer degree) throws Exception {

		//读取原图片信息
		File file = new File(source);
		if (!file.isFile()) {
			Exception exception = new IOException("文件(" + source + ")不存在！");
			throw exception;
		}
		Image img = ImageIO.read(file);
		//图片宽
		int width = img.getWidth(null);
		//图片高
		int height = img.getHeight(null);
		//文字大小
		int size = 50;
		//加水印
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img, 0, 0, width, height, null);
		//设置水印字体样式
		Font font = new Font("宋体", Font.PLAIN, size);
		//根据图片的背景设置水印颜色
		g.setColor(color);
		int x = width / 3;
		int y = size;
		int space = height / size;
		for (int i = 0; i < space; i++) {
			//如果最后一个坐标的y轴比height高，则退出
			if ((y + size) > height)
				break;
			if (null != degree) {
				//设置水印旋转
				g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
			}
			g.setFont(font);
			//水印位置
			g.drawString(word, x, y);
			y += (2 * size);
		}
		g.dispose();
		//输出图片
		File sf = new File(output, imageName + imageType);
		//保存图片
		ImageIO.write(bi, imageType.substring(1), sf);
	}

	/**
	  * 给图片添加单个文字水印，可设置水印文字旋转角度
	  * @param source 需要添加水印的图片路径(如：F:/images/source.jpg)
	  * @param outPut 添加水印后图片输出路径(如：F:/images/)
	  * @param imageName 图片名称(如：imageName)
	  * @param imageType 图片类型(如：jpg)
	  * @param color 水印文字的颜色
	  * @param word 水印文字
	  * @param degree 水印文字旋转角度，null表示不旋转
	  */
	private static void markImageBySingleText(String source, String output, String imageName, String imageType, Color color, String word,
			Integer degree) throws Exception {

		//读取原图片信息
		File file = new File(source);
		if (!file.isFile()) {
			Exception exception = new IOException("文件(" + source + ")不存在！");
			throw exception;
		}
		Image img = ImageIO.read(file);
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		//加水印
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img, 0, 0, width, height, null);
		//设置水印字体样式
		Font font = new Font("宋体", Font.PLAIN, 50);
		//根据图片的背景设置水印颜色
		g.setColor(color);
		if (null != degree) {
			//设置水印旋转
			g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
		}
		g.setFont(font);
		int x = width / 3;
		int y = height / 2;
		//水印位置
		g.drawString(word, x, y);
		g.dispose();
		//输出图片
		File sf = new File(output, imageName + imageType);
		//保存图片
		ImageIO.write(bi, imageType.substring(1), sf);
	}

	/**
		* 给图片加马赛克
		* @param source 原图片路径(如：F:/images/source.jpg)
		* @param output 打马赛克后，图片保存的路径(如：F:/images/)
		* @param imageName 图片名称(如：imageName)
		* @param imageType 图片类型(如：jpg)
		* @param size 马赛克尺寸，即每个矩形的宽高
		*/
	private static String markImageByMosaic(String source, String output, String imageName, String imageType, int size) {
		String result = "图片打马赛克出错";
		try {
			File file = new File(source);
			if (!file.isFile()) {
				return file + " 不是一个图片文件！";
			}
			//读取该图片
			BufferedImage img = ImageIO.read(file);
			//原图片宽
			int width = img.getWidth(null);
			//原图片高
			int height = img.getHeight(null);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//马赛克格尺寸太大或太小
			if (width < size || height < size) {
				return "马赛克格尺寸太大";
			}
			if (size <= 0) {
				return "马赛克格尺寸大小";
			}
			//x方向绘制个数
			int xcount = 0;
			//y方向绘制个数
			int ycount = 0;
			if (width % size == 0) {
				xcount = width / size;
			} else {
				xcount = width / size + 1;
			}
			if (height % size == 0) {
				ycount = height / size;
			} else {
				ycount = height / size + 1;
			}
			int x = 0;
			int y = 0;
			//绘制马赛克(绘制矩形并填充颜色)
			Graphics2D g = bi.createGraphics();
			for (int i = 0; i < xcount; i++) {
				for (int j = 0; j < ycount; j++) {
					//马赛克矩形格大小
					int mwidth = size;
					int mheight = size;
					//横向最后一个不够一个size
					if (i == xcount - 1) {
						mwidth = width - x;
					}
					//纵向最后一个不够一个size
					if (j == ycount - 1) {
						mheight = height - y;
					}
					//矩形颜色取中心像素点RGB值
					int centerX = x;
					int centerY = y;
					if (mwidth % 2 == 0) {
						centerX += mwidth / 2;
					} else {
						centerX += (mwidth - 1) / 2;
					}
					if (mheight % 2 == 0) {
						centerY += mheight / 2;
					} else {
						centerY += (mheight - 1) / 2;
					}
					Color color = new Color(img.getRGB(centerX, centerY));
					g.setColor(color);
					g.fillRect(x, y, mwidth, mheight);
					//计算下一个矩形的y坐标
					y += size;
				}
				//还原y坐标
				y = 0;
				//计算x坐标
				x += size;
			}
			g.dispose();
			File sf = new File(output, imageName + "." + imageType);
			//保存图片
			ImageIO.write(bi, imageType, sf);
			result = "打马赛克成功";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
