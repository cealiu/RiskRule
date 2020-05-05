package com.rule;

import com.rule.util.DroolsSink;
import com.rule.util.DroolsUtil;
import com.rule.util.JedisPoolUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import redis.clients.jedis.Jedis;

import java.util.Properties;

public class RiskRule {

	public static void main(String[] args) throws Exception{
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");

		FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), properties);
		//从最早开始消费
		consumer.setStartFromEarliest();
		DataStream<String> stream = env
				.addSource(consumer);
//		stream.print();
		//stream.map();
		Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
		if(Boolean.parseBoolean(jedis.get("isUpdateRule"))){
			DroolsUtil.updateRule();
		}
		stream.addSink(new DroolsSink());
		env.execute();
	}
}
