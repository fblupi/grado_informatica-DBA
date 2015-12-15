package practica3;

import java.awt.Point;

public class PropiedadesDrone {

    private Point gps;
    private int bateria;
    private boolean llegado;
    private Rol rol;
    public Rol _rol;

    public PropiedadesDrone() {
	throw new UnsupportedOperationException();
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
	throw new UnsupportedOperationException();
    }

    public void actualizarPercepcion(Percepcion percepcion) {
	throw new UnsupportedOperationException();
    }
}
