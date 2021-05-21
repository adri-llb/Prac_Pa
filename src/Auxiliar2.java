
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class Auxiliar2 extends Thread {
    private String id;
    private EstadoSalas estado_salas;
    private int t_min_descanso, t_max_descanso;
    private int vacunas_descanso;
    private int t_min_prep_vacuna, t_max_prep_vacunas;

    public Auxiliar2(String id, EstadoSalas estado_salas) {
        this.id = id;
        this.estado_salas = estado_salas;
        this.t_min_prep_vacuna = 5*100;
        this.t_max_prep_vacunas = 1*1000;
        this.t_min_descanso=1*1000;
        this.t_max_descanso=4*1000;
        this.vacunas_descanso = 20;
    }
    public void run(){        
        int vacunas_preparadas = 0;
        //while true   
        while(true){
        try {
            sleep(t_min_prep_vacuna+(int)(Math.random()*(t_max_prep_vacunas-t_min_prep_vacuna)));
        } catch (InterruptedException e) {
        }
        estado_salas.getInterfaz().getjTextFieldAux2().setText(id);
        estado_salas.preparaVacuna();
        vacunas_preparadas ++;        
        String mensaje = id+" prepara vacuna. Lleva "+vacunas_preparadas+" preparadas.";       
        System.out.println(mensaje);
        try {
            estado_salas.escribirLog(mensaje);            
            if(vacunas_preparadas % vacunas_descanso == 0){
                estado_salas.getInterfaz().getjTextFieldAux2().setText("");
                estado_salas.descansar(this.id,  this.t_min_descanso, this.t_max_descanso);                
            }
        } catch (IOException ex) {
            Logger.getLogger(Auxiliar2.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
    }    
}
