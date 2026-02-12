package org.iesch.psp.ForkJoin;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MinNumberTask extends RecursiveTask<Integer> {
    final static int TOTAL = 100_000_000;
    final static int THRESHOLD = 10_000_000;

    int[] numbers;
    int first;
    int length;

    public MinNumberTask(int[] numbers, int first, int length) {
        this.numbers = numbers;
        this.first = first;
        this.length = length;
    }

    // Obtenemos el mínimo de manera iterativa
    private int getMinIterative() {
        int min = Integer.MAX_VALUE;
        int high = first + length;
        for (int i = first; i < high; i++) {
            if (numbers[i] < min) {
                min = numbers[i];
            }
        }
        return min;
    }

    // Obtenemos el mínimo de manera recursiva
    private int getMinRecursive() {
        int mid = length / 2;
        MinNumberTask task1 = new MinNumberTask(numbers, first, mid);
        task1.fork();
        MinNumberTask task2 = new MinNumberTask(numbers, first + mid, length - mid);
        task2.fork();

        return Math.min(task1.join(), task2.join());
    }

    static int[] generateArray(int size) {
        int[] numbers = new int[size];
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = r.nextInt();
        }
        return numbers;
    }

    @Override
    protected Integer compute() {
        Integer min = 0;
        if (length > THRESHOLD) {
            min = getMinRecursive();
        } else {
            min = getMinIterative();
        }
        return min;
    }

    public static void main(String[] args) {
        int[] numbers = generateArray(TOTAL);

        MinNumberTask task = new MinNumberTask(numbers, 0, TOTAL);

        // Búsqueda secuencial
        long start = System.currentTimeMillis();
        int min = task.getMinIterative();
        long end = System.currentTimeMillis();
        System.out.println("Min number (" + min + ") iterative found in " + (end - start) + " ms");

        // Búsqueda recursiva en paralelo con fork-join
        ForkJoinPool pool = new ForkJoinPool();
        start = System.currentTimeMillis();
        Integer res = pool.invoke(task);
        end = System.currentTimeMillis();
        System.out.println("Min number (" + res + ") recursive parallel found in " + (end - start) + " ms");
    }
}