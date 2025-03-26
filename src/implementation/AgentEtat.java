/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementation;

import jade.core.Agent ;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author USER
 */
public class AgentEtat extends Agent {
    
    private int etageActuel = 5 ;
    private boolean enDeplacement = false ;
    
    @Override
    protected void setup(){
        System.out.println("Ascenseur pret a vous deplacer") ;
        
        addBehaviour(new CyclicBehaviour(){
            @Override
            public void action(){
                ACLMessage message = receive() ;
                if(message != null){
                    String contenu = message.getContent() ;
                    ACLMessage reponse = message.createReply() ;
                    if(contenu.startsWith("APPEL:")){
                        int etageAppel = Integer.parseInt(contenu.substring(6)) ;
                        if(!enDeplacement){
                            if(etageAppel != etageActuel){
                                System.out.println("Ascenseur appele a l'etage "+etageAppel) ;
                                deplacerAscenseur(etageAppel);
                                reponse.setContent("ASCENSEUR_ARRIVE") ;
                            }
                            ouvrirPorte() ;
                        }
                        else{
                            reponse.setContent("ASCENSEUR_OCCUPEE") ;
                        }
                        send(reponse) ;
                        System.out.println(reponse.getContent()) ;
                    }
                    else if(contenu.startsWith("DESTINATION:")){
                        int etageDestination = Integer.parseInt(contenu.substring(12)) ;
                        fermerPorte() ;
                        System.out.println("Deplacement vers l'etage "+etageDestination) ;
                        deplacerAscenseur(etageDestination);
                        System.out.println("Arrivee a l'etage "+etageDestination+". Ascenseur en attente") ;
                    }
                }
                else{
                    block() ;
                }
            }
        }) ;
    }
    
    private void deplacerAscenseur(int nouvelEtage){
        enDeplacement = true ;
        System.out.println("Deplacement en cours...") ;
        try{
            Thread.sleep(3000);
        }
        catch(Exception e){
            e.printStackTrace() ;
        }
        etageActuel = nouvelEtage ;
        enDeplacement = false ;
    }
    
    private void ouvrirPorte(){
        System.out.println("Ouverture des porte...") ;
        try{
            Thread.sleep(3000);
        }
        catch(Exception e){
            e.printStackTrace() ;
        }
    }
    
    private void fermerPorte(){
        System.out.println("Fermeture des porte...") ;
        try{
            Thread.sleep(3000);
        }
        catch(Exception e){
            e.printStackTrace() ;
        }
    }
}
