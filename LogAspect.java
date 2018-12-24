package com.zhijieyun.cbf.manager.aop;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zhijieyun.cbf.domain.dto.UserDto;
import com.zhijieyun.cbf.service.impl.EhrServiceConsumer;
import com.zhijieyun.cbf.view.common.util.DateUtil;
import com.zhijieyun.cbf.view.common.util.StringUtils;
import com.zhijieyun.zjylog.annotation.LogRecord;
import com.zhijieyun.zjylog.domain.entity.ZjyLog;
import com.zhijieyun.zjylog.service.IZjyLogUtilsService;
import com.zhijieyun.zjylog.util.PropertiesLoader;

@Component
@Aspect
public class LogAspect {
	private ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<Long>();
	private Logger logger = LoggerFactory.getLogger(Static.class);

	private static final class Static {
		private static IZjyLogUtilsService logUtilsService = EhrServiceConsumer.getZjyLogUtilsService();
	}

	@Pointcut(value = "@annotation(com.zhijieyun.zjylog.annotation.LogRecord)")
	public void pointCut() {
	}

	@Around("pointCut() && @annotation(logRecord)")
	public Object doAround(ProceedingJoinPoint pjp, LogRecord logRecord) throws Throwable {
		Object ret  = null;
		try {
			RequestAttributes ra = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) ra;
			HttpServletRequest request = sra.getRequest();
			long beginTime = System.currentTimeMillis();// 1、开始时间
			startTimeThreadLocal.set(beginTime); // 线程绑定变量（该数据只有当前请求的线程可见）
			if (logger.isDebugEnabled()) {
				logger.debug("开始计时: {}  URI: {}", new SimpleDateFormat("hh:mm:ss.SSS").format(beginTime),
						request.getRequestURI());
			}
			try {
				ret = pjp.proceed();
			} catch (Exception e) {
				// 错误日志
				addZjyLog(request, logRecord, ret,pjp, e);
				throw new Exception(e);
			}
			// 添加日志信息
			addZjyLog(request, logRecord, ret, pjp,null);
			long endTime = System.currentTimeMillis(); // 2、结束时间
			long executeTime = endTime - beginTime; // 3、获取执行时间
			if (logger.isDebugEnabled()) {
				logger.debug("计时结束：{}  耗时：{} ", new SimpleDateFormat("hh:mm:ss.SSS").format(endTime),
						DateUtil.formatDateTime(executeTime));
				// 用完之后销毁线程变量数据，防止内存泄漏
				startTimeThreadLocal.remove();
			}
			return ret;
		} catch (Exception e) {
			ret =  pjp.proceed();
			logger.error(e.getMessage());
		}
		return ret;
	}

	/**
	 * 添加日志信息.
	 * 
	 * @param request
	 * @param logRecord
	 * @param returnObj
	 * @param ex
	 */
	private void addZjyLog(HttpServletRequest request, LogRecord logRecord, Object returnObj, ProceedingJoinPoint pjp,Exception ex) {
		try {
			ZjyLog log = new ZjyLog();
			HttpSession session = request.getSession();
			String username = null;
			UserDto dto = null;
			if (session.getAttribute("login_user") != null) {
				// 从session中获取mpiId
				dto = (UserDto)session.getAttribute("login_user");
				username = dto.getUserInfo().getUserName();
			} else {
				// 从request请求中获取mpiId
				username = request.getParameter("loginForm:name");
			}
			Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
			String methodName = method.getName();
			String resourceClass  = pjp.getTarget().getClass().toString();
			String logResource = resourceClass +"---->>>>"+methodName;
			String moduleName = logRecord.moduleName();
			String actionType = logRecord.actionType();
			boolean needLogDb = logRecord.needLogDb();
			String title = moduleName + "-" + actionType;
			PropertiesLoader loader = new PropertiesLoader("conf/zjylog.properties");
			String syscode = loader.getProperty("zjylog.syscode");// 配置文件中获取系统编码
			String sysname = loader.getProperty("zjylog.sysname");// 配置文件中获取系统名称
			String writedb = loader.getProperty("zjylog.writedb");// 获取是否写入数据库配置
			log.setCreateBy(username);
			log.setTitle(title);
			log.setModuleName(moduleName);
			log.setActionType(actionType);
			log.setType(ex == null ? ZjyLog.TYPE_ACCESS : ZjyLog.TYPE_EXCEPTION);
			log.setRemoteAddr(StringUtils.getRemoteAddr(request));
			log.setUserAgent(request.getHeader("user-agent"));
			log.setRequestUri(request.getRequestURI());
			log.setParams(request.getParameterMap());
			log.setMethod(request.getMethod());
			log.setCreateDate(new Date());
			log.setSyscode(syscode);
			log.setSysname(sysname);
			log.setLogResource(logResource);
			log.setReturnValue(returnObj == null? null :returnObj.toString());
			if ("true".equals(writedb) && needLogDb) {
				Static.logUtilsService.saveLog(log, ex, username);
			} else {
				// 正常日志
				if (ZjyLog.TYPE_ACCESS.equals(log.getType())) {
					logger.info(log.toString());
				} else {
					// 错误日志
					logger.error(log.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
