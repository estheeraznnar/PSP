import modelo.Contacto;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

    private static final long serialVersionUID = 1L;
    public final boolean ok;
    public final String message;
    public final List<Contacto> data;

    public Response(boolean ok, String message, List<Contacto> data) {
        this.ok = ok;
        this.message = message;
        this.data = data;
    }

    public static Response okWithData(List<Contacto> data) {
        return new Response(true, null, data);
    }

    public static Response okMessage(String msg) {
        return new Response(true, msg, null);
    }

    public static Response error(String msg) {
        return new Response(false, msg, null);
    }

}
