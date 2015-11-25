/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agente;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LENOVO
 */
public class Agente extends SingleAgent{
     private ACLMessage outbox;
     private ACLMessage inbox;
     private boolean completado; 
    /**
     * @param args the command line arguments
     */
   public Agente(AgentID id) throws Exception{
       super(id);
   }
    @Override
    public void init(){
        completado=false;
    }
    @Override
   public void execute(){
        outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID("Jake"));
        outbox.setPerformative(ACLMessage.REQUEST);
	outbox.setContent("");
	System.out.println("Agente Preguntando a Jake");
	this.send(outbox);
        
        try {
             inbox = receiveACLMessage();
            } catch (InterruptedException ex) {
             Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
         }
        
        while (inbox.getPerformativeInt() == ACLMessage.REFUSE)  {
            System.out.println("Jake no quiere jugar");
            System.out.println("Agente Preguntando a Jake");
            this.send(outbox);
             try {
             inbox = receiveACLMessage();
             } catch (InterruptedException ex) {
             Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
         }
        }
        
        for(char i='A';i-1!='Z' && !completado;i++){
            outbox = new ACLMessage();
            outbox.setSender(this.getAid());
            outbox.setReceiver(new AgentID("Jake"));
            outbox.setPerformative(ACLMessage.QUERY_IF);
            outbox.setContent(i+"");
            System.out.println("Agente jugando con la letra: "+i);
            this.send(outbox);
        
           try {
               inbox = receiveACLMessage();
           } catch (InterruptedException ex) {
               Logger.getLogger(Agente.class.getName()).log(Level.SEVERE, null, ex);
           }
           
            if(inbox.getPerformativeInt()== ACLMessage.CONFIRM){
                completado=true;
                System.out.println(inbox.getContent());
            }
            if(inbox.getPerformativeInt()== ACLMessage.INFORM){
                System.out.println("La palabra esta asi:" +inbox.getContent());
            }
        }
        
        outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID("Jake"));
        outbox.setPerformative(ACLMessage.CANCEL);
	outbox.setContent("");
	System.out.println("Agente acabando la comunicación a Jake");
	this.send(outbox);
   
   
   };
   
   public void enviarMensajes(String mens){
        outbox = new ACLMessage();
	outbox.setSender(this.getAid());
	outbox.setReceiver(new AgentID());
	outbox.setContent(mens);
	System.out.println("Agente enviando mensaje");
	this.send(outbox);
   
   }
   public String recibirMensaje() throws InterruptedException{
       String salida;
       inbox = receiveACLMessage();
       salida=inbox.getContent();
       return salida;
   }
    @Override
   public void finalize(){
       super.finalize();
   }
}
