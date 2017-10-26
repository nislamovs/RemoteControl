package com.rest.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.apache.commons.lang3.StringUtils.split;

@Configuration
@Profile({ "development", "production"})
public class EmailConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(EmailConfiguration.class);

    @Autowired
    private Environment env;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(env.getRequiredProperty("email.smtp.host"));
        sender.setPort(env.getRequiredProperty("email.smtp.port", Integer.class));
        sender.setProtocol(env.getRequiredProperty("email.smtp.protocol"));
        sender.setUsername(env.getRequiredProperty("alert.email.username"));
        sender.setPassword(env.getRequiredProperty("alert.email.password"));

        LOG.info("SMTP host and port: {}:{}", sender.getHost(), sender.getPort());
        return sender;
    }

    @Bean
    public SimpleMailMessage alertMessageTemplate() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(env.getRequiredProperty("alert.email.from"));
        message.setReplyTo(env.getRequiredProperty("alert.email.reply.to"));
        message.setTo(split(env.getProperty("alert.email.to", ""), ','));
        message.setSubject(env.getRequiredProperty("alert.email.subject.prefix"));

        return message;
    }
}
