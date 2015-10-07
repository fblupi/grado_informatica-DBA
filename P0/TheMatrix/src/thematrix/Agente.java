
package thematrix;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author fblupi
 */
public class Agente extends SingleAgent {
    
    private final int INTERROGAR = 0, ESCUCHAR = 1, FIN = 2;
    private int estado;
    private ACLMessage inbox, outbox;
    private boolean exit;
    private String rec;
    
    public Agente(AgentID aid, String rec) throws Exception  {
        super(aid);
        this.rec = rec;
    }    
    
    @Override
    public void init() {
        System.out.println("[AG] " + this.getName() + " --> Init");
        estado = INTERROGAR;
        inbox = null;
        outbox = null;
        exit = false;   
    }
    
    @Override
    public void execute() {
        System.out.println("[AG] " + this.getName() + " --> Execute");
        while(!exit) {
            switch(estado) {
                case INTERROGAR:
                    System.out.println("[AG] " + this.getName() + " --> Interrogar");
                    outbox = new ACLMessage();
                    outbox.setSender(this.getAid());
                    outbox.setReceiver(new AgentID(rec));
                    if (Math.random() > .3) {
                        outbox.setContent("Pregunta");
                        estado = ESCUCHAR;
                    } else {
                        outbox.setContent("Liberado");
                        estado = FIN;
                    }
                    this.send(outbox);
                    break;
                case ESCUCHAR:
                    System.out.println("[AG] " + this.getName() + " --> Escuchar");
                    boolean respuesta = false;
                    while(!respuesta) {
                        try {
                            inbox = receiveACLMessage();
                            if(inbox.getContent().equals("Respuesta")) {
                                estado = INTERROGAR;
                                respuesta = true;
                            }
                        } catch (InterruptedException ex) {
                            System.err.println("[AG] " + this.getName() + " --> Error recibiendo mensaje");
                            respuesta = true;
                            exit = true;
                        }
                    }
                    break;
                case FIN:
                    System.out.println("[AG] " + this.getName() + " --> Fin");
                    exit = true;
                    break;
            }
        }
    }
    
    @Override
    public void finalize() {
        System.out.println("[AG] " + this.getName() + " --> Finalize");       
        super.finalize();
    }
    
}
