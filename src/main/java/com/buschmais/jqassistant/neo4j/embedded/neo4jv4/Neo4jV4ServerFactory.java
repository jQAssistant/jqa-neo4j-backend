package com.buschmais.jqassistant.neo4j.embedded.neo4jv4;

import java.util.Properties;

import com.buschmais.jqassistant.neo4j.embedded.EmbeddedNeo4jConfiguration;
import com.buschmais.jqassistant.neo4j.embedded.EmbeddedNeo4jServer;
import com.buschmais.jqassistant.neo4j.embedded.EmbeddedNeo4jServerFactory;
import com.buschmais.jqassistant.neo4j.embedded.extension.StaticContentResource;

import static com.buschmais.jqassistant.neo4j.embedded.neo4jv4.Neo4jV4CommunityNeoServer.STATIC_CONTENT_ROOT;

public class Neo4jV4ServerFactory implements EmbeddedNeo4jServerFactory {

    private static final String ALLOW_STORE_UPGRADE = "neo4j.allow_store_upgrade";
    private static final String KEEP_LOGICAL_LOGS = "neo4j.keep_logical_logs";

    private static final String DBMS_ALLOW_FORMAT_MIGRATION = "neo4j.dbms.allow_format_migration";
    private static final String DBMS_SECURITY_PROCEDURES_UNRESTRICTED = "neo4j.dbms.security.procedures.unrestricted";
    private static final String DBMS_TX_LOG_ROTATION_SIZE = "neo4j.dbms.tx_log.rotation.size";
    private static final String DBMS_TX_LOG_ROTATION_RETENTION_POLICY = "neo4j.dbms.tx_log.rotation.retention_policy";

    private static final String DBMS_CONNECTOR_BOLT_ENABLED = "neo4j.dbms.connector.bolt.enabled";
    private static final String DBMS_CONNECTOR_BOLT_LISTEN_ADDRESS = "neo4j.dbms.connector.bolt.listen_address";
    private static final String DBMS_CONNECTOR_HTTP_ENABLED = "neo4j.dbms.connector.http.enabled";
    private static final String DBMS_CONNECTOR_HTTP_LISTEN_ADDRESS = "neo4j.dbms.connector.http.listen_address";
    private static final String DBMS_UNMANAGED_EXTENSION_CLASSES = "neo4j.dbms.unmanaged_extension_classes";

    @Override
    public EmbeddedNeo4jServer getServer() {
        return new Neo4jV4CommunityNeoServer();
    }

    @Override
    public Properties getProperties(EmbeddedNeo4jConfiguration embedded) {
        Properties properties = new Properties();
        properties.setProperty(ALLOW_STORE_UPGRADE, Boolean.TRUE.toString());
        properties.setProperty(KEEP_LOGICAL_LOGS, Boolean.FALSE.toString());
        properties.setProperty(DBMS_ALLOW_FORMAT_MIGRATION, Boolean.TRUE.toString());
        properties.putIfAbsent(DBMS_SECURITY_PROCEDURES_UNRESTRICTED, "*");
        properties.putIfAbsent(DBMS_TX_LOG_ROTATION_SIZE, "50M");
        properties.putIfAbsent(DBMS_TX_LOG_ROTATION_RETENTION_POLICY, Boolean.FALSE.toString());

        if (embedded.isConnectorEnabled()) {
            properties.put(DBMS_CONNECTOR_HTTP_ENABLED, Boolean.TRUE.toString());
            properties.put(DBMS_CONNECTOR_HTTP_LISTEN_ADDRESS, embedded.getListenAddress() + ":" + embedded.getHttpPort());
            properties.put(DBMS_CONNECTOR_BOLT_ENABLED, Boolean.TRUE.toString());
            properties.put(DBMS_CONNECTOR_BOLT_LISTEN_ADDRESS, embedded.getListenAddress() + ":" + embedded.getBoltPort());
            properties.put(DBMS_UNMANAGED_EXTENSION_CLASSES, StaticContentResource.class.getPackage().getName() + "=" + STATIC_CONTENT_ROOT);
        }
        return properties;
    }
}
