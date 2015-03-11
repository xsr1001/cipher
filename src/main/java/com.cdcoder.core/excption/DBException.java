package com.cdcoder.core.excption;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: cipher
 * @Package com.cdcoder.core.excption
 * @Description: ${todo}
 * @date 2015/3/11 23:09
 */
public class DBException extends BaseException {

    public DBException() {
    }


    public DBException(String message) {
        super(message);
    }


    public DBException(String message, Throwable cause) {
        super(message, cause);
    }


    public DBException(Throwable cause) {
        super(cause);
    }


    public DBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
