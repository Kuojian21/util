package com.tools.document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.stream.FileImageOutputStream;

import com.google.common.collect.Lists;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfUtils {

	/**
	 * 合并
	 */
	public static void merge(String[] files, OutputStream os, boolean osc) {
		try {
			Document document = null;
			PdfCopy copy = null;
			for (String file : files) {
				PdfReader reader = new PdfReader(file);
				if (document == null) {
					document = new Document(reader.getPageSize(0));
					copy = new PdfCopy(document, os);
					document.open();
				}
				// reader.selectPages();
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
				reader.close();
			}
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			if (osc) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 分割
	 */
	public static void split(InputStream is, String path, String prefix, int count) {
		try {
			PdfReader reader = new PdfReader(is);
			int n = reader.getNumberOfPages();
			int size = n / count;
			if (n < count) {
				size = 1;
				count = n;
			}

			path = path + "/";
			for (int i = 0; i < count; i++) {
				Document document = new Document(reader.getPageSize(1));
				PdfCopy copy = new PdfCopy(document, new FileOutputStream(path + prefix + suffix(i)
						+ ".pdf"));
				document.open();
				for (int j = size * i + 1, len = (i == count - 1 ? n : size * (i + 1)); j <= len; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
				document.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private static String suffix(int i) {
		String s = "0000" + i;
		return s.substring(s.length() - 5);
	}

	public static void pdf2Img(InputStream is, String path, String prefix) throws IOException {
		PdfReader pdfReader = new PdfReader(is);
		path = path + "/";
		for (int i = 1, len = pdfReader.getNumberOfPages(); i <= len; i++) {
			FileImageOutputStream fios = new FileImageOutputStream(new File(path + prefix
					+ suffix(i) + ".jpg"));
			fios.write(pdfReader.getPageContent(i));
			fios.close();
		}
	}

	public static void fillTemplate(InputStream template,
			Map<String, String> dataMap, ByteArrayOutputStream os) {
		try {
			PdfReader reader = new PdfReader(template);
			PdfStamper pdfStamper = new PdfStamper(reader, os);
			AcroFields acroFileds = pdfStamper.getAcroFields();
			//设置字体
			ArrayList<BaseFont> fontList = Lists.newArrayList();
			BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
					BaseFont.NOT_EMBEDDED);
			fontList.add(baseFont);
			acroFileds.setSubstitutionFonts(fontList);
			//填充数据
			for (Entry<String, String> entry : dataMap.entrySet()) {
				acroFileds.setField(entry.getKey(), entry.getValue());
			}
			pdfStamper.setFormFlattening(true);
			pdfStamper.close();
		} catch (Exception e) {
			
		} finally {

		}
	}

}
