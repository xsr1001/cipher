
package com.cdcoder.core.mvc;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: 用户 接口
 * @Package com.cdcoder.core.cache
 * @Description: ${todo}
 * @date 2015/3/11 23:25
 */
public interface IUser {
	/**
	 * 最低权限
	 */
	byte ROLE_GENERAL = 0;
	
	/**
	 * 最高权限
	 */
	byte ROLE_TOP = Byte.MAX_VALUE;

	long getId();

	String getPassword();

	/**  
	 * @return  
	 */
	boolean isBlocked();

	/**  
	 * @return  
	 */
	byte getRole();
}
