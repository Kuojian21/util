package com.tool.kj;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KjZip {

    public static void unzip(String srcFile, String tarPath) throws IOException {
        File path = new File(tarPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        ZipFile file = new ZipFile(srcFile);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File entryFile = new File(path.getAbsoluteFile() + File.separator + entry.getName());
            if (entry.isDirectory()) {
                entryFile.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(entryFile));
            BufferedInputStream bis = new BufferedInputStream(file.getInputStream(entry));
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bis.close();
            bos.flush();
            bos.close();
        }
    }

}
