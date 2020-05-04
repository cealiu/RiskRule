package com.rule.util;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class DroolsSink extends RichSinkFunction {

	@Override
	public void invoke(Object value, Context context) throws Exception {
		
	}
}
