/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package implementation;

import jade.core.Runtime ;
import jade.core.Profile ;
import jade.core.ProfileImpl ;
import jade.wrapper.AgentContainer ;
import jade.wrapper.AgentController ;

public class Implementation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            Runtime rt = Runtime.instance() ;
            Profile profile = new ProfileImpl() ;
            profile.setParameter(Profile.MAIN_HOST, "localhost") ;
            profile.setParameter(Profile.GUI, "true") ;
            AgentContainer container = rt.createMainContainer(profile) ;
            AgentController etat = container.createNewAgent("Machine", AgentUtilite.class.getName(), null) ;
            etat.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }   
}
