package com.hello.riskControl.drools;

import com.hello.constants.Constants;
import com.hello.utils.GwDataStruct;
import com.hello.utils.GwDataStructService;
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
        if(!CompareMap.compareMapByEntrySet(RULE,newRule)){
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
        else {
            return false;
        }
    }

    public static Map RULE = new HashMap();

    public static void updateRule(){
        try {
            Map newRule = getNewRule();
            if (newRule != null ) {
                boolean isGoodRule=initKieSession(newRule);
                if(isGoodRule){
                    GwDataStructService gwDataStructService = new GwDataStructService();

                    try{
                        Class global = kieSessions.getGlobal("gwDataStructService").getClass();
                    }
                    catch (Exception e){
                        kieSessions.setGlobal("gwDataStructService", gwDataStructService);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map getNewRule() throws ClassNotFoundException, SQLException, UnsupportedEncodingException{
        Class.forName("org.postgresql.Driver");
        Connection connection= DriverManager.getConnection(Constants.POSTGREURL(),Constants.POSTGREUSER(), Constants.POSTGREPASSWORD());
//        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/riskcontrol", "dbuser", "dbuser@cys");
        String sql = Constants.POSTGRESQL();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Map rule = new HashMap();
        while (resultSet.next()) {
            rule.put( resultSet.getString("rulename"), resultSet.getString("rulecontent"));
        }
        statement.close();
        connection.close();
        return rule;
    }

    public static void execRule(GwDataStruct gwDataStruct){
        FactHandle ss = kieSessions.insert(gwDataStruct);
        kieSessions.fireAllRules();
        kieSessions.delete(ss);
    }

    public static KieSession getKieSessions(){
        try {
            Map<String,String> newRule = getNewRule();
            if (newRule != null ) {

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
                }
                kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());

                KieBaseConfiguration kieBaseConf = kieServices.newKieBaseConfiguration();
                kieBaseConf.setOption(MultithreadEvaluationOption.YES);
                kieBaseConf.setOption(MaxThreadsOption.get(10));

                kieBase = kieContainer.newKieBase(kieBaseConf);
               KieSession kieSession = kieBase.newKieSession();

                GwDataStructService gwDataStructService = new GwDataStructService();

                try{
                    Class global = kieSession.getGlobal("gwDataStructService").getClass();
                }
                catch (Exception e){
                    kieSession.setGlobal("gwDataStructService", gwDataStructService);
                }
                return kieSession;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws Exception {

        while (true){
            DroolsUtil.updateRule();
            Thread.sleep(1000*6);
        }
    }

}
