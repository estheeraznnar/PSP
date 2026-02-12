package org.example.EJ1Repaso;

/**
 * EJERCICIO 1: Sistema de Mensajería Segura Multihilo (4 puntos)
 * Contexto
 * Tenemos una aplicación que consta de las clases SecureMessageServer, SecureMessageClient y MessageData.
 *
 * SecureMessageServer: Servidor que permite recibir mensajes cifrados de los clientes.
 * El servidor, al recibir un mensaje cifrado, lo descifra y lo almacena. Permite que cada
 * cliente envíe todos los mensajes que quiera. Cuando el cliente termina de enviar mensajes,
 * la comunicación con ese cliente termina y el servidor queda a la espera de otras peticiones
 * de otros clientes. Si el mensaje recibido es "STATS", esto se considera una orden de estadísticas y
 * el servidor envía al cliente el número total de mensajes recibidos y finaliza su ejecución.
 *
 * SecureMessageClient: Esta clase conecta con el servidor SecureMessageServer, cifra un mensaje usando AES
 * y lo envía al servidor. A continuación, espera la confirmación del servidor.
 *
 * MessageData: Esta clase guarda datos de uso del servidor SecureMessageServer
 * (contador de mensajes recibidos y lista de mensajes).
 *
 * Tareas
 * 1. Crea una clase llamada SecureMessageStopper que se conecte a través de un socket TCP
 * al servidor SecureMessageServer y le envíe la señal de estadísticas (la señal es un mensaje que
 * contiene únicamente "STATS"). El servidor devolverá las estadísticas como respuesta a la orden, estos
 * datos deberán ser mostrados por consola por el programa SecureMessageStopper. Comprueba que el servidor
 * finaliza su ejecución. (2,5 puntos)
 *
 * 2. Tal y como está desarrollado, SecureMessageServer solo puede atender peticiones de
 * un único cliente al mismo tiempo. Transforma SecureMessageServer para que pueda responder a varios
 * clientes a la vez usando hilos. Puedes crear una clase adicional llamada ClientHandler si lo consideras
 * necesario. (4 puntos)
 *
 * 3. Crea una clase llamada SecureClientLauncher que lance 50 instancias de SecureMessageClient indicando
 * que cada una de ellas envíe 200 mensajes cifrados al servidor SecureMessageServer. (2,5 puntos)
 *
 * 4. Ahora vemos que tras ejecutar el launcher ejecutamos la clase SecureMessageStopper y no recibimos
 * siempre 10000 como número de mensajes procesados, que es el número correcto de mensajes procesados.
 * Modifica la clase MessageData para asegurar que cuenta correctamente mensajes usando sincronización. (1 punto)
 */

public class main {
}
