package practica3;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Drone extends SingleAgent {

    private final String NOMBRE_CONTROLADOR = "Ackbar";
    private final String NOMBRE_SERVIDOR = "Cerastes";

    public Drone(AgentID id) throws Exception {
	super(id);
    }

    public void init() {
	throw new UnsupportedOperationException();
    }

    public void execute() {
	throw new UnsupportedOperationException();
    }

    public void finalize() {
	throw new UnsupportedOperationException();
    }

    private void enviar(String receptor, int performativa, String contenido) {
	throw new UnsupportedOperationException();
    }
}
