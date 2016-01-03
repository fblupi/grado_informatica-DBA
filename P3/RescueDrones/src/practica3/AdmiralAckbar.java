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
import java.util.Scanner;

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
    private boolean moviendoPorPared = false;
    private double miPrimerScannerPared;
    private Point posicionAnterior = new Point(50, 50);
    private boolean moverUnaCasilla = true;
    private int dronesEnObjetivo = 0;

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
	flota.put("Drone60", null);
	flota.put("Drone61", null);
	flota.put("Drone62", null);
	flota.put("Drone63", null);
	estadoActual = Estado.INICIAL;
	subEstadoBuscando = Estado.ELECCION_DRONE;
	subEstadoEncontrado = Estado.ELECCION_DRONE;
    }

    public void execute() {
	while (!terminar) {
	    System.out.println("Execute terminar: " + terminar + " fase: " + estadoActual);
	    switch (estadoActual) {
		case INICIAL:
		    faseInicial();
		    break;

		case BUSCAR:
		    switch (subEstadoBuscando) {
			case ELECCION_DRONE:
			    faseEleccionDrone();
			    System.out.println("El dron es: " + droneElegido);
			    subEstadoBuscando = Estado.MOVER;
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
			case OBJETIVO_ENCONTRADO:
			    faseObjetivoEncontrado();
			    break;
		    }
		    break;
		case OBJETIVO_ENCONTRADO:
		    switch (subEstadoEncontrado) {
			case ELECCION_DRONE:
			    faseEleccionDrone();
			    if (droneElegido.equals("")) {
				estadoActual = Estado.FINALIZAR;
			    } else {
				System.out.println("El dron elegido es: " + droneElegido);
				System.out.println("");
				subEstadoEncontrado = Estado.MOVER;
			    }
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
		    if (percepcion.getGps().y == 99) {
			tamanoMapa = 100;
		    } else if (percepcion.getGps().y == 499 || percepcion.getGps().x >= 100) {
			tamanoMapa = 500;
		    }
		    actualizarMapa(percepcion);
		    System.out.println(message.getContent());
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
	if (mapa[posX][posY] != Celda.OBSTACULO) {
	    mapa[posX][posY] = Celda.getRecorrido(percepcion.getNombreDrone());   // Guarda posición actual como posición por donde ha pasado
	}
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

    /**
     * Elige hacia dónde quiere moverse en función de qué casilla adyascente al
     * bot está más cerca del objetivo y no es un obstáculo
     *
     * @author Francisco Javier Bolívar
     * @author Antonio David López
     * @return movimiento elegido
     */
    private String elegirMovimiento() {
	String decisionPared = "logout"; // De primeras asumimos que no se puede mover a ningún sitio
	String decisionNormal = "logout";
	String decisionLocal = "-";
	double distanciaPared = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí
	double distanciaNormal = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí
	Point posActual = flota.get(droneElegido).getGps();
	int posX = posActual.x;
	int posY = posActual.y;
//	System.out.println("\t\t\tPosicion actual: " + posActual.toString());
	for (int x = posX - 1; x <= posX + 1; x++) {
	    for (int y = posY - 1; y <= posY + 1; y++) {

		if (x >= 0 && x < TAMANO_MAPA && y >= 0 && y < TAMANO_MAPA) {
		    System.out.print(mapa[x][y]);
		    if (mapa[x][y] != Celda.OBSTACULO && mapa[x][y] != Celda.PARED && mapa[x][y] != Celda.getRecorrido(droneElegido) && celdaLibre(x, y)
			    && (x != posicionAnterior.x || y != posicionAnterior.y)) {
			decisionLocal = parserCoordMov(x - posX, y - posY);
			System.out.print(" -> " + decisionLocal);
			if (scanner[x][y] <= distanciaNormal) {
			    decisionNormal = decisionLocal;
			    distanciaNormal = scanner[x][y];
			}
			if (moviendoPorPared && scanner[x][y] <= distanciaPared && tieneParedCerca(x, y)) {
			    decisionPared = decisionLocal;
			    distanciaPared = scanner[x][y]; // Actualiza la distancia de la casilla más cercana
			}
		    }
		    System.out.println("");
		}
	    }
//	    System.out.println("");
	}

	if (moviendoPorPared) {
	    if (decisionPared.equals("logout")) {
		moviendoPorPared = false;
		return decisionNormal;
	    } else {
		return decisionPared;
	    }
	} else {
	    return decisionNormal;
	}
    }

    private boolean tieneParedCerca(int x, int y) {
	for (int i = x - 1; i <= x + 1; i++) {
	    for (int j = y - 1; j <= y + 1; j++) {
		if (i >= 0 && j >= 0 && i < TAMANO_MAPA && j < TAMANO_MAPA && mapa[i][j] == Celda.OBSTACULO) {
		    return true;
		}
	    }
	}
	return false;
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
	if (x == -1) {
	    if (y == -1) {
		return "moveNW";  // (-1, -1)
	    } else if (y == 0) {
		return "moveW";   // (-1,  0)
	    } else {
		return "moveSW";  // (-1,  1)
	    }
	} else if (x == 0) {
	    if (y == -1) {
		return "moveN";   // (0, -1)
	    } else {
		return "moveS";   // (0,  1)  
	    }
	} else {
	    if (y == -1) {
		return "moveNE";  // (1, -1)
	    } else if (y == 0) {
		return "moveE";   // (1,  0)  
	    } else {
		return "moveSE";  // (1,  1)  
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
	    inicializarMapa();
	    imagen.actualizarMapa(mapa);
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
	    int distanciaMin = Integer.MAX_VALUE, x, distancia;
	    for (Map.Entry<String, PropiedadesDrone> par : flota.entrySet()) {
		String nombre = par.getKey();
		PropiedadesDrone propiedades = par.getValue();

		x = propiedades.getGps().x;
		distancia = Math.min(x, Math.abs(tamanoMapa - x));

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
	    droneElegido = "";
	    HashMap<String, Integer> distancias = new HashMap<>();
	    distancias = calcularDistancias();
	    for (String nombre : distancias.keySet()) {
		if (flota.get(nombre).getRol() == Rol.MOSCA) {
		    droneElegido = nombre;
		}
	    }
	    if (droneElegido.equals("")) {
		for (Map.Entry<String, Integer> par : distancias.entrySet()) {
		    String nombre = par.getKey();
		    Integer distancia = par.getValue();
		    System.out.println(nombre + " -> " + distancia);
		    if (distancia < flota.get(nombre).getBateria()) {
			droneElegido = nombre;
		    }
		}
		if (droneElegido.equals("")) {
		    if (!distancias.isEmpty()) {
			droneElegido = (String) distancias.keySet().toArray()[0];
		    }
		}
	    }
	}
    }

    private HashMap<String, Integer> calcularDistancias() {
	HashMap<String, Integer> distancias = new HashMap<>();
	for (Map.Entry<String, PropiedadesDrone> par : flota.entrySet()) {
	    String nombre = par.getKey();
	    PropiedadesDrone propiedades = par.getValue();
	    if (!propiedades.getLlegado()) {
		distancias.put(nombre, calcularDistancia(propiedades));
	    }
	}
	return distancias;
    }

    private int calcularDistancia(PropiedadesDrone propiedades) {
	int distancia = (int) distancia(propiedades.getGps(), puntoObjetivo);
	return distancia * propiedades.getRol().getConsumo();
    }

    private void fasePercibir() {
	enviarMensaje(droneElegido, ACLMessage.QUERY_REF, JSON.key());
	try {
	    ACLMessage message = receiveACLMessage();
	    System.out.println(message.getPerformative() + ": " + message.getContent());
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
	subEstadoBuscando = Estado.REPOSTAR;
	subEstadoEncontrado = Estado.REPOSTAR;
    }

    private void faseRepostar() {
	PropiedadesDrone propiedades = flota.get(droneElegido);
	if (!propiedades.getLlegado() && propiedades.getBateria() <= propiedades.getRol().getConsumo()) {
	    enviarMensaje(droneElegido, ACLMessage.REQUEST, JSON.repostar());
	    try {
		ACLMessage message = receiveACLMessage();
		if (message.getPerformativeInt() != ACLMessage.INFORM) {
		    System.out.println(message.getPerformative() + ": " + message.getContent());
		    estadoActual = Estado.FINALIZAR;
		}
	    } catch (InterruptedException ex) {
		System.err.println(ex.toString());
		estadoActual = Estado.FINALIZAR;
	    }
	}
	subEstadoBuscando = Estado.MOVER;
	subEstadoEncontrado = Estado.MOVER;
    }

    private void reiniciarMapa() {
	for (int i = 0; i < TAMANO_MAPA; i++) {
	    for (int j = 0; j < TAMANO_MAPA; j++) {
		if (mapa[j][i] == Celda.getRecorrido(droneElegido)) {
		    mapa[j][i] = Celda.LIBRE;
		}
	    }
	}
    }

    /**
     * @author Antonio Espinosa
     */
    private void mover() {

	String decision;
	int[] pos = posicionOptima();
	Point p = flota.get(droneElegido).getGps();
	double scannerOptimo = scanner[p.x + pos[0]][p.y + pos[1]];

	if (moviendoPorPared) {  // si me estoy moviendo por la pared 
	    if (scannerOptimo < miPrimerScannerPared) { // si el scanneroptimo es menor al que tengo guardado 
		moviendoPorPared = false; // dejamos el movimiento por la pared
	    }
	    decision = elegirMovimiento();
	} else {
	    if (optimaEsPared(pos)) {   //si la optima es la pared y no nos estamos moviendo por la pared 
		if (!moviendoPorPared) {// si ahora mismo no estabamos moviendonos por la pared actualizamos los datos
		    moviendoPorPared = true;
		    miPrimerScannerPared = scannerOptimo;
		}
	    } else { //si a posicion optima no es la pared dejamos de movernos en la pared
		moviendoPorPared = false;
	    }
	    decision = elegirMovimiento();
	}
	if (decision.equals("logout")) {
	    reiniciarMapa();
	    mover();
	}
	pasos++;
	enviarMensaje(droneElegido, ACLMessage.REQUEST, JSON.mover(decision));
	subEstadoBuscando = Estado.PERCIBIR;
	subEstadoEncontrado = Estado.PERCIBIR;
	try {
	    ACLMessage message = receiveACLMessage();
	    if (message.getPerformativeInt() != ACLMessage.INFORM) {
		System.out.println(message.getPerformative() + ": " + message.getContent());
		estadoActual = Estado.FINALIZAR;
	    } else {
		System.out.println("Mensaje recibido: " + message.getContent());
	    }
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	    estadoActual = Estado.FINALIZAR;
	}

//	if (pasos == 100) {
//	    estadoActual = Estado.FINALIZAR;
//	}
    }

    private void moverMosca() {
	String decision = "";
	String decisionLocal;
	double distanciaNormal = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí
	Point posActual = flota.get(droneElegido).getGps();
	int posX = posActual.x;
	int posY = posActual.y;
	for (int y = posY - 1; y <= posY + 1; y++) {
	    for (int x = posX - 1; x <= posX + 1; x++) {
		if (x >= 0 && x < TAMANO_MAPA && y >= 0 && y < TAMANO_MAPA) {
		    if (mapa[x][y] != Celda.PARED && mapa[x][y] != Celda.getRecorrido(droneElegido) && celdaLibre(x, y)) {
			decisionLocal = parserCoordMov(x - posX, y - posY);
			if (scanner[x][y] <= distanciaNormal) {
			    decision = decisionLocal;
			    distanciaNormal = scanner[x][y];
			}
		    }
		}
	    }
	}
	pasos++;
	enviarMensaje(droneElegido, ACLMessage.REQUEST, JSON.mover(decision));
	subEstadoBuscando = Estado.PERCIBIR;
	subEstadoEncontrado = Estado.PERCIBIR;
	try {
	    ACLMessage message = receiveACLMessage();
	    if (message.getPerformativeInt() != ACLMessage.INFORM) {
		System.out.println(message.getPerformative() + ": " + message.getContent());
		estadoActual = Estado.FINALIZAR;
	    } else {
		System.out.println("Mensaje recibido: " + message.getContent());
	    }
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	    estadoActual = Estado.FINALIZAR;
	}
    }

    private void moverTeclado() {
	String decision = "";
	Scanner scanner = new Scanner(System.in);
	while (decision.equals("")) {
	    String letra = scanner.nextLine();
	    if (letra.length() > 0) {
		switch (letra.charAt(0)) {
		    case 'a':
			decision = "moveW";
			break;
		    case 's':
			decision = "moveS";
			break;
		    case 'd':
			decision = "moveE";
			break;
		    case 'q':
			decision = "moveNW";
			break;
		    case 'w':
			decision = "moveN";
			break;
		    case 'e':
			decision = "moveNE";
			break;
		    case 'z':
			decision = "moveSW";
			break;
		    case 'c':
			decision = "moveSE";
			break;
		}
	    }
	}

	pasos++;
	enviarMensaje(droneElegido, ACLMessage.REQUEST, JSON.mover(decision));
	subEstadoBuscando = Estado.PERCIBIR;
	subEstadoEncontrado = Estado.PERCIBIR;
	try {
	    ACLMessage message = receiveACLMessage();
	    if (message.getPerformativeInt() != ACLMessage.INFORM) {
		System.out.println(message.getPerformative() + ": " + message.getContent());
		estadoActual = Estado.FINALIZAR;
	    } else {
		System.out.println("Mensaje recibido: " + message.getContent());
	    }
	} catch (InterruptedException ex) {
	    System.err.println(ex.toString());
	    estadoActual = Estado.FINALIZAR;
	}
    }

    private void faseMover() {
	if (buscando) {
	    mover();
	} else {
	    PropiedadesDrone propiedades = flota.get(droneElegido);
	    if (propiedades.getLlegado()) {
		if (moverUnaCasilla && dronesEnObjetivo < flota.size() - 1) {
		    moverUnaCasilla = false;
		    if (propiedades.getRol() == Rol.MOSCA) {
			moverMosca();
		    } else {
			mover();
		    }
		} else {
		    dronesEnObjetivo++;
		    moverUnaCasilla = true;
		    subEstadoEncontrado = Estado.ELECCION_DRONE;
		}
	    } else {
		if (propiedades.getRol() == Rol.MOSCA) {
		    moverMosca();
		} else {
		    mover();
		}
	    }
	}
	posicionAnterior = flota.get(droneElegido).getGps();
    }

    private boolean celdaLibre(int x, int y) {
	for (PropiedadesDrone propiedades : flota.values()) {
	    if (propiedades.getGps().x == x && propiedades.getGps().y == y) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Busca en nuestro mapa 3x3 cual es la posición mas óptima de movimiento
     *
     * @author Antonio Espinosa
     * @return La posición optima de movimiento
     */
    private int[] posicionOptima() {
	int[] optima = new int[2];
	double floatMin = Float.MAX_VALUE;
	int x = flota.get(droneElegido).getGps().x;
	int y = flota.get(droneElegido).getGps().y;

	for (int i = -1; i <= 1; i++) {
	    for (int j = -1; j <= 1; j++) {
		if ((x + i) > 0 && (y + j) > 0 && (x + i) < TAMANO_MAPA && (y + j) < TAMANO_MAPA) {
		    if (scanner[x + i][y + j] < floatMin) {
			floatMin = scanner[x + i][y + j];
			optima[0] = i;
			optima[1] = j;
		    }
		}
	    }
	}
	return optima;
    }

    /**
     * Nos indica si la posición pasada es pared o si no lo es
     *
     * @author Antonio Espinosa
     * @param pos posición de nuestro mapa
     * @return True si la posición que te pasa es pared false si no lo es
     */
    private boolean optimaEsPared(int[] pos) {
	int x = flota.get(droneElegido).getGps().x;
	int y = flota.get(droneElegido).getGps().y;

	return mapa[x + pos[0]][y + pos[1]] == Celda.OBSTACULO;
    }

    private void faseObjetivoEncontrado() {
	if (buscando) {
	    buscando = false;
	    estadoActual = Estado.OBJETIVO_ENCONTRADO;
	}
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
    private void objetivoEncontrado() {
	boolean encontrado = false;
	int xMin = tamanoMapa, xMax = 0, yMin = tamanoMapa, yMax = 0;
	for (int i = 0; i < tamanoMapa; i++) {
	    for (int j = 0; j < tamanoMapa; j++) {
		if (mapa[j][i] == Celda.OBJETIVO) {
		    if (j < xMin) {
			xMin = j;
		    }
		    if (j > xMax) {
			xMax = j;
		    }
		    if (i < yMin) {
			yMin = i;
		    }
		    if (i > yMax) {
			yMax = i;
		    }
		    encontrado = true;
		}
	    }
	}
	if (encontrado) {
	    int j = (xMax + xMin) / 2;
	    int i = (yMax + yMin) / 2;
	    puntoObjetivo = new Point(j, i);
	    generarScanner();
	    subEstadoBuscando = Estado.OBJETIVO_ENCONTRADO;
	}
//	for (int i = 0; i < TAMANO_MAPA && !encontrado; i++) {
//	    for (int j = 0; j < TAMANO_MAPA && !encontrado; j++) {
//		if (mapa[j][i] == Celda.OBJETIVO) {
//		    puntoObjetivo = new Point(j, i);
//		    generarScanner();
//		    encontrado = true;
//		    subEstadoBuscando = Estado.OBJETIVO_ENCONTRADO;
//		}
//	    }
//	}
    }

    private boolean cercaPuntoObjetivo() {
	return distancia(flota.get(droneElegido).getGps(), puntoObjetivo) <= 3;
    }

    /**
     * Genera el punto objetivo para la busqueda
     *
     * @author Antonio David López Machado
     */
    boolean primero = true;

    private void generarPuntoObjetivo() {
//	System.out.println("\t\tPASOS: " + pasos + " maximos " + pasosMaximos + " cerca: " + cercaPuntoObjetivo());
	if (pasos >= pasosMaximos || cercaPuntoObjetivo()) {
	    puntoObjetivo = new Point();
	    Point p = flota.get(droneElegido).getGps();
	    pasos = 0;
	    Random random = new Random();
//	    puntoObjetivo.x = random.nextInt(tamanoMapa);
	    if (p.x < tamanoMapa / 2) {
		puntoObjetivo.x += tamanoMapa / 2;
	    }
	    p.y = random.nextInt(tamanoMapa);
//	    if (p.y == 0) {
//		puntoObjetivo.y = tamanoMapa - 1;
//	    } else {
//		puntoObjetivo.y = 0;
//	    }
	    if (primero) {
		puntoObjetivo.x = 5;
		puntoObjetivo.y = 5;
		primero = false;
	    }
	    pasosMaximos = (int) distancia(p, puntoObjetivo) * 2;
	    // generas un punto q sea tamanoMapa - posX, tamanoMapa - posY
	    //llamas a la funcion generar scanner
//	    System.out.println("\t\tPuntoObjetivo: " + puntoObjetivo.toString() + " pasos maximos: " + pasosMaximos);
	    generarScanner();
	}

    }

    private double distancia(Point a, Point b) {
	return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    /**
     * Genera el escáner del mapa completo hacia el puntoObjetivo
     *
     * @author Amanda Fernández Piedra
     */
    private void generarScanner() {
	for (int i = 0; i < TAMANO_MAPA; i++) {
	    for (int j = 0; j < TAMANO_MAPA; j++) {
		Point puntoAux = new Point(j, i);
		double distanciaAux = distancia(puntoAux, puntoObjetivo);
		scanner[j][i] = distanciaAux;
	    }
	}
//	for (int i = 0; i < TAMANO_MAPA; i++) {
//	    for (int j = 0; j < TAMANO_MAPA; j++) {
//		System.out.print(scanner[i][j] + " ");
//	    }
//	    System.out.println("");
//	}
//	estadoActual = Estado.FINALIZAR;
    }
}
