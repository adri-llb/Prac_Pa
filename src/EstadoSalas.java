
import static java.awt.SystemColor.control;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author josea
 */
public class EstadoSalas {
    private ArrayList<Paciente> recepcion = new ArrayList<>();
    private ArrayList<String> descanso = new ArrayList<>();
    private Paciente[] vacunacion_pacientes;
    private Sanitario[] vacunacion_sanitarios;
    private Paciente[] observacion_pacientes ;
    private String paciente_rec;
    private String aux_rec;
    private String aux_vac;
    private ArrayList<String> vacunas_disponibles;
    private ArrayList<Paciente> reaccion_pacientes = new ArrayList<>();
    private int num_pacientes;
    private int cap_sala_vacunas;
    private int cap_sala_observacion;
    private int vacunas;
    private File log;//creamos archivo de texto
    private FileWriter fw;
    private BufferedWriter bw; 
    private SalasJFrame interfaz;    
    private JTextField[] jTextField_puestos_vacunacion;
    private JTextField[] jTextField_puestos_observacion;
    //declaracion de locks y condition;
    private Lock c_reacciones = new ReentrantLock();
        private Condition esperar_sanitario_reaccion = c_reacciones.newCondition();
    private Lock c_recepcion = new ReentrantLock();
        private Condition vacio_recepcion = c_recepcion.newCondition();
        private Condition lleno_recepcion = c_recepcion.newCondition();
        private Condition esperar_comprobacion = c_recepcion.newCondition();
    private Lock c_descanso = new ReentrantLock();
        private Condition vacio_descano = c_descanso.newCondition();
        private Condition lleno_descanso = c_descanso.newCondition();
    private Lock c_vac_pacientes = new ReentrantLock();
        private Condition vacio_vac_pacientes = c_vac_pacientes.newCondition();
        private Condition lleno_vac_pacientes = c_vac_pacientes.newCondition();
        private Condition esperar_puesto = c_vac_pacientes.newCondition(); //para que el paciente espere a que se asigne un puesto en la sala de vacunacion
        private Condition esperar_paciente = c_vac_pacientes.newCondition();//para que el sanitario espere a que un paciente llegue a su puesto de vacunacion
        private Condition isVacunado = c_vac_pacientes.newCondition();//para que el paciente espere a que el sanitario le vacune antes de salir de la sala de vacunacion
        
    private Lock c_vac_sanitarios = new ReentrantLock();
        private Condition vacio_vac_sanitarios = c_vac_sanitarios.newCondition();
        private Condition lleno_vac_sanitarios = c_vac_sanitarios.newCondition();
    private Lock c_observacion = new ReentrantLock();
        private Condition vacio_observacion = c_observacion.newCondition();
        private Condition lleno_observacion = c_observacion.newCondition();
        
    private Lock c_prepara_vacuna = new ReentrantLock();
        private Condition vacio_vacunas = c_prepara_vacuna.newCondition();
        
    
    
    
    
