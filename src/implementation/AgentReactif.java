//Pour l'implementation de notre agent reactif, nous allons prendre le cas d'un distributeur automatique de banque
package implementation;

import jade.core.Agent ;
import jade.core.behaviours.CyclicBehaviour ;
import jade.lang.acl.ACLMessage ;
import java.util.Random;

public class AgentReactif extends Agent {
    Random rand = new Random() ;
    private boolean carteInseree = false ; //Reponse fourni par le lecteur de carte bancaire
    private final String pin = "1234" ; //Mot de passe stocker a la banque lie a la bancaire lu
    private int soldeClient = 50000 ; //Solde stocker a la banque lie a la carte lu
    private boolean op = false ; //Nous informe si l'operation s'est bien deroule ou pas
    private boolean auth = false ; //Dit si le client est authentifie pour acceder aux options du distributeur
    private boolean detecteurBillet = true ; //Dit si le client a le droit de faire
    private static int dab = 1000000 ; //Montant disponible a l'interieur du distributeur
    private int nbreEssai = 0 ; //Nombre d'essai de connexion
    
    @Override
    protected void setup(){
        System.out.println("Agent est pret a etre utilise") ;
        
        addBehaviour(new CyclicBehaviour(){
            @Override
            public void action(){
                ACLMessage message = receive() ;
                if(message != null){
                    String contenu = message.getContent() ;
                    ACLMessage reponse = message.createReply() ;
                    if(contenu.equals("CARTE_INSEREE")){
                        carteInseree = true ;
                        actionDemanderPin(reponse);
                        send(reponse) ;
                        System.out.println(reponse.getContent()) ;
                    }
                    else if(contenu.startsWith("MDP:") && carteInseree){
                        String mdp = contenu.substring(4) ;
                        if(mdp.equals(pin)){
                            auth = true ;
                            soldeClient = 100000 + 10*rand.nextInt(99001) ;
                            actionDemandeRetrait(reponse);
                            nbreEssai = 0 ;
                        }
                        else if(nbreEssai == 2){
                            actionBlocageCarte(reponse);
                        }
                        else{
                            reponse.setContent("Mot de passe incorrect") ;
                            nbreEssai++ ;
                        }
                        send(reponse) ;
                        System.out.println(reponse.getContent()) ;
                    }
                    else if(contenu.startsWith("RETRAIT:") && auth){
                        double montant = Double.parseDouble(contenu.substring(8) ) ;
                        detecteurBillet = montant < dab ;
                        if(montant <= soldeClient){
                            if(detecteurBillet){
                                soldeClient -= montant ;
                                actionDistribuerArgent(reponse,soldeClient);
                            }
                            else{
                                reponse.setContent("Montant disponible insuffisant. Le montant actuel est "+dab) ;
                            }
                        }
                        else{
                            reponse.setContent("Solde insuffisant. Le solde actuel est "+soldeClient) ;
                        }
                        op = true ;
                        send(reponse) ;
                        System.out.println(reponse.getContent()) ;
                    }
                    else if(contenu.startsWith("CARTE") && op){
                        carteInseree = false ;
                        auth = false ;
                        op = false ;
                        actionRetourCarte(reponse);
                        send(reponse) ;
                        System.out.println(reponse.getContent()) ;
                    }
                }
                else{
                    block() ;
                }
            }
        });
    }
    
    private void actionDemanderPin(ACLMessage message){
        message.setContent("Entrer votre code PIN : ");
    }
    
    private void actionDemandeRetrait( ACLMessage message){
        message.setContent("Votre solde est de : "+soldeClient+"\nVeuillez entrer le montant de votre retrait : ");
    }
    
    private void actionDistribuerArgent(ACLMessage message,double montant){
        dab -= montant ;
        message.setContent("Retrait effectue avec succes nouveau solde : "+montant);
    }
    
    private void actionRetourCarte(ACLMessage message){
        message.setContent("Veuillez retirer votre carte") ;
    }
    
    private void actionBlocageCarte(ACLMessage message){
        message.setContent("La carte a ete bloquee");
    }
    
}
