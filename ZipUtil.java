package com.zhijieyun.ds.util;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Zip压缩/解压缩工具类 实现对目标路径及其子路径下的所有文件及空目录的压缩
 *
 */
public class ZipUtil {
	private static Log _logger = LogFactory.getLog(ZipUtil.class);
	/** 缓冲器大小 */
	private static final int BUFFER = 512;

	/**
	 * 取的给定源目录下的所有文件及空的子目录 递归实现
	 *
	 * @param srcFile
	 *
	 * @return
	 */
	private static List<File> getAllFiles(File srcFile) {
		List<File> fileList = new ArrayList<File>();
		if (srcFile.isDirectory()) {
			File[] tmp = srcFile.listFiles();

			for (int i = 0; i < tmp.length; i++) {

				if (tmp[i].isFile()) {
					fileList.add(tmp[i]);
					_logger.debug("添加文件	" + tmp[i].getName());
				}

				if (tmp[i].isDirectory()) {
					if (tmp[i].listFiles().length != 0) {// 若不是空目录，则递归添加其下的目录和文件
						fileList.addAll(getAllFiles(tmp[i]));
					} else {// 若是空目录，则添加这个目录到fileList
						fileList.add(tmp[i]);
						_logger.debug("添加目录	" + tmp[i].getName());
					}
				}
			}
		} else {
			fileList.add(srcFile);
			_logger.debug("添加文件	" + srcFile.getName());
		}

		return fileList;
	}

	/**
	 * 取相对路径 依据文件名和压缩源路径得到文件在压缩源路径下的相对路径
	 *
	 * @param dirPath
	 *            压缩源路径
	 * @param file
	 *
	 * @return 相对路径
	 */
	private static String getRelativePath(String dirPath, File file) {
		File dir = new File(dirPath);
		if(!dir.isDirectory()){
			dir = dir.getParentFile();
		}
		String relativePath = file.getName();

		while (true) {
			file = file.getParentFile();

			if (file == null) {
				break;
			}

			if (file.equals(dir)) {
				break;
			} else {
				if (!"".equals(file.getName())) {
					relativePath = file.getName() + "/" + relativePath;
				}
			}
		} // end while

		return relativePath;
	}

	/**
	 * 创建文件 根据压缩包内文件名和解压缩目的路径，创建解压缩目标文件， 生成中间目录
	 * 
	 * @param dstPath
	 *            解压缩目的路径
	 * @param fileName
	 *            压缩包内文件名
	 *
	 * @return 解压缩目标文件
	 *
	 * @throws IOException
	 */
	private static File createFile(String dstPath, String fileName) throws IOException {
		String[] dirs = fileName.split("/");// 将文件名的各级目录分解
		File file = new File(dstPath);

		if (dirs.length > 1) {// 文件有上级目录
			for (int i = 0; i < dirs.length - 1; i++) {
				file = new File(file, dirs[i]);// 依次创建文件对象知道文件的上一级目录
			}

			if (!file.exists()) {
				file.mkdirs();// 文件对应目录若不存在，则创建
				_logger.debug("创建目录:	" + file.getCanonicalPath());
			}

			file = new File(file, dirs[dirs.length - 1]);// 创建文件

			return file;
		} else {
			if (!file.exists()) {
				file.mkdirs();// 若目标路径的目录不存在，则创建
				_logger.debug("创建目录:	" + file.getCanonicalPath());
			}

			file = new File(file, dirs[0]);// 创建文件

			return file;
		}
	}

	/**
	 * 解压缩方法
	 *
	 *
	 * @param zipFileName
	 *            压缩文件名
	 * @param dstPath
	 *            解压目标路径
	 *
	 * @return
	 */
	public static boolean unzip(String zipFileName, String dstPath) {
		_logger.debug("开始解压文件,文件路径		" + dstPath);
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFileName));
			ZipEntry zipEntry = null;
			byte[] buffer = new byte[BUFFER];// 缓冲器
			int readLength = 0;// 每次读出来的长度

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) {// 若是zip条目目录，则需创建这个目录
					File dir = new File(dstPath + "/" + zipEntry.getName());

					if (!dir.exists()) {
						dir.mkdirs();
						_logger.debug("创建解压文件夹:	" + dir.getCanonicalPath());
						continue;// 跳出
					}
				} else {

					File file = createFile(dstPath, zipEntry.getName());// 若是文件，则需创建该文件

					_logger.debug("创建解压文件:	" + file.getCanonicalPath());
					OutputStream outputStream = new FileOutputStream(file);

					while ((readLength = zipInputStream.read(buffer, 0, BUFFER)) != -1) {
						outputStream.write(buffer, 0, readLength);
					}

					outputStream.close();
					_logger.debug("解压文件:	" + file.getCanonicalPath());
				}
			} // end while
		} catch (FileNotFoundException e) {
			_logger.error("解压文件	" + zipFileName + "	到	" + dstPath + "	目录失败\r\n" + e.getMessage());
			return false;
		} catch (IOException e) {
			_logger.error("解压文件	" + zipFileName + "	到	" + dstPath + "	目录失败\r\n" + e.getMessage());
			return false;
		}

		_logger.debug("解压文件	" + zipFileName + "	到	" + dstPath + "	目录成功");

		return true;
	}

	/**
	 * 压缩方法 （可以压缩空的子目录）
	 * 
	 * @param srcPath
	 *            压缩源路径
	 * @param zipFileName
	 *            目标压缩文件
	 *
	 * @return
	 */
	public static boolean zip(String srcPath, String zipFileName) {
		_logger.debug("开始压缩文件，文件路径		" + srcPath);
		File srcFile = new File(srcPath);
		List<File> fileList = getAllFiles(srcFile);// 所有要压缩的文件
		byte[] buffer = new byte[BUFFER];// 缓冲器
		ZipEntry zipEntry = null;
		int readLength = 0;// 每次读出来的长度

		try {
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFileName));

			for (File file : fileList) {
				if (file.isFile()) {// 若是文件，则压缩这个文件
					zipEntry = new ZipEntry(getRelativePath(srcPath, file));
					zipEntry.setSize(file.length());
					zipEntry.setTime(file.lastModified());
					zipOutputStream.putNextEntry(zipEntry);

					InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

					while ((readLength = inputStream.read(buffer, 0, BUFFER)) != -1) {
						zipOutputStream.write(buffer, 0, readLength);
					}

					inputStream.close();
					_logger.debug("压缩文件	" + file.getCanonicalPath());
				} else {// 若是目录（即空目录）则将这个目录写入zip条目
					zipEntry = new ZipEntry(getRelativePath(srcPath, file) + "/");
					zipOutputStream.putNextEntry(zipEntry);
					_logger.debug("添加压缩文件目录		" + file.getCanonicalPath() + "/");
				}

			}

			zipOutputStream.close();
		} catch (FileNotFoundException e) {
			_logger.error("压缩文件	" + srcPath + "	到	" + zipFileName + "失败\r\n" + e.getMessage());
			return false;
		} catch (IOException e) {
			_logger.error("压缩文件	" + srcPath + "	到	" + zipFileName + "失败\r\n" + e.getMessage());
			return false;
		}

		_logger.debug("压缩文件	" + srcPath + "	到	" + zipFileName + "成功");

		return true;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
