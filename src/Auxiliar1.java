
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author josea
 */
public class Auxiliar1 extends Thread{
    private String id;
    private EstadoSalas estado_salas;
     private int t_min_check_cita, t_max_check_cita;
    private int t_min_descanso, t_max_descanso;
    private int registros_descanso;

    public Auxiliar1(String id, EstadoSalas estado_salas) {
        this.id = id;
        this.estado_salas = estado_salas;
        this.t_min_check_cita=500;
        this.t_max_check_cita=1000;
        this.t_min_descanso=3000;
        this.t_max_descanso=5000;
        this.registros_descanso = 10;
    }

    
    
    public void run(){
        int cont = 0;
        //while true apartir de aqui
        while(true){
        //vamos cogiendo pacientes hasta encontrar uno que este citado
        try{
            estado_salas.getInterfaz().getjTextFieldAux1().setText(this.id);
            estado_salas.comprobarPaciente();           
            Thread.sleep(t_min_check_cita+(int)Math.random()*(t_max_check_cita-t_min_check_cita));
            cont++;
            if(cont%registros_descanso==0){
                estado_salas.getInterfaz().getjTextFieldAux1().setText("");
                estado_salas.descansar(this.id,this.t_min_descanso,this.t_max_descanso);                
            }
            
            
        }catch (IOException | InterruptedException ex) {
            Logger.getLogger(Auxiliar2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        }
         
    }
    
}
