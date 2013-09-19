package com.babyshow.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

	/**
	 * 解压文件到指定目录
	 * @param zipPath
	 * @param descDir
	 * @throws IOException
	 */
	public static void unZipFiles(String zipPath,String descDir)throws IOException{
		unZipFiles(new File(zipPath), descDir);
	}
	
	/**
	 * 解压文件到指定目录
	 * @param zipFile
	 * @param descDir
	 * @throws IOException
	 */
	public static void unZipFiles(File zipFile,String descDir)throws IOException{
		File pathFile = new File(descDir);
		
		// 如果目录不存在则创建
		if(!pathFile.exists()){
			pathFile.mkdirs();
		}
		
		// 开始解压
		ZipFile zip = new ZipFile(zipFile);
		for(Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ){
			ZipEntry entry = entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);
			String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");
			
			//判断路径是否存在,不存在则创建文件路径
			File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
			if(!file.exists()){
				file.mkdirs();
			}
			
			//判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
			if(new File(outPath).isDirectory()){
				continue;
			}
			
			// 写文件
			OutputStream out = new FileOutputStream(outPath);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		}
	}
}
