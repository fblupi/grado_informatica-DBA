package practica3;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.HashMap;

public class AdmiralAckbar extends SingleAgent {

    private final short TAMANO_MAPA = 500;
    private final String NOMBRE_CONTROLADOR = "Cerastes";
    private HashMap<String, PropiedadesDrone> flota;
    private int energia;
    private Celda[][] mapa = new Celda[TAMANO_MAPA][TAMANO_MAPA];
    private boolean terminar;
    private String mundoAVisitar;
    private Estado estadoActual;
    private Imagen imagen;
    private String droneElegido;
    public Imagen _imagen;
    public Estado _estadoActual;

    public AdmiralAckbar(AgentID id, String mundoAVisitar) throws Exception {
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

    private void enviarMensaje(String receptor, int performativa, String contenido) {
	throw new UnsupportedOperationException();
    }

    private void inicializarMapa() {
	throw new UnsupportedOperationException();
    }

    private void actualizarMapa(Percepcion percepcion) {
	throw new UnsupportedOperationException();
    }

    private String elegirMovimiento() {
	throw new UnsupportedOperationException();
    }

    private String parseCoordMov(int x, int y) {
	throw new UnsupportedOperationException();
    }

    private void faseInicial() {
	throw new UnsupportedOperationException();
    }

    private void faseEleccionDrone() {
	throw new UnsupportedOperationException();
    }

    private void fasePercibir() {
	throw new UnsupportedOperationException();
    }

    private void faseRepostar() {
	throw new UnsupportedOperationException();
    }

    private void faseMover() {
	throw new UnsupportedOperationException();
    }

    private void faseObjetivoEncontrado() {
	throw new UnsupportedOperationException();
    }

    private void faseFinalizar() {
	throw new UnsupportedOperationException();
    }
}
