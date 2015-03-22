package com.cdcoder.core.mvc;


import com.cdcoder.core.excption.ActionException;
import com.cdcoder.core.util.CryptUtils;
import com.cdcoder.core.util.Multimedia;
import com.cdcoder.core.util.RequestUtils;
import com.cdcoder.core.util.ResourceUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: 请求上容器
 * @Package com.cdcoder.core.cache
 * @Description: ${todo}
 * @date 2015/3/11 23:25
 */
public class WebContext {
	private final static Logger log = LoggerFactory.getLogger(WebContext.class);

	private final static String UTF_8 = "UTF-8";
	
	private static int maxSize = 10 * 1024 * 1024;
	private final static String TEMP_UPLOAD_FILE = "$TEMP_UPLOAD_FILE$";

	private final static ThreadLocal<WebContext> CONTEXTS = new ThreadLocal<WebContext>();

	private ServletContext context;
	private HttpSession session;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Locale locale;
	private Map<String, Cookie> cookies;
	
	public final static byte[] E_KEY = new byte[] { '1', '2', '3', '4', '5', '6', '7', '8' };
	
	public final static String LOCALE = "___locale";
	public final static int MAX_AGE = 86400 * 365; // 默认一年时间 
	
	public final static String COOKIE_LOGIN = "___login";
	
	public final static String REQUEST_LOCALE_PARAM = "request_locale";
	
