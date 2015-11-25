
package juegoahorcadojake;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fblupi
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        AgentsConnection.connect("isg2.ugr.es",6000, "test", "guest", "guest", false);
        Agente agente = null;
        try {
            agente = new Agente(new AgentID("75926571"));
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        agente.start(); 
    }
    
}
