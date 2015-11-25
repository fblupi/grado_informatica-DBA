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
 * @author José Guadix
 */
public class AhorcadoJake {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	
	AgentsConnection.connect("isg2.ugr.es", 6000, "test", "guest", "guest", false);
	try {
	    Agente agente = new Agente(new AgentID("47545486"));
	    agente.start();
	} catch (Exception ex) {
	    System.out.println("Error al crear el agente");
	}
    }

}
