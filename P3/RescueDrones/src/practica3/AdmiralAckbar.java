package practica3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

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
    public Estado _estadoActual, _estadoencontrado, _estadobusqueda;

    public AdmiralAckbar(AgentID id, String mundoAVisitar) throws Exception {
	super(id);
	this.mundoAVisitar = mundoAVisitar;
    }

    public void init() {
	System.out.println(getName() + " Iniciandose ");
	inicializarMapa();
//	imagen.mostar();
	terminar = false;
	buscando = true;
	flota = new HashMap<>();
	flota.put("Drone0", null);
	flota.put("Drone1", null);
	flota.put("Drone2", null);
	flota.put("Drone3", null);
	_estadoActual = Estado.INICIAL;
    }

    public void execute() {
	System.out.println("Execute terminar: "  + terminar + " fase: " + _estadoActual);
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

    @Override
    public void finalize() {
	finalizarConversacion();
	guardarLog();
	imagen.guardarPNG(mundoAVisitar + " - " + JSON.getKey() + ".png");
	imagen.cerrar();
	super.finalize();
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
	System.out.println("Enviando mensaje a " + receptor + " tipo " + outbox.getPerformative() + " contenido " + contenido);
	this.send(outbox);
    }

    private ACLMessage recibirMensaje() {
	ACLMessage message = null;
	try {
	    message = receiveACLMessage();
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	}
	return message;
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

    private void iniciarConversacion() {
	String contenido = JSON.suscribirse(mundoAVisitar);
	enviarMensaje(NOMBRE_CONTROLADOR, ACLMessage.SUBSCRIBE, contenido);
	ACLMessage message = recibirMensaje();
	if (message != null && message.getPerformativeInt() == ACLMessage.INFORM) {
	    JSON.guardarKey(message.getContent());
	    for (String string : flota.keySet()) {
		contenido = JSON.checkin();
		enviarMensaje(string, ACLMessage.REQUEST, contenido);
	    }
	    for (int i = 0; i < flota.size(); i++) {
		message = recibirMensaje();
		if (message != null && message.getPerformativeInt() == ACLMessage.INFORM) {
		    PropiedadesDrone propiedades = new PropiedadesDrone();
		    propiedades.setRol(JSON.getRol(message.getContent()));
		    flota.put(message.getSender().toString(), propiedades);
		}
	    }
	}
	if (message == null) {
	    _estadoActual = Estado.FINALIZAR;
	}
    }

    private void inicializarPropiedadesDrone() {
	String contenido, nombreDrone;
	ACLMessage message = null;
	PropiedadesDrone propiedades;
	Percepcion percepcion;
	for (String string : flota.keySet()) {
	    contenido = JSON.key();
	    enviarMensaje(string, ACLMessage.QUERY_REF, contenido);
	}
	for (int i = 0; i < flota.size(); i++) {
	    message = recibirMensaje();
	    if (message == null) {
		_estadoActual = Estado.FINALIZAR;
	    } else if (message.getPerformativeInt() == ACLMessage.INFORM) {
		nombreDrone = message.getSender().toString();
		propiedades = flota.get(nombreDrone);
		percepcion = JSON.getPercepcion(message.getContent());
		propiedades.actualizarPercepcion(percepcion);
		flota.put(nombreDrone, propiedades);
		actualizarMapa(percepcion);
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
	System.out.println("FASE INICIAL");
	iniciarConversacion();
	inicializarPropiedadesDrone();
	if (_estadoActual != Estado.FINALIZAR) {
//	    _estadoActual = Estado.BUSCAR;
	    _estadoActual = Estado.FINALIZAR;
	}
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
	terminar = true;
    }

    private void finalizarConversacion() {
	String contenido = JSON.key();
	for (String string : flota.keySet()) {
	    enviarMensaje(string, ACLMessage.CANCEL, contenido);
	}
	enviarMensaje(NOMBRE_CONTROLADOR, ACLMessage.CANCEL, contenido);
    }

    private void guardarLog() {
	BufferedWriter writer = null;
	try {
	    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mundoAVisitar + " - " + JSON.getKey() + ".txt"), "utf-8"));
	    for (Map.Entry<String, PropiedadesDrone> par : flota.entrySet()) {
		String key = par.getKey();
		PropiedadesDrone value = par.getValue();
		writer.write(key);
		writer.newLine();
		writer.write("Rol: " + value.getRol().toString());
		writer.newLine();
		writer.write("Llegado: " + value.getLlegado());
		writer.newLine();
		writer.newLine();
	    }
	} catch (Exception ex) {
	    System.err.println(ex.getMessage());
	} finally {
	    if (writer != null) {
		try {
		    writer.close();
		} catch (IOException ex) {
		    System.out.println(ex.getMessage());
		}
	    }
	}
    }
}
