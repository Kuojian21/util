package com.tools.tree.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.tools.io.StreamTool;
import com.tools.tree.EmptyNode;
import com.tools.tree.Node;

public abstract class FileNode implements Node, Cloneable {

	private File file;

	public FileNode() {

	}

	public FileNode(File file) {
		super();
		this.file = file;
	}

	@Override
	public boolean isLeaf() {
		return this.file.isFile();
	}

	@Override
	public Node[] getChilds() {
		try {
			if (this.isLeaf()) {
				return EmptyNode.getEmptyChilds();
			}
			File[] files = file.listFiles();
			int len = files.length;
			Node[] nodes = new FileNode[len];
			for (int i = 0; i < len; i++) {
				if (files[i].getParentFile().equals(this.file)) {
					FileNode node = (FileNode) this.clone();
					node.init();
					node.file = files[i];
					nodes[i] = node;
				} else {
					nodes[i] = EmptyNode.getEmpty();
				}
			}
			return nodes;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return EmptyNode.getEmptyChilds();
		}
	}

	public void init() {
		
	}

	@Override
	public final void compute() {
		this.compute(this.file);
		if (this.includeZip() && this.isZip(this.file.getName())) {
			ZipInputStream zis = null;
			try {
				zis = this.getZipInputStream();
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					this.compute(this.file.getCanonicalPath(), entry, zis);
					zis.closeEntry();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				StreamTool.close(zis);
			}
		}
	}

	@Override
	public void before() {
		
	}

	@Override
	public void after() {
		
	}
	
	public void compute(File file) {
		if (!this.filter(file.getName())) {
			return;
		}
		switch (this.handleType()) {
		case FILE:
			if (this.handle(file)) {
				this.printAbsolutePath(file.getAbsolutePath());
			}
			break;
		case NAME:
			if (this.handle(file.getName())) {
				this.printAbsolutePath(file.getAbsolutePath());
			}
			break;
		case STREAM: {
			if (file.isDirectory()) {
				break;
			}
			InputStream in = null;
			try {
				in = StreamTool.toInputStream(file);
				if(this.handle(in)){
					this.printAbsolutePath(file.getAbsolutePath());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				StreamTool.close(in);
			}
			break;
		}
		case TEXT: {
			try {
				if (file.isDirectory()) {
					break;
				}
				BufferedReader br = StreamTool.buffer(StreamTool.toReader(StreamTool.toInputStream(file),
						"UTF-8"));
				String line = null;
				boolean print = false;
				while ((line = br.readLine()) != null) {
					if(this.handle(line)){
						print = true;
					}
				}
				if(print){
					this.printAbsolutePath(file.getAbsolutePath());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			break;

		}
	}

	public void compute(String path, ZipEntry entry, ZipInputStream zis) {
		if (this.filter(entry.getName())) {
			switch (this.handleType()) {
			case FILE:
				if(this.handle(entry)){
					this.printAbsolutePath(path + "###" + entry.getName());
				}
				break;
			case NAME:
				if(this.handle(entry.getName())){
					this.printAbsolutePath(path + "###" + entry.getName());
				}
				break;
			case STREAM:
				if (file.isDirectory()) {
					break;
				}
				if(this.handle(zis)){
					this.printAbsolutePath(path + "###" + entry.getName());
				}
				break;
			case TEXT: {
				try {
					if (entry.isDirectory()) {
						break;
					}
					BufferedReader br = StreamTool.buffer(StreamTool.toReader(zis, "UTF-8"));
					String line = null;
					boolean print = false;
					while ((line = br.readLine()) != null) {
						if(this.handle(line)){
							print = true;
						}
					}
					if(print){
						this.printAbsolutePath(path + "###" + entry.getName());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			}
		}
		if (this.isZip(entry.getName())) {
			try {
				ZipInputStream sZis = this.getZipInputStream(entry, zis);
				ZipEntry sEntry = null;
				while ((sEntry = zis.getNextEntry()) != null) {
					this.compute(path + "###" + entry.getName(), sEntry, sZis);
					sZis.closeEntry();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean handle(File file) {
		return false;
	}

	public boolean handle(ZipEntry entry) {
		return false;
	}

	public boolean handle(String text) {
		return false;
	}

	public boolean handle(InputStream is) {
		return false;
	}

	public boolean handle(ZipInputStream zis) {
		try {
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				this.handle(entry);
				if (this.isZip(entry.getName())) {
					this.handle(this.getZipInputStream(entry, zis));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public abstract HANDLE_TYPE handleType();

	public boolean filter(String name) {
		return true;
	}

	public boolean includeZip() {
		return true;
	}

	public boolean isZip(String name) {
		return name.endsWith(".zip") || name.endsWith(".jar");
	}

	public ZipInputStream getZipInputStream() {
		try {
			if (file.getName().endsWith(".zip")) {
				return new ZipInputStream(new FileInputStream(file));
			} else if (file.getName().endsWith(".jar")) {
				return new JarInputStream(new FileInputStream(file));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ZipInputStream getZipInputStream(ZipEntry entry, ZipInputStream zis) {
		try {
			if (entry.getName().endsWith(".zip")) {
				return new ZipInputStream(zis);
			} else if (entry.getName().endsWith(".jar")) {
				return new JarInputStream(zis);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void printAbsolutePath(String str){
		System.out.println(str);
	}

	@Override
	public NODE_TYPE type() {
		return NODE_TYPE.A;
	}

	public enum HANDLE_TYPE {
		FILE, NAME, TEXT, STREAM;
	}

}
