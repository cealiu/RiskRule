package com.rule.util;

import com.alibaba.fastjson.JSON;
import com.rule.dto.RuleObject;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class DroolsSink extends RichSinkFunction<RuleObject> {

	@Override
	public void invoke(RuleObject value, Context context) throws Exception {
		DroolsUtil.execRule(value);
		System.out.println(JSON.toJSONString(value));
	}

}
