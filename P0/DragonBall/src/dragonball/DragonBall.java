
package dragonball;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author fblupi
 */
public class DragonBall {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        AgentsConnection.connect("siadex.ugr.es",6000,"test","guest", "guest", false);
        Agente agente = new Agente(new AgentID("75926571Y"));
        agente.start(); 
    }
    
}
