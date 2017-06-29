package com.netease.common.util;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarSearcherUtil {

	private File[] jarFileArray;
	HashMap<String, Object[]> hash = new HashMap<String, Object[]>();

	private JarSearcherUtil() {	}
	
	public JarSearcherUtil(File[] jarFileArray) {
		this.jarFileArray = jarFileArray;
	}

	public void searchClassFileName() {
		JarFile jarFile = null;
		try {
			for (int i = 0; i < jarFileArray.length; i++) {
				try {
					jarFile = new JarFile(jarFileArray[i]);
					searchClassFileNameToMap(jarFile);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					jarFile.close();
				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
	private void searchClassFileNameToMap(JarFile jarFile) {
		Enumeration<JarEntry> enumJar = jarFile.entries();
		String matchFileName, tmpStr;
		while (enumJar.hasMoreElements()) {
			matchFileName = String.valueOf(enumJar.nextElement());
			if (matchFileName.endsWith(".class")) {
				tmpStr = matchFileName.replace('/', '.');
				tmpStr = tmpStr.substring(0, tmpStr.length() - ".class".length());

				Object[] obj = hash.get(tmpStr);
				if (obj == null) {
					ArrayList<String> lst = new ArrayList<String>();
					lst.add(jarFile.getName());
					hash.put(tmpStr, new Object[] { new Integer(1), lst });
				} else {
					ArrayList<String> lst = (ArrayList<String>) obj[1];
					lst.add(jarFile.getName());
					hash.put(tmpStr, new Object[] { new Integer((Integer) obj[0] + 1), lst });
				}
			}

		}
	}

	public String getSearchResult() {
		StringBuilder str = new StringBuilder();
		Object[] key = (Object[]) hash.keySet().toArray();
		for (int i = 0; i < key.length; i++) {
			Object[] obj = hash.get(key[i].toString());
			if ((Integer) obj[0] > 1) {
				str.append(key[i].toString());
				str.append("\r\n");
				ArrayList<String> lst = (ArrayList<String>) obj[1];
				for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					str.append(string);
					str.append("\r\n");
				}
				str.append("--------------------------");
			}
		}
		return str.toString();
	}

}
