
/*Crea una clase llamada Saldo, con un atributo que indique el saldo disponible.
El constructor dará valor inicial al saldo. Crea los métodos get (público) y set (privado)
para el saldo incluyendo un sleep() aleatorio. Crea otro metodo que reciba una cantidad
y se la añada al saldo, este metodo debe informar de quién realiza la operación,
la cantidad, el saldo inicial y el final. Este metodo debe ser definido como synchronized.
Crea una clase que implemente Runnable, desde el metodo run hemos de usar el metodo
que añade la cantidad al saldo. Crea el metodo main un objeto saldo con un valor inicial.
Visualiza el valor inicial. Crea varios hilos que compartan el objeto Saldo,
a cada hilo le asignamos un nombre y una cantidad. Lanzamos los hilos y esperamos que finalicen
para visualizar el saldo final. Comprueba el funcionamiento de la aplicación si quitando
synchronized de la declaración del metodo.*/

import java.util.Random;

class Saldo{
    private double saldo;

    public Saldo(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public synchronized void añadirSaldo(double cantidad, String nombreHilo){

        try {
            double saldoInicial = saldo;
            System.out.println(nombreHilo + " va a añadir " + cantidad + " | Saldo inicial: " + saldoInicial);
            Thread.sleep(new Random().nextInt(1000));
            saldo += cantidad;
            System.out.println(nombreHilo + " ha añadido " + cantidad + " | Saldo final: " + saldo);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public double getSaldo(){
        return saldo;
    }

}

class OperacionSaldo implements Runnable{
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

public class Ejercicio05 {

    public static void main(String[] args) {
      Saldo cuentaa = new Saldo(1000);
        System.out.println("El saldo inicial es: " + cuentaa.getSaldo());

        Thread s1 = new Thread(new OperacionSaldo(cuentaa, 100), "Hilo-1");
        Thread s2 = new Thread(new OperacionSaldo(cuentaa, 200), "Hilo-2");
        Thread s3 = new Thread(new OperacionSaldo(cuentaa, 300), "Hilo-3");

        s1.start();
        s2.start();
        s3.start();

        try {
            s1.join();
            s2.join();
            s3.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Saldo final: " + cuentaa.getSaldo());
    }

}

