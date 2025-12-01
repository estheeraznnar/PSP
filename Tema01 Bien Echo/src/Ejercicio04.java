
/*Crea una aplicación que conste de 2 hilos; el primero el hilo principal de la aplicación Java.
El hilo principal deberá lanzar un nuevo hilo encargado de imprimir por consola
los siguientes mensajes con un intervalo de 4 segundos entre cada uno de ellos
(Mensajes: “Programas”, “Procesos”, “Servicios”, “Hilos”).
El hilo principal debe quedar a la espera hasta que termine, mostrando cada segundo que está esperando
por la finalización del hilo hijo. La ejecución del programa debe durar 16s ya que son 4 mensajes
y 4s de espera por cada uno. Para poder reducir la duración de la ejecución
el programa debe aceptar por parámetro (metodo main) el tiempo de espera máximo que el hilo principal
esperará a la ejecución del hilo secundario, una vez superado ese tiempo el hilo principal
debe interrumpir la ejecución del hilo secundario y a partir de ese momento el hilo secundario
mostrará los mensajes restantes sin esperas entre la impresión de los mensajes para finalizar
la ejecución del hilo cuanto antes. El hilo principal debe mostrar por consola el mensaje
de finalización de la ejecución del programa. Puedes imprimir los mensajes que consideres
oportunos para verificar la correcta ejecución del programa. Calcula el tiempo de ejecución
del hilo principal y muéstralo por consola. Incluye el nombre del hilo que imprime por consola
cada vez que muestres un mensaje de salida.*/

public class Ejercicio04 {

    public static void main(String[] args) {

        //Tiempo maximo que espera el hilo principal
        long tiempMax = 16000;
        //Si se pasa un argumento por linea de comandos se convierte a milisegundos
        if (args.length > 0){
            tiempMax = Long.parseLong(args[0]) * 1000;
        }

        //Array de mensajes que imprimira el hilo secundario
        String[] mensajes = {"Programas", "Procesos", "Servicios", "Hilos"};
        //Creacion del hilo secundario con un nimbre personalizado
        Thread hiloSecun = new Thread(new HiloImpreso(mensajes), "Hilo-Secun");

        //Mensaje de inicio desde el hilo principal
        System.out.println("[" + Thread.currentThread().getName() + "] Iniciando ejecucion...");
        long inicio = System.currentTimeMillis(); //Marca de tiempo del inicio total del programa
        hiloSecun.start();//Inicio el hilo secundario
        long tiempIni = System.currentTimeMillis();//cuenta el tiempo transcurrido del hilo secun

        try {
            //Bucle que se ejecuta mientras el hilo secundario este vivo
            while (hiloSecun.isAlive()){
                //Calcula el tiempo transcuttido desde que inicio el hilo
                long tiempoTrans = System.currentTimeMillis() - tiempIni;
                if (tiempoTrans > tiempMax){//Si se supera el tiempo maximo, interrumpe al hilo secundario
                    System.out.println("Tiempo maximo ha sido superado. Interrumpiendo al hilo secundario..");
                    hiloSecun.interrupt();
                    break;
                }

                //Mensaje incicando que el hilo principal esta esperando
                System.out.println("[" + Thread.currentThread().getName() + "] Esperando al hilo hijo...");
                Thread.sleep(1000);//Pausa 1sg antes de la siguiente verificacion
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        //Marca de tiempo del final del programa
        long fin = System.currentTimeMillis();
        //Muestra el tiempo total de ejecucion en segundos
        System.out.println("[" + Thread.currentThread().getName() + "] Programa finalizado en " + (fin - inicio) / 1000.0 + " s.");
    }

}

//Clase que implementa el runnable para ejecutar la tarea del hilo secundario
class HiloImpreso implements Runnable{
    //Array de mensajes que se imprimiran
    private final String[] mensajes;

    //constructor que recibe el array de mensajes
    public HiloImpreso(String[] mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public void run() {
        //Recorro cada mensaje del array
        for (String ms : mensajes){
            //Imprimo el mensaje actual con el nombre del hilo
            System.out.println("[" + Thread.currentThread().getName() + "] " + ms);

            try {
                //Pausa 4sg entre mensajes
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                //Si el hilo es interrumpido muesta los mensajes restantes sin esperas
                System.out.println("[" + Thread.currentThread().getName() + "] Interrumpido. Mostrando mensajes restantes sin esperas...");
                for (String rest : mensajes){//Imprime todos los mensajes restantes sin pausas
                    System.out.println("[" + Thread.currentThread().getName() + "] " + rest);
                }
                break;//sale del bucle principal
            }
        }
        //Mensaje de finalizacion del hilo
        System.out.println("[" + Thread.currentThread().getName() + "] Finaliza.");
    }
}