    public EstadoSalas(int num_pacientes, int cap_sala_vacunas, int cap_sala_observacion, SalasJFrame interfaz) throws IOException {
        
        this.num_pacientes = num_pacientes;
        this.cap_sala_vacunas = cap_sala_vacunas;
        this.cap_sala_observacion = cap_sala_observacion;
        this.observacion_pacientes = new Paciente[cap_sala_observacion];
        this.vacunacion_pacientes = new Paciente[cap_sala_vacunas];
        this.vacunacion_sanitarios= new Sanitario[cap_sala_vacunas];
        this.log = new File(".\\log.txt");
        if(!log.exists()) log.createNewFile();
        this.fw = new FileWriter(log);
        this.bw = new BufferedWriter(fw);
        this.interfaz=interfaz;
        this.jTextField_puestos_vacunacion = new JTextField[cap_sala_vacunas];
        jTextField_puestos_vacunacion[0] = interfaz.getjTextFieldPV1(); jTextField_puestos_vacunacion[1] = interfaz.getjTextFieldPV2(); jTextField_puestos_vacunacion[2] = interfaz.getjTextFieldPV3(); jTextField_puestos_vacunacion[3] = interfaz.getjTextFieldPV4(); 
        jTextField_puestos_vacunacion[4] = interfaz.getjTextFieldPV5(); jTextField_puestos_vacunacion[5] = interfaz.getjTextFieldPV6(); jTextField_puestos_vacunacion[6] = interfaz.getjTextFieldPV7(); jTextField_puestos_vacunacion[7] = interfaz.getjTextFieldPV8();
        jTextField_puestos_vacunacion[8] = interfaz.getjTextFieldPV9(); jTextField_puestos_vacunacion[9] = interfaz.getjTextFieldPV10();
        this.jTextField_puestos_observacion = new JTextField[cap_sala_observacion];
        jTextField_puestos_observacion[0] = interfaz.getjTextFieldPO1(); jTextField_puestos_observacion[1] = interfaz.getjTextFieldPO2(); jTextField_puestos_observacion[2] = interfaz.getjTextFieldPO3(); jTextField_puestos_observacion[3] = interfaz.getjTextFieldPO4(); 
        jTextField_puestos_observacion[4] = interfaz.getjTextFieldPO5(); jTextField_puestos_observacion[5] = interfaz.getjTextFieldPO6(); jTextField_puestos_observacion[6] = interfaz.getjTextFieldPO7(); jTextField_puestos_observacion[7] = interfaz.getjTextFieldPO8(); 
        jTextField_puestos_observacion[8] = interfaz.getjTextFieldPO9(); jTextField_puestos_observacion[9] = interfaz.getjTextFieldPO10(); jTextField_puestos_observacion[10] = interfaz.getjTextFieldPO11(); jTextField_puestos_observacion[11] = interfaz.getjTextFieldPO12(); 
        jTextField_puestos_observacion[12] = interfaz.getjTextFieldPO13(); jTextField_puestos_observacion[13] = interfaz.getjTextFieldPO14(); jTextField_puestos_observacion[14] = interfaz.getjTextFieldPO15(); jTextField_puestos_observacion[15] = interfaz.getjTextFieldPO16(); 
        jTextField_puestos_observacion[16] = interfaz.getjTextFieldPO17(); jTextField_puestos_observacion[17] = interfaz.getjTextFieldPO18(); jTextField_puestos_observacion[18] = interfaz.getjTextFieldPO19(); jTextField_puestos_observacion[19] = interfaz.getjTextFieldPO20(); 
               
    }

    public ArrayList<Paciente> getRecepcion() {
        return recepcion;
    }
    
    
    public SalasJFrame getInterfaz() {
        return interfaz;
    }

    public int getVacunas() {
        return vacunas;
    }
    
    
    //FUNCIONES PARA LA INTERFAZ
    //actualizar la cola de recepcion
    public void imprimirColaRecepcion(JTextArea cola, ArrayList<Paciente> pacientes){
        String contenido = "";
        for(int i = 0;i<pacientes.size();i++){
            if(i %12 == 0) contenido += '\n';
            String nombre_pac = pacientes.get(i).getNombre();
            contenido += nombre_pac;
            if(i<pacientes.size()-1) contenido += ", ";
        }
        cola.setText(contenido);
    }
    
    public void imprimirSalaDescanso(JTextField cola,  ArrayList<String> descanso){
        String contenido = "";
        for (int i = 0; i < descanso.size(); i++) {
            contenido += descanso.get(i);
            if(i<descanso.size()-1) contenido += ", ";            
        }
        cola.setText(contenido);
    }
    
    //Le pasamos booleano para diferenciar si el que llama es paciente o sanitario TRUE=paciente, FALSE= sanitario
    public void imprimirPuestoVacEntrada(int puesto, String nombre, Boolean p_s){
         String contenido = nombre;         
        if(p_s)contenido = (jTextField_puestos_vacunacion[puesto].getText() + " | " + nombre);                
        
                jTextField_puestos_vacunacion[puesto].setText(contenido);
    }
    
