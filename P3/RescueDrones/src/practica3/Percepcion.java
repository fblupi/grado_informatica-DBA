package practica3;

import java.awt.Point;
/**
 * 
 * @author Amanda Fernández Piedra y Francisco Javier Ortega Palacios
 * Clase contenedora de la información sobre el mundo que recibe un dron
 */
public class Percepcion {

    private Point gps;
    private int bateria;
    private int[][] radar;
    private int energia;
    private boolean llegado;
    private String nombreDrone;

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

    public int[][] getRadar() {
	return this.radar;
    }

    public void setRadar(int[][] radar) {
	this.radar = radar;
    }

    public int getEnergia() {
	return this.energia;
    }

    public void setEnergia(int energia) {
	this.energia = energia;
    }

    public boolean getLlegado() {
	return this.llegado;
    }

    public void setLlegado(boolean llegado) {
	this.llegado = llegado;
    }

    public String getNombreDrone() {
	return this.nombreDrone;
    }

    public void setNombreDrone(String nombreDrone) {
	this.nombreDrone = nombreDrone;
    }
}
