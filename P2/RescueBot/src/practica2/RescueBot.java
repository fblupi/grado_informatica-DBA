/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.awt.Point;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static practica2.EstadosBot.*;

/**
 * @author Francisco Javier Bolívar
 * @author Antonio Espinosa
 * @author Amanda Fernández
 * @author José Guadix
 * @author Antonio David López
 * @author Francisco Javier Ortega
 */
public class RescueBot extends SingleAgent {

    private final int LIBRE = 0;
    private final int OBSTACULO = 1;
    private final int OBJETIVO = 2;
    private final int RECORRIDA = 3;
    private final int DESCONOCIDA = 4;
    private final int TAMANO_MAPA = 500;
    private final int NUM_SENSORES = 3;
    private final int ULT_POSICION = 5;
    private final int UMBRAL_FINALIZAR = 500;
    private final int UMBRAL_LIBERAR = 10;

    private EstadosBot estadoActual;
    private String nombreControlador;
    private int nivelBateria = 0;
    private int[] ultimoGPS = new int[2];
    private int[][] ultimoRadar = new int[5][5];
    private float[][] ultimoScanner = new float[5][5];
    private int[][] mapa = new int[TAMANO_MAPA][TAMANO_MAPA];
    private boolean terminar;
    private ACLMessage inbox, outbox;
    private final String mundoAVisitar;
    private Imagen imagen;
    private boolean conectando;
    private int pasos = 0;
    private long tiempoInicio;
    private boolean moviendoPorPared;
    private int[] miPrimeraPosPared = new int[2];
    private float miPrimerScannerPared;
    private HashMap<Point, Integer> posicionesPared = new HashMap<Point, Integer>();
    private HashMap<Integer, Point> posicionesALiberar = new HashMap<Integer, Point>();

    /**
     * Constructor de nuestro agente
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     * @param id
     * @param mundoAVisitar
     * @throws Exception
     */
    public RescueBot(AgentID id, String mundoAVisitar) throws Exception {
	super(id);
	this.mundoAVisitar = mundoAVisitar;
    }

    /**
     * Inicializamos las variables 
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
    @Override
    public void init() {
	System.out.println("Bot Iniciandose ");
	estadoActual = EstadosBot.ESTADO_INICIAL;
	nombreControlador = "Cerastes";
	inbox = null;
	outbox = null;
	terminar = false;
	inicializarMapa();
	imagen = new Imagen(mapa, mundoAVisitar);
	imagen.mostrar();
	moviendoPorPared = false;
	tiempoInicio = System.currentTimeMillis();
    }

    /**
     * Método execute de nuestro agente donde controla los estados por los que pasa durante la ejecución
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
    @Override
    public void execute() {
	System.out.println("Agente en ejecución");
	while (!terminar) {
	    switch (estadoActual) {
		case ESTADO_INICIAL:
		    iniciarConversacion();
		    break;
		case ESTADO_RECIBIR_DATOS:
		    faseRecibiendoDatos();
		    break;
		case ESTADO_REPOSTAR:
		    faseRepostar();
		    break;
		case ESTADO_MOVER:
		    faseMover();
		    break;
		case ESTADO_FINAL:
		    // En realidad este estado es aparentemente innecesario
		    System.out.println("Agente(" + this.getName() + ") Terminando ejecución");
		    terminar = true;
		    break;
		case ESTADO_ENCONTRADO:
		    faseObjetivoEncontrado();
		    break;
	    }
	}
    }

    /**
     * Método en el que nuestr agente cierra la sesión con los controladores
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
    @Override
    public void finalize() {
	System.out.println("Agente cerrandose");
	mapa[ultimoGPS[0]][ultimoGPS[1]] = ULT_POSICION;
	imagen.actualizarMapa(mapa);
	imagen.guardarPNG(mundoAVisitar + " - " + Date.from(Instant.now()).toString().replace(":", "-") + ".png");
	imagen.cerrar();
	super.finalize();
	mostrarResumen();
    }

    /**
     * Inicializamos la conversación de nuestro agente con los controladores 
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
    private void iniciarConversacion() {
	System.out.println("Agente pidiendo ID");
	enviarMensaje(JSON.escribirLogin(mundoAVisitar));
	conectando = true;
	estadoActual = ESTADO_RECIBIR_DATOS;
    }

    /**
     * Método en el que nuestro agente obtiene las respuestas de los controladores
     * @author José Guadix
     * @author Francisco Javier Ortega
     */
    private void faseRecibiendoDatos() {
	System.out.println("Agente Esperando respuesta");
	boolean exito = true;
	for (int i = 0; i < NUM_SENSORES + 1 && exito; i++) { //+1 por la respuesta
	    try {
		inbox = receiveACLMessage();
		System.out.println("Mensaje recibido: " + inbox.getContent());
		if (inbox.getContent().contains("scanner")) {
		    ultimoScanner = JSON.leerScanner(inbox.getContent());
		} else if (inbox.getContent().contains("radar")) {
		    ultimoRadar = JSON.leerRadar(inbox.getContent());
		} else if (inbox.getContent().contains("gps")) {
		    ultimoGPS = JSON.leerGPS(inbox.getContent());
		} else if (conectando) {
		    exito = JSON.conexionLogin(inbox.getContent());
		    conectando = false;
		} else {
		    exito = JSON.exitoAction(inbox.getContent());
		}
	    } catch (InterruptedException ex) {
		System.err.println("Agente Error de comunicación");
	    }
	}

	if (!exito) {
	    estadoActual = ESTADO_FINAL;
	} else if (ultimoRadar[2][2] == 2) {
	    estadoActual = ESTADO_ENCONTRADO;
	} else if (nivelBateria < 10) {
	    estadoActual = ESTADO_REPOSTAR;
	} else {
	    estadoActual = ESTADO_MOVER;
	}
    }

