package com.cdcoder.core.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author <a href="xsr1001@qq.com">sirun.xu</a>
 * @version V1.0
 *          <p></p>
 * @Title: 多媒体相关信息
 * @Package com.cdcoder.core.cache
 * @Description: ${todo}
 * @date 2015/3/21 21:25
 */

public abstract class Multimedia {

	public static int[] getImageSize(File img) throws IOException {
		BufferedImage bi = (BufferedImage) ImageIO.read(img);
		return new int[] { bi.getWidth(), bi.getHeight() };
	}

	/**
	 * 保存原图
	 * 
	 * @param img
	 * @param dest
	 * @return
	 * @throws java.io.IOException
	 */
	public static int[] saveImage(File img, String dest) throws IOException {
		File fileDest = new File(dest);
		if (!fileDest.getParentFile().exists())
			fileDest.getParentFile().mkdirs();
		String ext = FilenameUtils.getExtension(dest).toLowerCase();
		BufferedImage bi = (BufferedImage) ImageIO.read(img);
		return ImageIO.write(bi, ext.equals("png") ? "png" : "jpeg", fileDest) ? new int[] {
				bi.getWidth(), bi.getHeight() }
				: null;
	}

	public static class Size {
		public Size(int w, int h) {
			this.width = w;
			this.height = h;
		}

		public int width;
		public int height;
	}

	public static boolean isImageFile(String fn) {
		String ext = FilenameUtils.getExtension(fn);
		if (StringUtils.isBlank(ext))
			return false;
		return ArrayUtils.contains(IMG_EXTS, ext.toLowerCase());
	}

	public static boolean isAudioFile(String fn) {
		String ext = FilenameUtils.getExtension(fn);
		if (StringUtils.isBlank(ext))
			return false;
		return ArrayUtils.contains(AUDIO_EXTS, ext.toLowerCase());
	}

	public static boolean isVideoFile(String fn) {
		String ext = FilenameUtils.getExtension(fn);
		if (StringUtils.isBlank(ext))
			return false;
		return ArrayUtils.contains(VIDEO_EXTS, ext.toLowerCase());
	}

	public static boolean isFlashFile(String fn) {
		String ext = FilenameUtils.getExtension(fn);
		if (StringUtils.isBlank(ext))
			return false;
		return ArrayUtils.contains(FLASH_EXTS, ext.toLowerCase());
	}

	public static boolean isDocumentFile(String fn) {
		String ext = FilenameUtils.getExtension(fn);
		if (StringUtils.isBlank(ext))
			return false;
		return ArrayUtils.contains(DOCS_EXTS, ext.toLowerCase());
	}

	/**
	 * 所有支持的MIME TYPES,其他格式的图片不做支持
	 */
	public final static String[] IMG_EXTS = new String[] { "gif", "jpg",
			"jpeg", "png", "bmp" };

	/**
	 * 所有支持的视频MIME TYPES
	 */
	public final static String[] VIDEO_EXTS = new String[] { "avi", "rm",
			"3gp", "wmv", "mpg", "asf" };

	/**
	 * 所有支持的音频MIME TYPES
	 */
	public final static String[] AUDIO_EXTS = new String[] { "wma", "mp3",
			"arm", "mid", "aac", "imy" };

	/**
	 * Flash动画
	 */
	public final static String[] FLASH_EXTS = new String[] { "swf" };

	/**
	 * 文档类型
	 */
	public final static String[] DOCS_EXTS = new String[] { "txt", "htm",
			"html", "pdf", "doc", "rtf", "xls", "ppt" };

	public final static HashMap<String, String> MIME_TYPES = new HashMap<String, String>() {
		/** 描述 */
		private static final long serialVersionUID = 7133356034173876016L;

		{
			put("jar", "application/java-archive");
			put("jad", "text/vnd.sun.j2me.app-descriptor");
			put("sis", "application/vnd.symbian.install");
			put("sisx", "x-epoc/x-sisx-app");
			put("thm", "application/vnd.eri.thm");
			put("nth", "application/vnd.nok-s40theme");
			put("zip", "application/zip");
			put("rar", "application/octet-stream");
			put("cab", "application/octet-stream");

			put("gif", "image/gif");
			put("jpg", "image/jpeg");
			put("jpeg", "image/jpeg");
			put("png", "image/png");
			put("bmp", "image/bmp");

			put("avi", "video/x-msvideo");
			put("rm", "application/vnd.rn-realmedia");
			put("3gp", "video/3gpp");
			put("wmv", "video/x-ms-wmv");
			put("mpg", "video/mpg");
			put("asf", "video/x-ms-asf");
			put("flv", "video/x-flv");
			put("mp4", "video/mp4");

			put("wma", "audio/x-ms-wma");
			put("mp3", "audio/mp3");
			put("arm", "audio/amr");
			put("mid", "audio/x-midi");
			put("aac", "audio/aac");
			put("imy", "audio/imelody");

			put("swf", "application/x-shockwave-flash");

			put("txt", "text/plain");
			put("htm", "text/html");
			put("html", "text/html");
			put("pdf", "application/pdf");
			put("doc", "application/msword");
			put("rtf", "application/msword");
			put("docx", "application/msword");
			put("xls", "application/vnd.ms-excel");
			put("ppt", "application/vnd.ms-powerpoint");
			put("xlsx", "application/vnd.ms-excel");
			put("pptx", "application/vnd.ms-powerpoint");
			put("chm", "application/octet-stream");
		}
	};

}
