package org.example;

// ============================================
// 3. AplicacionAEMET.java - Interfaz Gráfica Principal
// ============================================

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Aplicación Cliente AEMET con interfaz gráfica
 * Contiene 7 pestañas para consultar diferentes predicciones meteorológicas
 *
 * Práctica 1 - PSP UD3
 */
public class AplicacionAEMET extends JFrame {

    private JTabbedPane tabbedPane;

    public AplicacionAEMET() {
        super("Aplicación Cliente AEMET - Predicción Meteorológica");

        // Configurar ventana principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Solicitar API Key al iniciar
        solicitarApiKey();

        // Crear panel de pestañas
        tabbedPane = new JTabbedPane();

        // Añadir las 7 pestañas según requisitos de la práctica
        tabbedPane.addTab("España", crearPanelEspana());
        tabbedPane.addTab("Comunidades", crearPanelComunidades());
        tabbedPane.addTab("Provincias", crearPanelProvincias());
        tabbedPane.addTab("Localidades", crearPanelLocalidades());
        tabbedPane.addTab("Montañas", crearPanelMontanas());
        tabbedPane.addTab("Playas", crearPanelPlayas());
        tabbedPane.addTab("Valores Climatológicos", crearPanelValoresClimatologicos());

        // Añadir panel de pestañas a la ventana
        add(tabbedPane);

        setVisible(true);
    }

    /**
     * Solicita la API Key al usuario
     */
    private void solicitarApiKey() {
        String apiKey = JOptionPane.showInputDialog(
                this,
                "Introduce tu API Key de AEMET OpenData:\n" +
                        "(Obtenerla en: https://opendata.aemet.es/centrodedescargas/obtencionAPIKey)",
                "Configuración API Key",
                JOptionPane.QUESTION_MESSAGE
        );

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            ClienteAEMET.setApiKey(apiKey.trim());
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se puede usar la aplicación sin API Key",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    // ============================================
    // PESTAÑA 1: PREDICCIÓN PARA ESPAÑA
    // ============================================

    private JPanel crearPanelEspana() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Área de texto para mostrar resultados
        JTextArea areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Botón para consultar
        JButton btnConsultar = new JButton("Consultar Predicción Nacional");
        btnConsultar.addActionListener(e -> {
            areaResultados.setText("Consultando...");
            // Ejecutar en hilo separado para no bloquear la interfaz
            new Thread(() -> {
                String resultado = ClienteAEMET.prediccionEspana();
                SwingUtilities.invokeLater(() -> areaResultados.setText(resultado));
            }).start();
        });

        // Panel superior con botón
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(btnConsultar);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // PESTAÑA 2: PREDICCIÓN POR COMUNIDADES AUTÓNOMAS
    // ============================================

    private JPanel crearPanelComunidades() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ComboBox con comunidades autónomas
        String[] comunidades = {
                "and - Andalucía",
                "ara - Aragón",
                "ast - Asturias",
                "bal - Baleares",
                "coo - Canarias",
                "can - Cantabria",
                "cle - Castilla y León",
                "clm - Castilla-La Mancha",
                "cat - Cataluña",
                "val - Comunidad Valenciana",
                "ext - Extremadura",
                "gal - Galicia",
                "mad - Madrid",
                "mur - Murcia",
                "nav - Navarra",
                "pva - País Vasco",
                "rio - La Rioja",
                "ceu - Ceuta",
                "mel - Melilla"
        };

        JComboBox<String> comboComunidades = new JComboBox<>(comunidades);

        // Área de resultados
        JTextArea areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Botón consultar
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> {
            String seleccion = (String) comboComunidades.getSelectedItem();
            String codigo = seleccion.substring(0, 3); // Extraer código

            areaResultados.setText("Consultando...");
            new Thread(() -> {
                String resultado = ClienteAEMET.prediccionComunidad(codigo);
                SwingUtilities.invokeLater(() -> areaResultados.setText(resultado));
            }).start();
        });

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Comunidad Autónoma:"));
        panelSuperior.add(comboComunidades);
        panelSuperior.add(btnConsultar);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // PESTAÑA 3: PREDICCIÓN POR PROVINCIAS
    // ============================================

