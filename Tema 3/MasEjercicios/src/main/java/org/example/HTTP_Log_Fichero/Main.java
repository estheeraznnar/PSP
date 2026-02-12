package org.example.HTTP_Log_Fichero;

/**
 * Echo HTTP con log en fichero
 * Tenemos una aplicación que consta de las clases HttpEchoServer, HttpEchoClient y RequestLog.
 *
 * HttpEchoServer: Servidor HTTP muy simple que escucha en el puerto 8080.
 * Cuando recibe una petición GET /echo?msg=... HTTP/1.1, responde con una página HTML que muestra el mensaje enviado.
 * El servidor debe escribir en un log cada petición recibida (fecha, IP cliente, mensaje) usando la clase RequestLog.
 *
 * HttpEchoClient: Cliente que se conecta por socket al servidor HTTP y envía peticiones GET /echo?msg=....
 * Muestra por consola el código de estado y el cuerpo de la respuesta.
 *
 * RequestLog: Clase que escribe líneas en un fichero de texto requests.log con la información de cada petición.
 *
 * Tareas:
 *
 * Implementa HttpEchoServer para que atienda peticiones de un único cliente cada vez, utilizando ServerSocket y Socket. (3 puntos)
 *
 * Implementa HttpEchoClient que pida por consola un mensaje, lo envíe al servidor con una petición GET y muestre la respuesta. (3 puntos)
 *
 * Implementa RequestLog para que cada petición se registre en el fichero requests.log con formato:
 * YYYY-MM-DD HH:MM:SS - IP_CLIENTE - msg=... (2 puntos)
 *
 * Modifica HttpEchoServer para que, si se recibe la ruta /stats, responda con una página HTML que muestre el número total de peticiones atendidas
 * (leyendo los datos desde RequestLog). (2 puntos)
 */

public class Main {
}
