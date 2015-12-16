package practica3;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Antonio Espinosa
 */
public class Imagen extends JFrame {

    private int TAM = 500;
    private BufferedImage bufferedImage;
    private JLabel labelMapa;

    private final int LIBRE = 0;
    private final int OBSTACULO = 1;
    private final int PARED = 2;
    private final int OBJETIVO = 3;
    private final int RECORRIDO1 = 4;
    private final int RECORRIDO2 = 5;
    private final int RECORRIDO3 = 6;
    private final int RECORRIDO4 = 7;
    private final int DESCONOCIDA = 8;
    private final int ULT_POSICION1 = 9;
    private final int ULT_POSICION2 = 10;
    private final int ULT_POSICION3 = 11;
    private final int ULT_POSICION4 = 12;

    /**
     * Constructor donde se inicializa todos los valores de nuestro mapa
     *
     * @param mapa
     * @param mundo
     */
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

    /**
     * Inicializamos los componentes de nuestra ventana
     */
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

    /**
     * Pintamos el mapa pasado como parametro
     *
     * @param mapa mapa a pintar
     */
    private void pintarMapa(int[][] mapa) {
        Color color;

        for (int i = 0; i < TAM; i++) {
            for (int j = 0; j < TAM; j++) {

                switch (mapa[i][j]) {
                    case LIBRE:
                        color = Color.WHITE;
                        break;
                    case OBSTACULO:
                    case PARED:
                        color = Color.BLACK;
                        break;
                    case OBJETIVO:
                        color = Color.RED;
                        break;
                    case RECORRIDO1:
                        color = Color.GREEN;
                        break;
                    case RECORRIDO2:
                        color = Color.YELLOW;
                        break;
                    case RECORRIDO3:
                        color = Color.BLUE;
                        break;
                    case RECORRIDO4:
                        color = Color.ORANGE;
                        break;
                    case DESCONOCIDA:
                        color = Color.LIGHT_GRAY;
                        break;
                    case ULT_POSICION1:
                    case ULT_POSICION2:
                    case ULT_POSICION3:
                    case ULT_POSICION4:
                        color = Color.PINK;
                        break;
                    default:
                        color = Color.MAGENTA;
                        break;
                }
                bufferedImage.setRGB(i, j, color.getRGB());
            }
        }
    }

    /**
     * Nos hace visible la ventana
     */
    public void mostar() {
        setVisible(true);
    }

    /**
     * Método para cerrar nuestra ventana
     */
    public void cerrar() {
        dispose();
    }

    /**
     * Método que actualiza el mapa pasado como parametro
     *
     * @param mapa mapa a actualizar
     */
    public void actualizarMapa(int[][] mapa) {
        pintarMapa(mapa);
        repaint();
    }

    /**
     * Método que guarda el mapa en un fichero png
     *
     * @param nombre del fichero en el que se guardara la imagen
     */
    public void guardarPNG(String nombre) {
        try {
            ImageIO.write(bufferedImage, "png", new File(nombre));
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
}
