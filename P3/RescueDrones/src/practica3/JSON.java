package practica3;

import com.eclipsesource.json.*;
import java.awt.Point;

public class JSON {

    private static String key;

    private static JsonObject parseToJson(String json) {
	return Json.parse(json).asObject();
    }

    public static String suscribirse(String mundo) {
	JsonObject object = new JsonObject();
	object.add("world", mundo);
	return object.toString();
    }

    public static void guardarKey(String json) {
	JsonObject object = Json.parse(json).asObject();
	key = object.getString("result", null);
    }

    public static String checkin() {
	JsonObject object = new JsonObject();
	object.add("comand", "checkin");
	object.add("key", key);
	return object.toString();
    }

    public static Rol getRol(String json) {
	JsonObject object = parseToJson(json);
	int rol = object.getInt("rol", -1);
	return Rol.getRol(rol);
    }

    public static String mover(String movimiento) {
	JsonObject object = new JsonObject();
	object.add("comand", movimiento);
	object.add("key", key);
	return object.toString();
    }

    public static String repostar() {
	JsonObject object = new JsonObject();
	object.add("comand", "refuel");
	object.add("key", key);
	return object.toString();
    }

    public static String key() {
	JsonObject object = new JsonObject();
	object.add("key", key);
	return object.toString();
    }

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

    private static int[][] parseSensor(JsonArray array) {
	int tam = (int) Math.sqrt(array.size());
	int[][] sensor = new int[tam][tam];
	int i = 0, j = 0;
	for (JsonValue value : array) {
	    sensor[i][j] = value.asInt();
	    j++;
	    if (j == tam) {
		j = 0;
		i++;
	    }
	}
	return sensor;
    }
}
