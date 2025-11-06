

/**
 * Crea un programa que trabaje con hilos. El hilo principal deberá lanzar 2 hilos, el primero escribirá por consola 15 veces “Hola “
 * cada 2 segundos. El segundo hilo escribirá “ mundo!” y el retorno de carro otras 15 veces también cada 2 segundos.
 * Si el hilo principal lanza el segundo con un pequeño retraso (unos 20ms) el texto se mostrará por consola
 * sin percatarnos que lo están escribiendo 2 hilos diferentes. Modifica el programa de manera que el hilo principal
 * interrumpa al primer hilo transcurridos 5s desde el arranque de los dos hilos. La respuesta ante la interrupción debe consistir
 * en la salida y finalización de la ejecución del hilo interrumpido.
 */
public class Ejer03 {
    public static void main(String[] args) {
        System.out.println("--- Inicio el programa de hilos ---\n");

        /**
         * Creo el primer hilo
         * Creo una instancia HiloHola que implementa el Runnable
         * y se la paso al contructor de Thread
         */
        Thread hilo1 = new Thread(new HiloHola(), "Hilo-1-Hola");

        /**
         * Creo el segundo holo
         * Hago lo mismo que con el HiloHola pero ahora HiloMundo
         */
        Thread hilo2 = new Thread(new HiloMundo(), "Holo-2-Mundo");

        //Guardo el tiempo de inicio para calcular la duracion total
        long tiempoInicio = System.currentTimeMillis();

        /**
         * Iniciar el primer hilo
         * Con start() se crea un nuevo hilo de ejecucion independiente
         * El hilo comienza a ejecutar el metodo run() de HiloHola
         */
        hilo1.start();

        /**
         * Retraso el inicio del segundo hilo 20milisegundos
         * por lo que los dos hilos se sincronicen visiualmente
         * para que parezca que escriben juntos: "HOLA MUNDO!"
         */
        try {
            Thread.sleep(20);
        }catch (InterruptedException e){
            System.err.println("Hilo principal interrumpido: " + e.getMessage());
        }

        //Ahora inicio el segundo hilo. Ahora creo otro hilo independiantemente que ejecuta hilomundo
        hilo2.start();

        //Espera 5sg y luego interrumpir el primer hilo
        try {
            //El programa principal se duerme 5sg
            Thread.sleep(5000);

            //Pasados 5sg, muestro un mensaje
            System.out.println("\nPRINCIPAL han pasado 5 seegundos. Interrumpiendo hilo 1...");

            //Envio una solicitud de interrupcion al primer hilo
            //Esto NO lo detiene inmediatamente, solo establece una bandera
            hilo1.interrupt();
        }catch (InterruptedException e){
            System.err.println("Hilo principal interrumpido: " + e.getMessage());
        }

        /**
         * ESTO NO ES NECESARIO SE PUEDE HACER SIN ESTO
         * Espero a que ambos hilos finalicen
         * join() hace que el hilo actual (main) espere a que el otro termine
         */
        /*try {
            //Espera a que el hilo1 termine
            hilo1.join();
            //Espera a que hilo2 termine
            hilo2.join();

            //Cuando ambos han terminado, calculamos el tiempo total
            long tiempoFin = System.currentTimeMillis();

            //Mensaje indicando que to do termino correctamente
            System.out.println("\n Ambos hilos finalizados");

            //Muestro cuanto tiempo paso en total
            System.out.println("Tiempo total de ejecucion: " + (tiempoFin - tiempoInicio) + " milisegundos");
        }catch (InterruptedException e){
            System.err.println("Error esperando a los hilos: " + e.getMessage());
        }*/

        System.out.println("--- Fin del programa ---");
    }
}

/**
 * Clase HiloHola
 * Implemento Runnable, lo que significa que puede ejecutarse como hilo
 * Este hilo esctibe "Hola" 15 veces, esperando 2sg entre cada uno
 * se interrumpe el hilo cuando recibe ina solicitud de interrupcion
 */

class HiloHola implements Runnable{

    //Variable contador para llevar registro de cuantas veces ha escrito
    private int cont = 0;

    /**
     * metodo run() - este es el codigo que ejecutara el hilo
     * se ejecuta cuando llamamos a thread.start(
     */
    @Override
    public void run(){
        //Mensaje indicando que este hilo ha comenzado
        System.out.println("HILO 1 Iniciado");

        try {
            /**
             * Bucle principal
             * Continuo mientras:
             * 1. contador <15 no ha completado las 15 iteraciones
             * 2. !Thread.currentThread().isInterrupted() no se han interrumpido
             */

            while (cont < 15 && !Thread.currentThread().isInterrupted()){

                //Escribe hola sin salto de linea
                System.out.print("Hola ");
                //Incremento el contador
                cont++;

                //El hilo 2 se duerme 2sg
                //Si se interrumpe durante este sleep, se lanza InterruptedException
                Thread.sleep(2000);
            }
        }catch (InterruptedException e){
            //Si se lanza interrumpcion entramos aqui
            System.out.println("\n Interrupcion recibida despues de  " + cont + "interrupciones");
        }

        //Mensaje indicando que el hilo ha finalizado
        System.out.println("Hilo 1 Finalizado");
    }
}

/**
 * CLASE HILOMUNDO
 * similar al hilohola, pero este escribe mundo y un salto de linea
 * este hilo NO sera interrumpido, asi que continuara hasta las 15 interrupciones
 */
class HiloMundo implements Runnable{
    // Variable contador para llevar registro de cuántas veces he escrito
    private int contador = 0;

    /**
     * Metodo run() - Este es el código que ejecutará el hilo
     * Se ejecuta cuando llamamos a thread.start()
     */
    @Override
    public void run() {
        // Mensaje indicando que este hilo ha comenzado
        //System.out.println("[HILO 2] Iniciado");

        try {
            /**
             * BUCLE PRINCIPAL
             * continuo mientras:
             * 1. contador < 15 (no hemos completado 15 iteraciones)
             * 2. !Thread.currentThread().isInterrupted() (no nos han interrumpido)
             *  Este hilo no será interrumpido, así que solo termina por contador
             */

            while (contador < 15 && !Thread.currentThread().isInterrupted()) {

                // Escribe " mundo!" con salto de línea (\n al final)
                System.out.println(" mundo!");

                contador++;

                // El hilo duerme 2 segundos (2000 milisegundos)
                Thread.sleep(2000);
            }

        } catch (InterruptedException e) {
            // Si se lanza InterruptedException, entramos aquí
            // (en nuestro caso, esto NO debería suceder porque no interrumpimos este hilo)
            System.out.println("[HILO 2] Interrupción recibida después de " + contador + " iteraciones");

            // Restauro el estado de interrupción
            Thread.currentThread().interrupt();
        }

        // Mensaje indicando que el hilo ha finalizado su ejecución
        System.out.println("[HILO 2] Finalizado");
    }
}
