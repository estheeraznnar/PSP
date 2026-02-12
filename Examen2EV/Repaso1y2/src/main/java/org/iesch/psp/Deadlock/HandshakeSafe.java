package org.iesch.psp.Deadlock;

public class HandshakeSafe {

    static class Person {
        private final String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void handshake(Person other) {
            // Ordenar los objetos para realizar el bloqueo de 2 objetos
            // siempre en el mismo orden evitando de esta forma el deadlock
            Person first, second;
            if (System.identityHashCode(this) < System.identityHashCode(other)) {
                first = this;
                second = other;
            } else {
                first = other;
                second = this;
            }

            // Sincronizar el apretón bloqueando ambos objetos
            synchronized (first) {
                synchronized (second) {
                    System.out.format("%s: %s me está dando la mano!%n",
                            this.name, other.getName());
                    other.handshakeBack(this);
                }
            }
        }

        public void handshakeBack(Person other) {
            System.out.format("%s: %s me ha devuelto el apretón!%n",
                    this.name, other.getName());
        }
    }

    public static void main(String[] args) {
        final Person juan = new Person("Juan");
        final Person maria = new Person("María");

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