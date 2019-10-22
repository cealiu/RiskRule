package com.hello.riskControl.drools;

import java.util.Map;

public class CompareMap {

    public static boolean compareMapByKeySet(Map<String,String> map1, Map<String,String> map2){

        if(map1.size()!=map2.size()){
            return false;
        }


        String tmp1;
        String tmp2;
        boolean b=false;

        for(String key:map1.keySet()){
            if(map2.containsKey(key)){
                tmp1=map1.get(key);
                tmp2=map2.get(key);

                if(null!=tmp1 && null!=tmp1){

                    if(tmp1.equals(tmp2)){
                        b=true;
                        continue;
                    }else{
                        b=false;
                        break;
                    }

                }else if(null==tmp1 && null==tmp2){
                    b=true;
                    continue;
                }else{
                    b=false;
                    break;
                }


            }else{
                b=false;
                break;
            }

        }

        return b;
    }


    public static boolean compareMapByEntrySet(Map<String,String> map1,Map<String,String> map2){

        if(map1.size()!=map2.size()){
            return false;
        }

        String tmp1;
        String tmp2;
        boolean b=false;

        for(Map.Entry<String, String> entry:map1.entrySet()){
            if(map2.containsKey(entry.getKey())){
                tmp1=entry.getValue();
                tmp2=map2.get(entry.getKey());

                if(null!=tmp1 && null!=tmp1){   //都不为null
                    if(tmp1.equals(tmp2)){
                        b=true;
                        continue;
                    }else{
                        b=false;
                        break;
                    }
                }else if(null==tmp1 && null==tmp2){  //都为null
                    b=true;
                    continue;
                }else{
                    b=false;
                    break;
                }
            }else{
                b=false;
                break;
            }
        }

        return b;
    }
}
