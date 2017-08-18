package com.tools.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.tools.logger.LogConstant;

public class StreamTool {
	public static void close(InputStream in) {
		String module = "关闭输入流";
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				LogConstant.runLog.error(module, "异常", e);
			}
		}
	}
	
	public static void close(OutputStream out) {
		String module = "关闭输出流";
		if (out != null) {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				LogConstant.runLog.error(module, "异常", e);
			}
		}
	}
	
	public static BufferedInputStream buffer(InputStream in){
		if(in instanceof BufferedInputStream){
			return (BufferedInputStream)in;
		}
		return new BufferedInputStream(in);
	}

	public static BufferedOutputStream buffer(OutputStream out){
		if(out instanceof BufferedOutputStream){
			return (BufferedOutputStream)out;
		}
		return new BufferedOutputStream(out);
	}
	
	public static BufferedReader buffer(Reader reader){
		if(reader instanceof BufferedReader){
			return (BufferedReader)reader;
		}
		return new BufferedReader(reader);
	}
	
	
	public static InputStream toInputStream(byte[] bytes){
		return new ByteArrayInputStream(bytes);
	}
	
	public static InputStream toInputStream(File file){
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Reader toReader(InputStream in,String charset){
		try {
			return new InputStreamReader(in,charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void close(Reader in) {
		String module = "关闭输入流";
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				LogConstant.runLog.error(module, "异常", e);
			}
		}
	}
	
	public static void close(Writer out) {
		String module = "关闭输出流";
		if (out != null) {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				LogConstant.runLog.error(module, "异常", e);
			}
		}
	}
	
	public static Reader toReader(File file){
		try {
			return new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Writer toWriter(File file){
		try {
			return new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	

}
