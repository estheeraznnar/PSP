package org.iesch.psp.SistemaProductorConsumidorParaEnvíodeCorreos;// --- ConsumidorCorreos.java ---
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class ConsumidorCorreos implements Runnable {

    private ColaCorreos cola;
    private String remitente;
    private String password;
    private String asunto;
    private String cuerpo;
    private int enviados = 0;
    private int errores = 0;

    public ConsumidorCorreos(ColaCorreos cola, String remitente,
                             String password, String asunto, String cuerpo) {
        this.cola = cola;
        this.remitente = remitente;
        this.password = password;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
    }

    @Override
    public void run() {
        String destinatario;

        while ((destinatario = cola.tomar()) != null) {
            if (enviarCorreo(destinatario)) {
                enviados++;
                System.out.println("[" + Thread.currentThread().getName()
                        + "] Enviado a: " + destinatario);
            } else {
                errores++;
                System.out.println("[" + Thread.currentThread().getName()
                        + "] Error enviando a: " + destinatario);
            }
        }

        System.out.println("[" + Thread.currentThread().getName()
                + "] Finalizado. Enviados: " + enviados + ", Errores: " + errores);
    }

    private boolean enviarCorreo(String destinatario) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remitente, password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.setSubject(asunto);
            message.setText(cuerpo);

            Transport.send(message);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public int getEnviados() { return enviados; }
    public int getErrores() { return errores; }
}