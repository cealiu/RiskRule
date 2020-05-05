package com.rule.util;

import com.rule.dto.RuleObject;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class DroolsSink extends RichSinkFunction<RuleObject> {

	@Override
	public void invoke(RuleObject value, Context context) throws Exception {
		DroolsUtil.execRule(value);
	}
}
