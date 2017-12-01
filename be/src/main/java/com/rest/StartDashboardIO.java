package com.rest;

import com.rest.configuration.H2ServerConfiguration;
import com.rest.configuration.JpaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Properties;


@Import({JpaConfiguration.class, H2ServerConfiguration.class})
@SpringBootApplication(scanBasePackages={"com.rest"})
@EnableScheduling
public class StartDashboardIO extends SpringBootServletInitializer {

    private static final String BUILD_PROPERTIES = "build.properties";
    private static final String DEV_ENVIRONMENT = "development";
    private static final String PROD_ENVIRONMENT = "production";

    private static final Logger LOG = LoggerFactory.getLogger(StartDashboardIO.class);

    public static void main(String[] args) {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, getProfile());
        SpringApplication.run(StartDashboardIO.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(StartDashboardIO.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, getProfile());
        super.onStartup(servletContext);
    }

    private static String getProfile() {
        Properties properties = new Properties();
        String env = "";
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(BUILD_PROPERTIES));
            env = properties.getProperty("application.environment").toLowerCase();
        } catch (Exception e) {
            LOG.error("Problem with loading build properties.", e);
        }

        return env.equals(PROD_ENVIRONMENT) ? PROD_ENVIRONMENT : DEV_ENVIRONMENT;
    }

    public static void testFindbugs() {
        //Findbugs test
        String s = null;
        System.out.println(s.charAt(4));
    }

}