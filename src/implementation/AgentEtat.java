/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementation;

import jade.core.Agent ;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import java.util.Collections;
import java.util.PriorityQueue ;
import java.util.Queue ;

/**
 *
 * @author USER
 */
public class AgentEtat extends Agent {
    
    private int etageActuel = 0 ; //Initialisation de l'etat initial ou se trouve l'ascenseur
    private boolean monte = true ; //Indique si l'ascenseur monte ou descends
    private boolean porte = false ; //Indique si la porte est ouverte ou pas
    private Queue<Integer> upRequests = new PriorityQueue<>() ; //Liste des etages ou sont sollicites l'ascenseur quand il monte
    private Queue<Integer> downRequests = new PriorityQueue<>(Collections.reverseOrder()) ; //Liste des etages ou sont sollicites l'ascenseur quand il descend
    
    @Override
    protected void setup(){
        System.out.println("Agent est pret a etre utilise") ;
        
        //Comportement d'ecoute des appels aux differents etages
        addBehaviour(new CyclicBehaviour(){
            @Override
            public void action(){
                //L'agent est en attente des messages lui etant destines
                ACLMessage message = receive() ;
                if(message != null){
                    try{
                        //Recuperation du contenu du message et creation de la reponse
                        int etage = Integer.parseInt(message.getContent()) ;
                        ajouterDemande(etage) ;
                        System.out.println("Nouvelle requete pour l'etage"+etage) ;
                    }
                    catch(Exception e){
                        e.printStackTrace() ;
                    }
                }
                else{
                    block() ;
                }
            }
        });
        
        //Comportement de deplacement de l'ascenseur
        addBehaviour(new TickerBehaviour(this,2000){
            @Override
            public void onTick(){
                if(monte && !upRequests.isEmpty()){
                    int etageCible = upRequests.poll() ;
                    seDeplacer(etageCible) ;
                }
                else if(!monte && !downRequests.isEmpty()){
                    int etageCible = downRequests.poll() ;
                    seDeplacer(etageCible) ;
                }
                else{
                    if(monte && !downRequests.isEmpty()){
                        monte = false ;
                    }
                    else if(!monte && !upRequests.isEmpty()){
                        monte = true ;
                    }
                }
            }
        });
    }
    
    //Fonction qui ajoute un etage de destination dans la liste
    public void ajouterDemande(int etageAppel){
        if(etageAppel > etageActuel){
            if(!upRequests.contains(etageAppel)){
                upRequests.add(etageAppel) ;
            }
        }
        else if(etageAppel < etageActuel){
            if(!downRequests.contains(etageAppel)){
                downRequests.add(etageAppel) ;
            }
        }
        else{
            System.out.println("Ascenseur a cet etage") ;
        }
    }
    
    public void seDeplacer(int etage){
        while(etageActuel != etage){
            if(etageActuel < etage){
                etageActuel++ ;
            }
            else{
                etageActuel-- ;
            }
            System.out.println("Ascenseur se deplace : etage "+etageActuel);
            try{
                Thread.sleep(3000) ;
            }
            catch(Exception e){
                e.printStackTrace() ;
            }
        }
        System.out.println("Arret a l'etage : "+etageActuel) ;
        addBehaviour(new OneShotBehaviour(){
            @Override
            public void action(){
                System.out.println("Ouverture des portes a l'etage : "+etageActuel) ;
                porte = true ;
                try{
                    Thread.sleep(3000) ;
                }
                catch(Exception e){
                    e.printStackTrace() ;
                }
                System.out.println("Fermeture des portes.....") ;
                porte = false ;
            }
        });
    }
}
