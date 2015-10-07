
package thematrix;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author fblupi
 */
public class TheMatrix {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final String nombreAgente = "Smith", nombreRebelde = "Neo";
        Agente smith = null;
        Rebelde neo = null;
        
        AgentsConnection.connect("localhost",5672,"test","guest", "guest", false);
        
        try {
            smith = new Agente(new AgentID(nombreAgente), nombreRebelde);
            neo = new Rebelde(new AgentID(nombreRebelde));
        } catch (Exception ex) {
            System.err.println("Error creando agentes");
            System.exit(1);
        }
        
        smith.start();
        neo.start();
    }
    
}
