package jake;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author Antonio Espinosa Jiménez
 */
public class Agente extends SingleAgent {

    private final int PREGUNTA_JUGAR = 0, ESCUCHA_JUGAR = 1, JUGANDO = 2;
    private int estado;
    boolean finalizar;
    private String letra;
    ACLMessage inbox, outbox;
    boolean encontrado;

    public Agente(AgentID aid) throws Exception {
        super(aid);
    }

    @Override
    public void init() {
        finalizar = false;
        estado = PREGUNTA_JUGAR;
        inbox = null;
        outbox = null;
        encontrado = false;
    }

    @Override
    public void execute() {
        while (!finalizar) {
            switch (estado) {
                case PREGUNTA_JUGAR:
                    System.out.println("VAMOS A JUGAR");
                    outbox = new ACLMessage();
                    outbox.setSender(this.getAid());
                    outbox.setReceiver(new AgentID("Jake"));
                    outbox.setPerformative(ACLMessage.REQUEST);
                    outbox.setContent("");
                    this.send(outbox);
                    System.out.println("Pregunto a Jake");
                    estado = ESCUCHA_JUGAR;
                    break;

                case ESCUCHA_JUGAR:
                    try {
                        inbox = this.receiveACLMessage();
                        System.out.println(this.getName() + ": Recibido mensaje "
                                + inbox.getContent()
                                + " de " + inbox.getSender().getLocalName());
                        if (inbox.getPerformativeInt() == ACLMessage.AGREE) {
                            System.out.println("Jake quiere jugar.");
                            estado = JUGANDO;
                        } else if (inbox.getPerformativeInt() == ACLMessage.REFUSE) {
                            System.out.println("Jake NO quiere jugar.");
                            estado = PREGUNTA_JUGAR;
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("ERROR al recibir mensaje de "
                                + inbox.getSender().getLocalName() + " : " + ex.toString());
                    }
                    break;

                case JUGANDO:
                    System.out.println("Ya estoy jugando.");
                    for (char i = 'A'; i - 1 != 'Z' && !encontrado; i++) {
                        outbox = new ACLMessage();
                        outbox.setSender(this.getAid());
                        outbox.setReceiver(new AgentID("Jake"));
                        outbox.setPerformative(ACLMessage.QUERY_IF);
                        outbox.setContent(i + "");
                        this.send(outbox);
                        System.out.println("Pruebo con la letra: " + i);

                        try {
                            inbox = this.receiveACLMessage();
                            System.out.println(this.getName() + ": Recibido mensaje "
                                    + inbox.getContent()
                                    + " de " + inbox.getSender().getLocalName());
                            if (inbox.getPerformativeInt() == ACLMessage.INFORM) {
                                System.out.println("Estado de la palabra: " + inbox.getContent());
                            } else if (inbox.getPerformativeInt() == ACLMessage.CONFIRM) {
                                System.out.println("Ya he encontrado la palabra: " + inbox.getContent());
                                encontrado = true;
                            }
                        } catch (InterruptedException ex) {
                            System.out.println("ERROR al recibir mensaje de "
                                    + inbox.getSender().getLocalName() + " : " + ex.toString());
                        }
                    }

                    outbox = new ACLMessage();
                    outbox.setSender(this.getAid());
                    outbox.setReceiver(new AgentID("Jake"));
                    outbox.setPerformative(ACLMessage.CANCEL);
                    this.send(outbox);
                    System.out.println("MANDO CANCEL PARA TERMINAR");
                    finalizar = true;
            }
        }
    }

    @Override
    public void finalize() {
        super.finalize();
    }
}
