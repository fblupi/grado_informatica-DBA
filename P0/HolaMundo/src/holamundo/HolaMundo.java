
package holamundo;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author fblupi
 */
public class HolaMundo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        AgentsConnection.connect("localhost",5672,"test","guest", "guest", false);
        Agente agente = new Agente(new AgentID("Fran"));
        agente.start();
    }
    
}
