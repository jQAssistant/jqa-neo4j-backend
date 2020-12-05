package com.buschmais.jqassistant.neo4j.embedded.neo4jv4;

import java.util.Collection;

import com.buschmais.jqassistant.neo4j.embedded.EmbeddedNeo4jConfiguration;
import com.buschmais.jqassistant.neo4j.embedded.EmbeddedNeo4jServer;

import org.neo4j.common.DependencyResolver;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.exceptions.KernelException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Neo4jV4CommunityNeoServer implements EmbeddedNeo4jServer {

    public static final String STATIC_CONTENT_ROOT = "/jqassistant/";

    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jV4CommunityNeoServer.class);
    protected DatabaseManagementService databaseManagementService;
    protected EmbeddedNeo4jConfiguration embeddedNeo4jConfiguration;

    @Override
    public String getVersion() {
        return "4.x";
    }

    @Override
    public final void initialize(DatabaseManagementService databaseManagementService, EmbeddedNeo4jConfiguration configuration,
        Collection<Class<?>> procedureTypes, Collection<Class<?>> functionTypes) {
        this.databaseManagementService = databaseManagementService;
        this.embeddedNeo4jConfiguration = configuration;
        registerProceduresAndFunctions(procedureTypes, functionTypes);
    }

    private void registerProceduresAndFunctions(Collection<Class<?>> procedureTypes, Collection<Class<?>> functionTypes) {
        GraphDatabaseService graphDatabaseService = databaseManagementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
        GlobalProcedures procedures = ((GraphDatabaseAPI) graphDatabaseService).getDependencyResolver().resolveDependency(GlobalProcedures.class,
                DependencyResolver.SelectionStrategy.SINGLE);
        for (Class<?> procedureType : procedureTypes) {
            try {
                LOGGER.debug("Registering procedure class " + procedureType.getName());
                procedures.registerProcedure(procedureType);
            } catch (KernelException e) {
                LOGGER.warn("Cannot register procedure class " + procedureType.getName(), e);
            }
        }
        for (Class<?> functionType : functionTypes) {
            try {
                LOGGER.debug("Registering function class " + functionType.getName());
                procedures.registerFunction(functionType);
            } catch (KernelException e) {
                LOGGER.warn("Cannot register function class " + functionType.getName(), e);
            }
        }
    }

}