    public void ImprimirPuestoVacSalida(int puesto, String sanitario, Boolean p_s){
        if(p_s) jTextField_puestos_vacunacion[puesto].setText(sanitario);
        else jTextField_puestos_vacunacion[puesto].setText("");
    }

    public void ImprimirPuestoObsEntrada(int puesto, String nombre, Boolean p_s){
        String contenido = nombre;
        if(!p_s) contenido = (jTextField_puestos_observacion[puesto].getText()+" | "+ nombre);
        jTextField_puestos_observacion[puesto].setText(contenido);
    }
    
    public void ImprimirPuestoObsSalida(int puesto){
        jTextField_puestos_observacion[puesto].setText("");
    }
    
    
    
    //-------------------------------------------------------------------------------------------------------------------------
    //METODOS RECEPCION--------------------------------------------------------------------------------------------------------
    public void entrarRecepcion(Paciente paciente) throws IOException{
        c_recepcion.lock();
        while(recepcion.size()== num_pacientes){
            try{
                lleno_recepcion.await();
            }catch(InterruptedException ex){}
        }
        try{
            recepcion.add(paciente);
            imprimirColaRecepcion(interfaz.getjTextAreaColaEspera(),recepcion);
            String mensaje = "El paciente "+paciente.getNombre()+" entra en recepción.";
            System.out.println(mensaje);
            this.escribirLog(mensaje);
            vacio_recepcion.signalAll();
        }finally{c_recepcion.unlock();}
        //interfaz actualizar recepcion
    }
    public void salirRecepcion(Paciente paciente) throws IOException{//cambiar
        c_recepcion.lock();
        while(!paciente.isComprobado()){
            try{
                esperar_comprobacion.await();
            }catch(InterruptedException ex){}
        }
        try{
            recepcion.remove(paciente);
            imprimirColaRecepcion(interfaz.getjTextAreaColaEspera(),recepcion);
            String mensaje = "El paciente "+paciente.getNombre()+" sale de la recepción.";
            System.out.println(mensaje);
            this.escribirLog(mensaje);
            
            lleno_recepcion.signalAll();
        }finally{c_recepcion.unlock();} 
    }
    
    public void comprobarPaciente() throws InterruptedException, IOException{
        
        Boolean cita = false;
        Paciente paciente = null;
        int index=0;
     
        while(!cita){
            c_recepcion.lock();
            while(recepcion.size()<=index) vacio_recepcion.await();
            try{
                paciente = recepcion.get(index);
                interfaz.getjTextFieldPacienteRecepcion().setText(paciente.getNombre());
                paciente.setComprobado(true);
                if(paciente.getProb_cita()>1){
                    cita = true;
                    String mensaje = "El paciente "+paciente.getNombre()+" tiene cita. ";
                    System.out.println(mensaje);
                    this.escribirLog(mensaje);

                }else{
                    String mensaje = "El paciente "+paciente.getNombre()+" NO tiene cita.";
                    System.out.println(mensaje);
                    this.escribirLog(mensaje);
                    paciente.setTerminar(true);
                }
                index++;
                esperar_comprobacion.signalAll();
            }finally{c_recepcion.unlock();}
        }

        //ahora vamos a coger un puesto para que el paciente entre en la sala de vacunacion
        c_vac_pacientes.lock();
         int puesto = getPuestoLibrePaciente();         
        while(puesto == 100) {
            lleno_vac_pacientes.await();
            puesto = getPuestoLibrePaciente();
        }
        try{
           paciente.setPuesto(puesto);
           esperar_puesto.signal();
        }finally{c_vac_pacientes.unlock();}
       
        
   }
    //----------------------------------------------------------------------------------------------------------------------------------
    //METODO PARA ESCRIBIR EN EL LOG (log.txt)
    public void escribirLog(String entrada) throws IOException{
        LocalDateTime tiempo = LocalDateTime.now();
        bw.write(tiempo.getHour() + ":"+ tiempo.getMinute() +":"+tiempo.getSecond() + " -> "+entrada+"\n");
        
    }
    public void cerrarLog() throws IOException{//llamar cando finalicemos la ejecucion
        bw.close();
    }
    //--------------------------------------------------------------------------------------------------------------------------------
    //METODOS DE SALA DE DESCANSO
    public void descansar(String nombre, int t_min, int t_max) throws IOException{
        if(nombre.charAt(0)=='S'){
           sacarSanitarioVac(nombre);
       }//ELSE sacamos auxiliar del puesto()
       descanso.add(nombre);
        imprimirSalaDescanso(interfaz.getjTextFieldDESCANSO(), descanso);
       String mensaje = "El empleado "+nombre+" entra en la sala de descanso.";
       System.out.println(mensaje);
       this.escribirLog(mensaje);
       try{
           Thread.sleep(t_min +(int)(Math.random()*(t_max-t_min)));
       }catch(InterruptedException ie){}
       descanso.remove(nombre);
       imprimirSalaDescanso(interfaz.getjTextFieldDESCANSO(), descanso);
       mensaje = "El empleado "+nombre+" sale de la sala de descanso.";
       System.out.println(mensaje);
       this.escribirLog(mensaje);
       
   }
    