    /**
     * Implementación del estado repostar
     * @author José Guadix
     * @author Francisco Javier Ortega
     */
    private void faseRepostar() {
	enviarMensaje(JSON.escribirAction("refuel"));
	nivelBateria = 100;
	estadoActual = ESTADO_RECIBIR_DATOS;
    }
    /**
     * Nos indica si nuestro agente ha vuelto a una de las posiciones del movimiento pared
     * @author 
     * @param pos poosición del mapa
     * @return True no esta en unas de las posiciones del movimiento pared  false si no lo esta
     */
    private boolean tieneSolucion(int[] pos) {
	for (Map.Entry<Point, Integer> entrySet : posicionesPared.entrySet()) {
	    Point key = entrySet.getKey();
	    Integer value = entrySet.getValue();
	    if (Math.abs(ultimoGPS[0] + pos[0] - 2 - key.x) == 0
		    && Math.abs(ultimoGPS[1] + pos[1] - 2 - key.y) == 0
		    && pasos - value > UMBRAL_FINALIZAR) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Implementación del estado movimiento encontrado
     * @author José Guadix
     * @author Francisco Javier Ortega
     */
    private void faseMover() {
	actualizarMapa();
	imagen.actualizarMapa(mapa);
	liberarPosiciones();

	String decision;
	int[] pos = posicionOptima();
	float scannerOptimo = ultimoScanner[pos[0]][pos[1]];
	
	if (!tieneSolucion(pos)) { // si no tiene solución 
	    System.out.println("\nLa solución no está accesible");
	    decision = "logout";
	} else if (moviendoPorPared) {  // si me estoy moviendo por la pared 
	    if (scannerOptimo < miPrimerScannerPared) { // si el scanneroptimo es menor al que tengo guardado 
		moviendoPorPared = false; // dejamos el movimiento por la pared
	    }
	    decision = elegirMovimiento();
	} else {
	    if (optimaEsPared(pos)) {   //si la optima es la pared y no nos estamos moviendo por la pared 
		if (!moviendoPorPared) {// si ahora mismo no estabamos moviendonos por la pared actualizamos los datos
		    moviendoPorPared = true;
		    miPrimerScannerPared = scannerOptimo;
		    posicionesPared.put(new Point(ultimoGPS[0], ultimoGPS[1]), pasos);
		}
	    } else { //si a posicion optima no es la pared dejamos de movernos en la pared
		moviendoPorPared = false;
	    }
	    decision = elegirMovimiento();
	}
	enviarMensaje(JSON.escribirAction(decision));
	if (decision.equals("logout")) {    // si no encontramos un movimiento cerramos la sesión
	    estadoActual = ESTADO_FINAL;
	} else { // sino actualizamos los datos
	    pasos++;
	    nivelBateria--;
	    estadoActual = ESTADO_RECIBIR_DATOS;
	}
    }
    /**
     * Nos indica si la posición pasada como parámetro tiene una pared cerca
     * @author Francisco Javier Ortega 
     * @author José Guadix
     * @author Antonio David López
     * @param x posición x de nuestro mapa
     * @param y posición y de nuestro mapa
     * 
     * @return Verdadero si esta cerca de la pared y false si no esta cerca de la pared
     */
    private boolean tieneParedCerca(int x, int y) {
	for (int i = x - 1; i <= x + 1; i++) {
	    for (int j = y - 1; j <= y + 1; j++) {
		if (i >= 0 && j >= 0 && i < 5 && j < 5 && ultimoRadar[i][j] == OBSTACULO) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Enviamos un mensaje pasado por parametro a un controlador.
     * 
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     * 
     * @param contenido mensaje a enviar al controlador
     */
    private void enviarMensaje(String contenido) {
	System.out.println("Enviando mensaje: " + contenido);
	outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID(nombreControlador));
	outbox.setContent(contenido);
	System.out.println("Agente enviando mensaje");
	this.send(outbox);
    }

    /**
     * Inicializa todas las casillas del mapa como desconocidas
     *
     * @author Francisco Javier Bolívar
     * @author Antonio David López
     */
    private void inicializarMapa() {
	for (int i = 0; i < TAMANO_MAPA; i++) {
	    for (int j = 0; j < TAMANO_MAPA; j++) {
		mapa[i][j] = DESCONOCIDA;
	    }
	}
    }

    /**
     * Actualiza el mapa con la posición por donde acaba de pasar y los valores
     * que recibe del radar
     *
     * @author Francisco Javier Bolívar
     * @author Antonio David López
     */
    private void actualizarMapa() {
	int posX = ultimoGPS[0];
	int posY = ultimoGPS[1];
	mapa[posX][posY] = RECORRIDA;   // Guarda posición actual como posición por donde ha pasado
	for (int i = 0, y = posY - 2; i < 5; i++, y++) {      // x: recorre el radar, i: recorre mapa desde la posición actual
	    for (int j = 0, x = posX - 2; j < 5; j++, x++) {  // y: recorre el radar, j: recorre mapa desde la posición actual
		if ((x >= 0 && x < TAMANO_MAPA) && (y >= 0 && y < TAMANO_MAPA)) { // No se sale del límite
		    if (mapa[x][y] == DESCONOCIDA) { // No machaca pasos anteriores
			mapa[x][y] = ultimoRadar[i][j];   // Actualiza casilla con el valor recibido del radar
		    }
		}
	    }
	}
    }
    /**
     * Busca en nuestro mapa 3x3 cual es la posición mas óptima de movimiento
     * @author Francisco Javier Ortega 
     * @author José Guadix
     * @author Antonio David López
     * @return La posición optima de movimiento
     */
    private int[] posicionOptima() {
	int[] optima = new int[2];
	float floatMin = Float.MAX_VALUE;
	for (int i = 1; i < 4; i++) {
	    for (int j = 1; j < 4; j++) {
		if (ultimoScanner[i][j] < floatMin) {
		    floatMin = ultimoScanner[i][j];
		    optima[0] = i;
		    optima[1] = j;
		}
	    }
	}
	return optima;
    }
    /**
     * Nos indica si la posición pasada es pared o si no lo es
     * @author Francisco Javier Ortega 
     * @author José Guadix
     * @author Antonio David López
     * @param pos posición de nuestro mapa
     * @return True si la posición que te pasa es pared false si no lo es
     */
    private boolean optimaEsPared(int[] pos) {
	return ultimoRadar[pos[0]][pos[1]] == OBSTACULO;
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
	String decisionLocal;
	float distanciaPared = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí
	float distanciaNormal = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí
	Point posActual = new Point(ultimoGPS[0], ultimoGPS[1]);
	int posX = ultimoGPS[0];
	int posY = ultimoGPS[1];
	for (int i = 1; i < 4; i++) {
	    for (int j = 1; j < 4; j++) {
		if (posX + j - 2 >= 0 && posX + j - 2 < TAMANO_MAPA
			&& posY + i - 2 >= 0 && posY + i - 2 < TAMANO_MAPA
			&& mapa[posX + j - 2][posY + i - 2] != OBSTACULO // No hay obstáculo
			&& mapa[posX + j - 2][posY + i - 2] != RECORRIDA // No se ha recorrido previamente
			&& !voyAEncerrarme(i, j)) {
		    decisionLocal = parserCoordMov(i, j);
		    if (ultimoScanner[i][j] <= distanciaNormal) {
			if (ultimoScanner[i][j] == distanciaNormal) {
			    posicionesALiberar.put(pasos, posActual);
			}
			decisionNormal = decisionLocal;    // Actualiza el movimiento de la casilla más cercana
			distanciaNormal = ultimoScanner[i][j];
		    }

		    if (moviendoPorPared && ultimoScanner[i][j] <= distanciaPared && tieneParedCerca(i, j)) {
			if (ultimoScanner[i][j] == distanciaPared) {
			    posicionesALiberar.put(pasos, posActual);
			}
			decisionPared = decisionLocal;
			distanciaPared = ultimoScanner[i][j]; // Actualiza la distancia de la casilla más cercana
		    }
		}
	    }
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
     * Implementación del estado objetivo encontrado
     * @author José Guadix
     * @author Francisco Javier Ortega
     */
    private void faseObjetivoEncontrado() {
	System.out.println("¡He encontrado el objetivo!");
	enviarMensaje(JSON.escribirAction("logout"));
	estadoActual = ESTADO_FINAL;
    }

    /**
     * Muestra el numero de pasos y el tiempo que tarda nuestro agente en encontrar el objetivo
     * @author Antonio Espinosa
     */
    private void mostrarResumen() {
	int segundosTotal = (int) (System.currentTimeMillis() - tiempoInicio) / 1000;
	int minutos = segundosTotal / 60;
	int segundos = segundosTotal - (minutos * 60);

	System.out.println("Pasos que ha dado el agente: " + pasos);
	System.out.println("Tiempo de ejecución del mapa: " + minutos + " Minutos " + segundos + " Segundos");
    }
    /**
     *  Recorremos nuestro mapa 3x3 para ver si tenemos almenos 2 posiciónes libres
     * @param y del mapa
     * @param x del mapa
     * @return True si el agente se va a encerrar y false si no se encierra
     */
    private boolean voyAEncerrarme(int y, int x) {
	int posX = ultimoGPS[0] + x - 2;
	int posY = ultimoGPS[1] + y - 2;
	int cont = 0;
        
	for (int i = -1; i <= 1; i++) {
	    for (int j = -1; j <= 1; j++) {
		if (posX + j >= 0 && posX + j < TAMANO_MAPA
			&& posY + i >= 0 && posY + i < TAMANO_MAPA) {
		    if (mapa[posX + j][posY + i] == LIBRE) {
			cont++;
		    } else if (mapa[posX + j][posY + i] == OBJETIVO) {
			return false;
		    }
		}
	    }
	}
	return cont <= 1;
    }
    /**
     * Libera las posiciones por donde puede volver a pasar cuando supera un determinado numero de pasos
     * @author José Guadix
     * @author Francisco Javier Bolivar 
     */
    private void liberarPosiciones() {
	//intentamos borrar devolviendo la posición si no existe devuelve null si existe ponemos esa posicion a LIBRE
	Point p = posicionesALiberar.remove(pasos - UMBRAL_LIBERAR);
	if (p != null) {
	    mapa[p.x][p.y] = LIBRE;
	    posicionesPared.remove(p);//Eliminamos ese punto para que no termine la ejecución si intentamos pasar por el.
	}
    }
}
