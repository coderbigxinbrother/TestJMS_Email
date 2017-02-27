package com.yc.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import com.jspsmart.upload.File;
import com.jspsmart.upload.Files;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

public class FileUploadUtil {

	private long maxFileSize = 10000000;
	private long totalMaxFileSize = 10 * maxFileSize;
	private String allowedFilesList = "jpg,JPG,jpeg,JPEG,bmp,BMP,gif,GIF";
	private String deniedFilesList = "exe,doc,bat,js,sh";

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public long getTotalMaxFileSize() {
		return totalMaxFileSize;
	}

	public void setTotalMaxFileSize(long totalMaxFileSize) {
		this.totalMaxFileSize = totalMaxFileSize;
	}

	public String getAllowedFilesList() {
		return allowedFilesList;
	}

	public void setAllowedFilesList(String allowedFilesList) {
		this.allowedFilesList = allowedFilesList;
	}

	public String getDeniedFilesList() {
		return deniedFilesList;
	}

	public void setDeniedFilesList(String deniedFilesList) {
		this.deniedFilesList = deniedFilesList;
	}

	public Map<String, List<String>> fileUpload(PageContext pageContext,
			String uploadfilepath) throws Exception {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		SmartUpload su = new SmartUpload();
		// 初始化
		su.initialize(pageContext);
		su.setCharset("utf-8");

		// 1.限制每个上传文件的最大长度.
		su.setMaxFileSize(maxFileSize);
		// 2.限制总上传数据的长度
		su.setTotalMaxFileSize(totalMaxFileSize);
		// 3.设定允许上传的文件(通过扩展名限制)，仅允许doc、txt文件.
		su.setAllowedFilesList(allowedFilesList);
		try {
			// 4.设定禁止上传的文件(通过扩展名限制)，禁止上传带有exe、bat、jsp、htm、html扩展名的文件和没有扩展名的文件
			su.setDeniedFilesList(deniedFilesList);
			su.upload(); // 完成上传, 已经将图片数据传进servlet
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		Request req = su.getRequest();

		// 取出多个参数
		Enumeration enu = req.getParameterNames();
		while (enu.hasMoreElements()) {
			String pname = (String) enu.nextElement();
			// System.out.println( pname+"\t"+ req.getParameter(pname));
			String[] pvalues = req.getParameterValues(pname);

			List<String> pvalueslist = Arrays.asList(pvalues); // 将一个数组转为list
			map.put(pname, pvalueslist);
		}

		// 文件也有多个
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		Files files = su.getFiles();
		for (Object obj : files.getCollection()) {
			String filepath = uploadfilepath + java.io.File.separator; // 路径不能写死
			// , //
			File f = (File) obj;
			String newname = sdf.format(new Date()) + "." + f.getFileExt();
			filepath += newname;
			try {
				f.saveAs(filepath, SmartUpload.SAVE_VIRTUAL);

				String fieldname = f.getFieldName(); // 上传的文件的字段名
				String oldfilename = f.getFileName();

				List<String> filelist = new ArrayList<String>();
				filelist.add(oldfilename);
				filelist.add(newname);

				map.put(fieldname, filelist);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

}
