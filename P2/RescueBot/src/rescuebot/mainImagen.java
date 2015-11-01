/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rescuebot;

import java.util.Random;

/**
 *
 * @author José Guadix
 */
public class mainImagen {

    private final static int TAM = 500;

    public static void main(String[] args) throws InterruptedException {
	int[][] mapa = new int[TAM][TAM];
	System.out.println("Creando mapa");
	for (int i = 0; i < TAM; i++) {
	    for (int j = 0; j < TAM; j++) {
		mapa[i][j] = 4;
	    }
	}

//	for (int i = 0; i < TAM; i++) {
//	    for (int j = 0; j < TAM; j++) {
//		System.out.print(mapa[i][j] + "\t");
//	    }
//	    System.out.println();
//	}
	System.out.println("Creando Imagen");
	Imagen imagen = new Imagen(mapa);
	imagen.mostrar();
	System.out.println("Simulando un camino");
	for (int i = 4; i < 100; i++) {
	    mapa[i][i] = 3;
	    imagen.actualizarMapa(mapa);
	    Thread.sleep(100);
	}
	System.out.println("Poniendo puntos aleatorios");
	Random random = new Random();
	int x, y;
	for (int i = 0; i < 1000; i++) {
	    x = random.nextInt(TAM);
	    y = random.nextInt(TAM);
	    mapa[x][y] = random.nextInt(4);
	    imagen.actualizarMapa(mapa);
	}
	System.out.println("Guardando imagen");
	imagen.guardarPNG("probando.png");
	System.out.println("La ventana se va a cerrar en 3 segundos");
	Thread.sleep(3000);
	imagen.cerrar();
    }

}
