import java.util.*;
import java.util.concurrent.*;

public class SumList {

    static class Addition implements Callable<Integer> {
        private int op1;
        private int op2;

        public Addition(int operador1, int operador2) {
            this.op1 = operador1;
            this.op2 = operador2;
        }

        @Override
        public Integer call() throws Exception {
            return op1 + op2;
        }
    }

    public static void main(String[] args) {
        // Creamos la instancia de ThreadPoolExecutor con 4 hilos
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        // Creamos un ArrayList para las tareas
        List<Addition> taskList = new ArrayList<Addition>();

        // Creamos las tareas a ejecutar; 10 sumas de 2 números aleatorios
        for (int i = 0; i < 10; i++) {
            int op1 = (int) (Math.random() * 100);
            int op2 = (int) (Math.random() * 100);
            Addition suma = new Addition(op1, op2);
            System.out.println("Task " + i + " is: " + op1 + " + " + op2);
            taskList.add(suma);
        }

        // Creamos la lista para los resultados
        List<Future<Integer>> resultList;

        try {
            // Lanzamos todas las tareas para su ejecución
            resultList = executor.invokeAll(taskList);

            // Pedimos al ejecutor que finalice
            executor.shutdown();

            // Mostramos los resultados
            for (int i = 0; i < resultList.size(); i++) {
                Future<Integer> res = resultList.get(i);
                System.out.println("Result of task " + i + " is: " + res.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}