    public void cambiarse(String nombre, int t_min, int t_max) throws IOException{
       descanso.add(nombre);
       String mensaje = "El empleado "+nombre+" entra en la sala de descanso para cambiarse.";
       System.out.println(mensaje);
       this.escribirLog(mensaje);
       try{
           Thread.sleep(t_min +(int)(Math.random()*(t_max-t_min)));
       }catch(InterruptedException ie){}
       descanso.remove(nombre);
       mensaje = "El empleado "+nombre+" sale de la sala de descanso, ha terminado de cambiarse.";
       System.out.println(mensaje);
       this.escribirLog(mensaje);
       
   }
   //---------------------------------------------------------------------------------------------------------------------------------
   //METODOS SALA VACUNACION
   public int getPuestoLibrePaciente(){//si la sala de vacunacion esta llena devuelve  100, de lo contrario devuelve el puesto de vacunacion
       int puesto = 100;
       for (int i = 0; i < vacunacion_pacientes.length; i++) {
                
                if(vacunacion_pacientes[i] == null && vacunacion_sanitarios[i] != null){
                puesto = i;
                break;
                }  
           }
       return puesto;
   }
   public void entrarVacunacionPaciente(Paciente paciente) throws IOException{
       c_vac_pacientes.lock();
        while(paciente.getPuesto() ==100){
            try{              
                esperar_puesto.await();
            }catch(InterruptedException ex){}
        }
        try{
            vacunacion_pacientes[paciente.getPuesto()]=paciente;
            imprimirPuestoVacEntrada(paciente.getPuesto(), paciente.getNombre(), true); //true porque es paciente
            String mensaje = "El paciente "+paciente.getNombre()+" entra en puesto de vacunacion "+(paciente.getPuesto()+1);
            System.out.println(mensaje);
            this.escribirLog(mensaje);
            esperar_paciente.signalAll();//despertamos a todos los sanitarios que esperan a un paciente para que vuelvan a comprobar la condicion(HECHO)
        }finally{c_vac_pacientes.unlock();}
   }
   

   public void preparaVacuna(){
       c_prepara_vacuna.lock();
       try {
                  vacunas ++;
                  interfaz.getjTextFieldVacunasDisp().setText(String.valueOf(vacunas));
                  vacio_vacunas.signal(); //AWAIT cuando un sanitario vaya a vacunar y no haya vacunas ==0(HECHO)
       } finally {c_prepara_vacuna.unlock();}
       }
      
   
   
     public int getPuestoLibreSanitario(){//si la sala de vacunacion esta llena devuelve  100, de lo contrario devuelve el puesto de vacunacion
       int puesto = 100;
       for (int i = 0; i < vacunacion_sanitarios.length; i++) {
                if(vacunacion_sanitarios[i] == null){
                puesto = i;
                break;
                }  
           }
       return puesto;
   }
   
