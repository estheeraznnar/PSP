public class Ejercicio4 {
    public static void main(String[] args) {
        // Tiempo máximo que esperará el hilo principal (en milisegundos)
        long tiempoMaximo = 16000; // valor por defecto (16 s)
        if (args.length > 0) {
            tiempoMaximo = Long.parseLong(args[0]) * 1000;
        }

        String[] mensajes = {"Programas", "Procesos", "Servicios", "Hilos"};
        Thread hiloSecundario = new Thread(new HiloImpresor(mensajes), "Hilo-Secundario");

        System.out.println("[" + Thread.currentThread().getName() + "] Iniciando ejecución...");
        long inicio = System.currentTimeMillis();

        hiloSecundario.start();

        long tiempoInicio = System.currentTimeMillis();
        try {
            while (hiloSecundario.isAlive()) {
                long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
                if (tiempoTranscurrido > tiempoMaximo) {
                    System.out.println("⏰ Tiempo máximo superado. Interrumpiendo al hilo secundario...");
                    hiloSecundario.interrupt();
                    break;
                }
                System.out.println("[" + Thread.currentThread().getName() + "] Esperando al hilo hijo...");
                Thread.sleep(1000);
            }

            hiloSecundario.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long fin = System.currentTimeMillis();
        System.out.println("✅ [" + Thread.currentThread().getName() + "] Programa finalizado en " + (fin - inicio) / 1000.0 + " s.");
    }
}

class HiloImpresor implements Runnable {
    private final String[] mensajes;

    public HiloImpresor(String[] mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public void run() {
        for (String msg : mensajes) {
            System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                System.out.println("⚠️ [" + Thread.currentThread().getName() + "] Interrumpido. Mostrando mensajes restantes sin esperas...");
                for (String restante : mensajes) {
                    System.out.println("[" + Thread.currentThread().getName() + "] " + restante);
                }
                break;
            }
        }
        System.out.println("[" + Thread.currentThread().getName() + "] Finaliza.");
    }
}
