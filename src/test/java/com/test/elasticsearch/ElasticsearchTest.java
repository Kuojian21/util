package com.test.elasticsearch;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.beust.jcommander.internal.Maps;
import com.java.kj.snowflake.KjSnowflake;

import io.searchbox.annotations.JestId;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

public class ElasticsearchTest {

	private static KjSnowflake kj = new KjSnowflake(1);

	public static JestClient jestClient() {
		JestClientFactory factory = new JestClientFactory();
		HttpClientConfig.Builder builder = new HttpClientConfig.Builder("http://localhost:9200");
		builder.connTimeout(10000000);
		builder.readTimeout(10000000);
		factory.setHttpClientConfig(builder.build());
		return factory.getObject();
	}

	public static void createIndex() throws IOException {
		JestClient jestClient = jestClient();
		DeleteIndex deleteIndex = new DeleteIndex.Builder("news").build();
		JestResult result = jestClient.execute(deleteIndex);
		System.out.println(result);

		Map<String, String> id = Maps.newHashMap();
		id.put("type", "long");
		id.put("store", "true");
		id.put("index", "not_analyzed");
		Map<String, String> t = Maps.newHashMap();
		t.put("type", "integer");
		t.put("store", "true");
		t.put("index", "not_analyzed");
		Map<String, String> extra1 = Maps.newHashMap();
		extra1.put("type", "string");
		extra1.put("store", "true");
		extra1.put("index", "not_analyzed");
		Map<String, String> extra2 = Maps.newHashMap();
		extra2.put("type", "string");
		extra2.put("store", "true");
		extra2.put("index", "not_analyzed");
		Map<String, String> extra3 = Maps.newHashMap();
		extra3.put("type", "string");
		extra3.put("store", "true");
		extra3.put("index", "not_analyzed");
		Map<String, String> title = Maps.newHashMap();
		title.put("type", "string");
		title.put("store", "no");
		title.put("index", "analyzed");
		Map<String, String> content = Maps.newHashMap();
		content.put("type", "string");
		content.put("store", "no");
		content.put("index", "analyzed");
		Map<String, Object> parameters = Maps.newHashMap();
		parameters.put("id", id);
		parameters.put("title", title);
		parameters.put("content", content);
		parameters.put("type", t);
		parameters.put("extra1", extra1);
		parameters.put("extra2", extra2);
		parameters.put("extra3", extra3);
		Map<String, Object> type = Maps.newHashMap();
		type.put("properties", parameters);
		Map<String, Object> mapping = Maps.newHashMap();
		mapping.put("kj", type);
		CreateIndex createIndex = new CreateIndex.Builder("news").mappings(mapping).build();
		result = jestClient.execute(createIndex);
		System.out.println(result);
	}

	public static void batchInsert() throws IOException {
		JestClient jestClient = jestClient();
		for (int i = 0; i < 10000; i++) {
			Bulk.Builder bulkBuilder = new Bulk.Builder().defaultIndex("news").defaultType("kj");
			for (int j = 0; j < 100; j++) {
				News news = new News();
				news.setId(kj.nextId());
				news.setTitle(randomStr(j));
				news.setContent(randomStr(j * 10));
				news.setExtra1(100 - ThreadLocalRandom.current().nextInt(100) + "");
				news.setExtra2(ThreadLocalRandom.current().nextInt(100) + "");
				news.setExtra3(100 - j + "");
				bulkBuilder.addAction(new Index.Builder(news).build());
			}
			jestClient.execute(bulkBuilder.build());
			System.out.println("==========processing " + i);
		}
	}

	private static char[] STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

	public static String randomStr(int n) {
		StringBuilder builder = new StringBuilder();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		for (int i = 0; i < n; i++) {
			builder.append(STR[random.nextInt(62)]);
			if (i % 5 == 0) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}

	public static void main(String[] args) throws IOException {
		createIndex();
		batchInsert();
	}

	public static class News {
		@JestId
		private long id;
		private Integer type;
		private String title;
		private String content;
		private String extra1;
		private String extra2;
		private String extra3;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public Integer getType() {
			return type;
		}

		public String getExtra1() {
			return extra1;
		}

		public void setExtra1(String extra1) {
			this.extra1 = extra1;
		}

		public String getExtra2() {
			return extra2;
		}

		public void setExtra2(String extra2) {
			this.extra2 = extra2;
		}

		public String getExtra3() {
			return extra3;
		}

		public void setExtra3(String extra3) {
			this.extra3 = extra3;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

}
