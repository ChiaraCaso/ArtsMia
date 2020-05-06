package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	//lo dice il testo che tipo di grafo dobbiamo fare
	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private Map<Integer, ArtObject> idMap;
	
	//possiamo creare il grafo nel costruttore
	public Model() {
		//this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<Integer, ArtObject>();
		
	}

	//possiamo creare il grafo anche nel metodo crea grafo 
	public void creaGrafo () {
		//vantaggio di metterla qui -> ricreare il grafo su diversi grafi di input
		this.grafo = new SimpleWeightedGraph<ArtObject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		ArtsmiaDAO dao = new ArtsmiaDAO();
		dao.listObjects(idMap); //da qui la mappa conterrà tutti gli oggetti presenti nella tabella object
		
		//AGGIUNGERE I VERTICI 
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//AGGIUNGERE GLI ARCHI
		
		//APPROCCIO 1 -- doppio for sui vertici -> dati due vertici controllo se sono collegati
		//troppo lento perchè ci sono troppi vertici e facciamo troppe query al database
	/*	for(ArtObject a1 : this.grafo.vertexSet()) {
			for(ArtObject a2 : this.grafo.vertexSet()) {
				//devo collegare a1 con a2?
				//controllo a priori se non esiste già l'arco (dato che il grafo non è orientato non serve inserire a1,a2 e a2,a1 )
				int peso = dao.getPeso(a1, a2);
				if(peso > 0) {
					if(!this.grafo.containsEdge(a1, a2)) {
					Graphs.addEdge(this.grafo, a1, a2, peso);
					}
				}
			}
		}
	*/	
		
		//APPROCCIO 2 -> mi faccio dare dal db direttamente tutte le adiacenze
		//APPROCCIO MIGLIORE
		for(Adiacenza a : dao.getAdiacenze()) {
			if(a.getPeso()>0) {
				Graphs.addEdge(this.grafo, idMap.get(a.getObj1()), idMap.get(a.getObj2()), a.getPeso());
			//non dobbiamo controllare in questo caso se l'arco esiste già perchè l'abbiamo fatto gia nella query 
			//ponendo obj1>obj2
			}
		}
		
	//	System.out.println(String.format("Grafo creato!  #vertici %d, #Archi %d",this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));	
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
}
