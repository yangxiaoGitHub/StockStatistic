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
			String title = stockDate + " ѡ��Ĺ�Ʊ";
			String[] headCells = { "���", "��Ʊ����", "��Ʊ����", "�ǵ���", "������" };
			String filePath = PropertiesUtils.getProperty("picturePath");
			String filePathName = filePath + stockDate + FILE_SUFFIX;
			Date nowDate = new Date();
			String notes = "����ͼƬ��ʱ�� " + DateUtils.dateTimeMsecToString(nowDate);
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

	//����һ�����
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
		//������
		for (int j = 0; j < totalrow + 1; j++) {
			graphics.setColor(Color.black);
			graphics.drawLine(startWidth, startHeight + (j + 1) * rowheight, startWidth + totalcol*colwidth, startHeight + (j + 1) * rowheight);
		}
		//������
		for (int k = 0; k < totalcol + 1; k++) {
			graphics.setColor(Color.black);
			graphics.drawLine(startWidth + k * colwidth, startHeight + rowheight, startWidth + k * colwidth, startHeight + rowheight*(totalrow+1));
		}
		//��������
		Font font = new Font("���Ŀ���", Font.BOLD, 22);
		graphics.setFont(font);
		//д����
		graphics.drawString(title, imageWidth/2 - titleWidth/2, startHeight+rowheight-offset);
		font = new Font("���Ŀ���", Font.BOLD, 18);
		graphics.setFont(font);

		//д���ͷ
		for (int m = 0; m < headCells.length; m++) {
			graphics.drawString(headCells[m].toString(), startWidth + colwidth * m + 5, startHeight + rowheight * 2 - offset);
		}

		//��������
		font = new Font("���Ŀ���", Font.PLAIN, 16);
		graphics.setFont(font);

		//д������
		for (int n=0; n<cellList.size(); n++) {
			String[] cellValue = cellList.get(n);
			for (int i=0; i<cellValue.length; i++) {
				graphics.drawString(cellValue[i].toString(), startWidth+colwidth*i+5, startHeight+rowheight*(n+3) - offset);
			}
		}

		font = new Font("���Ŀ���", Font.BOLD, 18);
		graphics.setFont(font);
		graphics.setColor(Color.blue);
		//д��ע
		String remark = "��ע��" + notes;
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
	  * ��ͼƬ��ͬλ����Ӷ��ͼƬˮӡ��������ˮӡͼƬ��ת�Ƕ�
	  * @param icon ˮӡͼƬ·��(�磺F:/images/icon.png)
	  * @param source û�м�ˮӡ��ͼƬ·��(�磺F:/images/source.jpg)
	  * @param output ��ˮӡ���ͼƬ·��(�磺F:/images/)
	  * @param imageName ͼƬ����(�磺imageName)
	  * @param imageType ͼƬ����(�磺jpg)
	  * @param degree ˮӡͼƬ��ת�Ƕȣ�Ϊnull��ʾ����ת
	  */
	private static String markImageByMoreIcon(String icon, String source, String output, String imageName, String imageType, Integer degree) {

		String result = "���ͼƬˮӡ����";
		try {
			File file = new File(source);
			File ficon = new File(icon);
			if (!file.isFile()) {
				return source + " ����һ��ͼƬ�ļ���";
			}
			//��icon���ص��ڴ���
			Image ic = ImageIO.read(ficon);
			//icon�߶�
			int icheight = ic.getHeight(null);
			//��ԴͼƬ�����ڴ���
			Image img = ImageIO.read(file);
			//ͼƬ��
			int width = img.getWidth(null);
			//ͼƬ��
			int height = img.getHeight(null);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//����һ��ָ��BufferedImage��Graphics2D����
			Graphics2D g = bi.createGraphics();
			//x,y��Ĭ���Ǵ�0���꿪ʼ
			int x = 0;
			int y = 0;
			//Ĭ������ˮӡͼƬ�ļ���߶���ˮӡͼƬ��1/3
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
			//���ö��߶εľ��״��Ե����
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			//����һ��ͼ���ڻ���ǰ���д�ͼ��ռ䵽�û��ռ��ת��
			g.drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			for (int i = 0; i < space; i++) {
				if (null != degree) {
					//����ˮӡ��ת
					g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
				}
				//ˮӡͼ���·����ˮӡһ��Ϊgif����png�ģ�����������͸����
				ImageIcon imgIcon = new ImageIcon(icon);
				//�õ�Image����
				Image con = imgIcon.getImage();
				//͸���ȣ���СֵΪ0�����ֵΪ1
				float clarity = 0.6f;
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, clarity));
				//��ʾˮӡͼƬ������λ��(x,y)
				//g.drawImage(con, 300, 220, null);
				g.drawImage(con, x, y, null);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
				y += (icheight + temp);
			}
			g.dispose();
			File sf = new File(output, imageName + "." + imageType);
			ImageIO.write(bi, imageType, sf); //����ͼƬ
			result = "ͼƬ������Iconˮӡ";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	  * ��ͼƬ��ӵ���ͼƬˮӡ��������ˮӡͼƬ��ת�Ƕ�
	  * @param icon ˮӡͼƬ·��(�磺F:/images/icon.png)
	  * @param source û�м�ˮӡ��ͼƬ·��(�磺F:/images/source.jpg)
	  * @param output ��ˮӡ���ͼƬ·��(�磺F:/images/)
	  * @param imageName ͼƬ����(�磺imageName)
	  * @param degree ˮӡͼƬ��ת�Ƕȣ�null��ʾ����ת
	  */
	private static String markImageBySingleIcon(String icon, String source, String output, String imageName, String imageType, Integer degree) {

		String result = "���ͼƬˮӡ����";
		try {
			File file = new File(source);
			File ficon = new File(icon);
			if (!file.isFile()) {
				return source + " ����һ��ͼƬ�ļ���";
			}
			//��icon���ص��ڴ���
			Image ic = ImageIO.read(ficon);
			//icon�߶�
			int icheight = ic.getHeight(null);
			//��ԴͼƬ�����ڴ���
			Image img = ImageIO.read(file);
			//ͼƬ��
			int width = img.getWidth(null);
			//ͼƬ��
			int height = img.getHeight(null);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//����һ��ָ��BufferedImage��Graphics2D����
			Graphics2D g = bi.createGraphics();
			//x,y��Ĭ���Ǵ�0���꿪ʼ
			int x = 0;
			int y = (height / 2) - (icheight / 2);
			//���ö��߶εľ��״��Ե����
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			//����һ��ͼ���ڻ���ǰ��ͼ��ռ䵽�û��ռ��ת��
			g.drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			if (null != degree) {
				//����ˮӡ��ת
				g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
			}
			//ˮӡͼ���·����ˮӡһ��Ϊgif����png�ģ�����������͸����
			ImageIcon imgIcon = new ImageIcon(icon);
			//�õ�Image����
			Image con = imgIcon.getImage();
			//͸���ȣ���СֵΪ0�����ֵΪ1
			float clarity = 0.6f;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, clarity));
			//��ʾˮӡͼƬ������λ��(x,y)
			g.drawImage(con, x, y, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g.dispose();
			File sf = new File(output, imageName + "." + imageType);
			//����ͼƬ
			ImageIO.write(bi, imageType, sf);
			result = "ͼƬ������Iconˮӡ";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	  * ��ͼƬ��Ӷ������ˮӡ��������ˮӡ������ת�Ƕ�
	  * @param source ��Ҫ���ˮӡ��ͼƬ·��(�磺F:/images/source.jpg)
	  * @param outPut ���ˮӡ��ͼƬ���·��(�磺F:/images/)
	  * @param imageName ͼƬ����(�磺imageName)
	  * @param imageType ͼƬ����(�磺jpg)
	  * @param word ˮӡ����
	  * @param degree ˮӡ������ת�Ƕȣ�null��ʾ����ת
	  */
	private static void markImageByMoreText(String source, String output, String imageName, String imageType, 
			                               Color color, String word, Integer degree) throws Exception {

		//��ȡԭͼƬ��Ϣ
		File file = new File(source);
		if (!file.isFile()) {
			Exception exception = new IOException("�ļ�(" + source + ")�����ڣ�");
			throw exception;
		}
		Image img = ImageIO.read(file);
		//ͼƬ��
		int width = img.getWidth(null);
		//ͼƬ��
		int height = img.getHeight(null);
		//���ִ�С
		int size = 50;
		//��ˮӡ
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img, 0, 0, width, height, null);
		//����ˮӡ������ʽ
		Font font = new Font("����", Font.PLAIN, size);
		//����ͼƬ�ı�������ˮӡ��ɫ
		g.setColor(color);
		int x = width / 3;
		int y = size;
		int space = height / size;
		for (int i = 0; i < space; i++) {
			//������һ�������y���height�ߣ����˳�
			if ((y + size) > height)
				break;
			if (null != degree) {
				//����ˮӡ��ת
				g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
			}
			g.setFont(font);
			//ˮӡλ��
			g.drawString(word, x, y);
			y += (2 * size);
		}
		g.dispose();
		//���ͼƬ
		File sf = new File(output, imageName + imageType);
		//����ͼƬ
		ImageIO.write(bi, imageType.substring(1), sf);
	}

	/**
	  * ��ͼƬ��ӵ�������ˮӡ��������ˮӡ������ת�Ƕ�
	  * @param source ��Ҫ���ˮӡ��ͼƬ·��(�磺F:/images/source.jpg)
	  * @param outPut ���ˮӡ��ͼƬ���·��(�磺F:/images/)
	  * @param imageName ͼƬ����(�磺imageName)
	  * @param imageType ͼƬ����(�磺jpg)
	  * @param color ˮӡ���ֵ���ɫ
	  * @param word ˮӡ����
	  * @param degree ˮӡ������ת�Ƕȣ�null��ʾ����ת
	  */
	private static void markImageBySingleText(String source, String output, String imageName, String imageType, Color color, String word,
			Integer degree) throws Exception {

		//��ȡԭͼƬ��Ϣ
		File file = new File(source);
		if (!file.isFile()) {
			Exception exception = new IOException("�ļ�(" + source + ")�����ڣ�");
			throw exception;
		}
		Image img = ImageIO.read(file);
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		//��ˮӡ
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img, 0, 0, width, height, null);
		//����ˮӡ������ʽ
		Font font = new Font("����", Font.PLAIN, 50);
		//����ͼƬ�ı�������ˮӡ��ɫ
		g.setColor(color);
		if (null != degree) {
			//����ˮӡ��ת
			g.rotate(Math.toRadians(degree), (double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
		}
		g.setFont(font);
		int x = width / 3;
		int y = height / 2;
		//ˮӡλ��
		g.drawString(word, x, y);
		g.dispose();
		//���ͼƬ
		File sf = new File(output, imageName + imageType);
		//����ͼƬ
		ImageIO.write(bi, imageType.substring(1), sf);
	}

	/**
		* ��ͼƬ��������
		* @param source ԭͼƬ·��(�磺F:/images/source.jpg)
		* @param output �������˺�ͼƬ�����·��(�磺F:/images/)
		* @param imageName ͼƬ����(�磺imageName)
		* @param imageType ͼƬ����(�磺jpg)
		* @param size �����˳ߴ磬��ÿ�����εĿ��
		*/
	private static String markImageByMosaic(String source, String output, String imageName, String imageType, int size) {
		String result = "ͼƬ�������˳���";
		try {
			File file = new File(source);
			if (!file.isFile()) {
				return file + " ����һ��ͼƬ�ļ���";
			}
			//��ȡ��ͼƬ
			BufferedImage img = ImageIO.read(file);
			//ԭͼƬ��
			int width = img.getWidth(null);
			//ԭͼƬ��
			int height = img.getHeight(null);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//�����˸�ߴ�̫���̫С
			if (width < size || height < size) {
				return "�����˸�ߴ�̫��";
			}
			if (size <= 0) {
				return "�����˸�ߴ��С";
			}
			//x������Ƹ���
			int xcount = 0;
			//y������Ƹ���
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
			//����������(���ƾ��β������ɫ)
			Graphics2D g = bi.createGraphics();
			for (int i = 0; i < xcount; i++) {
				for (int j = 0; j < ycount; j++) {
					//�����˾��θ��С
					int mwidth = size;
					int mheight = size;
					//�������һ������һ��size
					if (i == xcount - 1) {
						mwidth = width - x;
					}
					//�������һ������һ��size
					if (j == ycount - 1) {
						mheight = height - y;
					}
					//������ɫȡ�������ص�RGBֵ
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
					//������һ�����ε�y����
					y += size;
				}
				//��ԭy����
				y = 0;
				//����x����
				x += size;
			}
			g.dispose();
			File sf = new File(output, imageName + "." + imageType);
			//����ͼƬ
			ImageIO.write(bi, imageType, sf);
			result = "�������˳ɹ�";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
