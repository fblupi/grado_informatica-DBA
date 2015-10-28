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
	JsonObject gpsObject = object.get("gps").asObject();
	
	gps[0] = gpsObject.getInt("x", -1);
	gps[1] = gpsObject.getInt("y", -1);
	
	return gps;
    }
    
    /**
     * Crea la cadena codificada para iniciar conexión, registrandose en un mapa 
     * y con la petición de los sensores que se quieren activar.
     * @param mapa Nombre del mapa al que se desea conectar.
     * @return Devuelve la cadena que contiene codificado el Json
     */
    public static String escribirLogin(String mapa){
	JsonObject object = new JsonObject();
	
	object.add("command", "login");
	object.add("world", mapa);
	object.add("radar", "bot");
	object.add("scanner", "bot");
	object.add("gps", "bot");
	
	return object.toString();
    }
    
    /**
     * Crea la cadena codificada para realizar una acción
     * @param action Acción que se desea realizar
     * @return Devuelve la cadena que contiene codificado el Json
     */
    public static String escribirAction(String action){
	JsonObject object = new JsonObject();
	
	object.add("command", action);
	object.add("key", password);
	
	return object.toString();
    }
    
    /**
     * Comprueba si ha tenido exito la conexión inicial a partir de una cadena codificada en Json
     * @param json Cadena en Json
     * @return true si tiene exito, false en caso contrario.
     */
    public static boolean conexionLogin(String json){
	boolean result;
	if(json.contains("BAD_")){
	    result = false;
	}else{
	    JsonObject object = Json.parse(json).asObject();
	    password = object.getString("result", null);
	    if(password == null){
		result = false;
	    }else{
		result = true;
	    }
	}
	return result;
    }
    
    /**
     * Comprueba si ha tenido exito una acción a partir de una cadena codificada en Json
     * @param json Cadena en Json
     * @return true si tiene exito, false en caso contrario.
     */
    public static boolean exitoAction(String json){
	boolean result = json.contains("OK");
	
	return result;
    }
}
