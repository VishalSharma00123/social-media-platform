package com.message_service.message_service.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers((Connector connector) -> {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

                // ✅ Disable SO_LINGER to fix macOS socket issue
                protocol.setConnectionLinger(-1);

                // ✅ Additional optimizations
                protocol.setMaxKeepAliveRequests(100);
                protocol.setKeepAliveTimeout(20000);
                protocol.setConnectionTimeout(20000);

                System.out.println("═══════════════════════════════════════");
                System.out.println("✓ Tomcat Custom Configuration Applied");
                System.out.println("  - ConnectionLinger: " + protocol.getConnectionLinger());
                System.out.println("  - KeepAliveTimeout: " + protocol.getKeepAliveTimeout());
                System.out.println("═══════════════════════════════════════");
            });
        };
    }
}