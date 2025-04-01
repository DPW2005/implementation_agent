/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementation;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Random ;

public class AgentBut extends Agent {
    private double epargne ; // L'epargne actuel du client
    private double revenu ; // Le revenu mensuel du client
    private double depenses ; // Les depenses mensuelles du client
    private double objectif ; // L'objectif d'epargne du client
    private double interet ; // Le taux d'interet actuel
    private String etat ; // L'etat actuel de la situation financiere
    
    @Override
    protected void setup(){
        System.out.println("Agent est pret a etre utilise") ;
        epargne = 5000.0 ;
        revenu = 3000.0 ;
        depenses = 2000.0 ;
        objectif = 10000.0 ;
        interet = 0.02 ;
        etat = "Evaluation" ;
        
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                //Creation d'un modele pour recevoir les messages informatifs
                ACLMessage message = receive() ;
                if(message != null){
                    String contenu = message.getContent() ;
                    if(contenu.startsWith("MAJ:")){
                        //Simulation de la mise a jour des donnees financieres
                        epargne += Integer.parseInt(contenu.substring(4)) ;
                        System.out.println("Informations recues du client : "+contenu) ;
                        evaluationSituationFinanciere() ;
                    }
                    else{
                        System.out.println("Message non reconnu") ;
                    }
                }
                else{
                    block() ;
                }
            }
        });
        
        addBehaviour(new TickerBehaviour(this,10000){
            @Override
            public void onTick(){
                evaluationSituationFinanciere() ;
            }
        });
        
    }
    
    //Methode pour evaluer la situation financiere du client et changer d'etat si necessaire
    private void evaluationSituationFinanciere(){
        double epargneNette = revenu - depenses ;
        if(epargne < objectif * 0.5){
            etat = "Epargne insuffisante" ;
            envoyerConseilFinancier("Votre epargne est insuffisante. Envisagez de reduire vos depenses ou d'augmenter vos revenus") ;
            proposerMeilleureGestion() ;
        }
        else if(epargne >= objectif){
            etat = "Objectif atteint" ;
            envoyerConseilFinancier("Felicitations! Vous avez atteint votre objectif d'epargne. Pensez a investir vos fonds dans des options sures") ;
            suggererUtilisationFonds() ;
        }
        else if(epargne >= objectif * 0.8){
            etat = "Proche de l'objectif" ;
            envoyerConseilFinancier("Vous etes proche de votre objectif! Envisagez des placements a faibles risques") ;
            proposerPlacements() ;
        }
        System.out.println("Etat actuel : "+etat) ;
    }
    
    //Methode pour envoyer un conseil financier au client
    private void envoyerConseilFinancier(String conseil){
        ACLMessage message = new ACLMessage(ACLMessage.INFORM) ;
        message.addReceiver(getDefaultDF()) ;
        message.setContent(conseil) ;
        send(message) ;
        System.out.println("Conseil envoye : "+conseil) ;
    }
    
    //Methode pour conseiller une meilleure gestion des finances
    private void proposerMeilleureGestion(){
        envoyerConseilFinancier("Reduisez vos depenses inutiles et priviligiez l'epargne automatique") ;
    }
    
    //Methode pour suggerer l'utilisation des fonds lorsqu'un objectif est atteint
    private void suggererUtilisationFonds(){
        envoyerConseilFinancier("Vous pouvez utilisez vos fonds pour un invertissement immobilier ou pour un projet personnel.") ;
    }
    
    //Methode pour proposer des des placements a faibles risques
    private void proposerPlacements(){
        envoyerConseilFinancier("Envisagez des placements a faible risques comme les obligations d'Etat ou les comptes a terme") ;
    }
}
