package com.zhijieyun.dynportal.common.util;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 字符串处理工具类.
 */
public class StringUtils {
	public static final String EMPTY = "";

	private static final int INDEX_NOT_FOUND = -1;

	/**
	 * 定义私有构造函数,使其不可实例化和子类化
	 * 
	 */
	private StringUtils() {

	}

	/**
	 * 替换空字符
	 * 
	 * @param src
	 * @return
	 */
	public static String replaceEmpty(String src) {
		if (null == src || "".equals(src))
			return "";
		else
			return src.trim();
	}

	/**
	 * 中文编码
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static String encode(String src) throws Exception {
		if (null == src || src.equals(""))
			return "";
		else
			return new String(src.getBytes("ISO8859-1"), "GBK");
	}

	/**
	 * 字符串是否为空
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0 || value.trim().equals("");
	}

	/**
	 * 不为空
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	/**
	 * 判断字符串是否为空或空白对象
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isBlank(String value) {
		int strLen = 0;
		if (null == value || (strLen = value.length()) == 0) {
			return true;
		}

		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(value.charAt(i)) == false) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 去除空格
	 * 
	 * @param value
	 * @return null / string
	 */
	public static String trim(String value) {
		return (null == value) ? null : value.trim();
	}

	@SuppressWarnings("unchecked")
	public static String[] listToStrArr(List strList) {
		String[] strArr = new String[strList.size()];
		for (int i = 0; i < strList.size(); i++) {
			strArr[i] = strList.get(i).toString();
		}
		return strArr;
	}

	/**
	 * 为空或空白时 返回 null 反之 string.trim()
	 * 
	 * @param value
	 * @return null / string
	 */
	public static String trimToNull(String value) {
		return isEmpty(trim(value)) ? null : value.trim();
	}

	/**
	 * 为空或空白时 返回 "" 反之 string.trim()
	 * 
	 * @param value
	 * @return
	 */
	public static String trimToEmpty(String value) {
		return isEmpty(trim(value)) ? EMPTY : value.trim();
	}

	/**
	 * 去除左边空白
	 * 
	 * @param value
	 * @return
	 * 
	 *         <pre>
	 * StringUtils.removeLeftBlank(&quot;  \n\thahahaha&quot;)  = &quot;hahahaha&quot;
	 * StringUtils.removeLeftBlank(&quot;  \n\t&quot;)          = &quot;&quot;
	 * </pre>
	 */
	public static String removeLeftBlank(String value) {
		int strLen = 0;

		int start = 0;

		if (null == value || (strLen = value.length()) == 0) {
			return value;
		}

		while ((start < strLen)
				&& (Character.isWhitespace(value.charAt(start)))) {
			start++;
		}

		return value.substring(start);
	}

	/**
	 * 去除右边空白
	 * 
	 * @param value
	 * @return
	 */
	public static String removeRightBlank(String value) {
		int strLen = 0;

		if (null == value || (strLen = value.length()) == 0) {
			return value;
		}

		while ((strLen > 0)
				&& (Character.isWhitespace(value.charAt(strLen - 1)))) {
			strLen--;
		}

		return value.substring(0, strLen);
	}

	/**
	 * 查找字符
	 * 
	 * @param value
	 * @param searchChar
	 * @return
	 */
	public static int indexOf(String value, char searchChar) {
		if (isEmpty(value))
			return INDEX_NOT_FOUND;

		return value.indexOf(searchChar);
	}

	/**
	 * 查找字符
	 * 
	 * @param value
	 * @param searchChar
	 * @param startPos
	 *            开始下标
	 * @return
	 */
	public static int indexOf(String value, char searchChar, int startPos) {
		if (isEmpty(value))
			return INDEX_NOT_FOUND;

		return value.indexOf(searchChar, startPos);
	}

	/**
	 * 查找字符串
	 * 
	 * @param value
	 * @param searchString
	 * @return
	 */
	public static int indexOf(String value, String searchString) {
		if (null == value || null == searchString)
			return INDEX_NOT_FOUND;

		return value.indexOf(searchString);
	}

	/**
	 * 查找字符串
	 * 
	 * @param value
	 * @param searchString
	 * @param startPos
	 *            开始下标
	 * @return
	 */
	public static int indexOf(String value, String searchString, int startPos) {
		if (null == value || null == searchString || startPos < 0)
			return INDEX_NOT_FOUND;

		return value.indexOf(searchString, startPos);
	}

