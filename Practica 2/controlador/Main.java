/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package controlador;

import modelo.Fichero;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.math3.util.Pair;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author alexl
 */
public class Main {
    
    final static String RUTACSV = "C:\\Users\\alexl\\Documents\\Universidad\\Cuarto\\RI\\Practicas\\Practica2\\FicherosCSV";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, SAXException, TikaException {
        
        if(args.length == 0) {
            System.out.println("ERROR.\nNo ha introducido la ruta de los ficheros para analizar.");
        }else if (args.length > 1) {
            System.out.println("ERROR.\nSolo se debe introducir un paramétro.");
        }
        
        File directorio = new File(args[0]);
        
        if(directorio.isDirectory()) {
            File [] archivos = directorio.listFiles();
            List<Fichero> ficheros = new ArrayList<>();
            
            for(File archivo : archivos) {
                Fichero f = new Fichero(archivo);
                ficheros.add(f);
            }
            
            for(Fichero f : ficheros) {
                crearCSV(f);
            }
            
            System.out.println("\nCSV creados con éxisto.");
            
            crearFicherosFiltros(ficheros.get(9).getFiltros()); 
            System.out.println("Filtros aplicados con éxito");
            
            aplicarCustomAnalyzer(ficheros.get(9));
            System.out.println("Custom analyzer aplicado con éxito");
            
        }else{
            System.out.println("ERROR.\n La ruta no existe.");
        }
    }
    
     private static void crearCSV(Fichero f) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        File csv = new File(RUTACSV);
        ArrayList<List> aux = f.analizadores();
        String ruta = csv.getAbsolutePath();
        ruta = ruta + "\\";
        
        escribirCSV(ruta,aux.get(0),f.getNombre(),1);
        escribirCSV(ruta,aux.get(1),f.getNombre(),2);
        escribirCSV(ruta, aux.get(2), f.getNombre(), 3);
    }
    
    private static void escribirCSV(String ruta,List palabrasOrdenadas,String nombre,int numero) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(ruta + nombre + numero + ".csv","UTF-8");
            
        writer.println("Text;Size");
        Iterator it = new ReverseListIterator(palabrasOrdenadas);
            
        while(it.hasNext()) {
            String frecuencia = it.next().toString();
            frecuencia = frecuencia.replace('=',';');
            if(frecuencia.charAt(0) != ';') {
                writer.println(frecuencia);
            } 
        }
        
        writer.close();
    }
    
    private static void crearFicherosFiltros(ArrayList<Pair<String,String>> filtros) throws FileNotFoundException {
        
        for(Pair<String,String> filtro : filtros) {
            PrintWriter writer = new PrintWriter(RUTACSV + "\\" + filtro.getKey() + ".txt");
            
            writer.println(filtro.getValue());
            writer.close();
        }
    }
    
    private static void aplicarCustomAnalyzer(Fichero f) throws FileNotFoundException, IOException {
        PrintWriter writer = new PrintWriter(RUTACSV + "\\customAnalyzer.txt");
        
        writer.println(f.customAnalyzer());
        writer.close();
    }
}
