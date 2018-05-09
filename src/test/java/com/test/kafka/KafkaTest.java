package com.test.kafka;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import com.java.kj.kafka.KjConsumer;
import com.java.kj.kafka.KjConsumer.Action;
import com.java.kj.kafka.KjProducer;

public class KafkaTest {

	public static Properties producerProps() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.241.130:9092,192.168.241.130:9093,192.168.241.130:9094");
//		props.put("bootstrap.servers", "10.200.142.35:9090,10.200.142.36:9090,10.200.142.37:9090");
		props.put("acks", "all");
		props.put("buffer.memory", 1024 * 1024 * 256);
		props.put("compression.type", "snappy");// gzip/lz4

//		props.put("client.id", "1");
		
		props.put("retries", 10);
		props.put("retry.backoff.ms", 300);

		props.put("batch.size", 1024 * 16);
		props.put("linger.ms", 300);

		props.put("max.in.flight.requests.per.connection", 10);
		props.put("timeout.ms", 100);
		props.put("request.timeout.ms", 3000);
		props.put("metadata.fetch.timeout.ms", 3000);
		props.put("max.block.ms", "300");
		props.put("max.request.size", 1024 * 1024);
		props.put("receive.buffer.bytes", -1);
		props.put("send.buffer.bytes", -1);

		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return (Properties) props.clone();
	}
	
	public static Properties conumerProps() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.241.130:9092,192.168.241.130:9093,192.168.241.130:9094");
//		props.put("bootstrap.servers", "10.200.142.35:9090,10.200.142.36:9090,10.200.142.37:9090");
		
//		props.put("client.id", "");
		
		props.put("fetch.min.bytes", 1024);
		props.put("fetch.max.wait.ms", 100);
		
		props.put("max.partition.fetch.bytes", 1024*4);
		
		props.put("session.timeout.ms", 3000);
		props.put("heartbeat.interval.ms", 1000);
		
		props.put("auto.offset.reset", "latest");//earliest
		
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", 100);

		props.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor");//org.apache.kafka.clients.consumer.RoundRobinAssignor
		props.put("max.poll.records", 100);
		
		
		props.put("receive.buffer.bytes", 100);
		props.put("send.buffer.bytes", -1);

		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return (Properties) props.clone();
	}
	
	public static void main(String[] args) {
		String topic = "KJ_TEST";
		Properties props = producerProps();
		KjProducer<String, String> producer = new KjProducer<String, String>(props);
		for(int i = 0;i < 2;i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						producer.send(new ProducerRecord<String, String>(topic, "201803292024CORDER49316246"),
								new Callback() {
									@Override
									public void onCompletion(RecordMetadata metadata, Exception exception) {
//										new Exception("kafka回调线程").printStackTrace();
										if (exception != null) {
											 exception.printStackTrace();
										}
									}
								});
					}

				}

			}).start();
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Properties props = new Properties();
				props.put("bootstrap.servers", "192.168.241.130:9092,192.168.241.130:9093,192.168.241.130:9094");
				props.put("group.id", "12");
				props.put("enable.auto.commit", "false");
				props.put("auto.commit.interval.ms", "1000");
				props.put("session.timeout.ms", "30000");
				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				AtomicInteger count = new AtomicInteger(0);
				new KjConsumer<String, String>(props, new Action<String, String>() {
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
				new KjConsumer<String, String>(props, new Action<String, String>() {
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