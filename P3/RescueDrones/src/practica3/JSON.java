package practica3;

import com.eclipsesource.json.*;
import java.awt.Point;
/**
 * 
 * @author José Guadix
 */
public class JSON {

    private static String key; 

    /**
     * Convierte una cadena codificada en Json a un objeto de la clase JsonObject
     * 
     * @param json cadena codificada en Json
     * @return objeto JsonObject creado a partir de la cadena
     */
    private static JsonObject parseToJson(String json) {
	return Json.parse(json).asObject();
    }

    /**
     * Genera la cadena Json necesaria para registrarse en un mundo
     * 
     * @param mundo Nombre del mapa al que se desea conectar
     * @return Devuelve la cadena que contiene codificado el Json
     */
    public static String suscribirse(String mundo) {
	JsonObject object = new JsonObject();
	object.add("world", mundo);
	return object.toString();
    }

    /**
     * Recibe y almacena la key dada por el Controlador del servidor
     *
     * @param json Cadena que contiene codificado el Json con el contenido de la key
     */
    public static void guardarKey(String json) {
	JsonObject object = Json.parse(json).asObject();
	key = object.getString("result", null);
    }

    /**
     * Genera la cadena Json necesaria para hacer login en un mundo
     * 
     * @return Devuelve la cadena que contiene codificado el Json
     */
    public static String checkin() {
	JsonObject object = new JsonObject();
	object.add("command", "checkin");
	object.add("key", key);
	return object.toString();
    }

    /**
     * Convierte el rol codificado en Json a un valor.
     * 
     * @param json cadena que tiene codificado el rol de un dron
     * @return Devuelve el rol asignado a un dron
     */
    public static Rol getRol(String json) {
	JsonObject object = parseToJson(json);
	int rol = object.getInt("rol", -1);
	return Rol.getRol(rol);
    }

    /**
     * Genera la cadena Json necesaria para hacer un movimiento
     * 
     * @param movimiento Movimiento que se desea realizar
     * @return Devuelve la cadena que contiene codificado el Json
     */
    public static String mover(String movimiento) {
	JsonObject object = new JsonObject();
	object.add("command", movimiento);
	object.add("key", key);
	return object.toString();
    }

    /**
     * Genera la cadena Json necesaria para repostar
     * 
     * @return Devuelve la cadena que contiene codificado el Json
     */
    public static String repostar() {
	JsonObject object = new JsonObject();
	object.add("command", "refuel");
	object.add("key", key);
	return object.toString();
    }

    /**
     * Genera la cadena Json {"key", "valorKey"}
     * 
     * @return Devuelve la cadena que contiene codificado el Json
     */
    public static String key() {
	JsonObject object = new JsonObject();
	object.add("key", key);
	return object.toString();
    }

    /**
     * Convierte una cadena Json en un objeto Percepción
     * 
     * @param json Cadena que contiene codificada las percepciones
     * @return Devuelve las percepciones codificadas en la cadena Json
     */
    public static Percepcion getPercepcion(String json) {
	JsonObject object = parseToJson(json);
	Percepcion percepcion = new Percepcion();
	JsonObject result = object.get("result").asObject();
	percepcion.setBateria(result.getInt("battery", -1));
	Point gps = new Point();
	gps.x = result.getInt("x", -1);
	gps.y = result.getInt("y", -1);
	percepcion.setGps(gps);
	int[][] radar = parseSensor(result.get("sensor").asArray());
	percepcion.setRadar(radar);
	percepcion.setEnergia(result.getInt("energy", -1));
	percepcion.setLlegado(result.getBoolean("goal", false));
	return percepcion;
    }
    
    /**
     * Cambia de JsonArray a una matriz
     * 
     * @param array JsonArray que contiene el radar
     * @return La matriz de enteros correspondiente al array.
     */
    private static int[][] parseSensor(JsonArray array) {
	final int TAM = (int) Math.sqrt(array.size());
	int[][] sensor = new int[TAM][TAM];
	int i = 0, j = 0;
	for (JsonValue value : array) {
	    sensor[i][j] = value.asInt();
	    j++;
	    if (j == TAM) {
		j = 0;
		i++;
	    }
	}
	return sensor;
    }

    /**
     * Devuelve la key almacenada de la sesión
     * 
     * @return La key de la sesión
     */
    public static String getKey() {
	return key;
    }
}
