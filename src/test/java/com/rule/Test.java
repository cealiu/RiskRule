package com.rule;

import com.alibaba.druid.util.DruidWebUtils;
import com.rule.dto.RuleObject;
import com.rule.util.DroolsUtil;

public class Test {

	public static void main(String[] args) throws Exception {
		RuleObject ruleObject = new RuleObject();
		ruleObject.setUserName("liuce");
		ruleObject.setAge(18);
		ruleObject.setSex("male");
		DroolsUtil.execRule(ruleObject);
		DroolsUtil.execRule(ruleObject);
		DroolsUtil.execRule(ruleObject);
	}
}
