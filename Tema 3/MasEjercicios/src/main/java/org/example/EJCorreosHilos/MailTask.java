package org.example.EJCorreosHilos;

/**
 * Tarea que envía UN correo usando BulkMailClient.
 * Está pensada para ejecutarse en un hilo (ExecutorService).
 */
public class MailTask implements Runnable {

    private final String smtpHost;
    private final int smtpPort;
    private final String from;
    private final String password;
    private final String to;
    private final String subject;
    private final String body;
    private final MailStats stats;

    public MailTask(String smtpHost, int smtpPort, String from, String password,
                    String to, String subject, String body, MailStats stats) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.from = from;
        this.password = password;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.stats = stats;
    }

    /**
     * Cuando el hilo arranca, crea un BulkMailClient y envía un correo.
     * Actualiza las estadísticas según el resultado.
     */
    @Override
    public void run() {
        BulkMailClient client = new BulkMailClient(smtpHost, smtpPort, from, password);
        boolean ok = client.sendMail(to, subject, body);
        if (ok) {
            stats.incOk();
        } else {
            stats.incFail();
        }
    }
}
