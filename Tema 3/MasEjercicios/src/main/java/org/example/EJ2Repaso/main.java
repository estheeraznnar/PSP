package org.example.EJ2Repaso;

/**
 * EJERCICIO 2: Servidor FTP Seguro con Autenticación (4 puntos)
 * Contexto
 * Tenemos un servidor FTP básico que consta de las clasesFTPServer,FTPClient y UserCredentials.
 * FTPServer: Servidor que permite subir y descargar archivos. Actualmente acepta cualquier conexión sin autenticación.
 * FTPClient: Cliente que se conecta al servidor y puede enviar comandos: UPLOAD, DOWNLOAD, LIST, EXIT.
 * UserCredentials: Clase que almacena usuarios y contraseñas hasheadas.
 * Tareas
 * 1.Implementa un sistema de autenticación en FTPServer usandosalted password hashing(PBKDF2 + SHA256).
 * El servidor debe solicitar usuario y contraseña antes de aceptar cualquier comando. Crea al menos 3 usuarios de prueba:
 * "admin", "user1", "user2"(3 puntos)
 * 2.Modifica FTPServer para que use un pool de hilos (ExecutorService) que permita atender hasta 5 clientes simultáneamente.
 * Cada cliente debe ser manejado por un hilo independiente.(3 puntos)
 * 3.Añade un comando USERS que solo puede ejecutar el usuario "admin".
 * Este comando debe devolver la lista de usuarios conectados actualmente al servidor.(2 puntos)
 * 4.Crea una clase FTPStressTest que lance 20 clientes simultáneos intentando conectarse y subir archivos.
 * Verifica que el servidor maneja correctamente las conexiones concurrentes.(2 puntos)
 */
public class main {
}
