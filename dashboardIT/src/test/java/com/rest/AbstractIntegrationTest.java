package com.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.ResponseSpecification;
import com.sun.mail.pop3.POP3Store;
import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.currentTimeMillis;
import static org.hamcrest.Matchers.containsString;

public class AbstractIntegrationTest {

    public static final String BUILD_PROPERTIES = "build.properties";
    public static final String DEV_ENVIRONMENT = "development";
    public static final String PROD_ENVIRONMENT = "production";
    public static final String REGEX_DATETIME= "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    public static final Properties properties = new Properties();
    private static final Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    @Autowired
    Environment environment;

    @BeforeClass
    public static void initializeData() throws IOException {
        properties.load(ClassLoader.getSystemResourceAsStream(BUILD_PROPERTIES));
        String env = properties.getProperty("application.environment").toLowerCase();
        if (!env.equals(PROD_ENVIRONMENT)) {
            env = DEV_ENVIRONMENT;
        }
        properties.load(ClassLoader.getSystemResourceAsStream(env + ".properties"));
        RestAssured.baseURI = properties.getProperty("host");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        LOG.info("Before class launched and initialized.");
        LOG.info("Host : {}", properties.getProperty("host"));
    }

    @Before
    public void setUp() throws Exception {
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
    }

    public static final ResponseSpecification HEALTHCHECK_SPEC = new ResponseSpecBuilder().
            expectStatusCode(HttpStatus.OK.value()).
            expectBody(containsString("Dashboard IO")).
            expectBody(containsString("Environment")).
            expectBody(containsString("Deployment date")).
            expectBody(containsString("Status")).
            build();

    public static String getRandomUsername() {
        return "testuser-" + currentTimeMillis();
    }

    public Map<String, String> emailInboxContentIsOk() {

        Map<String, String> messageData = new ManagedMap<>();

        try {
            Session emailSession = Session.getDefaultInstance(properties);

            POP3Store emailStore = (POP3Store) emailSession.getStore(properties.getProperty("mail.storetype"));
            emailStore.connect(properties.getProperty("mail.username"), properties.getProperty("mail.password"));

            Folder emailFolder = emailStore.getFolder(properties.getProperty("mail.storefolder"));
            emailFolder.open(Folder.READ_WRITE);

            Message message = emailFolder.getMessages()[emailFolder.getMessageCount()-1];
            messageData.put("Subject", message.getSubject());
            messageData.put("From", message.getFrom()[0].toString());
            messageData.put("Body", getTextFromMessage(message));

            emailFolder.close(false);
            emailStore.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messageData;
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

    public void delayMs(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


