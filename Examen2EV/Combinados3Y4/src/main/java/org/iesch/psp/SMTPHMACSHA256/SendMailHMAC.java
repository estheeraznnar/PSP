package org.iesch.psp.SMTPHMACSHA256;// --- SendMailHMAC.java ---
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

public class SendMailHMAC {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Datos del emisor
        System.out.println("Introduce la dirección de correo emisor:");
        String fromEmail = sc.nextLine().trim();

        System.out.println("Introduce la contraseña del correo emisor:");
        String password = sc.nextLine().trim();

        // Destinatario
        System.out.println("Introduce la dirección de correo destinatario:");
        String toEmail = sc.nextLine().trim();

        // Cuerpo del mensaje
        System.out.println("Introduce el cuerpo del mensaje:");
        String bodyText = sc.nextLine();

        // Generamos clave HMAC
        byte[] hmacKey = HmacSHA256Util.generateKey();
        String hmacKeyBase64 = Base64.getEncoder().encodeToString(hmacKey);
        System.out.println("Clave HMAC (compartir con receptor): " + hmacKeyBase64);

        // Calculamos el HMAC del cuerpo del mensaje
        String hmac = HmacSHA256Util.getHmac(bodyText, hmacKey);
        System.out.println("HMAC-SHA256 del cuerpo: " + hmac);

        // El asunto contiene el HMAC para verificación
        String subject = "HMAC: " + hmac;

        // Propiedades SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Creamos el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, fromEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(bodyText);

            // Envío del mensaje
            Transport.send(message);
            System.out.println("Correo enviado correctamente.");

            // Simulamos la verificación del receptor
            System.out.println("\n--- RECEPTOR ---");
            System.out.println("Asunto recibido (contiene HMAC): " + subject);
            System.out.println("Cuerpo recibido: " + bodyText);

            // Extraemos el HMAC del asunto
            String hmacRecibido = subject.replace("HMAC: ", "");

            // Verificamos con la clave compartida
            if (HmacSHA256Util.checkHmac(bodyText, hmacRecibido, hmacKey)) {
                System.out.println("HMAC válido. El mensaje no ha sido alterado.");
            } else {
                System.out.println("ERROR: HMAC no coincide. El mensaje ha sido alterado.");
            }

        } catch (Exception e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}