package com.cdcoder.core.handler;


import com.cdcoder.core.mvc.WebContext;
import com.cdcoder.core.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: 异常处理
 * @Package com.cdcoder.core.cache
 * @Description: ${todo}
 * @date 2015/3/22 20:35
 */
public class SimpleExceptionHandler implements ExceptionHandler {
	private static final Logger log = LoggerFactory
			.getLogger(SimpleExceptionHandler.class);

	protected static final String ERROR_PAGE = "500";

	/**
	 * 
	 * @param rc
	 * @param exception
	 * @return
	 * @see  ExceptionHandler#handle( WebContext,
	 *      Exception)
	 */
	@Override
	public String handle(WebContext rc, Exception exception) {
		log.error(Exceptions.getStackTraceAsString(exception));

		rc.setRequestAttr("exception", exception);
		rc.setRequestAttr("errorMsg", exception.getCause().getMessage());
		return ERROR_PAGE;
	}

}
