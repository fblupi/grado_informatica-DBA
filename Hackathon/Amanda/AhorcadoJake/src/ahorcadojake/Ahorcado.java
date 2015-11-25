/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahorcadojake;

import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author louri
 */
public class Ahorcado extends SingleAgent{

    private ACLMessage inbox, outbox;
    private String nombreControlador;
    private final int ESTADO_INICIAL = 0;
    private final int ESTADO_JUGANDO = 1;
    private final int ESTADO_FINAL = 2;
    private final int ESTADO_QUIERO_JUGAR = 3;
    private int estado;
    
    public Ahorcado(AgentID id) throws Exception {
	super(id);
    }

    @Override
    public void init() {
	System.out.println("Bot Iniciandose ");
        inbox = null;
        outbox = null;
        nombreControlador = "Jake";
        estado = ESTADO_INICIAL;
    }

    @Override
    public void execute() {
	System.out.println("Agente en ejecución");
        boolean terminado= false;
        while(!terminado){
            switch(estado){
                case ESTADO_INICIAL:
                    faseInicial();
                    break;
                case ESTADO_JUGANDO:
                    faseJugando();
                    break;
                case ESTADO_FINAL:
                    terminado = true;
                    break;
                case ESTADO_QUIERO_JUGAR:
                    faseQuieroJugar();
                    break;
            }
        }
    }

    @Override
    public void finalize() {
	System.out.println("Agente cerrandose");
	super.finalize();
    }
    private void faseInicial(){
        System.out.println("Agente pidiendo ID");
        enviarMensaje(ACLMessage.REQUEST);
        estado = ESTADO_QUIERO_JUGAR;
    }
    private void faseQuieroJugar(){
        System.out.println("Agente Esperando respuesta");
            try {
                inbox = receiveACLMessage();
                System.out.println("Mensaje recibido: " + inbox.getContent());
                if (inbox.getPerformativeInt()==ACLMessage.REFUSE) {
                    System.out.println("Jake no quiere jugar conmigo :(");
                    estado = ESTADO_INICIAL;
                } else if (inbox.getPerformativeInt()==ACLMessage.AGREE) {
                    System.out.println("Jake quiere jugar conmigo :)");
                    estado = ESTADO_JUGANDO;
                }else{
                    enviarMensaje(ACLMessage.CANCEL);
                    estado = ESTADO_INICIAL;
                }
            } catch (InterruptedException ex) {
                System.err.println("Agente Error de comunicación");
            }
    }
    private void enviarMensaje(int contenido) {
	System.out.println("Enviando mensaje: " + contenido);
	outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID(nombreControlador));
        outbox.setPerformative(contenido);
	outbox.setContent("");
	System.out.println("Agente enviando mensaje");
	this.send(outbox);
    }

    private void faseJugando() {
        boolean palabraAcertada = false;
        for(char alphabet = 'A'; alphabet <= 'Z' && !palabraAcertada ;alphabet++) {
            enviarMensajeJugando(ACLMessage.QUERY_IF,alphabet);
            System.out.println("Agente jugando");
            try {
                inbox = receiveACLMessage();
                if (inbox.getPerformativeInt()==ACLMessage.INFORM) {
                    System.out.println("---->" + inbox.getContent());
                } else if (inbox.getPerformativeInt()==ACLMessage.CONFIRM) {
                    System.out.println("---->" + inbox.getContent());
                    palabraAcertada = true;
                    estado = ESTADO_FINAL;
                }else{
                    enviarMensaje(ACLMessage.CANCEL);
                }
            } catch (InterruptedException ex) {
                System.err.println("Agente Error de comunicación");
            }
        }
    }

    private void enviarMensajeJugando(int mensaje, char contenido) {
        System.out.println("Enviando mensaje: " + contenido);
	outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID(nombreControlador));
        outbox.setPerformative(mensaje);
	outbox.setContent(String.valueOf(contenido));
	System.out.println("Agente enviando mensaje");
	this.send(outbox);
    }
}
