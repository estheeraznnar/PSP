package org.example.FTP_Solo_Descarga;

/**
 *  Cliente FTP de solo descarga
 * Tenemos una aplicación que consta de las clases SimpleFTPClient y DownloadManager.
 *
 * SimpleFTPClient: Implementa un cliente FTP muy básico (modo texto) que se conecta a un servidor FTP, envía comandos básicos
 * (USER, PASS, PWD, LIST, RETR) y permite descargar un fichero concreto al disco local.
 * Se usará solo canal de control, no es necesario implementar el canal de datos correctamente, se puede simular la descarga leyendo texto.
 *
 * DownloadManager: Clase que pide al usuario por consola:
 * host FTP
 * usuario
 * contraseña
 * nombre de fichero a descargar y usa SimpleFTPClient para realizar la conexión y la descarga.
 *
 * Tareas:
 * Implementa en SimpleFTPClient el login (USER, PASS) y la lectura de la respuesta del servidor (código 230, etc.). (3 puntos)
 * Implementa el comando PWD para mostrar el directorio actual en el servidor. (2 puntos)
 * Implementa el comando LIST para mostrar el contenido del directorio actual por consola. (2 puntos)
 * Implementa un método downloadFile(String remoteName, String localName) que simule la descarga del fichero y lo guarde en disco. (3 puntos)
 */
public class Main {
}
