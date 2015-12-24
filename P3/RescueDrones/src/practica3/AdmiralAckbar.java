package practica3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.HashMap;

public class AdmiralAckbar extends SingleAgent {

    private final short TAMANO_MAPA = 500;
    private final String NOMBRE_CONTROLADOR = "Cerastes";
    private HashMap<String, PropiedadesDrone> flota;
    private int energia;
    private Celda[][] mapa = new Celda[TAMANO_MAPA][TAMANO_MAPA];
    private boolean terminar, buscando;
    private String mundoAVisitar;
    private Estado estadoActual;
    private Imagen imagen;
    private String droneElegido;
    public Imagen _imagen;
    public Estado _estadoActual,_estadoencontrado,_estadobusqueda;
    

    public AdmiralAckbar(AgentID id, String mundoAVisitar) throws Exception {
        super(id);
    }

    public void init() {
        iniciarConversacoin();
        inicializarMapa();
        terminar = false;
        buscando = true;

    }

    public void execute() {
        while (!terminar) {
            switch (_estadoActual) {
                case INICIAL:
                    faseInicial();
                    break;

                case BUSCAR:
                    while (buscando) {
                        switch (_estadobusqueda) {
                            case OBJETIVO_ENCONTRADO:
                                buscando = false;
                                faseObjetivoEncontrado();
                                break;
                            case ELECCION_DRONE:
                                faseEleccionDrone();
                                break;
                            case MOVER:
                                faseMover();
                                break;
                            case REPOSTAR:
                                faseRepostar();
                                break;
                            case PERCIBIR:
                                fasePercibir();
                                break;
                        }
                    }
                    break;
                case OBJETIVO_ENCONTRADO:
                    while (!buscando) {
                        switch (_estadoencontrado) {
                            case ELECCION_DRONE:
                                faseEleccionDrone();
                                break;
                            case MOVER:
                                faseMover();
                                break;
                            case REPOSTAR:
                                faseRepostar();
                                break;
                            case PERCIBIR:
                                fasePercibir();
                                break;
                        }
                    }

                    break;
                case FINALIZAR:
                    faseFinalizar();
                    break;

            }
        }
    }

    public void iniciarConversacoin() {

    }

    public void finalize() {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param receptor
     * @param performativa
     * @param contenido 
     * @author José Guadix
     */
    private void enviarMensaje(String receptor, int performativa, String contenido) {
        ACLMessage outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID(receptor));
        outbox.setPerformative(performativa);
	outbox.setContent(contenido);
        this.send(outbox); 
    }

    /**
     * @author José Guadix
     */
    private void inicializarMapa() {
        for (int i = 0; i < TAMANO_MAPA; i++) {
	    for (int j = 0; j < TAMANO_MAPA; j++) {
		mapa[i][j] = Celda.DESCONOCIDA;
	    }
	}
    }

    private void actualizarMapa(Percepcion percepcion) {
        throw new UnsupportedOperationException();
    }

    private String elegirMovimiento() {
        throw new UnsupportedOperationException();
    }

    /**
     * A partir de dos coordenadas selecciona hacia dónde se mueve
     *
     * @author Francisco Javier Bolívar
     * @author Antonio David López
     * @param x coordenada x hacia donde se mueve
     * @param y coordenada y hacia donde se mueve
     * @return movimiento elegido
     */
    private String parserCoordMov(int x, int y) {
	if (x == 1) {
	    if (y == 1) {
		return "moveNW";  // (1, 1)
	    } else if (y == 2) {
		return "moveN";   // (1, 2)
	    } else {
		return "moveNE";  // (1, 3)
	    }
	} else if (x == 2) {
	    if (y == 1) {
		return "moveW";   // (2, 1)
	    } else {
		return "moveE";   // (2, 3)  
	    }
	} else {
	    if (y == 1) {
		return "moveSW";  // (3, 1)
	    } else if (y == 2) {
		return "moveS";   // (3, 2)  
	    } else {
		return "moveSE";  // (3, 3)  
	    }
	}
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
