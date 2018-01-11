package org.spring.springboot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.spring.springboot.util.WebSocket.webSocketSet;
public class ConsumerKafka extends Thread {
	private String topic;
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerKafka.class);
	private KafkaConsumer<String,String> consumer;
	private String servers;
    public ConsumerKafka(){
    	
    }
    @Override
    public void run(){
        Properties props = new Properties();
        File file = new File("application.properties");
		try {
			FileInputStream in = new FileInputStream(file);
			props.load(in);
			topic = props.getProperty("bootstrap.kafka").trim();
			servers= props.getProperty("bootstrap.servers").trim();
		}catch(Exception e){
			LOGGER.info("加载配置文件出错");
		}
        props.put("bootstrap.servers", servers);
        props.put("group.id", "opengwsdk");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
       
        consumer = new KafkaConsumer<String,String>(props);
        consumer.subscribe(Arrays.asList(this.topic));
        while (true){
            try {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    for (WebSocket webSocket :webSocketSet){
                        webSocket.sendMessage(record.value());
                    }
                }
            }catch (IOException e){
            	LOGGER.info("获取kafka数据异常："+e.getMessage());
                continue;
            }
        }
    }

    public void close() {
        try {
            consumer.close();
        } catch (Exception e) {
        	LOGGER.info("关闭kafka出错",e.getMessage());
        }
    }

}
