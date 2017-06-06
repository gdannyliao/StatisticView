package com.ggdsn.jkl.statisticview;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiaoXingyu on 16/5/26.
 */
public class Utils {
	private static final char[] DIGITS = "0123456789ABCDEF".toCharArray();
	private static final String GB2312 = "gb2312";
	private static final String GB18030 = "gb18030";
	public static final int GB18030_ONE_BYTE = 0x80;
	public static final int GB18030_TWO_BYTES = 0x40;

	public static final String DINNER_SIGN = "■";
	public static final Map<String, OLEDCustomChar> dinnerHintCustomCodeMap = new HashMap<String, OLEDCustomChar>() {
		{
			// 取餐
			put("缺", new OLEDCustomChar("", 0xC8, 0xB1, 0xA4, 0x41));
			put("寻", new OLEDCustomChar("", 0xD1, 0xB0, 0xA4, 0x40));
			put("另", new OLEDCustomChar("", 0xC1, 0xED, 0xA4, 0x42));

			// 放餐
			put(DINNER_SIGN, new OLEDCustomChar("", 0xA1, 0xF6, 0xA3, 0x40));
			put("错", new OLEDCustomChar("", 0xB4, 0xED, 0xA3, 0x43));
			put("抖", new OLEDCustomChar("", 0xB6, 0xB6, 0xA3, 0x41));
			put("少", new OLEDCustomChar("", 0xC9, 0xD9, 0xA3, 0x42));

			// 通用
			put("…", new OLEDCustomChar("", 0xA1, 0xAD, 0xA1, 0x80));

		}
	};
	/**
	 * 将byte视为无符号, 转换成正整数.
	 *
	 * @param b 有符号byte
	 * @return 正整数.
	 */
	public static int uByte2int(byte b) {
		return 0xFF & b;
	}

	/**
	 * index增长的方向，为高位方向，但每个字节内部，依然是大端序
	 * 相当于：<br/>
	 * raw[0]   raw[1]   ... raw[8] <br/>
	 * 7-0      15-8     ... 63-56 bit
	 */
	public static long byteArray2long(byte[] raw) {
		if (raw == null) {
			throw new IllegalArgumentException("raw byte[] is null");
		}

		if (raw.length > 8) {
			throw new IllegalArgumentException("raw byte[] is too long");
		}

		long value = 0;
		for (int i = raw.length - 1; i >= 0; i--) {
			value = (value << 8) + (raw[i] & 0xff);
		}
		return value;
	}

