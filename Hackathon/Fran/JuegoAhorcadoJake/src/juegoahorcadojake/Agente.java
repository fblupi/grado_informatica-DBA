
package juegoahorcadojake;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fblupi
 */
public class Agente extends SingleAgent  {
    
    private final int PEDIR_JUGAR = 0;
    private final int JUGANDO = 1;
    private final int FINALIZAR_JUEGO = 2;
    private boolean exit;
    private ACLMessage inbox;
    private ACLMessage outbox;
    private int estado;
    private char letra;
    
    public Agente(AgentID aid) throws Exception  {
        super(aid);
    }    
    
    @Override
    public void init() {
        estado = PEDIR_JUGAR;
        inbox = null;
        outbox = null;
        exit = false;  
        letra = 'A';
    }
    
    @Override
    public void execute() {
        while(!exit) {
            switch(estado) {
                case PEDIR_JUGAR:
                    while(estado == PEDIR_JUGAR) {
                        enviarMensaje(ACLMessage.REQUEST, "");
                        try {
                            inbox = receiveACLMessage();
                            if(inbox.getPerformativeInt() == ACLMessage.AGREE) {
                                estado = JUGANDO;
                            }
                            System.out.println("\nMensaje recibido: " + inbox.getPerformative() + " --> " + inbox.getContent());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case JUGANDO:
                    enviarMensaje(ACLMessage.QUERY_IF, String.valueOf(letra));
                    try {
                        inbox = receiveACLMessage();
                        if(inbox.getPerformativeInt() == ACLMessage.CONFIRM) {
                            estado = FINALIZAR_JUEGO;
                        }
                        System.out.println("\nMensaje recibido: " + inbox.getPerformative() + " --> " + inbox.getContent());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    letra++;
                    break;
                case FINALIZAR_JUEGO:
                    enviarMensaje(ACLMessage.CANCEL, "");
                    exit = true;
                    break;
            }
        }
    }
    
    private void enviarMensaje(int performativa, String contenido) {
        outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Jake"));
        outbox.setPerformative(performativa);
        outbox.setContent(contenido);
        System.out.println("\nEnviando mensaje: " + outbox.getPerformative() + " --> " + outbox.getContent());
        this.send(outbox);
    }
}
