package jake;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author Antonio Espinosa Jiménez
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        AgentsConnection.connect("isg2.ugr.es",6000, "test", "guest", "guest", false);

        Agente alumno = new Agente(new AgentID("74681587"));
        alumno.start();
    }

}
