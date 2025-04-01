/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package implementation;

import jade.core.Agent ;
import jade.core.behaviours.CyclicBehaviour ;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage ;
import java.util.Random ;

public class AgentUtilite extends Agent {
    private double prixAction ; // Prix des actions
    private double prixObligation ; // Prix des obligations
    private double volatiliteMarche ; // Les fluctuations du marches
    private double toleranceRisque ; // Niveau de risque maximal accepte par l'investisseur
    private String etat ; // L'etat actuel du portefeuille
    
    @Override
    protected void setup(){
        System.out.println("Agent est pret a etre utilise") ;
        prixAction = 100.0 ;
        prixObligation = 50.0 ;
        volatiliteMarche = 1.5 ;
        toleranceRisque = 2.0 ;
        etat = "Evaluation" ;
        
        addBehaviour(new CyclicBehaviour(){
            @Override
            public void action(){
                ACLMessage message = receive() ;
                if(message != null){
                    String contenu = message.getContent() ;
                    System.out.println("Mise a jour du marche recue : "+contenu) ;
                    //Simulation de la mise a jour de la volatilite du marche
                    Random rand = new Random() ;
                    prixAction *= (0.95 + rand.nextDouble() * 0.1) ;
                    prixObligation *= (0.98 + rand.nextDouble() * 0.05) ;
                    volatiliteMarche = rand.nextDouble() * 3.0 ;
                    evaluerPorteFeuille() ;
                }
                else{
                    block() ;
                }
            }
        });
        
        addBehaviour(new TickerBehaviour(this,10000){
            @Override
            public void onTick(){
                evaluerPorteFeuille() ;
            }
        });
    }
    
    //Methode pour evaluer le portefeuille et ajuster en fonction des conditions
    private void evaluerPorteFeuille(){
        double utilite = calculerUtilite() ;
        if(volatiliteMarche > toleranceRisque){
            etat = "Trop risque" ;
            envoyerRecommandation("Votre porte feuille est trop risque. Envisage de diversifier vos placements") ;
            reequilibrerPortefeuille() ;
        }
        else if(utilite < 0.5){
            etat = "Peu rentable" ;
            envoyerRecommandation("Votre portefeuille est peu rentable. Trouvez des placements plus performants") ;
            trouverMeilleursPlacements() ;
        }
        else{
            etat = "Portefeuille equilibre" ;
            envoyerRecommandation("Votre portefeuille est stable. Aucun changement majeur n'est necessaire") ;
        }
        System.out.println("Etat actuel : "+etat+" | Utilite : "+utilite) ;
    }
    
    //Methode pour calculer l'utilite du portefeuille en fonction du rendement attendu et du risque
    private double calculerUtilite(){
        double rendementAttendu = prixAction * 0.07 + prixObligation * 0.03 ;
        double facteurRisque = volatiliteMarche / toleranceRisque ;
        return Math.max(0, Math.min(1, rendementAttendu / (1 + facteurRisque))) ;
    }
    
    //Methode pour envoyer une recommandation d'ajustement au client
    private void envoyerRecommandation(String conseil){
        ACLMessage message = new ACLMessage(ACLMessage.INFORM) ;
        message.addReceiver(getDefaultDF()) ;
        message.setContent(conseil) ;
        send(message) ;
        System.out.println("Recommandation envoyee : "+conseil) ;
    }
    
    //Methode pour reequilibrer le portefeuille
    private void reequilibrerPortefeuille(){
        envoyerRecommandation("Reequilibrage en cours : augmentation des obligations et reduction des actions") ;
    }
    
    //Methode pour trouver des placements plus performants
    private void trouverMeilleursPlacements(){
        envoyerRecommandation("Analyse des tendances pour identifier ds actifs plus performants") ;
    }
}
