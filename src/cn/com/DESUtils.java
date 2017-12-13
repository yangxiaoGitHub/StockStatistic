package cn.com;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils {

	/**
	 * ����
	 */
	public static byte[] encrypt(byte[] datasource, String password) throws Exception {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// ����һ���ܳ׹�����Ȼ��������DESKeySpecת����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher����ʵ����ɼ��ܲ���
		Cipher cipher = Cipher.getInstance("DES");
		// ���ܳ׳�ʼ��Cipher����
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		// ���ڣ���ȡ���ݲ�����
		// ��ʽִ�м��ܲ���
		return cipher.doFinal(datasource);
	}
	
	/**
	 * ����
	 */
	public static byte[] decrypt(byte[] src, String password) throws Exception {
		// DES�㷨Ҫ����һ�������ε������Դ
		SecureRandom random = new SecureRandom();
		// ����һ��DESKeySpec����
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// ����һ���ܳ׹���
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// ��DESKeySpec����ת����SecretKey����
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher����ʵ����ɽ��ܲ���
		Cipher cipher = Cipher.getInstance("DES");
		// ���ܳ׳�ʼ��Cipher����
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// ������ʼ���ܲ���
		return cipher.doFinal(src);
	}
	
	/**
	 * �����ݼ��ܳ�ʮ�������ַ���
	 */
	public static String encryptToHex(String data) {
		String bytesToHex = null;
		try {
			String password = PropertiesUtils.getProperty("encryptPassword");
			byte[] encryptData = encrypt(data.getBytes(), password);
			// ���ܺ��byte����ת����ʮ������
			bytesToHex = CommonUtils.bytesToHexString(encryptData);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bytesToHex;
	}
	
	/**
	 * �Ѽ��ܵ�ʮ�����ƽ��ܳ�ԭ����
	 */
	public static String decryptHex(String data) {
		String decryptData = null;
		try {
			String password = PropertiesUtils.getProperty("encryptPassword");
			// ʮ������ת����byte����
			byte[] hexToBytes = CommonUtils.hexStringToBytes(data);
			byte[] decryResult = decrypt(hexToBytes, password);
			decryptData = new String(decryResult);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return decryptData;
	}
}
