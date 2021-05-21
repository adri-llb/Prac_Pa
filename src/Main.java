
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author josea
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException  {
        // TODO code application logic here
        int num_pacientes=2000;
        int cap_sala_vacunacion=10;
        int cap_sala_observacion=20; 
        ExecutorService poolPacientes = new ThreadPoolExecutor(0,num_pacientes,0,TimeUnit.MINUTES,new LinkedBlockingQueue());
        //SalasJFrame sala =new  SalasJFrame();
        //sala.setVisible(true);
        
          
        Hospital h1 = new Hospital(poolPacientes,cap_sala_vacunacion,cap_sala_observacion,num_pacientes);
        h1.start();
        while(true){
            
        }
        
        
        
        
        
    }
    
}
