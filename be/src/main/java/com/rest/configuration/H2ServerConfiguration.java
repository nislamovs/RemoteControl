package com.rest.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
//@Profile("development")
public class H2ServerConfiguration {
//
//    private static final String H2_TCP_PORT="9092";
//    private static final String H2_WEB_PORT="8093";
//
//    // TCP port for remote connections, default 9092
//    @Value("${h2.tcp.port:"+H2_TCP_PORT+"}")
//    private String h2TcpPort;
//
//    // Web port, default 8093
//    @Value("${h2.web.port:"+H2_WEB_PORT+"}")
//    private String h2WebPort;
//
//    /**
//     * TCP connection to connect with SQL clients to the embedded h2 database.
//     *
//     * Connect to "jdbc:h2:tcp://localhost:9092/mem:testdb", username "sa", password empty.
//     */
//
//    @Bean
//    @ConditionalOnExpression("${h2.tcp.enabled:false}")
//    public Server h2TcpServer() throws SQLException {
//        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2TcpPort).start();
//    }
//
//    /**
//     * Web console for the embedded h2 database.
//     *
//     * Go to http://localhost:8093 and connect to the database "jdbc:h2:mem:main", username "sa", password empty.
//     */
//
//    @Bean
//    @ConditionalOnExpression("${h2.web.enabled:true}")
//    public Server h2WebServer() throws SQLException {
//        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", h2WebPort).start();
//    }
}