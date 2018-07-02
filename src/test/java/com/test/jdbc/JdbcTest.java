package com.test.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.google.common.collect.Maps;

import oracle.jdbc.driver.OracleDriver;

public class JdbcTest {

	public static byte[] b = new byte[1024*1024*128];

	public static void main(String[] args) {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		Lock w = lock.writeLock();
		Lock r = lock.readLock();
		DataSource dataSource = new SimpleDriverDataSource(new OracleDriver(),
				"jdbc:oracle:thin:@10.200.142.13:1521:devdb", "finance", "finance_qa163test");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		long b = System.currentTimeMillis();
		for(int i = 0 ; i <= 10;i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					List<Map<String,Object>> list = jdbcTemplate.query("select * from tb_plus_receive_code", new RowMapper<Map<String,Object>>() {
						@Override
						public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
							Map<String,Object> result = Maps.newHashMap();
							ResultSetMetaData rsmd = rs.getMetaData();
							for(int i = 1; i <=rsmd.getColumnCount();i++) {
								result.put(rsmd.getColumnName(i), rs.getObject(i));
							}
							return result;
						}
					});
					r.lock();
					w.lock();
					System.out.println(list);
				}
			}).start();

			if(i % 100 == 0) {
				System.out.println(i);
			}
		}
		System.out.println(System.currentTimeMillis() - b);
		r.lock();
		w.lock();
	}

}
