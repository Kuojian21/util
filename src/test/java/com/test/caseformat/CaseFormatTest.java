package com.test.caseformat;

import com.google.common.base.CaseFormat;

public class CaseFormatTest {
	public static void main(String[] args) {
		System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "tb_plus_receive_code"));
		System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "tbPlusReceiveCode"));
		System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "tb_plus_receive_code"));
		System.out.println(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, "TbPlusReceiveCode"));
		System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, "tb_plus_receive_code"));
	}
}
