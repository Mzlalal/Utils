package com.zhijieyun.ds.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
/**
 *  Oracle sqllder工具类
 * @author Jiashuang
 *
 */

public class SqlldrUtil {
	/**
	 *  Sqllder导出数据
	 * @param DBurl				数据库地址
	 * @param DBuserName		数据库用户名
	 * @param DBpassWord			 数据库密码
	 * @param ctlFilePath		控制文件路径
	 * @param strings			执行参数，与控制文件对应
	 */
	public static void exportData(String DBurl, String DBuserName, String DBpassWord,String ctlFilePath, String... strings) {

		StringBuffer buff = new StringBuffer();
		for (String s : strings) {
			buff.append(" ").append(s);
		}

		try {
			final Process process = Runtime.getRuntime()
					.exec("sqlplus " + DBuserName + "/" + DBpassWord + "@//" + DBurl + " @" + ctlFilePath + buff.toString());
			final InputStream is1 = process.getInputStream();
			// 3、获取进程的标准错误流
			final InputStream is2 = process.getErrorStream();
			// 4、启动两个线程，一个负责读标准输出流，另一个负责读标准错误流
			new Thread() {
				public void run() {
					BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
					try {
						String line1 = null;
						while ((line1 = br1.readLine()) != null) {
							// 如果出现错误信息
							if (line1.contains("SP2-") || line1.contains("ORA-") || line1.contains("ERROR:")) {
								// 关闭主线程
								// _logger.warn(line1);
								process.destroy();
							} else {
								// _logger.warn(line1);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is1.close();
							br1.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			new Thread() {
				public void run() {
					BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
					try {
						String line2 = null;
						while ((line2 = br2.readLine()) != null) {
							// _logger.warn(line2);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							br2.close();
							is2.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			// 5、等待主线程结束
			process.waitFor();
			// 6、关闭窗口
			process.destroy();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Sqllder导入数据
	 * @param DBurl				数据库地址
	 * @param DBuserName		数据库用户名
	 * @param DBpassWord		数据库密码
	 * @param ctlFilePath		控制文件路径
	 * @param dataFilePath		数据文件路径
	 */
	public static void importData(String DBurl, String DBuserName, String DBpassWord, String ctlFilePath,String dataFilePath){
		String path ;
		if(dataFilePath.lastIndexOf(".")!=-1){
			path = dataFilePath.substring(0,dataFilePath.lastIndexOf("."));
		}else{
			path = dataFilePath;
		}
		try {
			final Process process = Runtime.getRuntime()
					.exec("sqlldr userid=" + DBuserName + "/" + DBpassWord + "@" + DBurl + " control=" + ctlFilePath
							+ " data=" + dataFilePath + " log=" +path+ ".log" + " bad=" + path+ ".bad");
			// 等待主线程结束
			process.waitFor();
			// 关闭窗口
			process.destroy();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 *  根据查询语句创建导出控制文件
	 * @param ctlFilePath  	需要创建控制文件的路径
	 * @param selectSql		控制文件中查询语句	
	 * @param ReCreateFlag	如果文件存在，是否重新创建文件标志，为true是则重新创建 
	 */
	public static void  createExportCtlFile(String ctlFilePath,String selectSql,boolean ReCreateFlag){
		File file=new File(ctlFilePath);
		if(file.exists()){
			if(ReCreateFlag){
				file.delete();
			}else{
				return;
			}
		}
		//根据文件路径创建出目录
		FileUtil.createFilePathDir(ctlFilePath);
		BufferedWriter bw = null;
		StringBuffer sqlb = new StringBuffer();
		sqlb.append("set arraysize 100; \r\n");// --此参数可提高SPOOL卸载的速度，最大可以设置为5000--
		sqlb.append("set echo off;\r\n");// --显示start启动的脚本中的每个sql命令，缺省为on--
		sqlb.append("set feedback off;\r\n");// --回显本次sql命令处理的记录条数，缺省为on，设置显示“已选择XX行”--
		sqlb.append("set heading off;\r\n");// --输出域标题，字段的名称，缺省为on--
		sqlb.append("set pagesize 0;\r\n");// --输出每页行数，缺省为24,为了避免分页，可设定为0--
		sqlb.append("set linesize 5000;\r\n");// --每行允许的最大字符数，设置大些，免得数据被截断，但不宜过大，太大会大大降低导出的速度--
		sqlb.append("set numwidth 12;\r\n");// --输出number类型域长度，缺省为10--
//		sqlb.append("set termout off;\r\n");// --显示脚本中的命令的执行结果，缺省为on--
		sqlb.append("set timing off;\r\n");// --显示每个sql语句花费的执行时间，设置显示“已用时间：XXXX”--
		sqlb.append("set trimout on;\r\n");// --去除标准输出每行的拖尾空格，缺省为off--
		sqlb.append("set trimspool on;\r\n");// --去除重定向（spool）输出每行的拖尾空格，缺省为off--
		sqlb.append("set autorecovery on;\r\n");// --命令在恢复期间将自动应用所需的归档重做日志文件的默认文件名--
		sqlb.append("set verify off;\r\n");//可以关闭和打开提示确认信息old 1和new 1的显示.
		sqlb.append("spool '&1';\r\n");
		// sqlb.append(sql + "\r\n");
		sqlb.append(selectSql  + "; \r\n" );
		sqlb.append("spool off;\r\n");
		sqlb.append("exit;");
		try {
			bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(ctlFilePath))));
			bw.write(sqlb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建导入控制文件
	 * @param ctlFilePath		控制文件路径
	 * @param tableName			需要导入数据库表名
	 * @param terminateChar		分隔符
	 * @param sqlColumns		导入数据库对应字段
	 * @param ReCreateFlag		如果文件存在，是否重新创建文件标志，为true是则重新创建
	 */
	public static void  createImportCtlFile(String ctlFilePath,String tableName ,String terminateChar ,String sqlColumns,Boolean ReCreateFlag){
		File file=new File(ctlFilePath);
		if(file.exists()){
			if(ReCreateFlag){
				file.delete();
			}else{
				return;
			}
		}
		//根据文件路径创建出目录
		FileUtil.createFilePathDir(ctlFilePath);
		
		BufferedWriter bw = null;
		StringBuffer sqlb = new StringBuffer();
		// SKIP忽略行数,ROWS单次提交行数,ERRORS运行错误数,READSIZE读取数据缓存大小
		sqlb.append("OPTIONS(SKIP=0,ROWS=50000,ERRORS=1000,READSIZE=2147473647,bindsize=2147473647,parallel=true,SILENT=ALL) \r\n");// 导入配置项
		sqlb.append("LOAD DATA\r\n");// 导入声明
		
		sqlb.append("APPEND INTO TABLE " + tableName + "\r\n");// 导入的表
		sqlb.append("fields terminated by '" + terminateChar + "'\r\n");// 选择分隔符
		sqlb.append("trailing nullcols\r\n");//
		sqlb.append("(" + sqlColumns + ")\r\n");// 导入的字段
		try {
			bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(ctlFilePath))));
			bw.write(sqlb.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
