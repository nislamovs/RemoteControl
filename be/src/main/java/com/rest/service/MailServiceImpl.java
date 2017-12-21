package com.rest.service;

import com.rest.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.ISODateTimeFormat.dateTime;

@Component
@Profile({"development", "production"})
public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final ThreadLocal<String> REQUEST_URI = new ThreadLocal<>();
    private static final String SUBJECT_PATTERN = "%s (%d exception(s))";
    private static final String DEBUG_MESSAGE_PATTERN = "Sending message {}";
    private static final String SERVER_URL_PREFIX = "http://localhost:8080/DashboardIO/auth/activateuser/";

    private final List<Alert> alerts = new LinkedList<>();
    private final AtomicLong exceptionAlertCount = new AtomicLong();
    private final int maxAlerts;
    private final List<String> ignorePatterns;

    @Autowired
    Environment environment;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SimpleMailMessage alertMessageTemplate;

    @Autowired
    public MailServiceImpl(Environment env) {
        ignorePatterns = asList(split(env.getProperty("alert.email.ignorePatterns", ""), ','));
        maxAlerts = env.getProperty("alert.email.max.alerts", Integer.class, Integer.MAX_VALUE);
    }

    @Override
    public void sendActivationMail(User user) {
        System.out.println("We are sending activation mail");
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(environment.getProperty("alert.email.from"));
            helper.setTo(user.getEmail());
            helper.setSubject("Confirmation letter");
            helper.setText(generateConfirmationLink(user));
        } catch (MessagingException e) {
            System.out.println("Failed to parse email.");
        }

        sendMessage(message);
    }

    @Override
    public void sendNewPasswordMail(User user, String passwd) {
        System.out.println("We are sending new password mail");
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@noreply.com");
            helper.setTo(user.getEmail());
            helper.setSubject("New password");
            helper.setText("Hi " + user.getName() + "!\n" + "Your new password is: \n " + passwd );
        } catch (MessagingException e) {
            System.out.println("Failed to parse email.");
        }

        sendMessage(message);
    }

    @Override
    public void sendMail(String msg, String email) {
        System.out.println("We are sending mail");
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@noreply.com");
            helper.setTo(email);
            helper.setSubject("Testing mail service.");
            helper.setText(msg);
        } catch (MessagingException e) {
            System.out.println("Failed to parse email.");
        }

        sendMessage(message);
    }

    @Override
    public void sendError(Throwable t) {
        if (isEmpty(alertMessageTemplate.getTo()) || isIgnored(t)) {
            return;
        }
        Alert alert = new Alert(t);
        LOG.debug("Adding alert {} to queue", alert);
        synchronized (alerts) {
            if (exceptionAlertCount.getAndIncrement() < maxAlerts) {
                alerts.add(alert);
            }
        }
    }

    @Scheduled(fixedDelayString = "${alert.email.interval:600000}")
    public void sendExceptionAlerts() {
        Pair<List<Alert>, Long> batch = getBatch();
        if (batch.getKey().isEmpty()) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage(alertMessageTemplate);
        message.setSubject(format(SUBJECT_PATTERN, message.getSubject(), batch.getValue()));
        message.setText(buildMessage(batch.getKey()));
        sendMessage(message);
    }

    private String generateConfirmationLink(User user) {
        String link = SERVER_URL_PREFIX + "?username=" + user.getEmail() + "&keyword=" + user.getKeyword();
        return "Hi " + user.getName() + "!\n" + "Please follow this link: \n " + link
                     + "\n\nThank you for choosing DashboardIO!";
    }

    private boolean isIgnored(Throwable t) {
        String pattern = t.getClass().getName() + ":" + t.getMessage();
        for (String ignorePattern : ignorePatterns) {
            if (pattern.startsWith(ignorePattern)) {
                return true;
            }
        }
        return false;
    }

    private Pair<List<Alert>, Long> getBatch() {
        List<Alert> batch;
        long oldAlertCount;
        synchronized (alerts) {
            batch = new ArrayList<>(alerts);
            alerts.clear();
            oldAlertCount = exceptionAlertCount.getAndSet(0);
        }
        return Pair.of(batch, oldAlertCount);
    }

    private String buildMessage(List<Alert> batch) {
        StringBuilder strBuild = new StringBuilder(1024);
        for (Alert alert : batch) {
            strBuild.append("URL: ")
                    .append(alert.requestUrl)
                    .append(System.getProperty("line.separator") + "Timestamp: ")
                    .append(dateTime().print(alert.timestamp))
                    .append(System.getProperty("line.separator") + "Stack trace:" + System.getProperty("line.separator"))
                    .append(alert.stackTrace)
                    .append(System.getProperty("line.separator"));
        }
        return strBuild.toString();
    }

    private void sendMessage(MimeMessage message) {
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            System.out.println("Failed to send email to user! " + e.toString());
        }
    }

    private void sendMessage(SimpleMailMessage message) {
        try {
            LOG.debug(DEBUG_MESSAGE_PATTERN, message);
            javaMailSender.send(message);
        } catch (MailException e) {
            LOG.error("Failed to send alert email", e);
        }
    }

    private static String shortStackTrace(String stackTraceAsString) {
        String[] lines = StringUtils.split(stackTraceAsString, System.getProperty("line.separator"));
        for (int i = 1; i < lines.length; i++) {
            if (StringUtils.indexOfAny(lines[i], "spring", "reflect", "apache") > -1) {
                lines[i] = null;
            }
        }
        return join(lines, System.getProperty("line.separator").charAt(0)) + System.getProperty("line.separator");
    }

    private static String join(final String[] array, char separator) {
        if (array == null) return null;
        final StringBuilder sb = new StringBuilder(array.length * 16);

        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                if (i > 0) sb.append(separator);
                sb.append(array[i]);
            }
        }
        return sb.toString();
    }

    static final class Alert {
        private final String stackTrace;
        private final DateTime timestamp;
        private final String requestUrl;

        private Alert(Throwable t) {
            stackTrace = shortStackTrace(getStackTraceAsString(t));
            timestamp = now();

            String url = REQUEST_URI.get();
            requestUrl = StringUtils.isEmpty(url) ? "N/A" : url;
        }

        @Override
        public String toString() {
            return reflectionToString(this);
        }
    }
}
