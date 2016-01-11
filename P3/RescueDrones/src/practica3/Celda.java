package practica3;

/**
 * 
 * @author José Guadix Rosado
 */
public enum Celda {

    LIBRE, OBSTACULO, PARED, OBJETIVO, RECORRIDO0, RECORRIDO1, RECORRIDO2, RECORRIDO3,
    DESCONOCIDA, ULT_POSICION0, ULT_POSICION1, ULT_POSICION2, ULT_POSICION3;

    public static Celda getRecorrido(String nombre) {
	if (nombre.equals("Drone10")) {
	    return RECORRIDO0;
	} else if (nombre.equals("Drone11")) {
	    return RECORRIDO1;
	} else if (nombre.equals("Drone12")) {
	    return RECORRIDO2;
	} else {
	    return RECORRIDO3;
	}
    }

    public static Celda getUlt_Posicion(String nombre) {
	if (nombre.equals("Drone0")) {
	    return ULT_POSICION0;
	} else if (nombre.equals("Drone1")) {
	    return ULT_POSICION1;
	} else if (nombre.equals("Drone2")) {
	    return ULT_POSICION2;
	} else {
	    return ULT_POSICION3;
	}
    }

    public static Celda getCelda(int id) {
	Celda celda = DESCONOCIDA;
	switch (id) {
	    case 0:
		celda = LIBRE;
		break;
	    case 1:
		celda = OBSTACULO;
		break;
	    case 2:
		celda = PARED;
		break;
	    case 3:
		celda = OBJETIVO;
		break;
	}
	return celda;
    }
}
