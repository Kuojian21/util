package com.java.kj.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

public class KjConsumer<K, V> {

	private static final ExecutorService service = Executors.newCachedThreadPool();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					service.shutdown();
					service.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private KafkaConsumer<K, V> consumer = null;

	public KjConsumer(Properties props, Action<K, V> action, String... topic) {
		consumer = new KafkaConsumer<K, V>(props);
		consumer.subscribe(Arrays.asList(topic),new ConsumerRebalanceListener() {

			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				// TODO Auto-generated method stub
				
			}
			
		});
		consumer.seekToBeginning(new ArrayList<TopicPartition>());
		service.submit(new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						action.doAction(consumer.poll(500));
						consumer.commitAsync(new OffsetCommitCallback() {
							@Override
							public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets,
									Exception exception) {
								
							}
							
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				KjConsumer.this.consumer.close();
				KjConsumer.this.consumer.wakeup();
			}
		});
	}

	public static interface Action<K, V> {
		public void doAction(ConsumerRecords<K, V> records);
	}

}
