/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agente;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author LENOVO
 */
public class main {
     public static void main(String[] args) {
        // TODO code application logic here
            Agente agent=null;
            AgentsConnection.connect("isg2.ugr.es", 6000, "test", "guest", "guest", false);
	    try {
		agent = new Agente(new AgentID("76667039"));
	    } catch (Exception ex) {
		System.err.println("Error creando agentes");
		System.exit(1);
	    }
	    agent.start();
    }
}
