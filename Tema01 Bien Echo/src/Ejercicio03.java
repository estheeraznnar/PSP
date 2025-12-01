
/*Crea un programa que trabaje con hilos. El hilo principal deberá lanzar 2 hilos,
el primero escribirá por consola 15 veces “Hola “ cada 2 segundos.
El segundo hilo escribirá “ mundo!” y el retorno de carro otras 15 veces también cada 2 segundos.
Si el hilo principal lanza el segundo con un pequeño retraso (unos 20ms)
el texto se mostrará por consola sin percatarnos que lo están escribiendo 2 hilos diferentes.
Modifica el programa de manera que el hilo principal interrumpa al primer hilo transcurridos
5s desde el arranque de los dos hilos. La respuesta ante la interrupción debe consistir
en la salida y finalización de la ejecución del hilo interrumpido.
*/

public class Ejercicio03 {

    public static void main(String[] args) {
        Thread hilo1 = new Thread(new HiloHola(), "Hilo-Hola");
        Thread hilo2 = new Thread(new HiloMundo(), "Hilo-Mundo");

        hilo1.start();

        try {
            Thread.sleep(20); //Micro retraso para lazar el segundo hilo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hilo2.start();

        try {
            //Espero 5g para interrumpir el primer hilo
            Thread.sleep(5000);
            System.out.println("\n Interrumpiendo el hilo 1");
            hilo1.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class HiloHola implements Runnable{

    @Override
    public void run() {
        try {
            for (int i = 0; i < 15; i++) {
                System.out.print("Hola ");
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("\n " + Thread.currentThread().getName() + "Interrumpido. Terminando...");
        }
    }

}

class HiloMundo implements Runnable{

    @Override
    public void run() {
        try {
            for (int i = 0; i < 15; i++) {
                System.out.println("mundo!");
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("\n " + Thread.currentThread().getName() + " interrumpido.");
        }
    }

}
