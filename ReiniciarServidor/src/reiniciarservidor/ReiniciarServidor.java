
package reiniciarservidor;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;

/**
 *
 * @author José Guadix
 */
public class ReiniciarServidor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	boolean reiniciar = true;
	Scanner scanner = new Scanner(System.in);
	int num;
	String str;
	do {
	    System.out.println("Introduce la acción que desea realizar:");
	    System.out.println("1.- Pedir información");
	    System.out.println("2.- Reiniciar servidor");
	    str = scanner.next();
	    try{
		num = Integer.parseInt(str);
	    }catch(Exception e){
		num = -1;
	    }
	    System.out.println("num: " + num);
	} while (num != 1 && num != 2);

	if (num == 1) {
	    reiniciar = false;
	}
	
	AgentsConnection.connect("isg2.ugr.es", 6000, "test", "Unicornio", "Matute", false);
//	AgentsConnection.connect("localhost", 6000, "test", "guest", "guest", false);
	try {
	    Agente agente = new Agente(new AgentID("Agente"), reiniciar);
	    agente.start();
	} catch (Exception ex) {
	    System.err.println(ex.toString());
	}
    }

}
