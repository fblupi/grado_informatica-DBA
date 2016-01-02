package practica3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Drone extends SingleAgent {

    private final String NOMBRE_CONTROLADOR = "Ackbar___";
    private final String NOMBRE_SERVIDOR = "Cerastes";
    boolean terminar;
    private ACLMessage inbox, outbox;
    String receptor;

    public Drone(AgentID id) throws Exception {
	super(id);
    }

    public void init() {
	System.out.println(getName() + " Iniciandose ");
	inbox = null;
	outbox = null;
	terminar = false;
    }
    /**
     * @author Amanda Fernández Piedra y Francisco Javier Ortega Palacios
     * Método execute de los drones zombies
     */
    public void execute() {
        while(!terminar){
            try {
                inbox = receiveACLMessage();
//                System.out.println(getName() + " ha recibido: " + inbox.getContent());
                if(inbox.getPerformativeInt()==ACLMessage.CANCEL){
                    terminar = true;
                }else{
                    if(inbox.getSender().name.equals(NOMBRE_SERVIDOR)){
                        receptor = NOMBRE_CONTROLADOR;
                    }else{
                        receptor = NOMBRE_SERVIDOR;
                    }
                    enviar(receptor,inbox.getPerformativeInt(),inbox.getContent());
                }
            } catch (InterruptedException ex) {
                System.err.println("Agente Error de comunicación");
            }
        }
    }

    public void finalize() {
	super.finalize();
    }

    private void enviar(String receptor, int performativa, String contenido) {
	outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID(receptor));
        outbox.setPerformative(performativa);
	outbox.setContent(contenido);
//	System.out.println(getName() + ": enviando mensaje a " + receptor + " tipo " + outbox.getPerformative() + " contenido " + contenido);
        this.send(outbox);   
    }
}
