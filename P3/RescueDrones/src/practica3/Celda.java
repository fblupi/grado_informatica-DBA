package practica3;

/**
 * 
 * @author José Guadix Rosado
 */
public enum Celda {

    LIBRE, OBSTACULO, PARED, OBJETIVO, RECORRIDO0, RECORRIDO1, RECORRIDO2, RECORRIDO3,
    DESCONOCIDA, ULT_POSICION0, ULT_POSICION1, ULT_POSICION2, ULT_POSICION3;

    /**
     * Obtiene el valor de la celda para un dron en concreto
     * @param nombre nombre del dron
     * @return valor de la celda
     */
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

    /**
     * Obtiene el valor de la última posición para un dron en concreto
     * @param nombre nombre del dron
     * @return valor de la última posición
     */
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

    /**
     * Obtiene la celda según la identificación del radar
     * @param id Identificación en el radar puede ser 0, 1, 2 o 3
     * @return El valor de la celda
     */
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