    private JPanel crearPanelProvincias() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ComboBox con provincias (códigos del 01 al 52)
        String[] provincias = {
                "01 - Álava", "02 - Albacete", "03 - Alicante", "04 - Almería",
                "05 - Ávila", "06 - Badajoz", "07 - Baleares", "08 - Barcelona",
                "09 - Burgos", "10 - Cáceres", "11 - Cádiz", "12 - Castellón",
                "13 - Ciudad Real", "14 - Córdoba", "15 - La Coruña", "16 - Cuenca",
                "17 - Gerona", "18 - Granada", "19 - Guadalajara", "20 - Guipúzcoa",
                "21 - Huelva", "22 - Huesca", "23 - Jaén", "24 - León",
                "25 - Lérida", "26 - La Rioja", "27 - Lugo", "28 - Madrid",
                "29 - Málaga", "30 - Murcia", "31 - Navarra", "32 - Orense",
                "33 - Asturias", "34 - Palencia", "35 - Las Palmas", "36 - Pontevedra",
                "37 - Salamanca", "38 - Santa Cruz de Tenerife", "39 - Cantabria",
                "40 - Segovia", "41 - Sevilla", "42 - Soria", "43 - Tarragona",
                "44 - Teruel", "45 - Toledo", "46 - Valencia", "47 - Valladolid",
                "48 - Vizcaya", "49 - Zamora", "50 - Zaragoza", "51 - Ceuta", "52 - Melilla"
        };

        JComboBox<String> comboProvincias = new JComboBox<>(provincias);

