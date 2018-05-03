package com.test.kafka;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import com.java.kj.kafka.Consumer;
import com.java.kj.kafka.Consumer.Action;
import com.java.kj.kafka.Producer;

public class Kafka {

	public static void main(String[] args) {
		String topic = "KJ_TEST";
		new Thread(new Runnable() {
			@Override
			public void run() {
				Properties props = new Properties();
				props.put("bootstrap.servers", "10.200.142.35:9090,10.200.142.36:9090,10.200.142.37:9090");
				props.put("acks", "0");
				props.put("retries", 0);
				props.put("batch.size", 16384);
				props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				Producer<String, String> producer = new Producer<String, String>(props);
				while (true) {
					producer.send(new ProducerRecord<String, String>(topic, "201803292024CORDER49316246"),
							new Callback() {
								@Override
								public void onCompletion(RecordMetadata metadata, Exception exception) {
									/* new Exception().printStackTrace(); */
									if (exception != null) {
										exception.printStackTrace();
									}
								}
							});
				}

			}

		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Properties props = new Properties();
				props.put("bootstrap.servers", "10.200.142.35:9090,10.200.142.36:9090,10.200.142.37:9090");
				props.put("group.id", "12");
				props.put("enable.auto.commit", "false");
				props.put("auto.commit.interval.ms", "1000");
				props.put("session.timeout.ms", "30000");
				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				AtomicInteger count = new AtomicInteger(0);
				new Consumer<String, String>(props, new Action<String, String>() {
					@Override
					public void doAction(ConsumerRecords<String, String> records) {
						for (ConsumerRecord<String, String> record : records) {
							System.out.println(
									"fetched from partition " + record.partition() + ", offset: " + record.offset()
											+ ", message: " + record.value() + ",count=" + count.incrementAndGet());
						}
					}
				}, topic);

			}

		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Properties props = new Properties();
				props.put("bootstrap.servers", "10.200.142.35:9090,10.200.142.36:9090,10.200.142.37:9090");
				props.put("group.id", "12");
				props.put("enable.auto.commit", "false");
				props.put("auto.commit.interval.ms", "1000");
				props.put("session.timeout.ms", "30000");
				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				AtomicInteger count = new AtomicInteger(0);
				new Consumer<String, String>(props, new Action<String, String>() {
					@Override
					public void doAction(ConsumerRecords<String, String> records) {
						for (ConsumerRecord<String, String> record : records) {
							System.out.println("Thread2:fetched from partition " + record.partition() + ", offset: "
									+ record.offset() + ", message: " + record.value() + ",count="
									+ count.incrementAndGet());
						}
					}
				}, topic);

			}

		}).start();
	}
}