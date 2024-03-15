package prueba.pkg2.lab.ii;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

class Cancion {

    String nombre;
    String artista;
    int duracion;
    ImageIcon imagen;
    String tipo;
    String rutaArchivo;

    public Cancion(String nombre, String artista, int duracion, ImageIcon imagen, String tipo, String rutaArchivo) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.imagen = imagen;
        this.tipo = tipo;
        this.rutaArchivo = rutaArchivo;
    }

    public String toString() {
        return nombre + " - " + artista;
    }

    ImageIcon getImagen() {
        return imagen;
    }

    String getRutaArchivo() {
        return rutaArchivo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArtista() {
        return artista;
    }
}

class Nodo {

    Cancion cancion;
    Nodo siguiente;

    public Nodo(Cancion cancion) {
        this.cancion = cancion;
        this.siguiente = null;
    }
}

class ListaEnlazada {

    Nodo cabeza;
    boolean isEmptyMessageShown;

    public ListaEnlazada() {
        this.cabeza = null;
    }

    public void agregarCancion(Cancion cancion) {
        Nodo nuevoNodo = new Nodo(cancion);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo temp = cabeza;
            while (temp.siguiente != null) {
                temp = temp.siguiente;
            }
            temp.siguiente = nuevoNodo;
        }
    }

    public Cancion obtenerCancionSeleccionada() {
        List<Cancion> canciones = obtenerListaDeCanciones();

        if (canciones.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay canciones disponibles.", "Lista de canciones vacía", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Cancion cancionSeleccionada = (Cancion) JOptionPane.showInputDialog(
                null,
                "Seleccione una canción:",
                "Seleccionar Canción",
                JOptionPane.PLAIN_MESSAGE,
                null,
                canciones.toArray(),
                canciones.get(0));

        return cancionSeleccionada;
    }

    private List<Cancion> obtenerListaDeCanciones() {
        List<Cancion> canciones = new ArrayList<>();
        Nodo temp = cabeza;
        while (temp != null) {
            canciones.add(temp.cancion);
            temp = temp.siguiente;
        }
        return canciones;
    }

    boolean isEmpty() {
        return cabeza == null;
    }
}

class SongPanel extends JPanel {

    private JLabel nameLabel;
    private JLabel artistLabel;
    private JLabel imageLabel;

    public SongPanel(Cancion cancion) {
        setLayout(new BorderLayout());

        nameLabel = new JLabel(cancion.getNombre());
        artistLabel = new JLabel(cancion.getArtista());
        imageLabel = new JLabel(cancion.getImagen());

        add(imageLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(artistLabel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.CENTER);
    }

}

public class ReproductorMusical extends JFrame implements ActionListener {

    JButton playButton, stopButton, pauseButton, addButton, selectButton;
    JPanel imagePanel;
    JLabel imageLabel;
    ListaEnlazada listaCanciones;
    File currentSongFile;
    Clip clip;
    private JPanel songListPanel;

    public ReproductorMusical() {
        listaCanciones = new ListaEnlazada();

        playButton = new JButton("Play");
        stopButton = new JButton("Stop");
        pauseButton = new JButton("Pause");
        addButton = new JButton("Add");
        selectButton = new JButton("Select");

        playButton.addActionListener(this);
        stopButton.addActionListener(this);
        pauseButton.addActionListener(this);
        addButton.addActionListener(this);
        selectButton.addActionListener(this);

        imagePanel = new JPanel();
        imageLabel = new JLabel();
        imagePanel.add(imageLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(addButton);
        buttonPanel.add(selectButton);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(buttonPanel, BorderLayout.NORTH);
        container.add(imagePanel, BorderLayout.CENTER);

        songListPanel = new JPanel();
        songListPanel.setLayout(new BoxLayout(songListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(songListPanel);
        container.add(scrollPane, BorderLayout.WEST);

        setTitle("Reproductor Musical");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == playButton) {
            playCurrentSong();
        } else if (e.getSource() == stopButton) {
            stopCurrentSong();
        } else if (e.getSource() == pauseButton) {
            pauseCurrentSong();
        } else if (e.getSource() == addButton) {
            addSong();
        } else if (e.getSource() == selectButton) {
            selectSong();
        }
    }

    private void addSongPanel(Cancion cancion) {
        SongPanel songPanel = new SongPanel(cancion);
        songListPanel.add(songPanel);
        songListPanel.revalidate();
    }

    private void playCurrentSong() {
        if (currentSongFile != null) {
            try {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(currentSongFile);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            } catch (UnsupportedAudioFileException e) {
                JOptionPane.showMessageDialog(this, "Archivo de audio no compatible.", "Error de reproducción", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al reproducir el archivo de audio.", "Error de reproducción", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna canción.", "Error de reproducción", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void stopCurrentSong() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private void pauseCurrentSong() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            JOptionPane.showMessageDialog(this, "Reproducción pausada.", "Pausa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No hay ninguna canción reproduciéndose.", "Error de pausa", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cleanUpResources() {
        if (clip != null) {
            clip.close();
        }
    }

    private void addSong() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos WAV", "wav"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedAudioFile = fileChooser.getSelectedFile();
            String rutaArchivo = selectedAudioFile.getAbsolutePath(); 

            String artista = JOptionPane.showInputDialog(this, "Ingrese el nombre del artista:");

            int duracion = 0;

            JOptionPane.showMessageDialog(this, "Ahora seleccione una imagen para la canción.");

            JFileChooser imageFileChooser = new JFileChooser();
            imageFileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Imagen", "jpg", "jpeg", "png"));
            int imageResult = imageFileChooser.showOpenDialog(this);
            if (imageResult == JFileChooser.APPROVE_OPTION) {
                File selectedImageFile = imageFileChooser.getSelectedFile();

                System.out.println("Nombre del archivo: " + selectedAudioFile.getName());
                System.out.println("Ruta del archivo: " + selectedAudioFile.getAbsolutePath());
                System.out.println("Tamaño del archivo: " + selectedAudioFile.length() + " bytes");
                System.out.println("Formato de archivo: " + getFileExtension(selectedAudioFile));

                try {
                    Image image = ImageIO.read(selectedImageFile);
                    ImageIcon icon = new ImageIcon(image);
                    imageLabel.setIcon(icon); 
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                    imagePanel.repaint();

                    Cancion nuevaCancion = new Cancion(selectedAudioFile.getName(), artista, duracion, icon, "MP3", rutaArchivo);

                    listaCanciones.agregarCancion(nuevaCancion);

                    JOptionPane.showMessageDialog(this, "Canción agregada exitosamente.");

                    if (listaCanciones.isEmptyMessageShown) {
                        listaCanciones.isEmptyMessageShown = false;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String extension = "";
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }
        return extension;
    }

    private void selectSong() {
        Cancion selectedSong = listaCanciones.obtenerCancionSeleccionada();
        if (selectedSong != null) {
            currentSongFile = new File(selectedSong.getRutaArchivo());
            ImageIcon imagenCancion = selectedSong.getImagen();
            if (imagenCancion != null) {
                imageLabel.setIcon(imagenCancion);
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imagePanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "La canción seleccionada no tiene una imagen asociada.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna canción.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReproductorMusical reproductor = new ReproductorMusical();
            reproductor.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    reproductor.cleanUpResources();
                }
            });
        });
    }

}
