/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahorcadojake;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author José Guadix
 */
public class Agente extends SingleAgent {

    private final int ESTADO_PIDIENDO_JUGAR = 0;
    private final int ESTADO_JUGANDO = 1;
    private final int ESTADO_PALABRA_ACERTADA = 2;
    private final int ESTADO_ERROR_PROTOCOLO = 3;
    private int estadoActual;
    private boolean finalizarJuego;
    private char letra;

    public Agente(AgentID aid) throws Exception {
	super(aid);
    }

    @Override
    protected void init() {
	estadoActual = ESTADO_PIDIENDO_JUGAR;
	finalizarJuego = false;
	letra = 'A';
    }

    @Override
    protected void execute() {
	while (!finalizarJuego) {
	    switch (estadoActual) {
		case ESTADO_PIDIENDO_JUGAR:
		    fasePidiendoJugar();
		    break;
		case ESTADO_JUGANDO:
		    faseJugando();
		    break;
		case ESTADO_PALABRA_ACERTADA:
		    fasePalabraAcertada();
		    break;
		case ESTADO_ERROR_PROTOCOLO:
		    faseErrorProtocolo();
		    break;
	    }
	}
    }

    @Override
    public void finalize() {
	super.finalize();
    }

    private void enviarMensaje(int performativa, String content) {
	ACLMessage message = new ACLMessage();
	message.setSender(getAid());
	message.setReceiver(new AgentID("Jake"));
	message.setPerformative(performativa);
	message.setContent(content);
	System.out.println("Enviando mensaje: " + message.getPerformative() + " " + content);
	send(message);
    }

    private void fasePidiendoJugar() {
	enviarMensaje(ACLMessage.REQUEST, "");

	ACLMessage message = null;
	try {
	    message = receiveACLMessage();
	    if (message.getPerformativeInt() == ACLMessage.AGREE) {
		estadoActual = ESTADO_JUGANDO;
		System.out.println("Jake quiere jugar");
	    } else if (message.getPerformativeInt() == ACLMessage.NOT_UNDERSTOOD) {
		estadoActual = ESTADO_ERROR_PROTOCOLO;
	    } else {
		System.out.println("Jake no quiere jugar");
	    }
	} catch (InterruptedException ex) {
	    System.out.println("Error al recibir un mensaje");
	    estadoActual = ESTADO_ERROR_PROTOCOLO;
	}
    }

    private void faseJugando() {
	ACLMessage message = null;
	enviarMensaje(ACLMessage.QUERY_IF, String.valueOf(letra));
	try {
	    message = receiveACLMessage();
	    System.out.println("Mensaje recibido " + message.getContent());
	    if (message.getPerformativeInt() == ACLMessage.CONFIRM) {
		estadoActual = ESTADO_PALABRA_ACERTADA;
	    } else if (message.getPerformativeInt() == ACLMessage.NOT_UNDERSTOOD) {
		estadoActual = ESTADO_ERROR_PROTOCOLO;
	    }
	    if(letra == 'Z'){
		estadoActual = ESTADO_ERROR_PROTOCOLO;
	    }
	    letra++;
	} catch (InterruptedException ex) {
	    System.out.println("Error al recibir un mensaje");
	    estadoActual = ESTADO_ERROR_PROTOCOLO;
	}
    }

    private void fasePalabraAcertada() {
	System.out.println("Has acertado la palabra!");
	finalizarJuego = true;
    }

    private void faseErrorProtocolo() {
	System.out.println("Error de protocolo");
	enviarMensaje(ACLMessage.CANCEL, "");
	estadoActual = ESTADO_PIDIENDO_JUGAR;
    }
}
