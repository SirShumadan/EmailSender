package com.example.solution;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Sender {
    private Properties properties;

    public static void main(String[] args) {
        Sender sender = new Sender();

        sender.initProperties(args[0]);
        String text = sender.createTextMessage(args[1]);

//        Для запуска через IDE

//        sender.initProperties("src/main/resources/config.properties");
//        String text = sender.createTextMessage("src/main/resources/message.txt");


        String subject = sender.getProperties().getProperty("mail.subject");
        String to = sender.getProperties().getProperty("mail.to");
        String from = sender.getProperties().getProperty("mail.from");
        sender.send(subject, text, to, from);
        sender.printMessage(subject, text, to, from);
    }

    public Properties getProperties() {
        return properties;
    }

    //Отправка письма
    private void send(String subject, String text, String toEmail, String fromEmail) {
        Session session = createSession(
                properties.getProperty("mail.username"),
                properties.getProperty("mail.password"));
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    //Считывает исходный текст
    private String createTextMessage(String textSource) {
        final StringBuilder text = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(textSource))) {
            stream.forEach(t -> text.append(t).append(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return replaceTemplates(text.toString());
    }

    //Заменяет шаблоны {name} и {number} в тексте на данные из config.properties
    private String replaceTemplates(String text) {
        String fio = properties.getProperty("message.name");
        String phoneNumber = properties.getProperty("message.phonenumber");
        Pattern fioPattern = Pattern.compile("\\{name\\}");
        Pattern numberPattern = Pattern.compile("\\{number\\}");
        Matcher matcher = fioPattern.matcher(text);
        if (matcher.find()) {
            text = matcher.replaceAll(fio);
        }
        matcher = numberPattern.matcher(text);
        if (matcher.find()) {
            text = matcher.replaceAll(phoneNumber);
        }
        return text;
    }

    //Считывает настройки из файла config.properties
    private void initProperties(String propertiesSource) {
        properties = new Properties();
        try (FileInputStream in = new FileInputStream(propertiesSource)) {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Session createSession(final String username, final String password) {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        return Session.getInstance(properties, auth);
    }

    //Выводит в консоль отправленное сообщение
    private void printMessage(String subject, String text, String to, String from) {
        StringBuilder sb = new StringBuilder();
        sb.append("Message send: ")
                .append(System.lineSeparator())
                .append("-----------------------------------------")
                .append(System.lineSeparator())
                .append("From: ")
                .append(from)
                .append(System.lineSeparator())
                .append("To: ")
                .append(to)
                .append(System.lineSeparator())
                .append("Subject: ")
                .append(subject)
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(text)
                .append(System.lineSeparator())
                .append("-----------------------------------------");
        System.out.println(sb.toString());
    }


}
