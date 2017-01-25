package com.jeromeloisel.db.integration.test;

import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.script.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

import static com.google.common.io.Files.createTempDir;
import static org.elasticsearch.env.NodeEnvironment.NODE_ID_SEED_SETTING;

@Configuration
class NodeTestConfig {

  @Bean(destroyMethod="close")
  Node newNode() throws NodeValidationException {
    final Path tempDir = createTempDir().toPath();
    final Settings settings = Settings.builder()
      .put(ClusterName.CLUSTER_NAME_SETTING.getKey(), new ClusterName("single-node-cluster" + System.nanoTime()))
      .put(Environment.PATH_HOME_SETTING.getKey(), tempDir)
      .put(Environment.PATH_REPO_SETTING.getKey(), tempDir.resolve("repo"))
      .put(Environment.PATH_SHARED_DATA_SETTING.getKey(), createTempDir().getParent())
      .put("node.name", "single-node")
      .put("script.inline", "true")
      .put("script.stored", "true")
      .put(ScriptService.SCRIPT_MAX_COMPILATIONS_PER_MINUTE.getKey(), 1000)
      .put(EsExecutors.PROCESSORS_SETTING.getKey(), 1)
      .put(NetworkModule.HTTP_ENABLED.getKey(), false)
      .put("discovery.type", "zen")
      .put("transport.type", "local")
      .put(Node.NODE_DATA_SETTING.getKey(), true)
      .put(NODE_ID_SEED_SETTING.getKey(), System.nanoTime())
      .build();
    return new Node(settings).start();
  }

  @Bean(destroyMethod = "close")
  @Autowired
  Client client(final Node node) {
    return node.client();
  }
}
