package org.iesch.psp.mail;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;

public class EnvioCorreoAutomatizado {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== ENVÍO AUTOMATIZADO DE CORREOS ELECTRÓNICOS ===\n");

        // Solicitar datos del correo
        System.out.print("Dirección de correo emisor: ");
        String correoEmisor = sc.nextLine();

        System.out.print("Contraseña del correo emisor: ");
        String password = sc.nextLine();

        System.out.print("Dirección de correo destinatario: ");
        String correoDestinatario = sc.nextLine();

        System.out.print("Asunto del mensaje: ");
        String asunto = sc.nextLine();

        System.out.print("Cuerpo del mensaje: ");
        String cuerpo = sc.nextLine();

        System.out.print("¿Desea adjuntar un archivo? (s/n): ");
        String respuesta = sc.nextLine();

        String rutaArchivo = null;
        if (respuesta.equalsIgnoreCase("s")) {
            System.out.print("Ruta completa del archivo a adjuntar: ");
            rutaArchivo = sc.nextLine();
        }

        // Configurar servidor SMTP
        System.out.print("\nServidor SMTP (ej: smtp.gmail.com): ");
        String servidorSMTP = sc.nextLine();

        System.out.print("Puerto SMTP (ej: 587): ");
        String puerto = sc.nextLine();

        // Enviar correo
        try {
            if (rutaArchivo != null && !rutaArchivo.isEmpty()) {
                enviarCorreoConAdjunto(correoEmisor, password, correoDestinatario,
                        asunto, cuerpo, rutaArchivo, servidorSMTP, puerto);
            } else {
                enviarCorreoSimple(correoEmisor, password, correoDestinatario,
                        asunto, cuerpo, servidorSMTP, puerto);
            }

            System.out.println("\n✓ Correo enviado correctamente");

        } catch (Exception e) {
            System.err.println("\n✗ Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }

        sc.close();
    }

    private static void enviarCorreoSimple(String correoEmisor, String password,
                                           String correoDestinatario, String asunto,
                                           String cuerpo, String servidorSMTP, String puerto)
            throws Exception {

        // Configuración de propiedades SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorSMTP);
        props.put("mail.smtp.port", puerto);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Crear sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(correoEmisor, password);
            }
        });

        System.out.println("\nConectando al servidor SMTP...");
        System.out.println("Servidor: " + servidorSMTP);
        System.out.println("Puerto: " + puerto);

        // Crear mensaje
        Message mensaje = new MimeMessage(session);
        mensaje.setFrom(new InternetAddress(correoEmisor));
        mensaje.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(correoDestinatario));
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        System.out.println("Autenticando...");
        System.out.println("Enviando mensaje...");

        // Enviar mensaje
        Transport.send(mensaje);

        System.out.println("Mensaje enviado exitosamente");
    }

    private static void enviarCorreoConAdjunto(String correoEmisor, String password,
                                               String correoDestinatario, String asunto,
                                               String cuerpo, String rutaArchivo,
                                               String servidorSMTP, String puerto)
            throws Exception {

        // Configuración de propiedades SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorSMTP);
        props.put("mail.smtp.port", puerto);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Crear sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(correoEmisor, password);
            }
        });

        System.out.println("\nConectando al servidor SMTP...");
        System.out.println("Servidor: " + servidorSMTP);
        System.out.println("Puerto: " + puerto);

        // Crear mensaje
        Message mensaje = new MimeMessage(session);
        mensaje.setFrom(new InternetAddress(correoEmisor));
        mensaje.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(correoDestinatario));
        mensaje.setSubject(asunto);

        // Crear parte del texto
        MimeBodyPart textoMime = new MimeBodyPart();
        textoMime.setText(cuerpo);

        // Crear parte del adjunto
        MimeBodyPart adjuntoMime = new MimeBodyPart();
        DataSource fuente = new FileDataSource(new File(rutaArchivo));
        adjuntoMime.setDataHandler(new DataHandler(fuente));
        adjuntoMime.setFileName(new File(rutaArchivo).getName());
        adjuntoMime.setDisposition(Part.ATTACHMENT);

        System.out.println("Adjuntando archivo: " + new File(rutaArchivo).getName());

        // Crear multipart y añadir partes
        Multipart multipart = new MimeMultipart("mixed");
        multipart.addBodyPart(textoMime);
        multipart.addBodyPart(adjuntoMime);

        // Asignar contenido al mensaje
        mensaje.setContent(multipart);

        System.out.println("Autenticando...");
        System.out.println("Enviando mensaje con adjunto...");

        // Enviar mensaje
        Transport.send(mensaje);

        System.out.println("Mensaje con adjunto enviado exitosamente");
    }
}