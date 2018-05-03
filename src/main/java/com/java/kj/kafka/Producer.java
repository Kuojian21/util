package com.java.kj.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Producer<K, V> {

	private KafkaProducer<K, V> producer = null;

	public Producer(Properties props) {
		producer = new KafkaProducer<K, V>(props);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				producer.close();
			}
		});
	}
	
	public void send(ProducerRecord<K,V> record,Callback callback) {
		producer.send(record,callback);
	}

}
