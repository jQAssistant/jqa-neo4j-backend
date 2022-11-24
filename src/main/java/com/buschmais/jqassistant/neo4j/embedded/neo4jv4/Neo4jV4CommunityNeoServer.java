package com.buschmais.jqassistant.neo4j.embedded.neo4jv4;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.buschmais.jqassistant.neo4j.embedded.EmbeddedNeo4jServer;
import com.buschmais.jqassistant.neo4j.embedded.configuration.Embedded;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastore;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.neo4j.common.DependencyResolver;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.exceptions.KernelException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Neo4jV4CommunityNeoServer implements EmbeddedNeo4jServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jV4CommunityNeoServer.class);
    private EmbeddedDatastore embeddedDatastore;

    private Embedded embedded;

    private ClassLoader classLoader;

    private Server server;

    @Override
    public String getVersion() {
        return "4.x";
    }

    @Override
    public final void initialize(EmbeddedDatastore embeddedDatastore, Embedded embedded, ClassLoader classLoader, Collection<Class<?>> procedureTypes,
        Collection<Class<?>> functionTypes) {
        this.embeddedDatastore = embeddedDatastore;
        this.embedded = embedded;
        this.classLoader = classLoader;
        registerProceduresAndFunctions(procedureTypes, functionTypes);
    }

    @Override
    public void start() {
        this.server = new Server(new InetSocketAddress(embedded.listenAddress(), embedded.httpPort()));
        WebAppContext rootContext = getWebAppContext("/", "browser/");
        WebAppContext pluginContext = getWebAppContext("/jqassistant", "META-INF/jqassistant-static-content/");
        server.setHandler(new HandlerCollection(rootContext, pluginContext));
        LOGGER.info("Starting HTTP server, Neo4j browser available at http://{}:{}.", embedded.listenAddress(), embedded.httpPort());
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Cannot start embedded server.", e);
        }
    }

    private WebAppContext getWebAppContext(String contextPath, String resourceRoot) {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(contextPath);
        Resource resource = Resource.newResource(classLoader.getResource(resourceRoot));
        webAppContext.setBaseResource(resource);
        return webAppContext;
    }

    @Override
    public void stop() {
        if (server != null) {
            try {
                LOGGER.info("Stopping HTTP server.");
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException("Cannot stop embedded server.", e);
            }
        }
    }

    private void registerProceduresAndFunctions(Collection<Class<?>> procedureTypes, Collection<Class<?>> functionTypes) {
        GraphDatabaseService graphDatabaseService = embeddedDatastore.getManagementService()
            .database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
        GlobalProcedures procedures = ((GraphDatabaseAPI) graphDatabaseService).getDependencyResolver()
            .resolveDependency(GlobalProcedures.class, DependencyResolver.SelectionStrategy.SINGLE);
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