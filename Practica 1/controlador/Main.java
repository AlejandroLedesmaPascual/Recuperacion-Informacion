package controlador;

import modelo.Fichero;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.tika.exception.TikaException;
import org.apache.tika.sax.Link;
import org.xml.sax.SAXException;

/**
 * @author Alejandro Ledesma Pascual
 */
public class Main {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        ArrayList<Fichero> ficheros = new ArrayList<>();
        File directorio = new File("C:\\Users\\alexl\\Documents\\Universidad\\Cuarto\\RI\\Practicas\\Practica1\\FicherosPractica");
        
        boolean menu = true;
        String opcion;
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            cargarFicheros(directorio,ficheros);
        } catch (TikaException | FileNotFoundException | SAXException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        // Menu con las opciones que tiene disponible el programa
        // "-d" --> Crea una tabla con los datos de los ficheros
        // "-l" --> Muestra todos los enlaces de los ficheros
        // "-t" --> Crea un CSV para cada uno de los ficheros con la frecuencia
        //          de sus palabras
        while(menu) {
            imprimeMenu();
            opcion = entrada.readLine();
            
            if("-d".equals(opcion)) {
                crearTabla(ficheros);
            }else if("-l".equals(opcion)) {
                mostrarEnlaces(ficheros);
            }else if("-t".equals(opcion)) {
                crearCSV(ficheros);
            }else{
                menu = false;
            }
        }
    }
    
    /**
     * @brief Función que imprime el menú con las opciones disponibles
     */
    private static void imprimeMenu() {
        System.out.println("Que operación quiere realizar :");
        System.out.println("  Escribe '-d' para realizar una tabla con los datos");
        System.out.println("  Escribe '-l' para ver todos los enlaces del documento");
        System.out.println("  Escribe '-t para crear un CSV del documento");
        System.out.println("  Pulse cualquier otra tecla para salir");
    }
    
    /**
     * @brief Función que se encarga de cargar los ficheros del directorio 
     * a un array de ficheros para obtener los datos de los mismos
     * @param directorio Directorio donde se encuentran los ficheros de la 
     * práctica
     * @param ficheros Array de los ficheros donde los vamos a cargar
     * @throws IOException
     * @throws TikaException
     * @throws FileNotFoundException
     * @throws SAXException 
     */
    private static void cargarFicheros(File directorio,ArrayList<Fichero> ficheros) throws IOException, TikaException, FileNotFoundException, SAXException {
        File [] archivos = directorio.listFiles();
        
        for (File archivo : archivos) {
            Fichero f = new Fichero(archivo);
            ficheros.add(f);
        }
    }
    
    /**
     * @brief Se encarga de imprimir una tabla con el nombre, idioma,
     * codificación y tipo de los ficheros cargados anteriormente
     * @param ficheros Array donde se encuentran los ficheros
     */
    private static void crearTabla(ArrayList<Fichero> ficheros) {
        System.out.println("|    Nombre    |    Idioma    |    Codificación    |    Tipo Fichero    |");
        System.out.println(" ----------------------------------------------------------------------- ");
        
        for(Fichero f : ficheros) {
            System.out.println("\n|" + f.getNombre() + "  |  " + f.getIdioma() 
                                + "          |     " + f.getCodificacion() + "       |  " 
                                + f.getTipo()+ "   |\n");
            
        }
        
        System.out.println(" ------------------------------------------------------------------------ ");    
    }
    
    /**
     * @brief Muestra todos los enlaces de los ficheros cargados anteriormente
     * @param ficheros Array donde se encuentran los ficheros
     */
    private static void mostrarEnlaces(ArrayList<Fichero> ficheros) {
        for(Fichero f : ficheros) {
            
            System.out.println("Links del fichero --> " + f.getNombre());
            for(Link link : f.getEnlaces()) {
                System.out.println("    - " + link.toString());
            }
            System.out.println();
        } 
    }
    
    /**
     * @brief Crea un fichero CSV para cada fichero cargado donde se encuentra
     * la frecuencia con la que aparece cada palabra.
     * @param ficheros Array donde se encuentran los ficheros
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    private static void crearCSV(ArrayList<Fichero> ficheros) throws FileNotFoundException, UnsupportedEncodingException {
        File csv = new File("C:\\Users\\alexl\\Documents\\Universidad\\Cuarto\\RI\\Practicas\\Practica1\\FicherosCSV");
        String ruta = csv.getAbsolutePath();
        ruta = ruta + "\\";
        
        for(Fichero f : ficheros) {       
            PrintWriter writer = new PrintWriter(ruta + f.getNombre() + ".csv","UTF-8");
            
            writer.println("Text;Size");
            Iterator it = new ReverseListIterator(f.getPalabrasOrdenadas());
            
            while(it.hasNext()) {
                String frecuencia = it.next().toString();
                frecuencia = frecuencia.replace('=',';');
                if(frecuencia.charAt(0) != ';') {
                    writer.println(frecuencia);
                } 
            }
            writer.close();
        }
        
        System.out.println("\nCSV creados con éxisto.");
    }
}