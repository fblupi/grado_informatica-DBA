package practica3;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author José Guadix
 */
public class main {

    public static void main(String[] args) {
	PedirMapa pedirMapa = new PedirMapa(null, true);

	String mundo = pedirMapa.getMapa();
	if (mundo != null) {
	    AdmiralAckbar ackbar = null;
	    Drone[] drones = new Drone[4];

	    AgentsConnection.connect("isg2.ugr.es", 6000, "Cerastes", "Unicornio", "Matute", false);
	    try {
		ackbar = new AdmiralAckbar(new AgentID("Ackbar_____"), mundo);
		for (int i = 0; i < drones.length; i++) {
		    drones[i] = new Drone(new AgentID("Drone6" + i));
		    drones[i].start();
		}
		ackbar.start();
	    } catch (Exception ex) {
		System.err.println("Error creando agentes");
		System.err.println(ex.toString());
		System.exit(1);
	    }
	}
    }

}
