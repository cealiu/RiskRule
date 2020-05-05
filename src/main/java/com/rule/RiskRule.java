package com.rule;

import com.alibaba.fastjson.JSON;
import com.rule.dto.RuleObject;
import com.rule.util.DroolsSink;
import com.rule.util.DroolsUtil;
import com.rule.util.JedisPoolUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import redis.clients.jedis.Jedis;

import java.util.Properties;

public class RiskRule {

	public static void main(String[] args) throws Exception{
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");

		FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("user", new SimpleStringSchema(), properties);
		//从最早开始消费
		consumer.setStartFromEarliest();
		SingleOutputStreamOperator<RuleObject> ruleObject = env.addSource(consumer).setParallelism(1)
				.map(string -> JSON.parseObject(string, RuleObject.class));
//		Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
//		jedis.select(0);
//		String isUpdateRule = jedis.get("isUpdateRule");
//		if(null!=isUpdateRule&&Boolean.parseBoolean(isUpdateRule)){
//			DroolsUtil.updateRule();
//			jedis.set("isUpdateRule","false");
//			JedisPoolUtil.returnResource(jedis);
//			System.out.println("更改成功!");
//		}

		ruleObject.addSink(new DroolsSink());
		env.execute();
	}
}
