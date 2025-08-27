package com.interview.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public abstract class TestContainerBase {

    private static final Network NETWORK = Network.newNetwork();
    static KeycloakContainer keycloakContainer;

    static {
        keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:26.3.1")
                .withNetwork(NETWORK)
                .withNetworkAliases("keycloakdb")
                .withRealmImportFile("/tekmetric-realm.json")
                .withAdminUsername("admin")
                .withAdminPassword("admin")
                .withEnv("KC_DB", "dev-mem")
                .withExposedPorts(8080)
                .waitingFor(Wait.forListeningPort());
        keycloakContainer.start();
    }

    @BeforeAll
    static void beforeAll() {
        System.setProperty("com.c4-soft.springaddons.oidc.ops[0].iss", keycloakContainer.getAuthServerUrl() + "/realms/tekmetric");
        System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", keycloakContainer.getAuthServerUrl() + "/realms/tekmetric");
        System.setProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", keycloakContainer.getAuthServerUrl() + "/realms/tekmetric/protocol/openid-connect/certs");

    }

    public KeycloakContainer keycloakContainer() {
        return keycloakContainer;
    }

}
