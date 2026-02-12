package org.iesch.psp.ProductorConsumidor;

import java.util.Random;

public class NumberConsumer implements Runnable {
    private DropNumber drop;

    public NumberConsumer(DropNumber drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        int sum = 0;
        int number;

        while ((number = drop.take()) != DropNumber.DONE) {
            sum += number;
            System.out.println("Consumidor recibe: " + number + " - Suma acumulada: " + sum);
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {}
        }
        System.out.println("Suma total: " + sum);
    }
}