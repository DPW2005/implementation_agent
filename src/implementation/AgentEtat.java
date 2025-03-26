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
    
    private final int etageMax = 15 ; //Definition du nombre maximal d'etage
    private int etageActuel = 0 ; //Etage auquel se trouve l'ascenseur a un instant donne
    private int[] etagesAppel ; //Liste des etages ou ont ete appele l'ascenseur et par lequel il passera
    private int[] etagesDestination ; //Liste des etages de destination auxquels doit s'arreter l'ascenseur
    private boolean porte = false ; //Indique si les portes sont fermees ou pas
    private boolean monte ; //Indique si l'ascenseur monte ou pas
    
    @Override
    protected void setup(){
        System.out.println("Ascenseur pret a vous deplacer") ;
        
        
    }
    
    private void deplacerAscenseur(int nouvelEtage){
        System.out.println("Deplacement en cours...") ;
        try{
            Thread.sleep(3000);
        }
        catch(Exception e){
            e.printStackTrace() ;
        }
        etageActuel = nouvelEtage ;
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
