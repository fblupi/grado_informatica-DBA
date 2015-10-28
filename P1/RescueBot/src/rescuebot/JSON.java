/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescuebot;
import com.eclipsesource.json.*;
/**
 *
 * @author José Guadix
 */
public class JSON {
    private static final int TAM = 5;
    private static String password;
    
    /**
     * Guarda la contraseña de conexión para el posterior uso
     * @param pass contraseña de conexión
     */
    public static void establecerPassword(String pass){
	password = pass;
    }
    
    /**
     * Convierte el Scanner codificado en Json en una matriz de float
     * @param json Cadena que contiene codificado el Json con la clave "scanner"
     * @return Devuelve la matriz con el contenido del Json codificado
     */
    public static float[][] leerScanner(String json){
	float [][] scanner = new float[TAM][TAM];
	
	JsonObject object = Json.parse(json).asObject();
	JsonArray array = object.get("scanner").asArray();
	int i = 0, j=0;
	for (JsonValue value : array) {
	    scanner[i][j] = value.asFloat();
	    j++;
	    if(j==TAM){
		j=0;
		i++;
	    }
	}
	
	return scanner;
    }
    
    /**
     * Convierte el Radar codificado en Json en una matriz de int
     * @param json Cadena que contiene codificado el Json con la clave "radar"
     * @return Devuelve la matriz con el contenido del Json codificado
     */
    public static int[][] leerRadar(String json){
	int [][] radar = new int[TAM][TAM];
	
	JsonObject object = Json.parse(json).asObject();
	JsonArray array = object.get("radar").asArray();
	int i = 0, j=0;
	for (JsonValue value : array) {
	    radar[i][j] = value.asInt();
	    j++;
	    if(j==TAM){
		j=0;
		i++;
	    }
	}
	
	return radar;
    }
    
    /**
     * Convierte el Radar codificado en Json en un vector de int donde la
     * posición 0 es la coordenada x y la posición 1 la coordenada y
     * @param json Cadena que contiene codificado el Json con la clave "gps"
     * @return Devuelve la matriz con el contenido del Json codificado
     */
    public static int[] leerGPS(String json){
	int[] gps = new int[2];
	
	JsonObject object = Json.parse(json).asObject();
	
	
	return gps;
    }
    
    public static String escribirLogin(String mapa){
	String str ="";
	
	return str;
    }
    
    public static String escribirAction(String action){
	String str ="";
	
	return str;
    }
    
    public static boolean conexionLogin(String json){
	return true;
    }
    
    public static boolean exitoAction(String json){
	return true;
    }
}
