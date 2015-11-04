/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescuebot;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
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
    
    /**
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     * @param id
     * @param mundoAVisitar
     * @throws Exception 
     */
    
    public RescueBot(AgentID id, String mundoAVisitar) throws Exception{
        super(id);
        this.mundoAVisitar = mundoAVisitar;        
    }
    
    /**
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
     @Override
    public void init()  {
        System.out.println("Bot Iniciandose ");
        estadoActual = EstadosBot.ESTADO_INICIAL;
        nombreControlador = "Cerastes";
        inbox = null;
        outbox = null;
        terminar = false;
        
    }
    
    /**
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
    @Override
    public void execute()  {
        String mensaje;
        System.out.println("Agente en ejecución");
        while (!terminar)  {
            switch(estadoActual)  {
                case ESTADO_INICIAL:
                    iniciarConversacion();
                    break;
                case ESTADO_RECIBIR_DATOS:
                    faseRecibiendoDatos();
                    break;
                case ESTADO_RECIBIR_RESPUESTA:
                    
                    break;
                case ESTADO_REPOSTAR:
                    
                    break;
                case ESTADO_MOVER:
                    
                    break;
                case ESTADO_FINAL:
                    // En realidad este estado es aparentemente innecesario
                    System.out.println("Agente("+this.getName()+") Terminando ejecución");                   
                    terminar = true;
                    break;
                case ESTADO_ENCONTRADO:
                    
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
    public void finalize()  {
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
        estadoActual = ESTADO_RECIBIR_DATOS;
    }
    
    /**
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     * @param contenido 
     */
    private void enviarMensaje(String contenido){
        outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID(nombreControlador));
        outbox.setContent(contenido);
        System.out.println("Agente enviando mensaje");            
        this.send(outbox);
    }
    
    /**
     * @author Amanda Fernández
     * @author Francisco Javier Ortega
     * @author Antonio Espinosa
     */
    private void faseRecibiendoDatos() {
        System.out.println("Agente Esperando respuesta");
        boolean recibiendo=true;
        while (recibiendo)  {
            try {
                inbox = receiveACLMessage();
                if (inbox.getContent().equals("Respuesta")) { 
                    //status = INTERROGAR;
                    recibiendo=false;
                }
            } catch (InterruptedException ex) {
                System.err.println("Agente Error de comunicación");
                recibiendo=false;
                terminar = true;
            }                        
        }
    }
    
    /**
     * Inicializa todas las casillas del mapa como desconocidas
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
     * Actualiza el mapa con la posición por donde acaba de pasar y los valores que recibe del radar
     * @author Francisco Javier Bolívar
     * @author Antonio David López
     */
    private void actualizarMapa() {
        mapa[ultimoGPS[0]][ultimoGPS[1]] = RECORRIDA;   // Guarda posición actual como posición por donde ha pasado
        for (int x = 0, i = -2; x < 5; x++, i++) {      // x: recorre el radar, i: recorre mapa desde la posición actual
            for (int y = 0, j = -2; y < 5; y++, j++) {  // y: recorre el radar, j: recorre mapa desde la posición actual
                if ((ultimoGPS[0] + i >= 0 && ultimoGPS[0] + i < TAMANO_MAPA) 
                        && (ultimoGPS[1] + j >= 0 && ultimoGPS[1] + j < TAMANO_MAPA)) { // No se sale del límite
                   if (mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] == DESCONOCIDA)         // No machaca pasos anteriores
                        mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] = ultimoRadar[x][y];   // Actualiza casilla con el valor recibido del radar
                }
            }
        }
    }
    
    /**
     * Elige hacia dónde quiere moverse en función de qué casilla adyascente al bot está más cerca del objetivo y no es un obstáculo
     * @author Francisco Javier Bolívar
     * @author Antonio David López
     * @return movimiento elegido
     */
    public String elegirMovimiento() {
        String decision = "logout"; // De primeras asumimos que no se puede mover a ningún sitio
        float distanciaMin = Float.MAX_VALUE; // Se inicia a un valor muy alto para que la primera disponible se guarde aquí

        // Busca el movimiento
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                if (ultimoScanner[i][j] < distanciaMin  // La distancia es menor que la menor almacenada
                        && mapa[ultimoGPS[0] + i - 2][ultimoGPS[1] + j - 2] != OBSTACULO    // No hay obstáculo
                        && mapa[ultimoGPS[0] + i - 2][ultimoGPS[1] + j - 2] != RECORRIDA) { // No se ha recorrido previamente
                    distanciaMin = ultimoScanner[i][j]; // Actualiza la distancia de la casilla más cercana 
                    decision = parserCoordMov(i, j);    // Actualiza el movimiento de la casilla más cercana
                }
            }
        }
        
        return decision;
    }
    
    /**
     * A partir de dos coordenadas selecciona hacia dónde se mueve
     * @author Francisco Javier Bolívar
     * @author Antonio David López
     * @param x coordenada x hacia donde se mueve
     * @param y coordenada y hacia donde se mueve
     * @return movimiento elegido
     */
    public String parserCoordMov(int x, int y) {
        if (x == 1) {
            if (y == 1)
              return "moveNW";  // (1, 1)
            else if (y == 2)
              return "moveN";   // (1, 2)
            else
              return "moveNE";  // (1, 3)
        } else if (x == 2) {
            if (y == 1)
              return "moveW";   // (2, 1)
            else
              return "moveE";   // (2, 3)  
        } else {
            if (y == 1)
              return "moveSW";  // (3, 1)
            else if (y == 2)
              return "moveS";   // (3, 2)  
            else
              return "moveSE";  // (3, 3)  
        }
    }
    
}