   public int entrarVacunacionSanitario(Sanitario sanitario) throws IOException{
       c_vac_sanitarios.lock();
        int puesto = getPuestoLibreSanitario();
        while(puesto ==100){
            try{
                lleno_vac_sanitarios.await();
                puesto = getPuestoLibreSanitario();
            }catch(InterruptedException ex){}
            
        }
        try{
            vacunacion_sanitarios[puesto]=sanitario;
            imprimirPuestoVacEntrada(puesto, sanitario.getNombre(), false); //false porque es sanitario
            String mensaje = "El sanitario "+sanitario.getNombre()+" entra en puesto de vacunacion "+(puesto+1);
            System.out.println(mensaje);
            this.escribirLog(mensaje);
            return puesto;
        }finally{c_vac_sanitarios.unlock();}
   }     
   
   public void sacarSanitarioVac(String nombre){
       //HAY QUE HACERLO CON LOCKS
       for (int i = 0; i < vacunacion_sanitarios.length; i++) {
            
           if(vacunacion_sanitarios[i]!=null && vacunacion_sanitarios[i].getNombre().equals(nombre)){
               c_vac_sanitarios.lock();
               try{
                   vacunacion_sanitarios[i] = null;  
                   ImprimirPuestoVacSalida(i, nombre, false); //false porque es un sanitario
                   lleno_vac_sanitarios.signal();
               }finally{c_vac_sanitarios.unlock();}
               
       }
   }
    
   }
   
      public void sacarPacienteVac(Paciente paciente) throws IOException, InterruptedException{
       c_vac_pacientes.lock();
       while(!paciente.isVacunado()){ 
           isVacunado.await(); 
       }
        try{    
            String nombre = "";
            if( vacunacion_sanitarios[paciente.getPuesto()] != null)  nombre = vacunacion_sanitarios[paciente.getPuesto()].getNombre();
            ImprimirPuestoVacSalida(paciente.getPuesto(), nombre, true); //true porque es paciente
            String mensaje = "El paciente "+paciente.getNombre()+" sale del puesto de vacunacion "+(paciente.getPuesto()+1);
            System.out.println(mensaje);
            this.escribirLog(mensaje);
            vacunacion_pacientes[paciente.getPuesto()]=null;            
            lleno_vac_pacientes.signalAll();
        }finally{c_vac_pacientes.unlock();}
   }
      
   public void vacunar(int puesto) throws InterruptedException, IOException{
       //esperamos a que haya pacientes en el puesto
       c_vac_pacientes.lock();
       try{
           while(vacunacion_pacientes[puesto] == null) esperar_paciente.await();
           Sanitario sanitario =  vacunacion_sanitarios[puesto];
            Paciente paciente = vacunacion_pacientes[puesto]; 
            Thread.sleep(sanitario.getT_min_vacunacion()+(int)(Math.random()*(sanitario.getT_max_vacunacion()-sanitario.getT_min_vacunacion())));            
            String mensaje = "Paciente "+paciente.getNombre()+" vacunado en el puesto "+(puesto+1)+" por el sanitario "+sanitario.getNombre()+"(lleva "+(sanitario.getVacunados()+1)+" pacientes vacunados)";
            System.out.println(mensaje);
            this.escribirLog(mensaje);
             //establecemos la varibale vacunado del paciente como true para que pueda acceder a la sala de observacion
            paciente.setVacunado(true);
             isVacunado.signal();
       }finally{c_vac_pacientes.unlock();}
       
       //reducimos el numero de vacunas en 1
       c_prepara_vacuna.lock();
       while(vacunas == 0) vacio_vacunas.await();
       try {
                  vacunas --;
                  interfaz.getjTextFieldVacunasDisp().setText(String.valueOf(vacunas));
       } finally {c_prepara_vacuna.unlock();}

   } 
   //--------------------------------------------------------------------------------------------------------------------------------------------------------------
   //METODOS PARA LA SALA DE OBSERVACION
   public void siReaccion(Paciente paciente) throws InterruptedException, IOException{
       if(paciente.getProb_reaccion()<50){
           c_reacciones.lock();
           try {
               reaccion_pacientes.add(paciente);
               String mensaje = "Al paciente "+paciente.getNombre()+" le ha dado reacción en el puesto de observacion "+(paciente.getPuesto_observacion()+1)+" (esperando a un médico que le atienda)";
               System.out.println(mensaje);
               this.escribirLog(mensaje);
               esperar_sanitario_reaccion.await();//se bloquea a la espera de que un sanitario le atienda(SIGNAL cuando el médico le trate)
               
           }finally{c_reacciones.unlock();}           
       }
   }
   public void tratarReaccion(Sanitario sanitario) throws IOException, InterruptedException{
       /*miramos si hay pacientes en reacion_pacientes; si hay sacamos al primero ,hacemos sleep()  y hacemos signal de esperar_sanitario_reaccion .Si no, nada*/
       if(!reaccion_pacientes.isEmpty()){
           c_reacciones.lock();
           try {                   
                   Paciente paciente = reaccion_pacientes.get(0);                   
                   ImprimirPuestoObsEntrada(paciente.getPuesto_observacion(), sanitario.getNombre(), false);
                   String mensaje = "El sanitario "+sanitario.getNombre()+" trata al paciente "+paciente.getNombre()+" al que le había dado reacción en el puesto "+(paciente.getPuesto_observacion()+1);
                   System.out.println(mensaje);
                   this.escribirLog(mensaje);
                   Thread.sleep(sanitario.getT_min_problema()+(int)(Math.random()*(sanitario.getT_max_problema()-sanitario.getT_min_problema())));
                   ImprimirPuestoObsSalida(paciente.getPuesto_observacion());
                   reaccion_pacientes.remove(paciente);
                   esperar_sanitario_reaccion.signal();
               
           }finally{c_reacciones.unlock();}
           
       }
   }
   
