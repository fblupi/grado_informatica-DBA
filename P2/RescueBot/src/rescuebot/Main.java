
package rescuebot;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.Scanner;

/**
* @author Amanda Fernández
* @author Francisco Javier Ortega
* @author Antonio Espinosa
*/
public class Main {
    public static void main(String[] args) {
        System.out.println("Introduce el mapa en el que quieres iniciar sesión");
        
        String mundo;
        Scanner entrada = new Scanner(System.in);
        mundo = entrada.nextLine();
        
        RescueBot bot=null;

        AgentsConnection.connect("isg2.ugr.es",6000, "Cerastes", "Unicornio", "Matute", false);
        try {
            bot = new RescueBot(new AgentID("bot"), mundo);
        } catch (Exception ex) {
            System.err.println("Error creando agentes");
            System.exit(1);
        }
        bot.start();
    }
}
