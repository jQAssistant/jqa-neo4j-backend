package com.buschmais.jqassistant.neo4j.embedded;

import java.util.Collection;

import org.neo4j.dbms.api.DatabaseManagementService;

/**
 * Defines the interface for the server providing Neo4j and jQAssistant
 * functionality.
 */
public interface EmbeddedNeo4jServer {

    String getVersion();

    void initialize(DatabaseManagementService databaseManagementService, EmbeddedNeo4jConfiguration configuration, Collection<Class<?>> procedureTypes,
            Collection<Class<?>> functionTypes);

}
