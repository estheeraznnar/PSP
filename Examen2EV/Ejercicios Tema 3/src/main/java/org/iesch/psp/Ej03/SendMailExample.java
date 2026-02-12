package org.iesch.psp.Ej03;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;

public class SendMailExample {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("--- CLIENTE DE CORREO JAVA (JAKARTA MAIL) ---");

        // 1. Datos del emisor
        System.out.print("Tu correo (Gmail/IES): ");
        String fromEmail = sc.nextLine().trim();

        System.out.print("Tu Contraseña de Aplicación (16 letras): ");
        String password = sc.nextLine().trim();

        // 2. Datos del destinatario
        System.out.print("Destinatario: ");
        String toEmail = sc.nextLine().trim();

        // 3. Contenido
        System.out.print("Asunto: ");
        String subject = sc.nextLine().trim();

        System.out.print("Cuerpo del mensaje: ");
        String bodyText = sc.nextLine();

        // 4. Archivo adjunto
        System.out.print("¿Quieres adjuntar un archivo? (S/N): ");
        String adjuntar = sc.nextLine().trim();

        String pathAdjunto = "";
        if (adjuntar.equalsIgnoreCase("S")) {
            System.out.print("Ruta del archivo (ej: C:\\Users\\...\\foto.jpg): ");
            // Limpiamos comillas por si copias la ruta de Windows
            pathAdjunto = sc.nextLine().replace("\"", "").trim();
        }

        // 5. Configuración del Servidor SMTP (Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // Puerto TLS estándar
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        // Esta línea ayuda si estás en la red del instituto y hay proxys/firewalls que tocan los certificados
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // 6. Autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Creamos el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // 7. Lógica para adjuntar archivo
            if (!pathAdjunto.isEmpty()) {
                File file = new File(pathAdjunto);

                if (file.exists()) {
                    // --- CORREO CON ADJUNTO (MULTIPART) ---

                    // Parte 1: El texto
                    MimeBodyPart textPart = new MimeBodyPart();
                    textPart.setText(bodyText);

                    // Parte 2: El archivo
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(file.getName());

                    // Juntamos ambas partes
                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(textPart);
                    multipart.addBodyPart(attachmentPart);

                    message.setContent(multipart);
                    System.out.println("Archivo adjuntado: " + file.getName());

                } else {
                    System.out.println("AVISO: El archivo no existe. Se enviará solo texto.");
                    message.setText(bodyText);
                }

            } else {
                // --- CORREO SIMPLE (TEXTO PLANO) ---
                message.setText(bodyText);
            }

            // 8. Enviar
            System.out.println("Enviando mensaje...");
            Transport.send(message);
            System.out.println(">> ¡ÉXITO! Correo enviado correctamente.");

        } catch (AuthenticationFailedException e) {
            System.err.println("ERROR DE AUTENTICACIÓN: Usuario o contraseña rechazados.");
            System.err.println("Asegúrate de estar usando una CONTRASEÑA DE APLICACIÓN de Google, no la tuya normal.");
        } catch (MessagingException e) {
            System.err.println("ERROR AL ENVIAR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}