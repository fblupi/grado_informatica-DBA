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
 * @author José Guadix
 * @author Amanda Fernández
 * @author Francisco Javier Ortega
 * @author Antonio Espinosa
 */
public class RescueBot extends SingleAgent {

    private EstadosBot estadoActual;
    private String nombreControlador;
    private int nivelBateria;
    private String token;
    private int[] ultimoGPS;
    private int[][] ultimoRadar;
    private int[][] mapa;
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
                case FIN:
                    // En realidad este estado es aparentemente innecesario
                    System.out.println("Agente("+this.getName()+") Terminando ejecución");                   
                    terminar = true;
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
                    status = INTERROGAR;
                    recibiendo=false;
                }
            } catch (InterruptedException ex) {
                System.err.println("Agente Error de comunicación");
                recibiendo=false;
                terminar = true;
            }                        
        }
    }
    
}
