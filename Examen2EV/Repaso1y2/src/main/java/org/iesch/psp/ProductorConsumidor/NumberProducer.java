package org.iesch.psp.ProductorConsumidor;

import java.util.Random;

public class NumberProducer implements Runnable {
    private DropNumber drop;

    public NumberProducer(DropNumber drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        for (int i = 1; i <= 10; i++) {
            System.out.println("Productor envía: " + i);
            drop.put(i);
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {}
        }
        drop.put(DropNumber.DONE);
    }
}