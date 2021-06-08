import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;

public class Mail {
    private static final String USERNAME = "java.kodigo@gmail.com";
    private static final String PASSWORD = "kodigo1234";

    public void sendMail(String toEmail, String subject, String bodyMsg, String[] attachmentFileNames){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(USERNAME,PASSWORD);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(USERNAME));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject(subject);

            Multipart content = new MimeMultipart();

            MimeBodyPart bodyText = new MimeBodyPart();
            bodyText.setText(bodyMsg);
            content.addBodyPart(bodyText);

            for (String fileName : attachmentFileNames) {
                MimeBodyPart fileAttachment = new MimeBodyPart();
                fileAttachment.attachFile(fileName);
                content.addBodyPart(fileAttachment);
            }

            msg.setContent(content);

            Transport.send(msg);
            System.out.println("Mail sent successfully!");
        } catch (MessagingException | IOException exception){
            exception.printStackTrace();
        }
    }
}
