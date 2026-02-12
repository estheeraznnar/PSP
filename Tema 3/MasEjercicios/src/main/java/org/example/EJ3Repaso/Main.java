package org.example.EJ3Repaso;

/*EJERCICIO 3: Chat Multihilo con Cifrado (5 puntos)
Contexto
Vamos a crear un sistema de chat donde múltiples clientes pueden enviar mensajes que serán distribuidos a todos los demás clientes conectados.

Clases: ChatServer, ChatClient, ClientConnection, MessageCrypto.
ChatServer: Servidor de chat que mantiene una lista de clientes conectados y distribuye los mensajes recibidos a todos los clientes.
ChatClient: Cliente de chat que se conecta al servidor, envía mensajes y recibe mensajes de otros usuarios.
ClientConnection: Representa la conexión de cada cliente en el servidor (se ejecuta en un hilo).
MessageCrypto: Clase con métodos estáticos para cifrar y descifrar mensajes usando AES.

Tareas
    1. Implementa la clase ChatServer que:
        Escuche en el puerto 5000
        Use hilos para manejar cada cliente conectado
        Mantenga una lista thread-safe (CopyOnWriteArrayList) de todos los clientes
        Cuando reciba un mensaje de un cliente, lo distribuya a todos los demás
        Los mensajes deben estar cifrados con AES (3 puntos)

    2. Implementa la clase ChatClient que:
        Se conecte al servidor
        Solicite un nombre de usuario al iniciar
        Use dos hilos: uno para enviar mensajes (leer de consola) y otro para recibir mensajes del servidor
        Cifre los mensajes antes de enviarlos
        Descifre los mensajes recibidos (3 puntos)

    3. Implementa la clase MessageCrypto con métodos:
        static byte[] encrypt(String message, SecretKey key)
        static String decrypt(byte[] encrypted, SecretKey key)
        static SecretKey generateKey() - genera una clave AES de 128 bits
        Usa el algoritmo AES/CBC/PKCS5Padding (2 puntos)

    4. Añade un comando especial /disconnect que permita a un cliente desconectarse del servidor de forma ordenada.
    El servidor debe notificar a todos los demás cuando alguien se desconecta. (2 puntos)

*/
public class Main {
}
