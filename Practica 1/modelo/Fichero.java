package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.SAXException;

/**
 * @brief Clase con los datos de cada fichero
 * @author Alejandro Ledesma Pascual
 */
public class Fichero {
      
    private Tika tika;
    private String contenido;
    private String nombre;
    private String tipo;
    private String codificacion;
    private String idioma;
    private List<Link> enlaces;
    private List palabrasOrdenadas;
    
    /**
     * @brief Constructor con parámetros 
     * @param archivo Fichero de donde extraeremos todos los datos
     * @throws IOException
     * @throws TikaException
     * @throws FileNotFoundException
     * @throws SAXException 
     */
    public Fichero(File archivo) throws IOException, TikaException, FileNotFoundException, SAXException {
        this.tika = new Tika();
        this.enlaces = new ArrayList<>();

        setContenido(archivo);
        setNombre(archivo);
        setTipo(archivo);
        setCodificacion(archivo);
        setIdioma(archivo);
        calcularFrecuencia();
    }
    
    /**
     * @brief Se encarga de obtener el contenido del fichero
     * @param archivo Fichero de donde extraeremos todos los datos
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     * @throws TikaException 
     */
    private void setContenido(File archivo) throws FileNotFoundException, IOException, SAXException, TikaException {
        InputStream input = new FileInputStream(archivo);
        Parser parse = new AutoDetectParser();
        Metadata metadata = new Metadata();
        BodyContentHandler bodyContent = new BodyContentHandler(-1);
        LinkContentHandler linkContent = new LinkContentHandler();
        TeeContentHandler teeContent = new TeeContentHandler(linkContent,bodyContent);
        ParseContext context = new ParseContext();
        parse.parse(input, teeContent, metadata, context);
        
        this.contenido = bodyContent.toString();
        this.enlaces = linkContent.getLinks();
    }
    
    /**
     * @brief Función para obtener el nombre del archivo
     * @param archivo Fichero de donde extraeremos todos los datos
     */
    private void setNombre(File archivo) {
        this.nombre = "";
        
        String aux = archivo.getName();
        
        for(int i = 0;aux.charAt(i) != '.';i++) {
            this.nombre += aux.charAt(i);
        }    
    }
    
    /**
     * @brief Función para obtener el tipo del archivo
     * @param archivo Fichero de donde extraeremos todos los datos
     * @throws IOException 
     */
    private void setTipo(File archivo) throws IOException {
        this.tipo = tika.detect(archivo);
    }
    
    /**
     * @brief Función para obtener la codificación del archivo
     * @param archivo Fichero de donde extraeremos todos los datos
     * @throws TikaException
     * @throws FileNotFoundException 
     */
    private void setCodificacion(File archivo) throws TikaException, FileNotFoundException {
        InputStream input = new FileInputStream(archivo);
        AutoDetectReader reader = null;
        
        try {
            reader = new AutoDetectReader(input);
        }catch (IOException | TikaException e) {
	}
        
        Charset codificacionAux = reader.getCharset();
        
        this.codificacion = codificacionAux.toString();
    }
    
    /**
     * @brief Función para obtener el idioma del fichero
     * @param archivo Fichero de donde extraeremos todos los datos
     */
    private void setIdioma(File archivo) {
        LanguageDetector identificador = new OptimaizeLangDetector().loadModels();
        LanguageResult aux = identificador.detect(contenido);
        
        this.idioma = aux.getLanguage();
    }
    
    /**
     * @brief Función que se usa para calcular la frecuencia de cada palabra
     * dentro de un fichero.
     */
    private void calcularFrecuencia() {
        HashMap<String,Integer> frecuencia = new HashMap<>();
        
        String aux = contenido.toLowerCase();
        aux = aux.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚÜüñÑàìòùÀÈÌÒÙ ]", "");
        
        String palabra = "";
        for(int i = 0;i < aux.length();i++) {
            if(aux.charAt(i) != ' ') {
                palabra += aux.charAt(i);
            }else if(aux.charAt(i) == ' ') {
                //Si la palabra ya está en el map, se suma 1
                if(frecuencia.containsKey(palabra)) {
                    frecuencia.put(palabra,frecuencia.get(palabra) + 1);
                }else{
                    // Si no se encuentra, se añade la palabra iniciandola a 1
                    frecuencia.put(palabra,1);
                }        
                palabra = "";
            }
        }
        
        ordenar(frecuencia);
    }
    
    /**
     * @brief Función que ordena de manera descendente la lista de palabra según
     * el número de veces que haya aparecido
     * @param frecuencia Mapa donde están todas las palabras con sus frecuencias
     */
    private void ordenar(HashMap<String,Integer> frecuencia) {
        palabrasOrdenadas = new LinkedList(frecuencia.entrySet());
	Collections.sort(palabrasOrdenadas, (Object o1, Object o2) -> ((Comparable) (((Map.Entry) (o1)).getValue())).compareTo(((Map.Entry) (o2)).getValue()));       
    }
    
    /**
     * @brief Devuelve el nombre del fichero
     * @return Nombre del fichero
     */
    public String getNombre() {
        return this.nombre;
    }
    
    /**
     * @brief Devuelve el tipo del fichero
     * @return Tipo del fichero
     */
    public String getTipo() {
        return this.tipo;
    }
    
    /**
     * @brief Devuelve la codificación del fichero
     * @return Codificación del fichero
     */
    public String getCodificacion() {
        return this.codificacion;
    }
    
    /**
     * @brief Devuelve el idioma del fichero
     * @return Idioma del fichero
     */
    public String getIdioma() {
        return this.idioma;
    }
    
    /**
     * @brief Devuelve la lista de enlaces del fichero
     * @return Enlaces del fichero
     */
    public List<Link> getEnlaces() {
        return this.enlaces;
    }
    
    /**
     * @brief Devuelve la lista de las palabras ordenadas junto con su 
     * frecuencia
     * @return Lista de palabras que aparecen en el fichero
     */
    public List getPalabrasOrdenadas() {
        return this.palabrasOrdenadas;
    }
}
