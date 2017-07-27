package com.tools.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tools.io.IOTool;
import com.tools.logger.LogConstant;

public class ExcelTool {

	public static XSSFWorkbook empty() {
		return new XSSFWorkbook();
	}

	public static XSSFWorkbook get(InputStream is) throws IOException {
		return new XSSFWorkbook(is);
	}

	public static void write(XSSFWorkbook workbook, OutputStream os, List<List<String>> datas) {
		String module = "写入";
		try {
			XSSFSheet sheet = workbook.createSheet("sheet0");
			// percent style (%)
			XSSFCellStyle style = workbook.createCellStyle();
			style.setDataFormat(workbook.createDataFormat().getFormat("0%"));

			if (datas != null) {
				for (int i = 0; i < datas.size(); i++) {
					List<String> data = datas.get(i);
					if (data != null) {
						Row row = sheet.createRow(i);
						for (int j = 0; j < data.size(); j++) {
							row.createCell(j).setCellValue(data.get(j));
						}
					}
				}
			}
			// write to outputStream
			os.flush();
			workbook.write(os);
			os.close();
		} catch (IOException e) {
			LogConstant.runLog.error(module, "异常", e);
		} finally {
			close(workbook);
			IOTool.close(os);
		}

	}

	public static List<List<Object>> read(XSSFWorkbook workbook, int index) {
		List<List<Object>> datas = new ArrayList<List<Object>>();

		try {
			XSSFSheet sheet = workbook.getSheetAt(index);
			// read from excel
			for (Row row : sheet) {
				List<Object> line = new ArrayList<Object>();
				datas.add(line);
				for (Cell cell : row) {
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						line.add(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						line.add(cell.getCellFormula());
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						line.add(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
					case Cell.CELL_TYPE_BLANK:
					case Cell.CELL_TYPE_ERROR:
					default:
						line.add(cell.getStringCellValue());
					}
				}
			}
			return datas;
		} finally {
			close(workbook);
		}

	}

	public static void close(XSSFWorkbook workbook) {
		String module = "关闭";
		try {
			if (workbook != null) {
				workbook.close();
			}
		} catch (IOException e) {
			LogConstant.runLog.error(module, "异常", e);
		}
	}

}
