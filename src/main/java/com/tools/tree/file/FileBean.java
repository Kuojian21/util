package com.tools.tree.file;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import com.tools.tree.Traverse;

public class FileBean {

	public static void searchFile(File file,String name){
		FileNode root = new FileNode(file){
			public boolean handle(String text){
				return text.indexOf(name) >= 0;
			}
			
			@Override
			public HANDLE_TYPE handleType() {
				return HANDLE_TYPE.NAME;
			}
		};
		Traverse.newInstantce().traverse(root);
	}
	
	public static void searchText(File file,String str){
		FileNode root = new FileNode(file){
			public boolean handle(String text){
				return text.indexOf(str) >= 0;
			}
			
			@Override
			public HANDLE_TYPE handleType() {
				return HANDLE_TYPE.TEXT;
			}
		};
		Traverse.newInstantce().traverse(root);
	}
	
	
	public static int count(File file){
		AtomicInteger count = new AtomicInteger(0);
		FileNode root = new FileNode(file){
			int lv = 0;
			
			@Override
			public void init(){
				lv = 0;
			}
			
			@Override
			public boolean handle(String text){
				lv++;
				return true;
			}
			
			@Override
			public HANDLE_TYPE handleType() {
				return HANDLE_TYPE.TEXT;
			}
			
			@Override
			public void after(){
				count.addAndGet(lv);
			}
		};
		Traverse.newInstantce().traverse(root);
		return count.get();
	}
	
}
