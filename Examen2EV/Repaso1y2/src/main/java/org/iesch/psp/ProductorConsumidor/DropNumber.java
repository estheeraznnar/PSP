package org.iesch.psp.ProductorConsumidor;

public class DropNumber {
    public static final int DONE = -1;
    private int number;
    private boolean empty = true;

    public synchronized int take() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        empty = true;
        notifyAll();
        return number;
    }

    public synchronized void put(int number) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        empty = false;
        this.number = number;
        notifyAll();
    }
}