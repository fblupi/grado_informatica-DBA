/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescuebot;

/**
 *
 * @author LENOVO
 */
public class Pruebas {
  //Metodo inteligencia
    /*Guarda la pos actual
    
    
    
    */
    private final int LIMITE=20;
    private int[][] mapa = new int[LIMITE][LIMITE];
    private int[] ultimoGPS = new int[2];
    private int [][] ultimoRadar = new int[5][5];
    
    public void actualizarMapa() {
        ultimoGPS[0]=0;    
        ultimoGPS[1]=0;
        ultimoRadar[0][0] = 1;
        ultimoRadar[0][1] = 1;
        ultimoRadar[0][2] = 1;
        ultimoRadar[0][3] = 0;
        ultimoRadar[0][4] = 0;
        ultimoRadar[1][0] = 1;
        ultimoRadar[1][1] = 1;
        ultimoRadar[1][2] = 0;
        ultimoRadar[1][3] = 0;
        ultimoRadar[1][4] = 0;
        ultimoRadar[2][0] = 1;
        ultimoRadar[2][1] = 0;
        ultimoRadar[2][2] = 0;
        ultimoRadar[2][3] = 0;
        ultimoRadar[2][4] = 0;
        ultimoRadar[3][0] = 0;
        ultimoRadar[3][1] = 0;
        ultimoRadar[3][2] = 0;
        ultimoRadar[3][3] = 0;
        ultimoRadar[3][4] = 0;
        ultimoRadar[4][0] = 0;
        ultimoRadar[4][1] = 0;
        ultimoRadar[4][2] = 0;
        ultimoRadar[4][3] = 0;
        ultimoRadar[4][4] = 0;




        // Inicializar mapa (debería estar en el constructor)
        for(int i=0;i<LIMITE;i++){
            for(int j=0;j<LIMITE;j++) {
                mapa[i][j] = 0;
            }
        }

        mapa[ultimoGPS[0]][ultimoGPS[1]] = 1; // Guarda posición actual como posición por donde ha pasado
        for (int x = 0, i = -2; x < 5; x++, i++){ // x: recorre el radar, i: recorre mapa desde la posición actual
            for (int y = 0, j = -2; y < 5; y++, j++){ // y: recorre el radar, j: recorre mapa desde la posición actual
                if ((ultimoGPS[0] + i >= 0 && ultimoGPS[0] + i < LIMITE) && (ultimoGPS[1] + j >= 0 && ultimoGPS[1] + j < LIMITE)) { // No se sale del límite
                   if (mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] == 0) // No machaca pasos anteriores
                        mapa[ultimoGPS[0] + i][ultimoGPS[1] + j] = ultimoRadar[x][y];
                }
            }
        }
        
        for(int i=0;i<LIMITE;i++){
            for(int j=0;j<LIMITE;j++) {
               System.out.print(mapa[i][j]+" ");
            }
            System.out.println();
        }
    }
    public String mover(){
        String decision=new String();
        float dMax=Float.MAX_VALUE;

        for(int i=1;i<4;i++){
            for(int j=1;j<4;j++){
                if(ultimoScanner[i][j]<dMax && mapa[ultimoGPS+i-2][ultimoGPS+j-2]!=1){
                    dMax=ultimoScanner[i][j];
                    if(i==1){
                        if(j==1)
                          decision="moveNW";
                        if(j==2)
                          decision="moveN";  
                        if(j==3)
                          decision="moveNE";  
                    }
                    else if(i==2){
                        if(j==1)
                          decision="moveW";  
                        if(j==3)
                          decision="moveE";  
                    }
                    else if(i==3){
                        if(j==1)
                          decision="moveSW";
                        if(j==2)
                          decision="moveS";  
                        if(j==3)
                          decision="moveSE";  
                    }
                }
            }
        }
        
        
        return decision;
    }
}
