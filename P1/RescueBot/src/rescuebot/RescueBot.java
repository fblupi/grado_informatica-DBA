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
    
    public RescueBot(AgentID id, String mundoAVisitar) throws Exception{
        super(id);
        this.mundoAVisitar = mundoAVisitar;        
    }
    
     @Override
    public void init()  {
        System.out.println("Bot Iniciandose ");
        estadoActual = EstadosBot.ESTADO_INICIAL;
        nombreControlador = "Cerastes";
        inbox = null;
        outbox = null;
        terminar = false;
    }
    
    @Override
    public void execute()  {
        String mensaje;
        System.out.println("Agente en ejecución");
        while (!terminar)  {
            switch(estadoActual)  {
                case ESTADO_INICIAL:
                    iniciarConversacion();
                    
                    break;
                case ESCUCHAR:
                    System.out.println("Agente("+this.getName()+") Esperando respuesta");
                    boolean repetir=true;
                    while (repetir)  {
                        try {
                            inbox = receiveACLMessage();
                            if (inbox.getContent().equals("Respuesta")) { 
                                status = INTERROGAR;
                                repetir=false;
                            }
                        } catch (InterruptedException ex) {
                            System.err.println("Agente("+this.getName()+") Error de comunicación");
                            repetir=false;
                            exit = true;
                        }                        
                    }
                    break;
                case FIN:
                    // En realidad este estado es aparentemente innecesario
                    System.out.println("Agente("+this.getName()+") Terminando ejecución");                   
                    exit = true;
                    break;
            }
        }
    }
    
    @Override
    public void finalize()  {
        System.out.println("Agente cerrandose");       
        super.finalize();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	// TODO code application logic here
    }

    private void iniciarConversacion() {
        System.out.println("Agente pidiendo ID");            
        enviarMensaje(JSON.login(mundoAVisitar));
    }
    
    private void enviarMensaje(String contenido){
        outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        // Es necesario saber el nombre del agente al mandar
        // un mensaje por primera vez. También se puede pasar por
        // parámetro al constructor
        outbox.setReceiver(new AgentID(nombreControlador));
        outbox.setContent(contenido);
        System.out.println("Agente enviando mensaje");            
        this.send(outbox);
    }
    
}
