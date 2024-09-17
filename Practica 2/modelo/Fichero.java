/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.CharsRef;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.tartarus.snowball.ext.SpanishStemmer;
import org.xml.sax.SAXException;


/**
 *
 * @author alexl
 */
public class Fichero {
    
    private final static CharArraySet STOPWORDS = new CharArraySet(0, true);

    private String nombre;
    private String contenido;
    
    public Fichero(File archivo) throws IOException, FileNotFoundException, SAXException, TikaException {
        setNombre(archivo);
        setContenido(archivo);
        cargarStopword();
    }
    
    private void cargarStopword() {
        STOPWORDS.add("las");
        STOPWORDS.add("de");
        STOPWORDS.add("los");
        STOPWORDS.add("en");
        STOPWORDS.add("la");
    }
    
    private void setContenido(File archivo) throws FileNotFoundException, IOException, SAXException, TikaException {
        InputStream input = new FileInputStream(archivo);
        Parser parse = new AutoDetectParser();
        Metadata metadata = new Metadata();
        BodyContentHandler bodyContent = new BodyContentHandler(-1);
        LinkContentHandler linkContent = new LinkContentHandler();
        TeeContentHandler teeContent = new TeeContentHandler(linkContent,bodyContent);
        ParseContext context = new ParseContext();
        parse.parse(input, teeContent, metadata, context);
        
        contenido = bodyContent.toString();
    }
    
    private void setNombre(File archivo) {
        nombre = "";
        String aux = archivo.getName();
        
        for(int i = 0;aux.charAt(i) != '.';i++) {
            nombre += aux.charAt(i);
        }    
    }
    
    public String getNombre() {
        return this.nombre;
    }
    
    public String getContenido() {
        return this.contenido;
    }
    
    public ArrayList<List> analizadores() throws IOException {
        ArrayList<List> analizadores = new ArrayList<>();
        
        analizadores.add(simpleAnalyzer());
        analizadores.add(whiteSpaceAnalyzer());
        analizadores.add(standardAnalyzer());
        
        return analizadores;
    }
    
    private List simpleAnalyzer() throws IOException {
        HashMap<String,Integer> frecuencias = new HashMap<>();
        Analyzer an = new SimpleAnalyzer();
        List analizador;
        
        TokenStream stream = an.tokenStream(null, this.getContenido());
        
        stream.reset();
        while(stream.incrementToken()) {
            String palabra = stream.getAttribute(CharTermAttribute.class).toString();
            if(frecuencias.containsKey(palabra)) {
                frecuencias.put(palabra,frecuencias.get(palabra) + 1 );
            }else{
                frecuencias.put(palabra, 1);
            }
        }                                                                                                                                                                                                                                      
        
        analizador = new LinkedList(frecuencias.entrySet());
        Collections.sort(analizador, (Object o1, Object o2) -> ((Comparable) (((Map.Entry) (o1)).getValue())).compareTo(((Map.Entry) (o2)).getValue()));
        stream.end();
        stream.close();
        
        return analizador;
    }
    
    private List whiteSpaceAnalyzer() throws IOException {
        HashMap<String,Integer> frecuencias = new HashMap<>();
        Analyzer an = new WhitespaceAnalyzer();
        List analizador;
        
        TokenStream stream = an.tokenStream(null, this.getContenido());
        
        stream.reset();
        while(stream.incrementToken()) {
            String palabra = stream.getAttribute(CharTermAttribute.class).toString();
            if(frecuencias.containsKey(palabra)) {
                frecuencias.put(palabra,frecuencias.get(palabra) + 1 );
            }else{
                frecuencias.put(palabra, 1);
            }
        }                                                                                                                                                                                                                                      
        
        analizador = new LinkedList(frecuencias.entrySet());
        Collections.sort(analizador, (Object o1, Object o2) -> ((Comparable) (((Map.Entry) (o1)).getValue())).compareTo(((Map.Entry) (o2)).getValue()));
        stream.end();
        stream.close();
        
        return analizador;
    }
    
