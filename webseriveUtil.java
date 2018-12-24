package com.zhijieyun.gwc.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.core.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.zhijieyun.gwc.common.util.ContextBeanUtil;
import com.zhijieyun.gwc.dao.mapper.IDataPushInfoMapper;
import com.zhijieyun.gwc.domain.entity.dataupload.DataUploadPushLog;
import com.zhijieyun.gwc.domain.entity.dataupload.PushDataInfo;
import com.zhijieyun.gwc.service.DataPushInfoService;
import com.zhijieyun.gwc.service.dataupload.impl.DataUploadLogServiceImpl;

public class DataPushInfoServiceImpl implements DataPushInfoService {

	@Override
	public DataUploadPushLog DataCenterWebservice(String key, String date) {
			OutputStream os = null;
			try {
				// 获取诊疗数据
				List<PushDataInfo> list = dataPushInfoMapper.DataCenterWebservice(date,sqlCodes[i]);
				// 数据推送
				for (PushDataInfo temp : list) {
					// 设置推送xml 机构名和医生名
					temp.setOrgCode(orgCodes[i]);
					temp.setUsername(doctorCodes[i]);
					// 声明地址
					URL url = new URL(webServiceUrl);
					// 打开链接 转换成 httpUrl 请求
					HttpURLConnection uc = (HttpURLConnection) url.openConnection();
					uc.setRequestMethod("POST");
					// 设置参数:
					uc.setRequestProperty("Content-Length", "4096");
					uc.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");

					// 设置输入输出，新创建的connection默认是没有读写权限的，
					uc.setDoInput(true);
					uc.setDoOutput(true);

					// 组织SOAP协议数据，发送给服务端
					String operate = "ba800407ecb14125bd921bcdb99ec2a4";
					String soapXML = getXML(key, operate, parsePushXml(temp));

					// 获取推送数据ID
					pushLog.setDataId(temp.getId());
					// 设置开始时间
					pushLog.setStartTime(new Date());
					pushLog.setDoctorCode(temp.getDoctorCode());
					pushLog.setDoctorName(temp.getDoctorName());
					pushLog.setOrgCode(temp.getOrgCode());
					pushLog.setOrgName(temp.getOrgName());
					
					InputStream in = null;
					// 一条一条推送
					try {
						os = uc.getOutputStream();
						// 如果不设置UTF-8 数据报错
						os.write(soapXML.getBytes("UTF-8"));

						// 接收服务端的响应
						int responseCode = uc.getResponseCode();
						
						// 如果返回值为200
						if (200 == responseCode) {
							in = uc.getInputStream();
							BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
							String str = "";
							StringBuffer inRes = new StringBuffer();
							while ((str = bf.readLine()) != null) {
								inRes.append(str);
							}
							String returnStr = inRes.toString().split("<ns1:out>")[1].split("</ns1:out>")[0];
							pushLog.setDataStatus(returnStr);
						}
					} catch (ConnectException e) {
						// 连接超时
						pushLog = setPushDataError("connect timeout", pushLog, e);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						// 其他异常
						pushLog = setPushDataError("inner exception", pushLog, e);
					} finally {
						// 结束时间
						pushLog.setEndTime(new Date());
						// 保存日志
						dataUploadLogService.addDataPushMainLog(pushLog);
						
						// 关闭输入流
						if (in != null) {
							in.close();
						}
					}
				}
			} catch (Exception e) {
				// 其他异常
				pushLog = setPushDataError("outer exception", pushLog, e);
			}finally { 
				if (os!=null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return pushLog;
	}

	/**
	 * 设置数据推送异常
	 * @param msg 异常信息
	 * @param pushLog 异常日志
	 * @param e 异常
	 * @return DataUploadPushLog
	 */
	public DataUploadPushLog setPushDataError(String msg, DataUploadPushLog pushLog, Exception e) {
		pushLog.setDataStatus(msg);
		pushLog.setMemo(e.getMessage());
		_logger.info(msg + ":" + e.getMessage());
		return pushLog;
	}

	public static String getXML(String key, String operate, String strXml) {
		StringBuffer sb = new StringBuffer();
		sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.jiekou.com\">");
		sb.append("   <soapenv:Header/>");
		sb.append("   <soapenv:Body>");
		sb.append("      <web:DataCenterWebservice>");
		sb.append("         <web:in0>");
		sb.append("         <![CDATA["+key+"]]>");
		sb.append("         </web:in0>");
		sb.append("         <web:in1>");
		sb.append("         <![CDATA["+operate+"]]>");
		sb.append("          </web:in1>");
		sb.append("         <web:in2>");
		sb.append("<![CDATA[");
		sb.append(strXml);
		sb.append("]]>");
		sb.append("         </web:in2>");
		sb.append("      </web:DataCenterWebservice>");
		sb.append("   </soapenv:Body>");
		sb.append("</soapenv:Envelope>");
		return sb.toString();
	}
	/**
	 * 转换xml推送格式
	 * 
	 * @param info
	 * @return
	 */
	public String parsePushXml(PushDataInfo info) {
		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlStr.append(
				"<XMLTOPERSONS return=\"TRUE\" value=\"0\" biaoshi=\"2\" username=\""+ info.getUsername() +"\" prgid=\""+ info.getOrgCode() +"\">");
		// 个人基本信息
		xmlStr.append("<row name=\"T_DA_JKDA_RKXZL\" jkkh=\"\">");
		// 身份证号（必填）
		xmlStr.append("<field name=\"DSfzh\">" + info.getIdcard() + "</field>");
		// 接诊记录
		xmlStr.append("<subrow name=\"T_YF_ZSJL\">");
		xmlStr.append("<field name=\"id\">"+info.getId()+"</field>");
		// 接诊医生
		String doctorName = "";
		if (!StringUtils.isEmpty(info.getDoctorName())) {
			doctorName = info.getDoctorName();
		}
		xmlStr.append("<field name=\"YJzys\">" + doctorName + "</field>");
		// 就诊者的主观资料
		String complaint = "";
		if (!StringUtils.isEmpty(info.getComplaint())) {
			complaint = info.getComplaint();
		}
		xmlStr.append("<field name=\"YZz\">" + complaint + "</field>");
		// 就诊者的客观资料
		StringBuffer objectives = new StringBuffer();// 客观资料
		if (!StringUtils.isEmpty(info.getTemperatureType())) {
			objectives.append("体温类型：" + info.getTemperatureType() + "，");
		}
		if (!StringUtils.isEmpty(info.getTemperature())) {
			objectives.append("体温：" + info.getTemperature() + "°C，");
		}
		if (!StringUtils.isEmpty(info.getPulse())) {
			objectives.append("脉搏：" + info.getPulse() + "次/分，");
		}
		if (!StringUtils.isEmpty(info.getBloodPressure())) {
			objectives.append("收缩压：" + info.getBloodPressure() + "mmHg，");
		}
		if (!StringUtils.isEmpty(info.getBloodPressure2())) {
			objectives.append("舒张压：" + info.getBloodPressure2() + "mmHg，");
		}
		if (!StringUtils.isEmpty(info.getBloodSugar())) {
			objectives.append("血糖：" + info.getBloodSugar() + "mmol/L，");
		}
		if (!StringUtils.isEmpty(info.getHeight())) {
			objectives.append("身高：" + info.getHeight() + "cm，");
		}
		if (!StringUtils.isEmpty(info.getWeight())) {
			objectives.append("体重：" + info.getWeight() + "kg，");
		}
		if (!StringUtils.isEmpty(info.getBmi())) {
			objectives.append("BMI：" + info.getBmi() + "，");
		}
		if (!StringUtils.isEmpty(info.getBmiTip())) {
			objectives.append("BMITip：" + info.getBmiTip() + "，");
		}
		xmlStr.append("<field name=\"YJcjg\">" + objectives.toString() + "</field>");
		// 评估
		String assessment = "";
		if (!StringUtils.isEmpty(info.getAssessment())) {
			assessment = info.getAssessment();
		}
		xmlStr.append("<field name=\"YZdjl\">" + assessment + "</field>");
		// 处置计划
		String disposalPlan = "";
		if (!StringUtils.isEmpty(info.getDisposalPlan())) {
			disposalPlan = info.getDisposalPlan();
		}
		xmlStr.append("<field name=\"YClcs\">" + disposalPlan + "</field>");
		// 暂不对应
		xmlStr.append("<field name=\"presentregion\"></field>");
		// 接诊时间 （必填）
		xmlStr.append("<field name=\"happentime\">" + info.getAcceptsTime() + "</field>");
		// 此项赋值0
		xmlStr.append("<field name=\"qdqxz\">0</field>");
		xmlStr.append("</subrow>");
		xmlStr.append("</row>");
		xmlStr.append("</XMLTOPERSONS>");
		
		return xmlStr.toString();
	}

	public String getWebServiceUrl() {
		return webServiceUrl;
	}

	public void setWebServiceUrl(String webServiceUrl) {
		this.webServiceUrl = webServiceUrl;
	}

	public String getWebPushOrgCode() {
		return webPushOrgCode;
	}

	public void setWebPushOrgCode(String webPushOrgCode) {
		this.webPushOrgCode = webPushOrgCode;
	}

	public String getWebPushDoctorCode() {
		return webPushDoctorCode;
	}

	public void setWebPushDoctorCode(String webPushDoctorCode) {
		this.webPushDoctorCode = webPushDoctorCode;
	}


	public String getWebSqlOrgCode() {
		return webSqlOrgCode;
	}

	public void setWebSqlOrgCode(String webSqlOrgCode) {
		this.webSqlOrgCode = webSqlOrgCode;
	}
}
