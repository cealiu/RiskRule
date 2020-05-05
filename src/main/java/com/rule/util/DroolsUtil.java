package com.rule.util;


import com.rule.constant.Constants;
import com.rule.dto.RuleObject;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.MaxThreadsOption;
import org.kie.internal.conf.MultithreadEvaluationOption;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

//import org.kie.internal.KnowledgeBase;
//import org.drools.core.impl.KnowledgeBaseFactory;


public class DroolsUtil {
	//    static StatefulKnowledgeSession kieSession;
	static volatile KieSession kieSessions;
	static volatile KieContainer kieContainer;
	static volatile KieBase kieBase;


	public static boolean initKieSession(Map<String,String> newRule) {

		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kfs = kieServices.newKieFileSystem();
		for (Map.Entry<String, String> entry : newRule.entrySet()) {
			kfs.write("src/main/resources/rules/"+entry.getKey()+".drl", entry.getValue().getBytes());
		}
		KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
		Results results = kieBuilder.getResults();
		if (results.hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
			System.out.println(results.getMessages());
			System.out.println("-----------语法解析报错，drools规则更新无效------------------");
			return false;
		}
		kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());

		KieBaseConfiguration kieBaseConf = kieServices.newKieBaseConfiguration();
		kieBaseConf.setOption(MultithreadEvaluationOption.YES);
		kieBaseConf.setOption(MaxThreadsOption.get(100));
		kieBaseConf.setOption(EqualityBehaviorOption.EQUALITY);
//            kieBaseConf.setOption(EventProcessingOption.STREAM);
		kieBase = kieContainer.newKieBase(kieBaseConf);
//            KieSessionConfiguration kieSessionConfiguration = kieServices.newKieSessionConfiguration();
//            kieSessionConfiguration.setOption();
		kieSessions = kieBase.newKieSession();
		RULE = newRule;
		return true;

	}

	public static Map RULE = new HashMap();

	public static void updateRule(){
		try {
			Map newRule = getNewRule();
			if (newRule != null ) {
				boolean isGoodRule=initKieSession(newRule);
				if(!isGoodRule){
//					throw new Exception("update rule failed!");
					System.out.println("rule parse failed, run older rule!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map getNewRule() throws ClassNotFoundException, SQLException, UnsupportedEncodingException{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection= DriverManager.getConnection(Constants.MYSQLURL,Constants.MYSQLUSER, Constants.MYSQLPASSWORD);
		String sql = Constants.SQL;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		Map rule = new HashMap();
		while (resultSet.next()) {
			rule.put( resultSet.getString("rule_name"), resultSet.getString("rule_content"));
		}
		statement.close();
		connection.close();
		return rule;
	}

	public static void execRule(RuleObject ruleObject){
		FactHandle ss = kieSessions.insert(ruleObject);
		kieSessions.fireAllRules();
		kieSessions.delete(ss);
	}
}

