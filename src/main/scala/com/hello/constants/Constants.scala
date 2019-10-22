package com.hello.constants

import com.hello.utils.PropertiesLoad.{loadFilePath,getString}
object Constants {
  loadFilePath()


  val METADATA_BROKER_SOAINFOR_LIST = "metadata.broker.soainfo.list"
  val KAFKA_SOAINFOR_TOPICS="kafka.soainfo.topics"
  val KAFKA_SOAINFOR_GROPID="kafka.soainfo.groupid"
  val KAFKA_USERGW_GROPID="kafka.metrics-userGw.groupid"

  val KAFKA_USERGW_HOUR="kafka.metrics-userGw.hour"
  val KAFKA_USERGW_MINUTE="kafka.metrics-userGw.minute"
  val KAFKA_USERGW_SECOND="kafka.metrics-userGw.second"

  val METADATA_BROKER_GATEWAY_LIST ="metadata.broker.gateway.list"
  val KAFKA_HTTP_TOPICS="kafka.gateway.topics.http"

  val MONGODBHOST = getString("mongodb.host")

  val REDISHOST = getString("redis.host")
  val REDISPASSWORD = getString("redis.password")

  val POSTGRESQL = getString("postgre.sql")
  val POSTGREURL = getString("postgre.url")
  val POSTGREUSER = getString("postgre.user")
  val POSTGREPASSWORD = getString("postgre.password")

  val RULERESULTTOPIC = getString("kafka.ruleresult.topic")
  val BOOTSTRAP_SERVERS = getString("metadata.broker.gateway.list")



  //elasticsearch
  val ES_NDOES = "es.ndoes"
  val ES_PORT = "es.port"
  val ES_HOST = getString("ES.host")

  //zookeeper Cluster
  val ZOOKEEPER_LIST = "zookeeper.list"

  //spark task param
  val HDFS_RISKCONTROL_FILE_PATH = "hdfs.riskcontrol.file.path"

  val DINGURL="dingtalk.webhook"

}
