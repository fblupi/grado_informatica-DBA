package practica2;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * @author Amanda Fernández
 * @author Francisco Javier Ortega
 * @author Antonio Espinosa
 */
public class Main {

    public static void main(String[] args) {
	PedirMapa pedirMapa = new PedirMapa(null, true);

	String mundo = pedirMapa.getMapa();
	if (mundo != null) {
	    RescueBot bot = null;

	    AgentsConnection.connect("isg2.ugr.es", 6000, "Cerastes", "Unicornio", "Matute", false);
	    try {
		bot = new RescueBot(new AgentID("bot"), mundo);
	    } catch (Exception ex) {
		System.err.println("Error creando agentes");
		System.exit(1);
	    }
	    bot.start();
	}

    }
}