	static {
		// BeanUtils对时间转换的初始化设置
		ConvertUtils.register(new SqlDateConverter(null), java.sql.Date.class);
		ConvertUtils.register(new Converter() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
			SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-M-d H:m");

			@SuppressWarnings("rawtypes")
			public Object convert(Class type, Object value) {
				if (value == null)
					return null;
				if (value instanceof Date)
					return (value);
				try {
					return sdfTime.parse(value.toString());
				} catch (ParseException e) {
					try {
						return sdf.parse(value.toString());
					} catch (ParseException e1) {
						return null;
					}
				}
			}
		}, Date.class);
	}

	/**
	 * 初始化请求上下文，默认转码为UTF8
	 *
	 * @param servletContext
	 * @param request
	 * @param response
	 */
	public static WebContext begin(ServletContext servletContext,
			HttpServletRequest request, HttpServletResponse response) {
		WebContext wc = new WebContext();

		wc.context = servletContext;
		wc.request = autoUploadRequest(encodeRequest(request));// 是否是上传请求
		wc.response = response;

		// 保存request_locale参数设置
		wc.saveLocale();

		wc.response.setCharacterEncoding(UTF_8);
		wc.session = request.getSession(false); //默认不创建session
		//wc.session = req.getSession();

		wc.cookies = new HashMap<String, Cookie>();// 获取cookie
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie ck : cookies) {
				wc.cookies.put(ck.getName(), ck);
			}
		}

		CONTEXTS.set(wc);
		return wc;
	}

	/**
	 * 删除上传临时文件，清除context上下文。
	 */
	public void end() {
		String tmpPath = (String) request.getAttribute(TEMP_UPLOAD_FILE);
		if (tmpPath != null) {
			try {
				FileUtils.deleteDirectory(new File(tmpPath));
			} catch (IOException e) {
				log.error("Failed to cleanup upload directory: " + tmpPath, e);
			}
		}
		this.context = null;
		this.request = null;
		this.response = null;
		this.session = null;
		this.cookies = null;
		this.locale = null;

		CONTEXTS.remove();
	}

	/**
	 * 自动文件上传请求的封装
	 *
	 * @param request
	 * @return
	 */
	private static HttpServletRequest autoUploadRequest(HttpServletRequest request) {
		if (isMultipart(request)) {
			String path = System.getProperty("java.io.tmpdir") + RandomStringUtils.randomAlphanumeric(10);
			File dir = new File(path);
			if (!dir.exists() && !dir.isDirectory())
				dir.mkdirs();
			try {
				request.setAttribute(TEMP_UPLOAD_FILE, dir.getCanonicalPath());
				return new MultipartRequest(request, dir.getCanonicalPath(), maxSize, UTF_8);
			} catch (NullPointerException e) {
			} catch (IOException e) {
				log.error("Failed to save upload files into temp directory: " + path, e);
			}
		}

		return request;
	}

	/**
	 * 自动编码处理
	 *
	 * @param req
	 * @return
	 */
	private static HttpServletRequest encodeRequest(HttpServletRequest req) {
		if (req instanceof RequestProxy)
			return req;
		HttpServletRequest autoEncodingReq = req;
		if ("POST".equalsIgnoreCase(req.getMethod())) {
			try {
				autoEncodingReq.setCharacterEncoding(UTF_8);
			} catch (UnsupportedEncodingException e) {
			}
		}

		return autoEncodingReq;
	}

	/**
	 * @return the maxSize
	 */
	public static int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public static void setMaxSize(int maxSize) {
		WebContext.maxSize = maxSize;
	}

	/**
	 * 获取当前请求的上下文
	 *
	 * @return
	 */
	public static WebContext get() {
		return CONTEXTS.get();
	}

	public ServletContext getContext() {
		return context;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public HttpSession getSession() {
		return session;
	}

	public HttpSession getSession(boolean create) {
		return (session == null && create) ? (session = request.getSession()) : session;
	}

	public String getQueryString() {
		return request.getQueryString();
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getParams() {
		return request.getParameterNames();
	}

	public String getParam(String name) {
		return request.getParameter(name);
	}

	public String getParam(String name, String... defValue) {
		String v = request.getParameter(name);
		return (v != null) ? v : ((defValue.length > 0) ? defValue[0] : null);
	}

	public long getParam(String name, long defValue) {
		return NumberUtils.toLong(getParam(name), defValue);
	}

	public int getParam(String name, int defValue) {
		return NumberUtils.toInt(getParam(name), defValue);
	}

	public byte getParam(String name, byte defValue) {
		return NumberUtils.toByte(getParam(name), defValue);
	}

	public String[] getParams(String name) {
		return request.getParameterValues(name);
	}

	public Object getRequestAttr(String name) {
		HttpServletRequest request = getRequest();
		return (request != null) ? request.getAttribute(name) : null;
	}

	public void setRequestAttr(String key, Object value) {
		request.setAttribute(key, value);
	}

	public Object getSessionAttr(String name) {
		HttpSession ssn = getSession();
		return (ssn != null) ? ssn.getAttribute(name) : null;
	}

	public void setSessionAttr(String key, Object value) {
		HttpSession ssn = getSession(true);
		ssn.setAttribute(key, value);
	}

	public Cookie getCookie(String name) {
		return cookies.get(name);
	}

	public void setCookie(String name, String value, int maxAge, boolean allSubDomain) {
		RequestUtils.setCookie(request, response, name, value, maxAge, allSubDomain);
	}

	public void deleteCookie(String name, boolean all_domain) {
		RequestUtils.deleteCookie(request, response, name, all_domain);
	}

	public String getHeader(String name) {
		return request.getHeader(name);
	}

	public void setHeader(String name, String value) {
		response.setHeader(name, value);
	}

	public void setHeader(String name, int value) {
		response.setIntHeader(name, value);
	}

	public void setHeader(String name, long value) {
		response.setDateHeader(name, value);
	}

	public void forward(String uri) throws ServletException, IOException {
		RequestDispatcher rd = context.getRequestDispatcher(uri);
		rd.forward(request, response);
	}

	public void redirect(String uri) throws IOException {
		response.sendRedirect(uri);
	}

	public void include(String uri) throws ServletException, IOException {
		RequestDispatcher rd = context.getRequestDispatcher(uri);
		rd.include(request, response);
	}

	public static boolean isMultipart(HttpServletRequest request) {
		return ((request.getContentType() != null) &&
				(request.getContentType().toLowerCase().startsWith("multipart")));
	}

	public boolean isUpload() {
		return (request instanceof MultipartRequest);
	}

	public boolean isRobot() {
		return RequestUtils.isRobot(request);
	}

	public long getId() {
		return getParam("id", 0L);
	}

	public String getIp() {
		return RequestUtils.getRemoteAddr(request);
	}

	public File getFile(String fieldName) {
		if (request instanceof MultipartRequest)
			return ((MultipartRequest) request).getFile(fieldName);
		return null;
	}

	public File getImage(String fieldname) {
		File imgFile = getFile(fieldname);
		return (imgFile != null && Multimedia.isImageFile(imgFile.getName())) ? imgFile : null;
	}

	public String getServletPath() {
		return request.getServletPath();
	}

	public String getURI() {
		return request.getRequestURI();
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	public static String getWebrootPath() {
		String root = WebContext.class.getResource("/").getFile();
		try {
			root = new File(root).getParentFile().getParentFile().getCanonicalPath();
			root += File.separator;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return root;
	}

	/**
	 * 从cookie中获取locale信息
	 * @return
	 */
	public Locale getLocale() {
		if (locale != null) {
			return locale;
		}

		Cookie cookie = getCookie(LOCALE);
		if (cookie != null) {
			return LocaleUtils.toLocale(cookie.getValue());
		}

		return request.getLocale();
	}

	public void saveLocale(String localeValue) {
		saveLocale(localeValue, MAX_AGE);
	}

	/**
	 * 将local信息存入cookie
	 * @param localeValue
	 */
	public void saveLocale(String localeValue, int maxAge) {
		if (localeValue != null) {
			try {
				locale = LocaleUtils.toLocale(localeValue);
				response.setLocale(locale);// 放入请求
				setCookie(LOCALE, locale.getLanguage() + "_" + locale.getCountry(), maxAge, true);
			} catch (Exception e) {
				log.warn("setLocale is error, localeValue=" + localeValue + "is not used.");
			}
		}
	}

	/**
	 * 从url参数中获取locale并存入cookie
	 */
	public void saveLocale() {
		String requestLocale = getParam(REQUEST_LOCALE_PARAM);
		saveLocale(requestLocale);
	}

	/**
	 * 输出信息到浏览器
	 *
	 * @param msg
	 * @throws java.io.IOException
	 */
	public void print(Object msg) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		if (!UTF_8.equalsIgnoreCase(response.getCharacterEncoding()))
			response.setCharacterEncoding(UTF_8);
		response.getWriter().print(msg);
	}

	public void printJson(String[] key, Object[] value) throws IOException {
		StringBuilder json = new StringBuilder("{");
		for (int i = 0; i < key.length; i++) {
			if (i > 0)
				json.append(',');
			boolean isNum = value[i] instanceof Number;
			json.append("\"");
			json.append(key[i]);
			json.append("\":");
			if (!isNum)
				json.append("\"");
			json.append(value[i]);
			if (!isNum)
				json.append("\"");
		}
		json.append("}");
		print(json.toString());
	}

	public void printJson(String key, Object value) throws IOException {
		printJson(new String[] { key }, new Object[] { value });
	}
	
	public void notFound() throws IOException {
		error(HttpServletResponse.SC_NOT_FOUND);
	}

	public void error(int code, String... msg) throws IOException {
		if (msg.length > 0)
			response.sendError(code, msg[0]);
		else
			response.sendError(code);
	}

	public void forbidden() throws IOException {
		error(HttpServletResponse.SC_FORBIDDEN);
	}

	public void closeCache() {
		setHeader("Pragma", "No-cache");
		setHeader("Cache-Control", "no-cache");
		setHeader("Expires", 0L);
	}

	public ActionException fromResource(String bundle, String key, Object... args) {
		String res = ResourceUtils.getStringForLocale(request.getLocale(), bundle, key, args);
		return new ActionException(res);
	}
	
	/**
	 * 将HTTP请求参数映射到bean对象中
	 * 
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public <T> T convertBean(Class<T> beanClass) {
		try {
			T bean = beanClass.newInstance();
			BeanUtils.populate(bean, request.getParameterMap());
			return bean;
		} catch (Exception e) {
			throw new ActionException(e.getMessage());
		}
	}
	
	public void populate(Object bean) throws IllegalAccessException, InvocationTargetException {
		BeanUtils.populate(bean, request.getParameterMap());
	}
	
	/**
	 * 去除contextPath的URI
	 * @return
	 */
	public String getURIAndExcludeContextPath() {
		if (getContextPath().equals("")) {
			return getURI();
		} else {
			String t = StringUtils.substringAfter(getURI(), getContextPath());
			if (!t.startsWith("/")) {
				t = "/" + t;
			}
			return t;
		}
	}
	
	/**
	 * 从cookie中读取保存的用户信息
	 * 
	 * @return
	 */
	public IUser getUserFromCookie() {
		try {
			Cookie cookie = getCookie(COOKIE_LOGIN);
			if (cookie != null && StringUtils.isNotBlank(cookie.getValue())) {
				return getUserByUUID(cookie.getValue());
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 保存登录信息
	 * 
	 * @param user
	 * @param save
	 */
	public void saveUserInCookie(IUser user, boolean save) {
		String newValue = genLoginKey(user, getIp(), getHeader("user-agent"));
		int maxAge = save ? MAX_AGE : -1;
		deleteCookie(COOKIE_LOGIN, true);
		setCookie(COOKIE_LOGIN, newValue, maxAge, true);
	}

	/**
	 * 删除登录信息
	 */
	public void deleteUserFromCookie() {
		deleteCookie(COOKIE_LOGIN, true);
	}

	/**
	 * 从cookie中读取保存的用户信息
	 * 
	 * @param uuid
	 * @return
	 */
	public IUser getUserByUUID(String uuid) {
		if (StringUtils.isBlank(uuid))
			return null;
		String ck = decrypt(uuid);
		final String[] items = StringUtils.split(ck, '|');
		if (items.length == 5) {
			String ua = getHeader("user-agent");
			int uaCode = (ua == null) ? 0 : ua.hashCode();
			int oldUaCode = Integer.parseInt(items[3]);
			if (uaCode == oldUaCode) {
				return new IUser() {
					public boolean isBlocked() {
						return false;
					}

					public long getId() {
						return NumberUtils.toLong(items[0], -1L);
					}

					public String getPassword() {
						return items[1];
					}

					public byte getRole() {
						return IUser.ROLE_GENERAL;
					}
				};
			}
		}
		return null;
	}
	
	/**
	 * 生成用户登录标识字符串
	 * 
	 * @param user
	 * @param ip
	 * @param userAgent
	 * @return
	 */
	public static String genLoginKey(IUser user, String ip, String userAgent) {
		StringBuilder sb = new StringBuilder();
		sb.append(user.getId());
		sb.append('|');
		sb.append(user.getPassword());
		sb.append('|');
		sb.append(ip);
		sb.append('|');
		sb.append((userAgent == null) ? 0 : userAgent.hashCode());
		sb.append('|');
		sb.append(System.currentTimeMillis());
		return encrypt(sb.toString());
	}

	/**
	 * 加密
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String value) {
		byte[] data = CryptUtils.encrypt(value.getBytes(), E_KEY);
		try {
			return URLEncoder.encode(new String(Base64.encodeBase64(data)), UTF_8);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 解密
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String value) {
		try {
			value = URLDecoder.decode(value, UTF_8);
			if (StringUtils.isBlank(value))
				return null;
			byte[] data = Base64.decodeBase64(value.getBytes());
			return new String(CryptUtils.decrypt(data, E_KEY));
		} catch (UnsupportedEncodingException excp) {
			return null;
		}
	}
	
	/**
	 * 自动解码
	 * 
	 */
	private static class RequestProxy extends HttpServletRequestWrapper {
		private String uriEncoding;

		RequestProxy(HttpServletRequest request, String encoding) {
			super(request);
			this.uriEncoding = encoding;
		}

		/**
		 * 重载getParameter
		 */
		public String getParameter(String paramName) {
			String value = super.getParameter(paramName);
			return decodeParamValue(value);
		}

		/**
		 * 重载getParameterMap
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Map<String, Object> getParameterMap() {
			Map params = super.getParameterMap();
			HashMap<String, Object> newParams = new HashMap<String, Object>();
			Iterator<String> iter = params.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				Object oValue = params.get(key);
				if (oValue.getClass().isArray()) {
					String[] values = (String[]) params.get(key);
					String[] new_values = new String[values.length];
					for (int i = 0; i < values.length; i++)
						new_values[i] = decodeParamValue(values[i]);

					newParams.put(key, new_values);
				} else {
					String value = (String) params.get(key);
					String newValue = decodeParamValue(value);
					if (newValue != null)
						newParams.put(key, newValue);
				}
			}
			return newParams;
		}

		/**
		 * 重载getParameterValues
		 */
		public String[] getParameterValues(String arg) {
			String[] values = super.getParameterValues(arg);
			for (int i = 0; values != null && i < values.length; i++)
				values[i] = decodeParamValue(values[i]);
			return values;
		}

		/**
		 * 参数转码
		 * 
		 * @param value
		 * @return
		 */
		private String decodeParamValue(String value) {
			if (StringUtils.isBlank(value) || StringUtils.isBlank(uriEncoding)
					|| StringUtils.isNumeric(value))
				return value;
			try {
				return new String(value.getBytes("8859_1"), uriEncoding);
			} catch (Exception e) {
			}
			return value;
		}
	}
}