        // Área de resultados
        JTextArea areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Botón consultar
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> {
            String seleccion = (String) comboProvincias.getSelectedItem();
            String codigo = seleccion.substring(0, 2); // Extraer código

            areaResultados.setText("Consultando...");
            new Thread(() -> {
                String resultado = ClienteAEMET.prediccionProvincia(codigo);
                SwingUtilities.invokeLater(() -> areaResultados.setText(resultado));
            }).start();
        });

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Provincia:"));
        panelSuperior.add(comboProvincias);
        panelSuperior.add(btnConsultar);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // PESTAÑA 4: PREDICCIÓN POR LOCALIDADES
    // ============================================

    private JPanel crearPanelLocalidades() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campo de texto para código de municipio
        JTextField txtCodigoMunicipio = new JTextField(10);

        // Área de resultados
        JTextArea areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Botón consultar
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> {
            String codigo = txtCodigoMunicipio.getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Introduce un código de municipio",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            areaResultados.setText("Consultando...");
            new Thread(() -> {
                String resultado = ClienteAEMET.prediccionLocalidad(codigo);
                SwingUtilities.invokeLater(() -> areaResultados.setText(resultado));
            }).start();
        });

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Código Municipio (ej: 28079 para Madrid):"));
        panelSuperior.add(txtCodigoMunicipio);
        panelSuperior.add(btnConsultar);

        // Panel de ayuda
        JPanel panelAyuda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAyuda = new JLabel("ℹ️ Consulta códigos en: https://www.ine.es/daco/daco42/codmun/cod_ccaa_provincia.htm");
        lblAyuda.setFont(new Font("Arial", Font.ITALIC, 11));
        panelAyuda.add(lblAyuda);

        JPanel panelNorte = new JPanel(new GridLayout(2, 1));
        panelNorte.add(panelSuperior);
        panelNorte.add(panelAyuda);

        panel.add(panelNorte, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // PESTAÑA 5: PREDICCIÓN POR MACIZOS MONTAÑOSOS
    // ============================================

    private JPanel crearPanelMontanas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ComboBox con macizos montañosos
        String[] macizos = {
                "nev1 - Sierra Nevada",
                "pir1 - Pirineo Catalán",
                "pir2 - Pirineo Navarro",
                "pir3 - Pirineo Aragonés"
        };

        JComboBox<String> comboMacizos = new JComboBox<>(macizos);

        // Área de resultados
        JTextArea areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Botón consultar
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> {
            String seleccion = (String) comboMacizos.getSelectedItem();
            String codigo = seleccion.substring(0, 4);

            areaResultados.setText("Consultando...");
            new Thread(() -> {
                String resultado = ClienteAEMET.prediccionMontana(codigo);
                SwingUtilities.invokeLater(() -> areaResultados.setText(resultado));
            }).start();
        });

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Macizo Montañoso:"));
        panelSuperior.add(comboMacizos);
        panelSuperior.add(btnConsultar);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // PESTAÑA 6: PREDICCIÓN POR PLAYAS
    // ============================================

    private JPanel crearPanelPlayas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campo de texto para código de playa
        JTextField txtCodigoPlaya = new JTextField(10);

        // Área de resultados
        JTextArea areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Botón consultar
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> {
            String codigo = txtCodigoPlaya.getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Introduce un código de playa",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            areaResultados.setText("Consultando...");
            new Thread(() -> {
                String resultado = ClienteAEMET.prediccionPlaya(codigo);
                SwingUtilities.invokeLater(() -> areaResultados.setText(resultado));
            }).start();
        });

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(new JLabel("Código Playa:"));
        panelSuperior.add(txtCodigoPlaya);
        panelSuperior.add(btnConsultar);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // PESTAÑA 7: VALORES CLIMATOLÓGICOS DIARIOS
    // ============================================

    private JPanel crearPanelValoresClimatologicos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con controles
        JPanel panelControles = new JPanel(new GridLayout(4, 2, 5, 5));

        // Campos de entrada
        JTextField txtFechaInicio = new JTextField("2024-01-01");
        JTextField txtFechaFin = new JTextField("2024-01-31");
        JComboBox<Estacion> comboEstaciones = new JComboBox<>();

        panelControles.add(new JLabel("Fecha Inicio (AAAA-MM-DD):"));
        panelControles.add(txtFechaInicio);
        panelControles.add(new JLabel("Fecha Fin (AAAA-MM-DD):"));
        panelControles.add(txtFechaFin);
        panelControles.add(new JLabel("Estación Meteorológica:"));
        panelControles.add(comboEstaciones);

        // Botón cargar estaciones
        JButton btnCargarEstaciones = new JButton("Cargar Estaciones");
        btnCargarEstaciones.addActionListener(e -> {
            btnCargarEstaciones.setEnabled(false);
            btnCargarEstaciones.setText("Cargando...");

            new Thread(() -> {
                List<Estacion> estaciones = ClienteAEMET.obtenerEstaciones();
                SwingUtilities.invokeLater(() -> {
                    if (estaciones != null) {
                        comboEstaciones.removeAllItems();
                        for (Estacion est : estaciones) {
                            comboEstaciones.addItem(est);
                        }
                        JOptionPane.showMessageDialog(panel,
                                "Estaciones cargadas: " + estaciones.size(),
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    btnCargarEstaciones.setEnabled(true);
                    btnCargarEstaciones.setText("Cargar Estaciones");
                });
            }).start();
        });

        // Botón consultar
        JButton btnConsultar = new JButton("Consultar Datos");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnCargarEstaciones);
        panelBotones.add(btnConsultar);

        panelControles.add(panelBotones);

        // Tabla para mostrar resultados
        String[] columnas = {"Fecha", "Estación", "Tª Media", "Tª Máx", "Tª Mín", "Precipitación"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);

        // Acción del botón consultar
        btnConsultar.addActionListener(e -> {
            if (comboEstaciones.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(panel,
                        "Primero carga las estaciones",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String fechaIni = txtFechaInicio.getText().trim();
            String fechaFin = txtFechaFin.getText().trim();
            Estacion estacion = (Estacion) comboEstaciones.getSelectedItem();

            modeloTabla.setRowCount(0); // Limpiar tabla

            new Thread(() -> {
                List<ValoresDiarios> valores = ClienteAEMET.valoresClimatologicos(
                        fechaIni, fechaFin, estacion.indicativo);

                SwingUtilities.invokeLater(() -> {
                    if (valores != null && !valores.isEmpty()) {
                        for (ValoresDiarios v : valores) {
                            modeloTabla.addRow(new Object[]{
                                    v.fecha,
                                    v.nombre,
                                    v.tmed + "°C",
                                    v.tmax + "°C",
                                    v.tmin + "°C",
                                    v.prec + " mm"
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel,
                                "No se encontraron datos",
                                "Información",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }).start();
        });

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ============================================
    // MÉTODO MAIN
    // ============================================

    public static void main(String[] args) {
        // Establecer Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear y mostrar la aplicación
        SwingUtilities.invokeLater(() -> new AplicacionAEMET());
    }
}


