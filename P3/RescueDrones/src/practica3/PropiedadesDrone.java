package practica3;

import java.awt.Point;

/**
 * @author Amanda Fernández Piedra, Francisco Javier Ortega Palacios
 * Contenedor de la información que tiene el controlador sobre un dron
 */
public class PropiedadesDrone {

    private Point gps;
    private int bateria;
    private boolean llegado;
    private Rol rol;
    
    
    public PropiedadesDrone() {
	super();
    }

    public Point getGps() {
	return this.gps;
    }

    public void setGps(Point gps) {
	this.gps = gps;
    }

    public int getBateria() {
	return this.bateria;
    }

    public void setBateria(int bateria) {
	this.bateria = bateria;
    }

    public boolean getLlegado() {
	return this.llegado;
    }

    public void setLlegado(boolean llegado) {
	this.llegado = llegado;
    }

    public Rol getRol() {
	return this.rol;
    }

    public void setRol(Rol rol) {
	this.rol = rol;
    }

    public void setRol(int id) {
	this.rol = Rol.getRol(id);
    }

    public void actualizarPercepcion(Percepcion percepcion) {
	gps = percepcion.getGps();
        bateria = percepcion.getBateria();
        llegado = percepcion.getLlegado();
    }
}