	public static byte[] getIntBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		return bytes;
	}

	public static boolean compareArray(byte[] needCheckSum, byte[] currentCheckSum) {
		if (needCheckSum.length != currentCheckSum.length) {
			return false;
		}
		int length = needCheckSum.length;
		for (int i = 0; i < length; i++) {
			if (needCheckSum[i] != currentCheckSum[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * byte[]按十六进制转字符串,指定长度
	 *
	 * @param byteArray 待转换的byte[]
	 * @return 16进制的String
	 */
	public static String bytesToHexString(byte[] byteArray, int byteRead) {
		if (byteArray == null || byteRead > byteArray.length || byteRead <= 0) {
			return null;
		}
		char[] buf = new char[byteRead * 2];
		for (int i = 0; i < byteRead; i++) {
			buf[i * 2] = DIGITS[(byteArray[i] >> 4) & 0xf];
			buf[i * 2 + 1] = DIGITS[byteArray[i] & 0xf];
		}
		return new String(buf);
	}

	/**
	 * byte[]按十六进制转字符串
	 *
	 * @param byteArray 待转换的byte[]
	 * @return 16进制的String
	 */
	public static String bytesToHexString(byte[] byteArray) {
		if (byteArray == null) {
			return null;
		}
		return bytesToHexString(byteArray, byteArray.length);
	}

	/**
	 * byte[]按十六进制转字符串,并且按照指定的长度和分隔符分隔
	 *
	 * @param byteArray 待转换的byte[]
	 * @param split 分隔长度
	 * @param splitStr 用来分隔的符号
	 * @return 16进制的字符串
	 */
	public static String bytesToHexString(byte[] byteArray, int split, String splitStr) {
		if (split >= byteArray.length || split <= 0) {
			throw new IllegalArgumentException("split out of range");
		}
		char[] splitCharArray = splitStr.toCharArray();
		int num = (int) Math.ceil((double) byteArray.length / split) - 1;
		// 按十六进制转字符串，长度增加了一倍
		char[] buf = new char[byteArray.length * 2 + num * splitCharArray.length];
		int j = 0;// 记录插入的总字符数,便于计算插入字符的位置
		int index;
		split *= 2;// 总长增加了一倍，所以分隔长度也要乘以2
		for (int i = 0; i < byteArray.length; i++) {
			index = i * 2;
			buf[index + j] = DIGITS[(byteArray[i] >> 4) & 0xf];// 得到高4位的16进制表示
			buf[index + j + 1] = DIGITS[byteArray[i] & 0xf];// 得到低4位的16进制表示
			if ((index + 2) % split == 0 && (index + 2 + j) < buf.length) {
				// 如果下一个数的position整出了分隔长度,说明要开始插入字符
				for (char aChar : splitCharArray) {
					buf[index + 2 + j] = aChar;
					j++;
				}
			}
		}
		return new String(buf);
	}

	public static String byteToHex(int aByte) {
		return byteToHex((byte) aByte);
	}
	public static String byteToHex(byte aByte) {
		char[] buf = new char[2];
		buf[0] = DIGITS[(aByte >> 4) & 0xf];// 得到高4位的16进制表示
		buf[1] = DIGITS[aByte & 0xf];// 得到低4位的16进制表示
		return new String(buf);
	}

	/**
	 * 将字符串转换成gb2312编码格式的字节数组
	 *
	 * @param string 待转换的string
	 * @return GB2312编码
	 */
	public static byte[] encodeWithGB2312(String string) {
		if (string == null) {
			return null;
		}
		return encode(string, GB2312);
	}

	/**
	 * 将字符串转换成gb2312编码格式的字节数组, 有最大长度限制
	 *
	 * @param maxLength 字节数组的最大长度限制
	 * @param string 待转换的string
	 * @return GB2312编码
	 */
	public static byte[] encodeWithGB2312(int maxLength, String string) {
		if (string == null || maxLength <= 0) {
			return null;
		}
		byte[] bytes = encode(string, GB2312);
		if (maxLength < bytes.length) {
			throw new IllegalArgumentException("string " + string + " is too long");
		}
		return bytes;
	}

	/**
	 * 将字符串转换成gb18030编码格式的字节数组
	 *
	 * @param string 待转换的string
	 * @return GB18030编码
	 */
	public static byte[] encodeWithGB18030(String string) {
		if (string == null) {
			return null;
		}
		return encode(string, GB18030);
	}

	/**
	 * 将字符串转换成协议格式的字节数组
	 *
	 * @param string 待转换的string
	 * @return 协议格式的编码
	 */
	public static byte[] encodeWithMcu(String string) {
		byte[] bytes =encodeWithGB18030(string);
		replaceCustomCode(bytes);
		return bytes;
	}

	private static void replaceCustomCode(byte[] bytes) {
		int length = bytes.length;
		Collection<OLEDCustomChar> map = dinnerHintCustomCodeMap.values();
		for (int i = 0; i < length; i++) {
			// 1字节字符
			if (Utils.uByte2int(bytes[i]) < GB18030_ONE_BYTE) {
				continue;
			}

			// 4字节字符
			if (Utils.uByte2int(bytes[i + 1]) < GB18030_TWO_BYTES) {
				i += 3;
				continue;
			}

			// 2字节字符
			byte low = bytes[i];
			byte high = bytes[i + 1];

			for (OLEDCustomChar customChar : map) {
				if (low == customChar.gb2312Low && high == customChar.gb2312High) {
					bytes[i] = customChar.customLow;
					bytes[i + 1] = customChar.customHigh;
					break;
				}
			}
			i++;
		}
	}
	/**
	 * 将字符串转换成指定编码格式的字节数组
	 *
	 * @param string 待转换string
	 * @param encoding 编码格式
	 * @return 指定编码的字节数组
	 */
	public static byte[] encode( String string, String encoding) {
		try {
			return string.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(encoding + " missing");
		}
	}

	public static byte[] checksum(byte[] array, int offset, int length) {
		return getIntBytes(crc(array, offset, length));
	}

	/**
	 * 外包Scanner校验和, 各字节取异或。
	 */
	public static byte checksumScanner(byte[] buffer, int start, int length) {
		int sum = buffer[start];
		for (int i = start + 1; i < start + length; i++) {
			sum ^= buffer[i];
		}
		return (byte) sum;
	}
	/**
	 * 何裕德的CRC函数
	 */
	private static int crc(byte[] buffer, int offset, int len) {
		int xBit;
		int data;
		int crc = 0xFFFFFFFF;    // init
		int dwPolynomial = 0x04c11db7;//X^32 + X^26 + X^23 + X^22 + X^16 + X^12 + X^11 + X^10 +X^8 + X^7 + X^5 + X^4 + X^2+ X^ +1
		while (len-- > 0) {
			xBit = 1 << 31;
			// 这里赋值不能忘了转型
			data = Utils.uByte2int(buffer[offset++]);
			for (int bits = 0; bits < 32; bits++) {
				if ((crc & 0x80000000) != 0) {
					crc <<= 1;
					crc ^= dwPolynomial;
				} else {
					crc <<= 1;
				}
				if ((data & xBit) != 0) {
					crc ^= dwPolynomial;
				}
				// 这里移位要用>>>，不能用>>
				xBit >>>= 1;
			}
		}
		return crc;
	}
}
