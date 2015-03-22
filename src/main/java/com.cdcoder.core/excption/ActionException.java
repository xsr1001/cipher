package com.cdcoder.core.excption;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: web的异常
 * @Package com.cdcoder.core.excption
 * @Description:
 * @date 2015/3/11 23:05
 */
public class ActionException extends BaseException {

	/** 描述  */
	private static final long serialVersionUID = -2340732188202382451L;

	/**  
	 *   
	 */ 
	public ActionException() {
		super();
	}

	/**  
	 * @param message
	 * @param cause  
	 */ 
	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**  
	 * @param message  
	 */ 
	public ActionException(String message) {
		super(message);
	}

	/**  
	 * @param cause  
	 */ 
	public ActionException(Throwable cause) {
		super(cause);
	}
}
