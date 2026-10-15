package it.unipd.dei.webapp.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

/**
 * Utility class for sending emails.
 * 
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */
public class EmailSender {

    /**
     * A LOGGER for this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(EmailSender.class,
            StringFormatterMessageFactory.INSTANCE);

    /**
     * Sends an email.
     *
     * @param to      the recipient's email address.
     * @param subject the email subject.
     * @param content the email content.
     * @throws Exception if any error occurs.
     */
    public static void sendEmail(String to, String subject, String content) throws Exception {

        LOGGER.info("Sending email to: %s", to);

        final String from = System.getenv("MAIL_USER");
        final String password = System.getenv("MAIL_PASS");
        final String host = System.getenv("MAIL_HOST");
        final String port = System.getenv("MAIL_PORT");

        if (from == null || password == null || host == null || port == null) {
            LOGGER.error("Email configuration is missing in environment variables.");
            throw new Exception("Email configuration missing.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            LOGGER.info("Email successfully sent to: %s", to);
        } catch (MessagingException e) {
            LOGGER.error("Unable to send email to: %s", to, e);
            throw e;
        }
    }
}
