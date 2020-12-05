package com.buschmais.jqassistant.neo4j.embedded;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Describes the configuration to apply to the embedded Neo4j server.
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EmbeddedNeo4jConfiguration {

    public static final String DEFAULT_LISTEN_ADDRESS = "localhost";
    public static final int DEFAULT_BOLT_PORT = 7687;
    public static final int DEFAULT_HTTP_PORT = 7474;

    @Default
    private boolean connectorEnabled = false;

    @Default
    private String listenAddress = DEFAULT_LISTEN_ADDRESS;

    @Default
    private Integer boltPort = DEFAULT_BOLT_PORT;

    @Default
    private Integer httpPort = DEFAULT_HTTP_PORT;

}
