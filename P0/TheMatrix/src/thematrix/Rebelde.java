
package thematrix;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author fblupi
 */
public class Rebelde extends SingleAgent {
    
    private final int INTERROGADO = 0, RESPUESTA = 1, FIN = 2;
    private int estado;
    private ACLMessage inbox, outbox;
    private boolean exit;
    
    public Rebelde(AgentID aid) throws Exception  {
        super(aid);
    }       
    
    @Override
    public void init() {
        System.out.println("[RE] " + this.getName() + " --> Init");
        estado = INTERROGADO;
        inbox = null;
        outbox = null;
        exit = false;  
    }
    
    @Override
    public void execute() {
        System.out.println("[RE] " + this.getName() + " --> Execute");
        while(!exit) {
            switch(estado) {
                case INTERROGADO:
                    System.out.println("[RE] " + this.getName() + " --> Interrogado");
                    try {
                        inbox = receiveACLMessage();
                        switch (inbox.getContent()) {
                            case "Pregunta":
                                estado = RESPUESTA;
                                break;
                            case "Liberado":
                                estado = FIN;
                                break;
                        }
                    } catch (InterruptedException ex) {
                        System.err.println("[RE] " + this.getName() + " --> Error recibiendo mensaje");
                        exit = true;
                    }
                    break;
                case RESPUESTA:
                    System.out.println("[RE] " + this.getName() + " --> Respuesta");
                    outbox = new ACLMessage();
                    outbox.setSender(this.getAid());
                    outbox.setReceiver(inbox.getSender());
                    outbox.setContent("Respuesta");
                    this.send(outbox);
                    estado = INTERROGADO;
                    break;
                case FIN:
                    System.out.println("[RE] " + this.getName() + " --> Fin");
                    exit = true;
                    break;
            }
        }
    }
    
    @Override
    public void finalize() {
        System.out.println("[RE] " + this.getName() + " --> Finalize");  
        super.finalize();
    } 
}
