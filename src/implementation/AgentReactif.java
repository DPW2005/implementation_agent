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
    private boolean detecteurBillet = true ; //Dit si les fonds presents dans le distributeur permet d'effectuer le retrait
    private static int dab = 1000000 ; //Montant disponible a l'interieur du distributeur
    private int nbreEssai = 0 ; //Nombre d'essai de connexion
    
    //Procedure de l'initialisation de l'agent
    @Override
    protected void setup(){
        System.out.println("Agent est pret a etre utilise") ;
        
        //Ajout des comportements de l'agent apres sa creation
        addBehaviour(new CyclicBehaviour(){
            @Override
            public void action(){
                //L'agent est en attente des messages lui etant destines
                ACLMessage message = receive() ;
                if(message != null){
                    //Recuperation du contenu du message et creation de la reponse
                    String contenu = message.getContent() ;
                    ACLMessage reponse = message.createReply() ;
                    //Lecture de la carte bancaire apres insertion dans le lecteur de carte
                    if(contenu.equals("CARTE_INSEREE")){
                        carteInseree = true ;
                        actionDemanderPin(reponse);
                        send(reponse) ;
                        System.out.println(reponse.getContent()) ;
                    }
                    //Lecture du mdp apres insertion de la carte ayant qui doit etre precede du mot cle "MDP:"
                    else if(contenu.startsWith("MDP:") && carteInseree){
                        String mdp = contenu.substring(4) ;
                        //Si le mot de passe est correct
                        if(mdp.equals(pin)){
                            auth = true ;
                            soldeClient = 100000 + 10*rand.nextInt(99001) ;
                            actionDemandeRetrait(reponse);
                            nbreEssai = 0 ;
                        }
                        //Si on a atteint le nombre maximal d'essai
                        else if(nbreEssai == 2){
                            actionBlocageCarte(reponse);
                        }
                        //Si le mot de passe est incorrect et on dispose encore d'essai
                        else{
                            reponse.setContent("Mot de passe incorrect") ;
                            nbreEssai++ ;
                        }
                        //Envoi de la reponse
                        send(reponse) ;
                        System.out.println(reponse.getContent()) ;
                    }
                    //Lecture du montant du retrait apres validation du mdp qui doit etre precede du mot cle "RETRAIT:"
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
                    //Retrait de la carte bancaire apres la fin des operations
                    else if(contenu.startsWith("CARTE") && op){
                        carteInseree = false ; //Reinitialisation du lecteur de carte
                        auth = false ; //Reinitialisation de l'authentification
                        op = false ; //Reinitialisation du status des operations
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
    
    //Fonction qui actionne l'afficheur du distributeur en vue d'obtenir le code PIN du client
    private void actionDemanderPin(ACLMessage message){
        message.setContent("Entrer votre code PIN : ");
    }
    
    //Fonction qui actionne l'afficheur du distributeur en vue d'obtenir le montantt que souhaite retirer client
    private void actionDemandeRetrait( ACLMessage message){
        message.setContent("Votre solde est de : "+soldeClient+"\nVeuillez entrer le montant de votre retrait : ");
    }
    
    //Fonction qui actionne la caisse pour retirer l'argent
    private void actionDistribuerArgent(ACLMessage message,double montant){
        dab -= montant ; //Detection du montantt du montant du distributeur
        message.setContent("Retrait effectue avec succes nouveau solde : "+montant);
    }
    
    //Fonction qui actionne le mecanisme de retour de la carte
    private void actionRetourCarte(ACLMessage message){
        message.setContent("Veuillez retirer votre carte") ;
    }
    
    //Fonction qui actionne le mecanisme de blocage de la carte
    private void actionBlocageCarte(ACLMessage message){
        message.setContent("La carte a ete bloquee");
    }
    
}
