package org.iesch.psp.Deadlock;

public class HandshakeDeadlock {

    static class Person {
        private final String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public synchronized void handshake(Person other) {
            System.out.format("%s: %s me está dando la mano!%n", this.name, other.getName());
            other.handshakeBack(this);
        }

        public synchronized void handshakeBack(Person other) {
            System.out.format("%s: %s me ha devuelto el apretón!%n", this.name, other.getName());
        }
    }

    public static void main(String[] args) {
        final Person juan = new Person("Juan");
        final Person maria = new Person("María");

        // Este código puede causar deadlock
        new Thread(new Runnable() {
            public void run() {
                juan.handshake(maria);
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                maria.handshake(juan);
            }
        }).start();
    }
}