   public void sacarPacienteObs(Paciente paciente) throws IOException{
       /*ponemos observacion_pacientes[paciente.getPuesto()] a null, imprimos cosas y hacemos signal de lleno_observacion*/
       c_observacion.lock();
       try{
           ImprimirPuestoObsSalida(paciente.getPuesto_observacion());                    
           String mensaje ="El paciente "+paciente.getNombre()+" sale de la sala de observación (puesto "+(paciente.getPuesto_observacion()+1)+") y se va del hospital." ;
           System.out.println(mensaje);
           this.escribirLog(mensaje);
           observacion_pacientes[paciente.getPuesto_observacion()]=null;  
           lleno_observacion.signal();
           
       }finally{c_observacion.unlock();}
   }
   
   public void meterPacienteObs(Paciente paciente) throws IOException, InterruptedException{
        c_observacion.lock();
        
        int puesto = getPuestoLibreObs();
        
        while(puesto == 100){
            //System.out.println("\n"+paciente.getNombre()+" BLOQUEADO por puesto\n");
            lleno_observacion.await();//SIGNAL cuando un paciente salga de la sala de observacion
            puesto = getPuestoLibreObs();
        }
        try{
            observacion_pacientes[puesto]=paciente;
            ImprimirPuestoObsEntrada(puesto, paciente.getNombre(), true);
            paciente.setPuesto_observacion(puesto);//establecemos el puesto de observacion en el paciente
            String mensaje = "El paciente "+paciente.getNombre()+" entra en la sala de observacion (puesto "+(puesto+1)+")";
            System.out.println(mensaje);
            this.escribirLog(mensaje);
            Thread.sleep(paciente.getT_espera_observacion());
            
        }finally{c_observacion.unlock();}
       
   }
   public int getPuestoLibreObs(){//si la sala de observacion esta llena devuelve  100, de lo contrario devuelve el puesto de observacion
       int puesto = 100;
       for (int i = 0; i < observacion_pacientes.length; i++) {
                if(observacion_pacientes[i]== null){
                puesto = i;
                }  
           }
       return puesto;
   }
   //----------------------------------------------------------------------------------------------------------------------------------

   
   //----------------------------------------------------------------------------------------------------------------------------------
   //CUANDO SE TERMINE CERRAR EL TXT PA QUE SE GUARDE
} 
   
    
    
    


