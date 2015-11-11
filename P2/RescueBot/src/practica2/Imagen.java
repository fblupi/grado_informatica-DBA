package practica2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 
 * @author Amanda Fernández
 * @author José Guadix
 */
public class Imagen extends javax.swing.JFrame {
    
    private final int LIBRE = 0;
    private final int OBSTACULO = 1;
    private final int OBJETIVO = 2;
    private final int RECORRIDO = 3;
    private final int DESCONOCIDO = 4;
    private final int ULT_POSICION = 5;

    private final int TAM = 500;
    private BufferedImage bufferedImage;
    private javax.swing.JLabel labelMapa;

    public Imagen(int[][] mapa, String mundo) {
	initComponents();
	setLocationRelativeTo(null);
	setTitle("Traza de recorrido - " + mundo);
	setSize(TAM + 50, TAM + 70);
	labelMapa.setSize(TAM, TAM);
	bufferedImage = new BufferedImage(TAM, TAM, BufferedImage.TYPE_INT_RGB);
	pintarMapa(mapa);
	ImageIcon i = new ImageIcon(bufferedImage);
	labelMapa.setIcon(i);
    }

    
    private void initComponents() {

        labelMapa = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(500, 500));
        setMinimumSize(new java.awt.Dimension(500, 500));
        setResizable(false);

        labelMapa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelMapa.setMaximumSize(new java.awt.Dimension(500, 500));
        labelMapa.setMinimumSize(new java.awt.Dimension(500, 500));
        labelMapa.setPreferredSize(new java.awt.Dimension(500, 500));
        labelMapa.setRequestFocusEnabled(false);
        getContentPane().add(labelMapa, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void pintarMapa(int[][] mapa) {
	Color color;

	for (int i = 0; i < TAM; i++) {
	    for (int j = 0; j < TAM; j++) {

		switch (mapa[i][j]) {
		    case LIBRE:
			color = Color.WHITE;
			break;
		    case OBSTACULO:
			color = Color.BLACK;
			break;
		    case OBJETIVO:
			color = Color.RED;
			break;
		    case RECORRIDO:
			color = Color.GREEN;
			break;
		    case DESCONOCIDO:
			color = Color.LIGHT_GRAY;
			break;
                    case ULT_POSICION:
                        color = Color.PINK;
                        break;
		    default:
			color = Color.ORANGE;
			break;
		}
		bufferedImage.setRGB(i, j, color.getRGB());
	    }
	}
    }

    public void mostrar() {
	setVisible(true);
    }

    public void cerrar() {
	dispose();
    }

    public void actualizarMapa(int mapa[][]) {
	pintarMapa(mapa);
	repaint();
    }

    public void guardarPNG(String nombre) {
	try {
	    ImageIO.write(bufferedImage, "png", new File(nombre));
	} catch (IOException ex) {
	    System.out.println(ex.toString());
	}
    }

}
