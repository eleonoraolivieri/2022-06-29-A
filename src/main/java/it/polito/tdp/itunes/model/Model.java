package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge> grafo;
	private ItunesDAO dao;
	private Map<Integer,Album> idMap;
	private List<Album> album;

	
	public Model() {
		dao = new ItunesDAO();
		idMap = new HashMap<Integer,Album>();
		dao.getAllAlbums(idMap);
	}
	
	public void creaGrafo(int n) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		//aggiungo vertici "filtrati"
		album = dao.getVertici(n,idMap);
		Graphs.addAllVertices(this.grafo, album);
		
		//aggiungo archi
		for(Arco a : dao.getArchi(n,idMap)) {
			if(this.grafo.containsVertex(a.getV1()) && 
					this.grafo.containsVertex(a.getV2())) {
				DefaultWeightedEdge e = this.grafo.getEdge(a.getV1(), a.getV2());
				if(e == null) 
					Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		
		System.out.println("VERTICI: " + this.grafo.vertexSet().size());
		
		
		
		
	}
	
	public String getAdiacenze(Album a) {
		 List<AlbumBilancio> albumBilancioList = new ArrayList<>();
		List<Album> vertici = Graphs.successorListOf(this.grafo, a);
		int bilancio = 0;
		double pesoE = 0;
		double pesoU = 0;
		
		
		
		for(Album v: vertici) {
			Set<DefaultWeightedEdge> entranti = this.grafo.incomingEdgesOf(v);
			System.out.println(entranti);
			Set<DefaultWeightedEdge> uscenti = this.grafo.outgoingEdgesOf(v);
			System.out.println(uscenti);
			for(DefaultWeightedEdge e:entranti) {
				pesoE = pesoE+ this.grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge u:uscenti) {
				pesoU = pesoU +  this.grafo.getEdgeWeight(u);
			}
			bilancio = (int) (pesoE - pesoU);
			
			
			AlbumBilancio albumBilancio = new AlbumBilancio(v, bilancio);
            albumBilancioList.add(albumBilancio);
		    pesoE = 0;
			pesoU = 0;
			
		}
		
		Collections.sort(albumBilancioList);
        
        StringBuilder st = new StringBuilder();
        for (AlbumBilancio albumBilancio : albumBilancioList) {
            st.append(albumBilancio.getA()).append(" bilancio: ").append(albumBilancio.getBilancio()).append("\n");
        }
        
        return st.toString();
	
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	

	public List<Album> getVertici() {
		
		return this.album;
	}
	
	
	
	
}
