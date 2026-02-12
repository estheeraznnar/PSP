package org.example;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * Ejercicio 3: Envío automatizado de correos electrónicos
 * Desarrolla una aplicación que permita el envío de correos electrónicos utilizando los protocolos de correo electrónico estándar (SMTP).
 * Requisitos:
 * •
 * La aplicación debe:
 * •
 * Solicitar al usuario:
 * •
 * Dirección de correo emisor
 * •
 * Dirección de correo destinatario
 * •
 * Asunto
 * •
 * Cuerpo del mensaje
 * •
 * Conectarse a un servidor SMTP autenticado.
 * •
 * Enviar el correo electrónico correctamente.
 * •
 * Mostrar mensajes de confirmación o error.
 * •
 * (Opcional) Adjuntar un archivo al correo electrónico.
 */

public class Ejer03 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. Solicitar datos al usuario por consola
        System.out.print("Correo emisor: ");
        String emisor = sc.nextLine();
        System.out.print("Contraseña (o token de aplicación): ");
        String password = sc.nextLine();
        System.out.print("Correo destinatario: ");
        String destinatario = sc.nextLine();
        System.out.print("Asunto: ");
        String asunto = sc.nextLine();
        System.out.print("Cuerpo del mensaje: ");
        String cuerpo = sc.nextLine();

        // 2. Configuración de propiedades para el servidor SMTP (específico para Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // Habilita la autenticación del usuario
        props.put("mail.smtp.starttls.enable", "true"); // Fuerza el uso de cifrado TLS (seguridad)
        props.put("mail.smtp.host", "smtp.gmail.com"); // Servidor de salida de Gmail
        props.put("mail.smtp.port", "587"); // Puerto estándar para TLS

        // 3. Crear la sesión de correo vinculando las propiedades y el autenticador
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Proporciona las credenciales al servidor cuando se soliciten
                return new PasswordAuthentication(emisor, password);
            }
        });

        try {
            // 4. Construir el objeto del mensaje
            Message mensaje = new MimeMessage(session);
            // Establece quién envía el correo
            mensaje.setFrom(new InternetAddress(emisor));
            // Define el destinatario (permite listas separadas por comas)
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            // Título del correo
            mensaje.setSubject(asunto);
            // Contenido en texto plano (puedes usar setContent para HTML)
            mensaje.setText(cuerpo);

            // 5. Intentar el envío a través del transporte SMTP configurado
            Transport.send(mensaje);
            System.out.println("¡Éxito! El correo ha sido enviado correctamente.");

        } catch (MessagingException e) {
            // Gestión de errores (fallo de red, credenciales incorrectas, etc.)
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}

