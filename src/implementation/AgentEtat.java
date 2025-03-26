/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementation;

import jade.core.Agent ;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
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
    private Queue<Integer> requests = new PriorityQueue<>() ; //Liste des etages ou sont sollicites l'ascenseur
    
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
                if(!requests.isEmpty()){
                    int etageCible = requests.peek() ;
                    if(etageActuel < etageCible){
                        etageActuel++ ;
                        monte = true ;
                        System.out.println("L'ascenseur monte a l'etage : "+etageActuel) ;
                    }
                    else if(etageActuel > etageCible){
                        etageActuel-- ;
                        monte = false ;
                        System.out.println("L'ascenseur descend a l'etage : "+etageActuel) ;
                    }
                    else{
                        System.out.println("L'ascenseur s'arrete a l'etage : "+etageActuel) ;
                        requests.poll() ;
                        //Comportement d'ouverture et de fermeture des portes de l'ascenseur
                        addBehaviour(new OneShotBehaviour(){
                            @Override
                            public void action(){
                                System.out.println("Ouverture des portes a l'etage"+etageActuel) ;
                                porte = true ;
                                //Simulation d'une attente de 3 secondes avant la fermeture des portes
                                try{
                                    System.out.println("Attente de personnel....") ;
                                    Thread.sleep(3000) ;
                                }
                                catch(Exception e){
                                    e.printStackTrace() ;
                                }
                                System.out.println("Fermeture des portes....") ;
                                porte = false ;
                            }
            
                        });
                    }
                }
            }
        });
    }
    
    //Fonction qui ajoute un etage de destination dans la liste
    public void ajouterDemande(int etageAppel){
        if(!requests.contains(etageAppel)){
            requests.add(etageAppel) ;
        }
    }
    
}
