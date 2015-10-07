
package holamundo;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author fblupi
 */
public class Agente extends SingleAgent {
   
    public Agente(AgentID aid) throws Exception  {
        super(aid);
    }

    @Override
    public void execute()  {
        System.out.println("\n\nHola mundo soy un agente\n");
    }
    
}
