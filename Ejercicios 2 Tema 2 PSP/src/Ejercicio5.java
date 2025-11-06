import java.util.Random;

class Saldo {
    private double saldo;

    public Saldo(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public synchronized void añadirSaldo(double cantidad, String nombreHilo) {
        try {
            double saldoInicial = saldo;
            System.out.println(nombreHilo + " va a añadir " + cantidad + " | Saldo inicial: " + saldoInicial);
            Thread.sleep(new Random().nextInt(1000)); // simulamos retardo
            saldo += cantidad;
            System.out.println(nombreHilo + " ha añadido " + cantidad + " | Saldo final: " + saldo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getSaldo() {
        return saldo;
    }
}

class OperacionSaldo implements Runnable {
    private final Saldo saldo;
    private final double cantidad;

    public OperacionSaldo(Saldo saldo, double cantidad) {
        this.saldo = saldo;
        this.cantidad = cantidad;
    }

    @Override
    public void run() {
        saldo.añadirSaldo(cantidad, Thread.currentThread().getName());
    }
}

public class Ejercicio5 {
    public static void main(String[] args) {
        Saldo cuenta = new Saldo(1000);
        System.out.println("💰 Saldo inicial: " + cuenta.getSaldo());

        Thread t1 = new Thread(new OperacionSaldo(cuenta, 200), "Hilo-1");
        Thread t2 = new Thread(new OperacionSaldo(cuenta, 300), "Hilo-2");
        Thread t3 = new Thread(new OperacionSaldo(cuenta, 150), "Hilo-3");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("✅ Saldo final: " + cuenta.getSaldo());
    }
}
