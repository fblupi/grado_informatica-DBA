/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.time.Instant;
import java.util.Date;
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

    private EstadosBot estadoActual;
    private String nombreControlador;
    private int nivelBateria = 0;
    private String token;
    private int[] ultimoGPS = new int[2];
    private int[][] ultimoRadar = new int[5][5];
    private float[][] ultimoScanner = new float[5][5];
    private int[][] mapa = new int[TAMANO_MAPA][TAMANO_MAPA];
    private boolean terminar;
    private ACLMessage inbox, outbox;
    private String mundoAVisitar;
    private Imagen imagen;
    private boolean conectando;
    private int pasos = 0;
    private long tiempoInicio;

    /**
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
	imagen = new Imagen(mapa);
	imagen.mostrar();
        tiempoInicio = System.currentTimeMillis();
    }

    /**
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
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
    @Override
    public void finalize() {
	System.out.println("Agente cerrandose");
	imagen.guardarPNG(mundoAVisitar + " - " + Date.from(Instant.now()).toString().replace(":", "-") + ".png");
	imagen.cerrar();
	super.finalize();
        mostrarResumen();
    }

    /**
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
     * @author José Guadix
     * @author Francisco Javier Ortega
     */
    private void faseRepostar() {
	enviarMensaje(JSON.escribirAction("refuel"));
	nivelBateria = 100;
	estadoActual = ESTADO_RECIBIR_DATOS;
    }
    
    /**
     * @author José Guadix
     * @author Francisco Javier Ortega
     */
    private void faseMover() {
	actualizarMapa();
	imagen.actualizarMapa(mapa);
	String decision = elegirMovimiento();
	enviarMensaje(JSON.escribirAction(decision));
	if (decision.equals("logout")) {
	    estadoActual = ESTADO_FINAL;
	} else {
            pasos++;
	    nivelBateria--;
	    estadoActual = ESTADO_RECIBIR_DATOS;
	}
    }

    /**
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     * @param contenido
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
	mapa[ultimoGPS[0]][ultimoGPS[1]] = RECORRIDA;   // Guarda posición actual como posición por donde ha pasado
	for (int x = 0, j = -2; x < 5; x++, j++) {      // x: recorre el radar, i: recorre mapa desde la posición actual
	    for (int y = 0, i = -2; y < 5; y++, i++) {  // y: recorre el radar, j: recorre mapa desde la posición actual
		if ((ultimoGPS[0] + i >= 0 && ultimoGPS[0] + i < TAMANO_MAPA)
			&& (ultimoGPS[1] + j >= 0 && ultimoGPS[1] + j < TAMANO_MAPA)) { // No se sale del límite
		    if (mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] == DESCONOCIDA) { // No machaca pasos anteriores
		   
			mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] = ultimoRadar[x][y];   // Actualiza casilla con el valor recibido del radar
		    }
		}
	    }
	}
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
	String decision = "logout"; // De primeras asumimos que no se puede mover a ningún sitio
	float distanciaMin = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí

	// Busca el movimiento
	for (int j = 1; j < 4; j++) {
	    for (int i = 1; i < 4; i++) {
		if (ultimoScanner[j][i] < distanciaMin // La distancia es menor que la menor almacenada
			&& ultimoGPS[0] + i - 2 >= 0 && ultimoGPS[1] + j - 2 >= 0) {
		    if (mapa[ultimoGPS[0] + i - 2][ultimoGPS[1] + j - 2] != OBSTACULO // No hay obstáculo
			    && mapa[ultimoGPS[0] + i - 2][ultimoGPS[1] + j - 2] != RECORRIDA) { // No se ha recorrido previamente

			distanciaMin = ultimoScanner[j][i]; // Actualiza la distancia de la casilla más cercana
			decision = parserCoordMov(j, i);    // Actualiza el movimiento de la casilla más cercana
		    }
		}
	    }
	}
        
	return decision;
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
     * @author Francisco Javier Ortega
     */
    private void faseObjetivoEncontrado() {
	System.out.println("¡He encontrado el objetivo!");
	enviarMensaje(JSON.escribirAction("logout"));
	estadoActual = ESTADO_FINAL;
    }
    
    /**
     * @author Antonio Espinosa
     */
    private void mostrarResumen() {
        int segundosTotal = (int) (System.currentTimeMillis() - tiempoInicio )/1000;
        int minutos = segundosTotal / 60;  
        int segundos = segundosTotal - (minutos * 60); 
        
        System.out.println("Pasos que ha dado el agente: " + pasos);
        System.out.println("Tiempo de ejecución del mapa: "+ minutos + " Minutos " + segundos + " Segundos");
    }    
}
