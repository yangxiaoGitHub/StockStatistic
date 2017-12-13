package cn.com;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils {

	/**
	 * 加密
	 */
	public static byte[] encrypt(byte[] datasource, String password) throws Exception {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(datasource);
	}
	
	/**
	 * 解密
	 */
	public static byte[] decrypt(byte[] src, String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}
	
	/**
	 * 把数据加密成十六进制字符串
	 */
	public static String encryptToHex(String data) {
		String bytesToHex = null;
		try {
			String password = PropertiesUtils.getProperty("encryptPassword");
			byte[] encryptData = encrypt(data.getBytes(), password);
			// 加密后的byte数组转换成十六进制
			bytesToHex = CommonUtils.bytesToHexString(encryptData);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bytesToHex;
	}
	
	/**
	 * 把加密的十六进制解密成原数据
	 */
	public static String decryptHex(String data) {
		String decryptData = null;
		try {
			String password = PropertiesUtils.getProperty("encryptPassword");
			// 十六进制转化成byte数组
			byte[] hexToBytes = CommonUtils.hexStringToBytes(data);
			byte[] decryResult = decrypt(hexToBytes, password);
			decryptData = new String(decryResult);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return decryptData;
	}
}
