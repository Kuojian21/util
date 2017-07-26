package com.tools.zxing;

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

public class ZxingTool {

	public OutputStream image(String content, OutputStream out) {
		try {
			MultiFormatWriter writer = new MultiFormatWriter();
			Map<EncodeHintType, String> hints = Maps.newHashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);
			MatrixToImageWriter.writeToStream(bitMatrix, "jpg", out);
			return out;
		} catch (WriterException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
