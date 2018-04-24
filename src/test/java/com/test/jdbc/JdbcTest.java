package com.test.jdbc;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import oracle.jdbc.driver.OracleDriver;

public class JdbcTest {

	public static void main(String[] args) {
		DataSource dataSource = new SimpleDriverDataSource(new OracleDriver(),
				"jdbc:oracle:thin:@10.200.142.13:1521:devdb", "finance", "finance_qa163test");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		long b = System.currentTimeMillis();
		for(int i = 0 ; i <= 10000;i++) {
			jdbcTemplate.execute("select * from tb_plus_receive_code where id = 'jxssngymfvibec'");
			if(i % 100 == 0) {
				System.out.println(i);
			}
		}
		System.out.println(System.currentTimeMillis() - b);
	}

}
