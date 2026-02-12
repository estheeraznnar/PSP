// --- ServidorTCPDeFicherosConPoolDeHilosYHash/FileResponse.java ---
package org.iesch.psp.ServidorTCPDeFicherosConPoolDeHilosYHash;

import java.io.Serializable;

/**
 * Respuesta del servidor al cliente
 * Similar a CalcResponse del PDF (página 40)
 */
public class FileResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final FileRequest request;  // Petición original
    private final boolean ok;           // Éxito de la operación
    private final String mensaje;       // Mensaje descriptivo
    private final String[] fileList;    // Lista de ficheros (para LIST)
    private final byte[] fileData;      // Datos del fichero (para DOWNLOAD)
    private final String fileHash;      // Hash SHA-256 del fichero

    /**
     * Constructor para LIST
     */
    public FileResponse(FileRequest request, boolean ok, String mensaje, String[] fileList) {
        this.request = request;
        this.ok = ok;
        this.mensaje = mensaje;
        this.fileList = fileList;
        this.fileData = null;
        this.fileHash = null;
    }

    /**
     * Constructor para DOWNLOAD
     */
    public FileResponse(FileRequest request, boolean ok, String mensaje,
                        byte[] fileData, String fileHash) {
        this.request = request;
        this.ok = ok;
        this.mensaje = mensaje;
        this.fileList = null;
        this.fileData = fileData;
        this.fileHash = fileHash;
    }

    /**
     * Constructor para errores y QUIT
     */
    public FileResponse(FileRequest request, boolean ok, String mensaje) {
        this.request = request;
        this.ok = ok;
        this.mensaje = mensaje;
        this.fileList = null;
        this.fileData = null;
        this.fileHash = null;
    }

    // Getters
    public FileRequest getRequest() { return request; }
    public boolean isOk() { return ok; }
    public String getMensaje() { return mensaje; }
    public String[] getFileList() { return fileList; }
    public byte[] getFileData() { return fileData; }
    public String getFileHash() { return fileHash; }
}