
/*Esta clase representa un servidor que permite recibir mensajes de texto que envian los clientes. El servidor, haciendose eco de la
recepcion del mensaje, devuelve el mismo mensaje de texto de vuelta al cliente tras rcibirlo. El servidor permite que cada cliente envie
todos los mensajes que quiera , cuando el cliente termina de enviar mensajes la comunicacion con es cliente termina y el servidor queda
a la espera de otras peticiones de otros clientes. Si el mesnjae recibido es ".", esto se considera una orden de parada y el servidor EchoServer y envia un numero.
 */

/*1.
cre una clase llamada echoStopper que se conecte atraves de un socket TCP al servidor Echo server y le envie una señal de parada(la señal de parada
es un mensaje que contiene unicamente un punto "."). Tal como esta creado el servidor este devolvera los datos de uso del servidor como respuesta
a la orden de parada, estos datos deberan ser mostrados por consola por el programa EchoStopper.
Comprueba que el servidor finaliza su ejecucion
 */
/*2.
Tal y como esta desarrollada esta clase  solo puede atender peticiones de un unico cliente al mismo tiempo. Transforma EchoServer para que pueda responder
a varios clientes a la vez. Ouedes crear una clase adicional llamada EchoServeThread si lo consideras necesario.
 */
/*3.
crea una clase echoClientLauncher que lance 100 instancias de EchoClient indicando que cada una de ellas envie 100 mensajes al servidor EchoServer.
 */
/*4.
ahora tras ejecutar el launcher ejecutamoslaclase echoStopper y no recibimos siempre 10000 como numero de mensajes procesados,
que es el numero correcto de mensajes procesados. Modifica la clase EchoData para asegurar que cuenta correctamente mensajes y caracteres.
 */

public class EchoServer {
}
