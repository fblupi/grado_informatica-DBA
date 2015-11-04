/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescuebot;

import TraceDaddy.Daddy;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static rescuebot.EstadosBot.*;

/**
 *
 * @author Francisco Javier Bolívar
 * @author Antonio Espinosa
 * @author Amanda Fernández
 * @author José Guadix
 * @author Antonio David López
 * @author Francisco Javier Ortega
 *
 */
public class RescueBot extends SingleAgent {

    private final int LIBRE = 0;
    private final int OBSTACULO = 1;
    private final int OBJETIVO = 2;
    private final int RECORRIDA = 3;
    private final int DESCONOCIDA = 4;
    private final int TAMANO_MAPA = 500;

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
    private int NUM_SENSORES = 3;
    private Imagen imagen;
    private boolean conectando;

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
	    imagen.guardarPNG(mundoAVisitar + " - " + Date.from(Instant.now()).toString().replace(":", "-") + ".png");
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException ex) {
		Logger.getLogger(RescueBot.class.getName()).log(Level.SEVERE, null, ex);
	    }
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
		    imagen.guardarPNG(mundoAVisitar + " final - " + Date.from(Instant.now()).toString().replace(":", "-") + ".png");
		    imagen.cerrar();
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
	super.finalize();
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
	    actualizarMapa();
	    imagen.actualizarMapa(mapa);
	    estadoActual = ESTADO_MOVER;
	}
    }

    private void faseRepostar() {
	enviarMensaje(JSON.escribirAction("refuel"));
	nivelBateria = 100;
	estadoActual = ESTADO_RECIBIR_DATOS;
    }

    private void faseMover() {
	String decision = elegirMovimiento();
	enviarMensaje(JSON.escribirAction(decision));
	if (decision.equals("logout")) {
	    estadoActual = ESTADO_FINAL;
	} else {
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
//		System.out.println("x: " + x + "y: " + y + "i: " + i + "j: " + j);
		System.out.print("ultimoGPS[0] +i: " + (ultimoGPS[0]+i) + " ultimoGPS[1] + j: " + (ultimoGPS[1] + j));
		if ((ultimoGPS[0] + i >= 0 && ultimoGPS[0] + i < TAMANO_MAPA)
			&& (ultimoGPS[1] + j >= 0 && ultimoGPS[1] + j < TAMANO_MAPA)) { // No se sale del límite
		    System.out.print(" -> entra v: " + ultimoRadar[x][y]);
		    if (mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] == DESCONOCIDA) // No machaca pasos anteriores
		    {
			mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] = ultimoRadar[x][y];   // Actualiza casilla con el valor recibido del radar
		    }
//		    System.out.print(mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] + " ");
		}
		System.out.println("");
	    }
//	    System.out.println("");
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
    public String elegirMovimiento() {
	String decision = "logout"; // De primeras asumimos que no se puede mover a ningún sitio
	float distanciaMin = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí

	// Busca el movimiento
	for (int j = 1; j < 4; j++) {
	    for (int i = 1; i < 4; i++) {
//		System.out.print("ultimoGPS[0] + i - 1: " + (ultimoGPS[0] + i - 1) + ", ultimoGPS[1] + j - 1: " + (ultimoGPS[1] + j - 1));
//		System.out.println(" -> " + distanciaMin + " -> " + ultimoScanner[i][j]);
		if (ultimoScanner[i][j] < distanciaMin // La distancia es menor que la menor almacenada
//			&& ultimoGPS[1] + j - 1 >= 0 && ultimoGPS[0] + i - 1 >= 0
			&& mapa[ultimoGPS[0] + i - 1][ultimoGPS[1] + j - 1] != OBSTACULO // No hay obstáculo
			&& mapa[ultimoGPS[0] + i - 1][ultimoGPS[1] + j - 1] != RECORRIDA) { // No se ha recorrido previamente
//		    System.out.println("ultimoGPS[0] + i: " + (ultimoGPS[0] + i) + ", ultimoGPS[1] + j: " + (ultimoGPS[1] + j));
		   
		    distanciaMin = ultimoScanner[i][j]; // Actualiza la distancia de la casilla más cercana
		    decision = parserCoordMov(i, j);    // Actualiza el movimiento de la casilla más cercana
		}
	    }
	}
	System.out.println(decision + ": " + distanciaMin);
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
    public String parserCoordMov(int y, int x) {
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

    private void faseObjetivoEncontrado() {
	System.out.println("¡He encontrado el objetivo!");
	enviarMensaje(JSON.escribirAction("logout"));
	estadoActual = ESTADO_FINAL;
    }

}
