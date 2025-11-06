public class Ejercicio3 {
    public static void main(String[] args) {
        Thread hilo1 = new Thread(new HiloHola(), "Hilo-Hola");
        Thread hilo2 = new Thread(new HiloMundo(), "Hilo-Mundo");

        hilo1.start();

        try {
            Thread.sleep(20); // pequeño retraso antes de lanzar el segundo hilo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hilo2.start();

        try {
            // Esperamos 5 segundos antes de interrumpir el primer hilo
            Thread.sleep(5000);
            System.out.println("\n🛑 Interrumpiendo al hilo 1...");
            hilo1.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// --- Hilo que imprime "Hola "
class HiloHola implements Runnable {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 15; i++) {
                System.out.print("Hola ");
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("\n⚠️ " + Thread.currentThread().getName() + " interrumpido. Terminando...");
        }
    }
}

// --- Hilo que imprime "mundo!"
class HiloMundo implements Runnable {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 15; i++) {
                System.out.println("mundo!");
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("\n⚠️ " + Thread.currentThread().getName() + " interrumpido.");
        }
    }
}
