/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahorcadojake;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author louri
 */
public class AhorcadoJake {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	Ahorcado bot = null;

	AgentsConnection.connect("isg2.ugr.es", 6000, "test", "guest", "guest", false);
        try {
            bot = new Ahorcado(new AgentID("76636715"));
        } catch (Exception ex) {
            System.err.println("Error creando agentes");
            System.exit(1);
        }
        bot.start();
    }
}
