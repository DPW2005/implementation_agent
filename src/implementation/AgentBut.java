/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementation;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Random ;

public class AgentBut extends Agent {
    private enum Etats {IDLE,EN_VOL,ESQUIVE_OBSTACLE,LIVRAISON,RETOUR,EN_CHARGE} ;
    private Etats etatActuel = Etats.IDLE ;
    private double batterie = 100.0 ;
    private String positionActuel = "Base" ;
    private String destination = null ;
    private boolean charge = false ;
    private Random rand = new Random() ;
    
    @Override
    protected void setup(){
        System.out.println("Agent est pret a etre utilise") ;
        
        addBehaviour(new CyclicBehaviour(){
            @Override
            public void action(){
                ACLMessage message = receive() ;
                if(message != null){
                   traitementMessage(message) ; 
                }
                majEtat() ;
                choisirAction() ;
                block() ;
            }
        });
    }
    
    private void traitementMessage(ACLMessage msg){
        if(msg.getPerformative() == ACLMessage.REQUEST){
            System.out.println("Mission recue : Livraison vers "+msg.getContent()) ;
            destination = msg.getContent() ;
            charge = true ;
            etatActuel = Etats.EN_VOL ;
        }
    }
    
    private void majEtat(){
        if(etatActuel == Etats.EN_VOL){
            batterie -= 5 ;
            if(rand.nextDouble() < 0.2){
                etatActuel = Etats.ESQUIVE_OBSTACLE ;
                System.out.println("Obstacle detectee! Modification de la trajectoire...") ;
            }
            else if(batterie < 20){
                etatActuel = Etats.EN_CHARGE ;
                System.out.println("Batterie faible1 Retour a la base pour charge...") ;
            }
            else if(destination.equals(positionActuel)){
                etatActuel = Etats.LIVRAISON ;
                System.out.println("Colis livre a "+destination) ;
                charge = false ;
            }
        }
        if(etatActuel == Etats.LIVRAISON){
            etatActuel = Etats.RETOUR ;
        }
        if(etatActuel == Etats.RETOUR && positionActuel.equals("Base")){
            etatActuel = Etats.IDLE ;
            System.out.println("Agent pret pour une nouvelle mission.") ;
        }
    }
    
    private void choisirAction(){
        switch(etatActuel){
            case EN_VOL :
                System.out.println("Agent en vol vers "+destination) ;
                positionActuel = destination ;
                break ;
            case ESQUIVE_OBSTACLE :
                System.out.println("Trajectoire ajustee pour eviter l'obstacle.") ;
                etatActuel = Etats.EN_VOL ;
                break ;
            case EN_CHARGE :
                batterie = 100.0 ;
                positionActuel = "Base" ;
                etatActuel = Etats.RETOUR ;
                break ;
            case RETOUR :
                System.out.println("Retour a la base...") ;
                positionActuel = "Base" ;
                break ;
            default :
                break ;
        }
    }
}