	/**
	 * 查询指定字符出现次数的下标 从0开始
	 * 
	 * @param value
	 * @param searchChar
	 * @param orderIndex
	 *            出现次数
	 * @return
	 */
	public static int orderIndexOf(String value, char searchChar, int orderIndex) {
		if (null == value || orderIndex < 0) {
			return INDEX_NOT_FOUND;
		}

		int index = INDEX_NOT_FOUND;

		int found = 0;
		do {
			index = value.indexOf(searchChar, index + 1);
			if (index < 0)
				return index;

			found++;

		} while (found < orderIndex);

		return index;
	}

	public static int orderIndexOf(String value, String searchString,
			int orderIndex) {
		if (null == value || null == searchString || orderIndex < 0) {
			return INDEX_NOT_FOUND;
		}

		int index = INDEX_NOT_FOUND;

		int found = 0;
		do {
			index = value.indexOf(searchString, index + 1);
			if (index < 0)
				return index;

			found++;

		} while (found < orderIndex);

		return index;
	}

	/**
	 * 字符串是否包含制定字符
	 * 
	 * @param value
	 * @param containChar
	 * @return
	 */
	public static boolean contains(String value, char containChar) {
		if (isEmpty(value))
			return false;

		return value.indexOf(containChar) >= 0 ? true : false;
	}

	/**
	 * 字符串是否包含指定子字符串--区分大小写
	 * 
	 * @param value
	 * @param containString
	 * @return
	 */
	public static boolean contains(String value, String containString) {
		return StringUtils.indexOf(value, containString) >= 0 ? false : true;
	}

	/**
	 * 字符串是否包含指定子字符串--不区分大小写
	 * 
	 * @param value
	 * @param containString
	 * @return
	 */
	public static boolean containsIgnoreCase(String value, String containString) {
		return StringUtils.contains(value.toLowerCase(),
				containString.toLowerCase());
	}

	/**
	 * 对Unicode进行转码 eg:StringUtils.decodeUnicode("\\u7777\\u8765")=睷蝥
	 * 
	 * @param value
	 * @return
	 */
	public static String decodeUnicode(String value) {
		if (null == value) {
			return "";
		}

		StringBuffer result = new StringBuffer("");

		StringTokenizer stk = new StringTokenizer(value, "\\u");

		char c;

		for (; stk.hasMoreTokens(); result.append(c)) {
			String ii = stk.nextToken();
			c = (char) Integer.parseInt(ii, 16);
		}

		return result.toString();
	}

	/**
	 * 替换字符串中指定的子字符串
	 * 
	 * @param srcData
	 * @param src
	 * @param patten
	 * @return
	 */
	public static String changeToString(String srcData, String src,
			String patten) {
		int nLen = 0;
		int nSrcLen = src.length();

		int nPos = srcData.indexOf(src);

		while (nPos > 0) {
			nLen = srcData.length();
			srcData = srcData.substring(0, nPos)
					+ patten
					+ (nLen > nPos ? (srcData.substring(nPos + nSrcLen, nLen))
							: "");
			nPos = srcData.indexOf(src);
		}

		return srcData;
	}

	/**
	 * 字符串格式化,对回车符号进行HTML标签替换
	 * 
	 * @param srcData
	 *            需格式化的字符串
	 * @return 已格式化的字符串
	 */
	public static String getEnterLine(String srcData) {
		int nLen = 0;
		int nPos = srcData.indexOf('\r');

		while (nPos > 0) {
			nLen = srcData.length();
			srcData = srcData.substring(0, nPos) + "<br>"
					+ (nLen > nPos ? srcData.substring(nPos + 1, nLen) : "");
			nPos = srcData.indexOf('\r');
		}

		return srcData;
	}

