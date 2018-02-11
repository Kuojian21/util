package com.kafka.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

public class Kafka {

	public static void demoProduce() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "10.200.142.37:9097,10.200.142.38:9097,10.200.142.39:9097");
		props.put("acks", "0");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		producer.send(
				new ProducerRecord<String, String>("chargeReturnTopic_test20180209", "LM420A1CDA949D56D000", "2"));
		producer.close();
		
	}

	public static void demoConsume() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "10.200.142.37:9097,10.200.142.38:9097,10.200.142.39:9097");
		props.put("group.id", "12");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList("chargeReturnTopic_test20180209"));    
        consumer.seekToBeginning(new ArrayList<TopicPartition>());  
		int index = 0;
		while(index++ < 10) {
			ConsumerRecords<String, String> records = consumer.poll(1000);    
            for(ConsumerRecord<String, String> record : records) {    
                System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value());    
            } 
		}
		consumer.close();
	}

	public static void main(String[] args) {
		demoConsume();
		for(int i = 0 ; i < 10;i++) {
			demoProduce();
		}
	}
}