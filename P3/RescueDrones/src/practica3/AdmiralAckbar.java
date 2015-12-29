package practica3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdmiralAckbar extends SingleAgent {

    private final short TAMANO_MAPA = 500;
    private final String NOMBRE_CONTROLADOR = "Cerastes";
    private short tamanoMapa = 0;
    private HashMap<String, PropiedadesDrone> flota;
    private int energia;
    private Celda[][] mapa = new Celda[TAMANO_MAPA][TAMANO_MAPA];
    private double[][] scanner = new double[TAMANO_MAPA][TAMANO_MAPA];
    private boolean terminar, buscando;
    private String mundoAVisitar;
    private Imagen imagen;
    private String droneElegido;
    private Estado estadoActual, subEstadoBuscando, subEstadoEncontrado;
    private int pasos = 0;
    private int pasosMaximos = 0;
    private Point puntoObjetivo = null;

    public AdmiralAckbar(AgentID id, String mundoAVisitar) throws Exception {
	super(id);
	this.mundoAVisitar = mundoAVisitar;
    }

    public void init() {
	System.out.println(getName() + " Iniciandose ");
	inicializarMapa();
	imagen = new Imagen(mapa, mundoAVisitar);
	imagen.mostar();
	terminar = false;
	buscando = true;
	flota = new HashMap<>();
	flota.put("Drone0", null);
	flota.put("Drone1", null);
	flota.put("Drone2", null);
	flota.put("Drone3", null);
	estadoActual = Estado.INICIAL;
    }

    public void execute() {
	System.out.println("Execute terminar: " + terminar + " fase: " + estadoActual);
	while (!terminar) {
	    switch (estadoActual) {
		case INICIAL:
		    faseInicial();
		    break;

		case BUSCAR:
		    switch (subEstadoBuscando) {
			case OBJETIVO_ENCONTRADO:
			    buscando = false;
			    faseObjetivoEncontrado();
			    break;
			case ELECCION_DRONE:
			    faseEleccionDrone();
			    break;
			case MOVER:
			    generarPuntoObjetivo();
			    faseMover();
			    break;
			case REPOSTAR:
			    faseRepostar();
			    break;
			case PERCIBIR:
			    fasePercibir();
			    objetivoEncontrado();
			    break;
		    }
		    break;
		case OBJETIVO_ENCONTRADO:
		    switch (subEstadoEncontrado) {
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
	guardarImagen();
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
	System.out.println(getName() + ": enviando mensaje a " + receptor + " tipo " + outbox.getPerformative() + " contenido " + contenido);
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

    /**
     * @author José Guadix
     */
    private void iniciarConversacion() {
	String contenido = JSON.suscribirse(mundoAVisitar);
	enviarMensaje(NOMBRE_CONTROLADOR, ACLMessage.SUBSCRIBE, contenido);
	ACLMessage message = null;
	try {
	    message = receiveACLMessage();
	    if (message.getPerformativeInt() == ACLMessage.INFORM) {
		JSON.guardarKey(message.getContent());
		for (String string : flota.keySet()) {
		    contenido = JSON.checkin();
		    enviarMensaje(string, ACLMessage.REQUEST, contenido);
		}
		for (int i = 0; i < flota.size(); i++) {
		    message = receiveACLMessage();
		    if (message != null && message.getPerformativeInt() == ACLMessage.INFORM) {
			PropiedadesDrone propiedades = new PropiedadesDrone();
			propiedades.setRol(JSON.getRol(message.getContent()));
			flota.put(message.getSender().name, propiedades);
		    }
		}
	    }
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	    estadoActual = Estado.FINALIZAR;
	}
    }

    /**
     * @author José Guadix
     */
    private void inicializarPropiedadesDrone() {
	String contenido, nombreDrone;
	ACLMessage message = null;
	PropiedadesDrone propiedades;
	Percepcion percepcion;
	for (String string : flota.keySet()) {
	    contenido = JSON.key();
	    enviarMensaje(string, ACLMessage.QUERY_REF, contenido);
	}
	try {
	    for (int i = 0; i < flota.size(); i++) {
		message = receiveACLMessage();
		if (message.getPerformativeInt() == ACLMessage.INFORM) {
		    nombreDrone = message.getSender().name;
		    propiedades = flota.get(nombreDrone);
		    percepcion = JSON.getPercepcion(message.getContent());
		    percepcion.setNombreDrone(nombreDrone);
		    propiedades.actualizarPercepcion(percepcion);
		    flota.put(nombreDrone, propiedades);
		    if (percepcion.getGps().x == 99) {
			tamanoMapa = 100;
		    } else if (percepcion.getGps().x == 499 || percepcion.getGps().y >= 100) {
			tamanoMapa = 500;
		    }
		    actualizarMapa(percepcion);
		}
	    }
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	    estadoActual = Estado.FINALIZAR;
	}
    }

    private void actualizarMapa(Percepcion percepcion) {
	int posX = percepcion.getGps().x;
	int posY = percepcion.getGps().y;
	mapa[posX][posY] = Celda.getRecorrido(percepcion.getNombreDrone());   // Guarda posición actual como posición por donde ha pasado
	int[][] radar = percepcion.getRadar();
	int tam = radar.length;
	for (int i = 0, y = posY - tam / 2; i < tam; i++, y++) {
	    for (int j = 0, x = posX - tam / 2; j < tam; j++, x++) {
		if ((x >= 0 && x < TAMANO_MAPA) && (y >= 0 && y < TAMANO_MAPA)) { // No se sale del límite
		    if (mapa[x][y] == Celda.DESCONOCIDA) { // No machaca pasos anteriores
			mapa[x][y] = Celda.getCelda(radar[i][j]);   // Actualiza casilla con el valor recibido del radar
		    }
		}
	    }
	}
	imagen.actualizarMapa(mapa);
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

    /**
     * @author José Guadix
     */
    private void faseInicial() {
	iniciarConversacion();
	inicializarPropiedadesDrone();
	if (estadoActual != Estado.FINALIZAR) {
	    estadoActual = Estado.BUSCAR;
	}
	if (tamanoMapa == 0) {
	    estadoActual = Estado.INICIAL;
	}
    }

    /**
     * 
     * @author José Guadix
     */
    private void faseEleccionDrone() {
	if (buscando) {
	    int rolMax = -1;
	    int distanciaMin = Integer.MAX_VALUE, x, y, distancia;
	    for (Map.Entry<String, PropiedadesDrone> par : flota.entrySet()) {
		String nombre = par.getKey();
		PropiedadesDrone propiedades = par.getValue();

		x = propiedades.getGps().x;
		y = propiedades.getGps().y;
		distancia = Math.abs(x - y);
		if (propiedades.getLlegado()) {
		    distanciaMin = 0;
		    rolMax = propiedades.getRol().getId();
		    droneElegido = nombre;
		} else if (propiedades.getRol().getId() == rolMax) {
		    if (distancia < distanciaMin) {
			distanciaMin = distancia;
			rolMax = propiedades.getRol().getId();
			droneElegido = nombre;
		    }
		} else if (propiedades.getRol().getId() > rolMax) {
		    distanciaMin = distancia;
		    rolMax = propiedades.getRol().getId();
		    droneElegido = nombre;
		}
	    }
	} else {

	}
    }

    private void fasePercibir() {
	try {
	    ACLMessage message = receiveACLMessage();
	    if (message.getPerformativeInt() == ACLMessage.INFORM) {
		String nombreDrone = message.getSender().name;
		PropiedadesDrone propiedades = flota.get(nombreDrone);
		Percepcion percepcion = JSON.getPercepcion(message.getContent());
		percepcion.setNombreDrone(nombreDrone);
		propiedades.actualizarPercepcion(percepcion);
		flota.put(nombreDrone, propiedades);
		actualizarMapa(percepcion);
	    }
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	    estadoActual = Estado.FINALIZAR;
	}
    }

    private void faseRepostar() {
	enviarMensaje(droneElegido, ACLMessage.REQUEST, JSON.repostar());
	try {
	    receiveACLMessage();
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	    estadoActual = Estado.FINALIZAR;
	}
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
	File file = new File("seguimiento");
	if (!file.exists()) {
	    file.mkdir();
	}
	BufferedWriter writer = null;
	try {
	    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("seguimiento/" + mundoAVisitar + " - " + JSON.getKey() + ".txt"), "utf-8"));
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

    private void guardarImagen() {
	for (Map.Entry<String, PropiedadesDrone> par : flota.entrySet()) { //Cambia el color de la ultima posición si no ha llegado
	    String key = par.getKey();
	    PropiedadesDrone value = par.getValue();
	    if (!value.getLlegado()) {
		mapa[value.getGps().x][value.getGps().y] = Celda.getUlt_Posicion(key);
	    }
	}
	imagen.guardarPNG("seguimiento/" + mundoAVisitar + " - " + JSON.getKey() + ".png");
	imagen.cerrar();
    }

    /**
     * Comprueba si se ha encontrado el objetivo en el mapa global
     *
     * @author Francisco Javier Bolívar
     * @return punto donde se encuentra el objetivo, null si no lo encuentra
     */
    private Point objetivoEncontrado() {
	Point p = null;
	for (int i = 0; i < TAMANO_MAPA; i++) {
	    for (int j = 0; j < TAMANO_MAPA; j++) {
		if (mapa[i][j] == Celda.OBJETIVO) {
		    p.x = i;
		    p.y = j;
		    return p;
		}
	    }
	}
	return p;
    }

    /**
     * Genera el punto objetivo para la busqueda
     *
     * @author Antonio David López Machado
     */
    private void generarPuntoObjetivo() {
	Point p = null;
	p.x = flota.get(droneElegido).getGps().x;
	p.y = flota.get(droneElegido).getGps().y;
	if (pasos > pasosMaximos) {
	    pasos = 0;
	    if (p.x < tamanoMapa / 2) {
		puntoObjetivo.x = tamanoMapa;
	    } else {
		puntoObjetivo.x = 0;
	    }

	    Random posy = new Random();
	    puntoObjetivo.y = posy.nextInt(tamanoMapa);
        // generas un punto q sea tamanoMapa - posX, tamanoMapa - posY
	    //llamas a la funcion generar scanner
	    generarScanner();
	}
	//si pasos < pasos Maximo 

    }

    /**
     * Genera el escáner del mapa completo hacia el puntoObjetivo
     *
     * @author Amanda Fernández Piedra
     */
    private void generarScanner() {
	for (int i = 0; i < TAMANO_MAPA; i++) {
	    for (int j = 0; j < TAMANO_MAPA; j++) {
		Point puntoAux = new Point(i, j);
		double distanciaAux = puntoAux.distance(puntoObjetivo);
		scanner[i][j] = distanciaAux;
	    }
	}

    }
}
