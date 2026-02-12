package org.example.EJCorreosHilos;

/**
 * Envío Masivo de Correos con SMTP y Sockets
 * Tenemos una aplicación que consta de las clases BulkMailClient, MailTask, MailStats y MainBulkMail.
 *
 * BulkMailClient: Implementa un cliente SMTP que se conecta por socket a un servidor SMTP (por ejemplo smtp.gmail.com puerto 587),
 * realiza los comandos EHLO, STARTTLS, AUTH LOGIN, MAIL FROM, RCPT TO, DATA, QUIT, y envía un mensaje de correo en texto plano al mismo destinatario.
 * MailTask: Representa la tarea de enviar un único correo (asunto + cuerpo) a un destinatario concreto usando BulkMailClient.
 * MailStats: Clase que mantiene contadores del número de correos enviados correctamente y fallidos, de forma segura para hilos.
 * MainBulkMail: Clase principal que pide por consola:
 *  dirección del remitente
 *  contraseña (o contraseña de aplicación)
 *  dirección de destino
 *  número de correos a enviar
 *  y lanza varias tareas en paralelo para enviar N correos al mismo destinatario, mostrando al final un resumen de cuántos se han enviado correctamente.
 *
 * Requisitos:
 * El envío de correo debe hacerse implementando el protocolo SMTP con Socket y comandos de texto (EHLO, AUTH LOGIN, MAIL FROM, etc.), como en la teoría.
 * No se puede usar JavaMail ni librerías externas de correo.
 * Se deben usar hilos: un ExecutorService con un pool de hilos que lance varias instancias de MailTask para enviar muchos correos.
 * MailStats debe ser thread-safe (usar AtomicInteger o synchronized).
 * Al final, MainBulkMail debe mostrar:
 *  Total de correos pedidos
 *  Correos enviados OK
 *  Correos fallidos
 */

public class Main {
}