	/**
	 * 去掉字符串中的回车换行符
	 * 
	 * @param srcData
	 *            需去掉回车换行符的字符串
	 * @return 去掉回车换行符的字符串
	 */
	public static String filterSpace(String srcData) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < srcData.length(); i++) {
			int asc = srcData.charAt(i);

			if (asc != 13 && asc != 10)
				sb.append(srcData.subSequence(i, i + 1));
		}
		return sb.toString();
	}

	/**
	 * 替换字符串中的双引号为HTML中引号表示标签
	 * 
	 * @param srcData
	 *            需替换的字符串
	 * @return 替换后的字符串
	 */
	public static String changeHtmlQuotSign(String srcData) {
		int nLen = 0;
		int nPos = srcData.indexOf('\"');

		while (nPos > 0) {
			nLen = srcData.length();
			srcData = srcData.substring(0, nPos) + "&quot;"
					+ (nLen > nPos ? srcData.substring(nPos + 1, nLen) : "");
			nPos = srcData.indexOf('\"');
		}
		return srcData;

	}

	/**
	 * 后缀补零操作
	 * 
	 * @param src
	 * @param length
	 * @return
	 */
	public static String suffixesRepairZeros(String src, int length) {
		StringBuffer result = new StringBuffer("");
		int index = StringUtils.replaceEmpty(src).length();
		result.append(StringUtils.replaceEmpty(src));
		if (index < length) {
			for (int i = (length - index); i > 0; i--) {
				result.append("0");
			}
		}
		return result.toString();
	}

	/**
	 * 单引号转换为双引号
	 * 
	 * @param srcData
	 *            需替换的字符串
	 * @return 替换后的字符串
	 */
	public static String changeInvertedComma(String srcData) {
		int nLen = 0;
		int nPos = srcData.indexOf('\'');

		while (nPos > 0) {
			nLen = srcData.length();
			srcData = srcData.substring(0, nPos) + "\""
					+ (nLen > nPos ? srcData.substring(nPos + 1, nLen) : "");
			nPos = srcData.indexOf('\'');
		}
		return srcData;
	}

	/**
	 * 首字母大写
	 * 
	 * @param param
	 * @return
	 */
	public static String upperCaseFirstWord(String param) {
		return param.substring(0, 1).toUpperCase() + param.substring(1);
	}

	/**
	 * 从身份证号提取出生日期
	 * 
	 * @param idCard
	 * @return
	 */
	public static String idCardtoBirthday(String idCard) {
		if (StringUtils.isEmpty(idCard))
			return "";
		idCard = StringUtils.replaceEmpty(idCard);
		if (idCard.length() == 15) {
			return "19" + idCard.substring(6, 8) + "-"
					+ idCard.substring(8, 10) + "-" + idCard.substring(10, 12);
		} else if (idCard.length() == 18) {
			return idCard.substring(6, 10) + "-" + idCard.substring(10, 12)
					+ "-" + idCard.substring(12, 14);
		} else {
			return "";
		}
	}

	/**
	 * 从字符串中提取数字部分
	 * 
	 * @return
	 */
	public static String stringToNumericPortion(String src) {
		StringBuffer result = new StringBuffer("");
		Pattern pattern = Pattern.compile("[^0-9]");
		String[] temp = pattern.split(src);
		for (String item : temp) {
			if (!"".equals(item.trim()))
				result.append(item.trim());
		}
		return result.toString();
	}

	/**
	 * 货币金额小写转大写
	 * 
	 * @param money
	 *            货币金额
	 * @return
	 */
	public static String moneyLToU(String money) {
		String result = "";
		StringBuffer item = new StringBuffer("");
		int index = 1;
		int len = 0;
		if (null == money)
			throw new RuntimeException("The input param is null");

		money = money.trim();
		if ("".equals(money))
			throw new java.lang.RuntimeException("The input param is empty");

		try {
			Float.parseFloat(money);
		} catch (NumberFormatException ex) {
			throw ex;
		}

		if (money.indexOf(".") >= 0
				&& money.length() - 1 >= money.indexOf(".") + 2)
			money = money.substring(0, money.indexOf(".") + 3);
		else if (money.indexOf(".") >= 0
				&& money.length() - 1 < money.indexOf(".") + 2)
			money = money + "0";
		else
			money = money + ".00";

		len = money.length();
		for (; index <= len; index++) {
			switch (money.substring(len - index, len - index + 1).charAt(0)) {
			case '.':
				item.append("元");
				break;
			case '0':
				item.append("零");
				break;
			case '1':
				item.append("壹");
				break;
			case '2':
				item.append("贰");
				break;
			case '3':
				item.append("叁");
				break;
			case '4':
				item.append("肆");
				break;
			case '5':
				item.append("伍");
				break;
			case '6':
				item.append("陆");
				break;
			case '7':
				item.append("柒");
				break;
			case '8':
				item.append("捌");
				break;
			case '9':
				item.append("玖");
				break;
			}

			switch (index) {
			case 1:
				item.append("分");
				break;
			case 2:
				item.append("角");
				break;
			case 3:
				item.append("");
				break;
			case 4:
				item.append("");
				break;
			case 5:
				item.append("拾");
				break;
			case 6:
				item.append("佰");
				break;
			case 7:
				item.append("仟");
				break;
			case 8:
				item.append("万");
				break;
			case 9:
				item.append("拾");
				break;
			case 10:
				item.append("佰");
				break;
			case 11:
				item.append("仟");
				break;
			case 12:
				item.append("亿");
				break;
			case 13:
				item.append("拾");
				break;
			case 14:
				item.append("佰");
				break;
			case 15:
				item.append("仟");
				break;
			case 16:
				item.append("万");
				break;
			}
			result = item.toString() + result;
			item.delete(0, item.length());
		}
		result = result.replaceAll("零拾", "零");
		result = result.replaceAll("零佰", "零");
		result = result.replaceAll("零仟", "零");
		result = result.replaceAll("零零零", "零");
		result = result.replaceAll("零零", "零");
		result = result.replaceAll("零角零分", "整");
		result = result.replaceAll("零分", "整");
		result = result.replaceAll("零角", "零");
		result = result.replaceAll("零亿零万零元", "亿元");
		result = result.replaceAll("亿零万零元", "亿元");
		result = result.replaceAll("零亿零万", "亿");
		result = result.replaceAll("零万零元", "万元");
		result = result.replaceAll("万零元", "万元");
		result = result.replaceAll("零亿", "亿");
		result = result.replaceAll("零万", "万");
		result = result.replaceAll("零元", "元");
		result = result.replaceAll("零零", "零");

		if ("元".equals(result.substring(0, 1)))
			result = result.substring(1);

		if ("零".equals(result.substring(0, 1)))
			result = result.substring(1);
		if ("角".equals(result.substring(0, 1)))
			result = result.substring(1);
		if ("分".equals(result.substring(0, 1)))
			result = result.substring(1);

		if ("整".equals(result.substring(0, 1)))
			result = "零元整";

		return result;
	}

	public static String stringTChar(String src) {
		StringBuffer result = new StringBuffer("");
		String temp = "";
		while (src.length() > 2) {
			temp = src.substring(0, 2);

			// 加密
			result.append(temp).append("|");
			src = src.substring(2);
		}
		if (src.length() > 0) {
			// 加密
			result.append(src);
		} else {
			result.substring(0, result.length() - 1);
		}
		return result.toString();
	}

	/**
	 * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
	 * 
	 * @param char c, 需要判断的字符
	 * @return boolean, 返回true,Ascill字符
	 */
	private static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
	 * 
	 * @param String
	 *            s ,需要得到长度的字符串
	 * @return int, 得到的字符串长度
	 */
	public static int strLength(String s) {
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}

	/**
	 * 截取字符串长度 以字节为单位截取
	 * 
	 * @param str
	 *            原始字符串
	 * @param pstart
	 *            开始 最小值为 0
	 * @param pend
	 *            需要截取的字符串长度
	 * @return 截取之后的字符串长度
	 */
	public static String getSubString(String str, int pstart, int pend) {
		String resu = "";
		int beg = 0;
		int end = 0;
		int count1 = 0;
		char[] temp = new char[str.length()];
		str.getChars(0, str.length(), temp, 0);
		boolean[] bol = new boolean[str.length()];
		for (int i = 0; i < temp.length; i++) {
			bol[i] = false;
			if ((int) temp[i] > 255) {// 说明是中文
				count1++;
				bol[i] = true;
			}
		}

		if (pstart > str.length() + count1) {
			resu = null;
		}
		if (pstart > pend) {
			resu = null;
		}
		if (pstart < 1) {
			beg = 0;
		} else {
			beg = pstart - 1;
		}
		if (pend > str.length() + count1) {
			end = str.length() + count1;
		} else {
			end = pend;// 在substring的末尾一样
		}
		// 下面开始求应该返回的字符串
		if (resu != null) {
			if (beg == end) {
				int count = 0;
				if (beg == 0) {
					if (bol[0] == true)
						resu = null;
					else
						resu = new String(temp, 0, 1);
				} else {
					int len = beg;// zheli
					for (int y = 0; y < len; y++) {// 表示他前面是否有中文,不管自己
						if (bol[y] == true)
							count++;
						len--;// 想明白为什么len--
					}
					// for循环运行完毕后，len的值就代表在正常字符串中，目标beg的上一字符的索引值
					if (count == 0) {// 说明前面没有中文
						if ((int) temp[beg] > 255)// 说明自己是中文
							resu = null;// 返回空
						else
							resu = new String(temp, beg, 1);
					} else {// 前面有中文，那么一个中文应与2个字符相对
						if ((int) temp[len + 1] > 255)// 说明自己是中文
							resu = null;// 返回空
						else
							resu = new String(temp, len + 1, 1);
					}
				}
			} else {// 下面是正常情况下的比较
				int temSt = beg;
				int temEd = end - 1;// 这里减掉一
				for (int i = 0; i < temSt; i++) {
					if (bol[i] == true)
						temSt--;
				}// 循环完毕后temSt表示前字符的正常索引
				for (int j = 0; j < temEd; j++) {
					if (bol[j] == true)
						temEd--;
				}// 循环完毕后temEd-1表示最后字符的正常索引
				if (bol[temSt] == true)// 说明是字符，说明索引本身是汉字的后半部分，那么应该是不能取的
				{
					int cont = 0;
					for (int i = 0; i <= temSt; i++) {
						cont++;
						if (bol[i] == true)
							cont++;
					}
					if (pstart == cont)// 是偶数不应包含,如果pstart<cont则要包含
						temSt++;// 从下一位开始
				}
				if (bol[temEd] == true) {// 因为temEd表示substring
											// 的最面参数，此处是一个汉字，下面要确定是否应该含这个汉字
					int cont = 0;
					for (int i = 0; i <= temEd; i++) {
						cont++;
						if (bol[i] == true)
							cont++;
					}
					if (pend < cont)// 是汉字的前半部分不应包含
						temEd--;// 所以只取到前一个
				}
				if (temSt == temEd) {
					resu = new String(temp, temSt, 1);
				} else if (temSt > temEd) {
					resu = null;
				} else {
					resu = str.substring(temSt, temEd + 1);
				}
			}
		}
		return resu;// 返回结果
	}

	/**
	 * 解决文档文件名乱码.
	 */
	public static String toUtf8String(String source) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
					ex.printStackTrace();
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
						k += 256;
					}
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	/**
	 * join string.
	 * 
	 * @param array
	 *            String array.
	 * @return String.
	 */
	public static String join(String[] array) {
		if (array.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (String s : array)
			sb.append(s);
		return sb.toString();
	}
	
	public static String getJson(String resultCode, String resultMsg,
			String statusCode, String data) {
		JSONObject json=new JSONObject();
		json.element("resultCode", resultCode);
		json.element("resultMsg", tansSpecailChar(resultMsg));
		json.element("statusCode", statusCode);
		json.element("data", data);
		return json.toString();
	}
	
	public static String toJson(Object res){
		String jsonArr = "{\"RESULTLIST\"" + ":"
		+ JSONArray.fromObject(res).toString()
		+"}";
		return jsonArr;
	}
	
	/**
	 * 日期转换 如01/01/2012 变成20120101
	 * 
	 * @return
	 */
	public static String stringTransform(String data) {
		StringBuilder sb = new StringBuilder();
		String[] array = data.split("/");
		sb.append(array[2]);
		sb.append(array[1]);
		sb.append(array[0]);
		return sb.toString();
	}
	
	/**
	 * 合并ID字符串，并去除重�?
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String mergeIdStr(String str1, String str2,
			boolean removeRepate) {
		StringBuffer buf = new StringBuffer();
		if (!StringUtils.isEmpty(str1)) {
			buf.append(str1);
		}
		if (!StringUtils.isEmpty(str1) && !StringUtils.isEmpty(str2)) {
			buf.append(",");
		}
		if (!StringUtils.isEmpty(str2)) {
			buf.append(str2);
		}
		String str = buf.toString();
		if (removeRepate) {
			str = removeRepeate(str);
		}
		return str;
	}
	
	/**
	 * 去除带分割符","里的重复ID
	 * 
	 * @param str
	 * @return
	 */
	public static String removeRepeate(String str) {
		String[] arr = str.split(",");
		if (arr.length == 1)
			return str;
		List<String> list = new LinkedList<String>();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (!list.contains(arr[i]) && !"".equals(arr[i]))
				list.add(arr[i]);
		}
		for (int i = 0; i < list.size(); i++) {
			if (i != 0)
				buf.append(",");
			buf.append(list.get(i));
		}
		return buf.toString();
	}
	
	/**
	 * 转义特殊字符
	 * @author：liufeng
	 * @param str
	 * @return
	 */
	public static String tansSpecailChar(String str) {
		if (str != null) {
				str = str.replaceAll("&", "&amp;");
				str = str.replaceAll("<", "&lt;");
				str = str.replaceAll(">", "&gt;");
				str = str.replaceAll("\"", "&quot;");
				return str;
		}
		return null;
	}
	/**
	 * 去NULL
	 * @author：liufeng
	 * @param obj
	 * @return
	 */
	public static String  escapeNullValue(Object obj){
		if(obj==null){
			return "";
		}else{
			return obj.toString();
		}
	}
	
	public static String toJson(Object res,Object wpd){
		String jsonArr = "{\"RESULTLIST\"" + ":"
		+ JSONArray.fromObject(res).toString()
		+ ",\"WPD\":"
		+ JSONArray.fromObject(wpd).toString().replace("[", "").replace("]", "")+"}";
		return jsonArr;
	}
	
	/**
	 * 用UTF-8编码
	 * 
	 * @param str
	 * @return
	 */
	public static String Base64Encode(String str, String encoding) {
		if (str != null) {
			BASE64Encoder bASE64Encoder = new BASE64Encoder();
			try {
				str = bASE64Encoder.encode(str.getBytes(encoding));
				return str;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String urlEncode(String str, String encoding) {
		try {
			return URLEncoder.encode(str, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 用UTF-8解码
	 * 
	 * @param str
	 * @return
	 */
	public static String Base64Decode(String str, String encoding) {
		if (str != null) {
			BASE64Decoder decode = new BASE64Decoder();
			try {
				str = str.replaceAll("%2B", "+");
				str = str.replaceAll("%2F", "/");
				str = str.replaceAll("%3D", "=");
				str = new String(decode.decodeBuffer(str), encoding);
				return str;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 将字符串首字母转换为大写
	 * 
	 * @param s
	 * @return
	 */
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(
					Character.toUpperCase(s.charAt(0))).append(s.substring(1))
					.toString();
	}
	
	public static String converArrayString(String name, String strsname) {
		StringBuffer str = new StringBuffer();
		boolean fal = false;
		String[] strs = strsname.split(",");
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].equals(name) || strs[i] == name) {
				fal = true;
				break;
			}

		}
		if (fal) {
			for (int i = 0; i < strs.length; i++) {
				if (!strs[i].equals(name) || strs[i] != name) {
					if (i == strs.length - 1) {
						str.append(strs[i]);
					} else {
						str.append(strs[i]).append(",");
					}
				}
			}
		} else {
			for (int i = 0; i < strs.length; i++) {
				str.append(strs[i]).append(",");
			}
			str.append(name);
		}

		return str.toString();
	}
	
	public static String subString(String str, int endIndex) {
		if (null == str || str.length() <= endIndex) {
			return str;
		} else {
			return str.substring(0, endIndex);
		}
	}

	/**
	 * 
	 * @param value
	 * @param replaceString超长部分替换
	 * @return
	 */
	public static String subStringChinese(String value, String replaceString,
			int maxLength) {
		try {
			if (value.length() > maxLength) {
				float curSize = 0;
				StringBuffer sb = new StringBuffer("");
				char[] charArray = value.toCharArray();
				int enchar = 0;
				for (int j = 0; j < charArray.length; j++) {
					String str = String.valueOf(charArray[j]);

					if (str.getBytes("utf-8").length == 1) {
						curSize += 0.5;
						enchar++;
					} else {
						curSize += 1;
					}
					if (curSize <= maxLength) {
						sb.append(str);
					} else {
						sb.append(replaceString);
						break;
					}
				}
				value = sb.toString();
			}
		} catch (Exception e) {
			//loggerService.error("截取字符串异�?", e);
		}
		return value;
	}

	/**
	 * 把文件分类列表转成定数字符串显示
	 * 
	 * @param list
	 * @return
	 */

	public static String getFileTextTention(String fileName) {
		if (fileName != null) {
			String[] arr = fileName.split(".");
			if (arr[0].equals("*") && arr.length == 2) {
				return arr[1];
			}
		}
		return null;
	}
}
