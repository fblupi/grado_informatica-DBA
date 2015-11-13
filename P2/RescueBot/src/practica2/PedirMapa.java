/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

/**
 *
 * @author José Guadix
 */
public class PedirMapa extends javax.swing.JDialog {
    private String nombreMapa;
    /**
     * Creates new form PedirMapa
     */
    public PedirMapa(java.awt.Frame parent, boolean modal) {
	super(parent, modal);
	initComponents();
	setTitle("Selecciona un mapa");
	nombreMapa = null;
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonsMapas = new javax.swing.ButtonGroup();
        mapa1 = new javax.swing.JRadioButton();
        mapa4 = new javax.swing.JRadioButton();
        mapa7 = new javax.swing.JRadioButton();
        mapaOtro = new javax.swing.JRadioButton();
        mapa8 = new javax.swing.JRadioButton();
        mapa9 = new javax.swing.JRadioButton();
        mapa2 = new javax.swing.JRadioButton();
        mapa5 = new javax.swing.JRadioButton();
        mapa3 = new javax.swing.JRadioButton();
        mapa6 = new javax.swing.JRadioButton();
        textoOtro = new javax.swing.JTextField();
        buttonAceptar = new javax.swing.JButton();
        buttonSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        buttonsMapas.add(mapa1);
        mapa1.setSelected(true);
        mapa1.setText("Mapa 1");

        buttonsMapas.add(mapa4);
        mapa4.setText("Mapa 4");

        buttonsMapas.add(mapa7);
        mapa7.setText("Mapa 7");

        buttonsMapas.add(mapaOtro);
        mapaOtro.setText("Otro");
        mapaOtro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mapaOtroItemStateChanged(evt);
            }
        });

        buttonsMapas.add(mapa8);
        mapa8.setText("Mapa 8");

        buttonsMapas.add(mapa9);
        mapa9.setText("Mapa 9");

        buttonsMapas.add(mapa2);
        mapa2.setText("Mapa 2");

        buttonsMapas.add(mapa5);
        mapa5.setText("Mapa 5");

        buttonsMapas.add(mapa3);
        mapa3.setText("Mapa 3");

        buttonsMapas.add(mapa6);
        mapa6.setText("Mapa 6");

        textoOtro.setEditable(false);
        textoOtro.setToolTipText("");

        buttonAceptar.setText("Aceptar");
        buttonAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAceptarActionPerformed(evt);
            }
        });

        buttonSalir.setText("Salir");
        buttonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(mapaOtro)
                            .addComponent(mapa7)
                            .addComponent(mapa4)
                            .addComponent(mapa1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(mapa5)
                                    .addComponent(mapa2)
                                    .addComponent(mapa8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(mapa9)
                                    .addComponent(mapa6)
                                    .addComponent(mapa3)))
                            .addComponent(textoOtro)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(buttonSalir)
                        .addGap(30, 30, 30)
                        .addComponent(buttonAceptar)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mapa3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mapa6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mapa9))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mapa2)
                            .addComponent(mapa1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mapa5)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(mapa4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(mapa7)
                                    .addComponent(mapa8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(mapaOtro)
                                    .addComponent(textoOtro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSalir)
                    .addComponent(buttonAceptar))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mapaOtroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mapaOtroItemStateChanged
        textoOtro.setEditable(mapaOtro.isSelected());
    }//GEN-LAST:event_mapaOtroItemStateChanged

    private void buttonAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAceptarActionPerformed
        if(mapa1.isSelected()){
	    nombreMapa = "map1";
	}else if(mapa2.isSelected()){
	    nombreMapa = "map2";
	}else if(mapa3.isSelected()){
	    nombreMapa = "map3";
	}else if(mapa4.isSelected()){
	    nombreMapa = "map4";
	}else if(mapa5.isSelected()){
	    nombreMapa = "map5";
	}else if(mapa6.isSelected()){
	    nombreMapa = "map6";
	}else if(mapa7.isSelected()){
	    nombreMapa = "map7";
	}else if(mapa8.isSelected()){
	    nombreMapa = "map8";
	}else if(mapa9.isSelected()){
	    nombreMapa = "map9";
	}else if(mapaOtro.isSelected()){
	    nombreMapa = textoOtro.getText();
	}
	this.dispose();
    }//GEN-LAST:event_buttonAceptarActionPerformed

    private void buttonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_buttonSalirActionPerformed

    /**
     * 
     * @return El nombre del mapa o null en caso de cerrar la ventana.
     */
    public String getMapa(){
	setVisible(true);
	return nombreMapa;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAceptar;
    private javax.swing.JButton buttonSalir;
    private javax.swing.ButtonGroup buttonsMapas;
    private javax.swing.JRadioButton mapa1;
    private javax.swing.JRadioButton mapa2;
    private javax.swing.JRadioButton mapa3;
    private javax.swing.JRadioButton mapa4;
    private javax.swing.JRadioButton mapa5;
    private javax.swing.JRadioButton mapa6;
    private javax.swing.JRadioButton mapa7;
    private javax.swing.JRadioButton mapa8;
    private javax.swing.JRadioButton mapa9;
    private javax.swing.JRadioButton mapaOtro;
    private javax.swing.JTextField textoOtro;
    // End of variables declaration//GEN-END:variables
}