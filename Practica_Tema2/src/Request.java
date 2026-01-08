import java.io.Serializable;

public class Request implements Serializable {

    public static final long serialVersionUID = 1L;
    public enum Type {LIST, BUSCAR, QUIT, ENTER}
    public final Type type;
    public final String argument;

    public Request(Type type, String argument) {
        this.type = type;
        this.argument = argument;
    }

    public static Request list() { return new Request(Type.LIST, null);}
    public static Request buscar(String nombre) { return new Request(Type.BUSCAR, nombre);}
    public static Request quit() { return new Request(Type.QUIT, null);}
    public static Request enter() { return new Request(Type.ENTER, null);}
}
