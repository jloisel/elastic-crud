package com.jeromeloisel.db.integration.test;

import java.nio.file.Path;

import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.io.Files;

@Configuration
class NodeTestConfig {

  @Bean(destroyMethod="close")
  Node newNode() {
    final Path tempDir = Files.createTempDir().toPath();
    Node build = NodeBuilder.nodeBuilder().local(true).data(true).settings(Settings.builder()
        .put(ClusterName.SETTING, "single-node-cluster")
        .put("path.home", tempDir)
        .put("path.shared_data", tempDir.getParent())
        .put("node.name", "node")
        .put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, 1)
        .put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 0)
        .put("script.inline", "on")
        .put("script.indexed", "on")
        .put(EsExecutors.PROCESSORS, 1) // limit the number of threads created
        .put("http.enabled", false)
        .put(InternalSettingsPreparer.IGNORE_SYSTEM_PROPERTIES_SETTING, true) // make sure we get what we set :)
        ).build();
    build.start();
    return build;
  }
  
  @Bean
  @Autowired
  Client client(final Node node) {
    return node.client();
  }
}