    private List standardAnalyzer() throws IOException {
        HashMap<String,Integer> frecuencias = new HashMap<>();
        Analyzer an = new StandardAnalyzer(STOPWORDS);
        List analizador;
        
        TokenStream stream = an.tokenStream(null, this.getContenido());
        
        stream.reset();
        while(stream.incrementToken()) {
            String palabra = stream.getAttribute(CharTermAttribute.class).toString();
            if(frecuencias.containsKey(palabra)) {
                frecuencias.put(palabra,frecuencias.get(palabra) + 1 );
            }else{
                frecuencias.put(palabra, 1);
            }
        }                                                                                                                                                                                                                                      
        
        analizador = new LinkedList(frecuencias.entrySet());
        Collections.sort(analizador, (Object o1, Object o2) -> ((Comparable) (((Map.Entry) (o1)).getValue())).compareTo(((Map.Entry) (o2)).getValue()));
        stream.end();
        stream.close();
        
        return analizador;
    }
    
    private String aplicarFiltro(TokenStream stream) throws IOException {
        stream.reset();
        
        String aux = "";
        while (stream.incrementToken()) {
            aux += stream.getAttribute(CharTermAttribute.class) + " ";
        }
        
        stream.end();
        stream.close();
        
        return aux;     
    }
    
    public ArrayList<Pair<String,String>> getFiltros() throws IOException {
        
        ArrayList<Pair<String,String>> filtros = new ArrayList<>();
        Tokenizer source = new StandardTokenizer();
        
        // LowerCaseFilter
        source.setReader(new StringReader(contenido));
        filtros.add(new Pair<>("LowerCaseFilter",aplicarFiltro(new LowerCaseFilter(source))));
        
        // StopFilter
        source.setReader(new StringReader(contenido));
        filtros.add(new Pair<>("StopFilter",aplicarFiltro(new StopFilter(source,STOPWORDS))));
        
        // SnowballFilter
        source.setReader(new StringReader(contenido));
        filtros.add(new Pair<>("SnowballFilter",aplicarFiltro(new SnowballFilter(source,new SpanishStemmer()))));
        
        // ShingleFilter
        source.setReader(new StringReader(contenido));
        filtros.add(new Pair<>("ShingleFilter",aplicarFiltro(new ShingleFilter(source,3))));
        
        // EdgeNGramCommonFilter
        source.setReader(new StringReader(contenido));
        filtros.add(new Pair<>("EdgeNGramCommonFilter",aplicarFiltro(new EdgeNGramTokenFilter(source,3))));
        
        // NGramTokenFilter
        source.setReader(new StringReader(contenido));
        filtros.add(new Pair<>("NGramTokenFilter",aplicarFiltro(new NGramTokenFilter(source, 3))));
        
        // CommonGramsFilter
        source.setReader(new StringReader(contenido));
        filtros.add(new Pair<>("CommonGramsFilter",aplicarFiltro(new CommonGramsFilter(source,STOPWORDS))));
        
        // SynonymFilter
        source.setReader(new StringReader(contenido));
        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        builder.add(new CharsRef("contar"), new CharsRef("enumerar"), true);
        builder.add(new CharsRef("enumerar"), new CharsRef("contar"), true);
        builder.add(new CharsRef("distintos"), new CharsRef("diferentes"), true);
        builder.add(new CharsRef("diferentes"), new CharsRef("distintos"), true);
        
        SynonymMap map = builder.build();
        
        filtros.add(new Pair<>("SynonymFilter",aplicarFiltro(new SynonymFilter(source,map,true))));
            
        return filtros;
    }
    
    public String customAnalyzer() throws IOException {
        String aux = "";
        
        Analyzer ana = CustomAnalyzer.builder(Paths.get("..//doc"))
                                                    .withTokenizer(StandardTokenizerFactory.NAME)
                                                    .addTokenFilter(LowerCaseFilterFactory.NAME)
                                                    .addTokenFilter(StopFilterFactory.NAME, "ignoreCase", "false", "words", "stopwords.txt", "format", "wordset")
                                                    .addTokenFilter(SnowballPorterFilterFactory.NAME,"language","Spanish").build();
                
        
        TokenStream stream = ana.tokenStream(null, new StringReader(contenido));
        
        stream.reset();
        while (stream.incrementToken()) {
            aux += stream.getAttribute(CharTermAttribute.class) + " ";
        }
                
        stream.end();
        stream.close();         
           
        return aux;
    }
  
}