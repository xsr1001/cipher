
package com.cdcoder.core.handler;


import com.cdcoder.core.mvc.WebContext;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: 异常处理
 * @Package com.cdcoder.core.cache
 * @Description: ${todo}
 * @date 2015/3/22 20:25
 */

public interface ExceptionHandler {
	String handle(WebContext rc, Exception exception);
}
