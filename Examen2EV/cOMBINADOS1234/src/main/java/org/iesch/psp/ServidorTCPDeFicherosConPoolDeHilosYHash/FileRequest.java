// --- ServidorTCPDeFicherosConPoolDeHilosYHash/FileRequest.java ---
package org.iesch.psp.ServidorTCPDeFicherosConPoolDeHilosYHash;

import java.io.Serializable;

/**
 * Petición de fichero del cliente al servidor
 * Similar a CalcRequest del PDF (página 39)
 */
public class FileRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // Tipos de petición
    public enum RequestType {
        LIST,       // Listar ficheros disponibles
        DOWNLOAD,   // Descargar fichero
        QUIT        // Desconectar
    }

    private final RequestType type;
    private final String fileName; // Nombre del fichero (para DOWNLOAD)

    /**
     * Constructor para LIST y QUIT
     */
    public FileRequest(RequestType type) {
        this.type = type;
        this.fileName = null;
    }

    /**
     * Constructor para DOWNLOAD
     */
    public FileRequest(RequestType type, String fileName) {
        this.type = type;
        this.fileName = fileName;
    }

    public RequestType getType() { return type; }
    public String getFileName() { return fileName; }
}