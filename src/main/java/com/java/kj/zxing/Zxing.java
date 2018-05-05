package com.java.kj.zxing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class Zxing {

	private static final MultiFormatWriter WRITER = new MultiFormatWriter();

	private Zxing() {

	}

	public OutputStream matrix(String content, OutputStream out) {
		try {
			Map<EncodeHintType, Object> hints = Maps.newHashMap();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);
			BitMatrix bitMatrix = WRITER.encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "jpg", baos);
			return baos;
		} catch (WriterException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		new Zxing().matrix("https://www.lmlc.com", new FileOutputStream(new File("text.jpg")));
	}

}
