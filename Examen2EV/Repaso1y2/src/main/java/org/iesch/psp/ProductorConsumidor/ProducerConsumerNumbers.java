package org.iesch.psp.ProductorConsumidor;

public class ProducerConsumerNumbers {
    public static void main(String[] args) {
        DropNumber drop = new DropNumber();
        (new Thread(new NumberProducer(drop))).start();
        (new Thread(new NumberConsumer(drop))).start();
    }
}