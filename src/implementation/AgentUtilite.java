/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementation;

import jade.core.Agent ;
import jade.core.behaviours.CyclicBehaviour ;
import jade.lang.acl.ACLMessage ;
import java.util.Random ;

public class AgentUtilite extends Agent {
    private enum Etats {IDLE,EN_MOUVEMENT, ARRET_AU_FEU, ESQUIVE_OBSTACLE, CHANGEMENT_VOIE, ARRET_URGENCE} ;
    private Etats etatActuel = Etats.IDLE ;
    private double batterie = 100.0 ;
    private double vitesse = 0.0 ;
    private String positionActuel = "Base" ;
    private String destination = null ;
    private boolean obstacle = false ;
    private boolean feuRouge = false ;
    private boolean traffic = false ;
    private Random rand = new Random() ;
    private double poidsReel = 0.4 ;
    private double poidsRecommande = 0.3 ;
    private double poidsConforme = 0.2 ;
    private double poidsEnernetique = 0.1 ;
    
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
            System.out.println("Mission recue : destination vers "+msg.getContent()) ;
            destination = msg.getContent() ;
            etatActuel = Etats.EN_MOUVEMENT ;
        }
    }
    
    private void majEtat(){
        if(rand.nextDouble() < 0.2){
            obstacle = true ;
        }
        if(rand.nextDouble() < 0.1){
            feuRouge = true ;
        }
        if(rand.nextDouble() < 0.3){
            traffic = true ;
        }
        if(etatActuel == Etats.EN_MOUVEMENT){
            vitesse += 50 + rand.nextInt(20) ;
            batterie -= 10 ;
            if(obstacle){
                etatActuel = Etats.ESQUIVE_OBSTACLE ;
            }
            else if(feuRouge){
                etatActuel = Etats.ARRET_AU_FEU ;
            }
            else if(traffic){
                etatActuel = Etats.CHANGEMENT_VOIE ;
            }
            if(batterie < 10){
                etatActuel = Etats.ARRET_URGENCE ;
            }
        }
        if(etatActuel == Etats.ARRET_AU_FEU || etatActuel == Etats.ESQUIVE_OBSTACLE){
            vitesse = 0.0 ;
        }
        if(etatActuel == Etats.ARRET_URGENCE){
            vitesse = 0.0 ;
            System.out.println("Batteir faible, arret d'urgence.") ;
        }
    }
    
    private void choisirAction(){
        double utiliteMouvement = calculerUtilite(Etats.EN_MOUVEMENT) ;
        double utiliteEsquive = calculerUtilite(Etats.ESQUIVE_OBSTACLE) ;
        double utiliteArret = calculerUtilite(Etats.ARRET_AU_FEU) ;
        double utiliteChangement = calculerUtilite(Etats.CHANGEMENT_VOIE) ;
        double utiliteArretUrgence = calculerUtilite(Etats.ARRET_URGENCE) ;
        
        double[] utilites = {utiliteMouvement, utiliteEsquive, utiliteArret, utiliteChangement, utiliteArretUrgence} ;
        double maxUtilite = utilites[0] ;
        Etats meilleurEtat = Etats.EN_MOUVEMENT ;
        for(int i = 1 ; i < utilites.length ; i++){
            if(utilites[i] > maxUtilite){
                maxUtilite = utilites[i] ;
                meilleurEtat = Etats.values()[i] ;
            }
        }
        etatActuel = meilleurEtat ;
        realiserAction(etatActuel) ;
    }
    
    private double calculerUtilite(Etats etat){
        double utilite = 0 ;
        switch(etat){
            case EN_MOUVEMENT :
                utilite = (poidsReel * (100.0 / vitesse)) - (poidsRecommande * (obstacle ? 1 : 0)) ;
                break ;
            case ESQUIVE_OBSTACLE :
                utilite = -poidsRecommande - poidsConforme ;
                break ;
            case ARRET_AU_FEU :
                utilite = poidsReel * -1 + poidsRecommande * 0 + poidsEnernetique * -0.2 ;
                break ;
            case CHANGEMENT_VOIE :
                utilite = poidsReel * 0.5 - poidsRecommande * 0.5 - poidsEnernetique * 0.5 ;
                break ;
            case ARRET_URGENCE :
                utilite = -poidsRecommande * 10 - poidsEnernetique * 0.5 ;
                break ;
        }
        return utilite ;
    }
    
    private void realiserAction(Etats etat){
        switch(etat){
            case EN_MOUVEMENT :
                System.out.println("Voiture en deplacement a "+vitesse+" km/h.") ;
                positionActuel = destination ;
                break ;
            case ESQUIVE_OBSTACLE :
                System.out.println("Obstacle detecte! Evitement de l'obstacle") ;
                vitesse = 0 ;
                break ;
            case ARRET_AU_FEU :
                System.out.println("Arret au feu rouge") ;
                break ;
            case CHANGEMENT_VOIE :
                System.out.println("Changement de voie pour eviter le traffic") ;
                break ;
            case ARRET_URGENCE :
                System.out.println("Arret d'urgence du a une batteire faible") ;
                break ;
            default :
                break ;
        }
    }
}
