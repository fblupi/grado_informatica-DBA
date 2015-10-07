
package dragonball;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fblupi
 */
public class Agente extends SingleAgent {
    
    public Agente(AgentID aid) throws Exception  {
        super(aid);
    }

    @Override
    public void execute()  {
        ACLMessage outbox = new ACLMessage(); 
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Songoanda"));
        outbox.setContent("Hola");
        this.send(outbox);
        try {
            ACLMessage inbox = this.receiveACLMessage();
            System.out.println("\nRecibido mensaje " + inbox.getContent() + " de " + inbox.getSender().getLocalName());
        } catch (InterruptedException ex) {
            Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
        }
        outbox = new ACLMessage(); 
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Songoanda"));
        outbox.setContent("olocciP");
        this.send(outbox);
    }
    
}
