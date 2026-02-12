package org.iesch.psp.Inmutables;

public record ImmutablePoint(int x, int y) {

    public ImmutablePoint translate(int dx, int dy) {
        return new ImmutablePoint(x + dx, y + dy);
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }

    public static void main(String[] args) {
        ImmutablePoint p1 = new ImmutablePoint(5, 10);
        System.out.println("Punto original: " + p1);

        ImmutablePoint p2 = p1.translate(3, -2);
        System.out.println("Punto trasladado: " + p2);

        // El punto original no ha cambiado
        System.out.println("Punto original después de traslación: " + p1);
    }
}