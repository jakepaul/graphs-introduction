package ca.uwo.eng.se2205.lab7.travel;

import ca.uwo.eng.se2205.lab7.graphs.DirectedGraph;
import ca.uwo.eng.se2205.lab7.graphs.Edge;
import ca.uwo.eng.se2205.lab7.graphs.Vertex;
import com.google.common.collect.ArrayTable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * Optimized utility for shopping for flights.
 */
@ParametersAreNonnullByDefault
public final class FlightShopper {
    
    private DirectedGraph<Airport, Flight> initialGraph = new DirectedGraph<>();
    /**
     * Builds a {@code FlightShopper} via a {@link Set} of {@link Airport}s and {@link Flight}s.
     *
     * @param airports The available airports
     * @param flights All available flights
     */
    public FlightShopper(Collection<? extends Airport> airports, Collection<? extends Flight> flights) {
        // DO NOT CHANGE THE METHOD SIGNATURE
        // Initialize your shopper
        for(Airport air: airports){
            initialGraph.newVertex(air);
        }
        Vertex<Airport, Flight> dep = null, arr = null;
        for(Flight fly: flights){
            for(Vertex<Airport, Flight> air: initialGraph.vertices()){
                if(air.getElement().equals(fly.getDeparture())){
                    dep = air;
                }
                if(air.getElement().equals(fly.getArrival())){
                    arr = air;
                }
            }
            initialGraph.newEdge(dep, arr, new Flight(dep.getElement(), arr.getElement(), fly.getCost()));
        }
    }
    
    
    
    /**
     * Finds the cheapest flight from two {@link Airport}s.
     * @param from Starting airport
     * @param to Ending airport
     * @return Cheapest {@code Itinerary} to fly between {@code from} and {@code to}
     */
    public Itinerary price(Airport from, Airport to) {
        Vertex<Airport, Flight> leaving = null, going = null;
        for(Edge<Flight, Airport> e: initialGraph.edges()){
            if(e.getWeight().getDeparture().equals(from)){
                leaving = e.u();
            }
            if(e.getWeight().getArrival().equals(to)){
                going = e.v();
            }
        }
        assert leaving != null;
        assert going != null;
        List<Vertex<Airport, Flight>> path = FloydWarshall(initialGraph, leaving, going);
        List<Flight> itinerary = new ArrayList<>();
        for(int i = 0; i < path.size()-1; i++){
            itinerary.add(initialGraph.getEdge(path.get(i),path.get(i+1)).getWeight());
        }
        return new Itinerary(itinerary);
    }
    
    private List<Vertex<Airport, Flight>> FloydWarshall(DirectedGraph<Airport, Flight> graph, Vertex<Airport, Flight> start, Vertex<Airport, Flight> end){
        ArrayTable<Vertex<Airport, Flight>, Vertex<Airport, Flight>, Double> path = ArrayTable.create(graph.vertices(), graph.vertices());
        ArrayTable<Vertex<Airport, Flight>, Vertex<Airport, Flight>, Vertex<Airport,Flight>> flights = ArrayTable.create(graph.vertices(), graph.vertices());
        for(Vertex<Airport, Flight> v: graph.vertices()){
            for(Edge<Flight, Airport> e: v.outgoingEdges()){
                flights.put(v, e.v(), e.v());
                path.put(v, e.v(), e.getWeight().getCost());
            }
        }
        
        for(Vertex<Airport,Flight> k: graph.vertices()) {
            for (Vertex<Airport, Flight> i : graph.vertices()) {
                if (i != k && path.get(i, k) != null) {
                    for (Vertex<Airport, Flight> j : graph.vertices()) {
                        if (i != j && k != j && path.get(k, j) != null) {
                            if (path.get(i, j) == null) {
                                flights.put(i, j, flights.get(i,k));
                                path.put(i, j, path.get(i, k) + path.get(k, j));
                            } else if (path.get(i, j) > path.get(i, k) + path.get(k, j)) {
                                flights.put(i, j, flights.get(i,k));
                                path.put(i, j, path.get(i, k) + path.get(k, j));
                            }
                        }
                    }
                }
            }
        }
        List<Vertex<Airport, Flight>> pathToEnd = new ArrayList<>();
        if(flights.get(start, end) == null){
            return null;
        }
        pathToEnd.add(start);
        while(start != end){
            start = flights.get(start, end);
            pathToEnd.add(start);
        }
        return pathToEnd;
    }
    
    public static void main(String[] args){
        
        
        List<Airport> airports = new LinkedList<Airport>();
        
        Airport ATL = new Airport("ATL", 0.0, 0.0);
        Airport ORD = new Airport("ORD", 0.0, 0.0);
        Airport YYZ = new Airport("YYZ", 0.0, 0.0);
        Airport HND = new Airport("HND", 0.0, 0.0);
        Airport LHR = new Airport("LHR", 0.0, 0.0);
        Airport PVG = new Airport("PVG", 0.0, 0.0);
        Airport PEK = new Airport("PEK", 0.0, 0.0);
        
        airports.add(ATL);
        airports.add(ORD);
        airports.add(YYZ);
        airports.add(HND);
        airports.add(LHR);
        airports.add(PVG);
        airports.add(PEK);
        
        List<Flight> flights = new LinkedList<Flight>();
        flights.add(new Flight(ATL,LHR,1029));
        flights.add(new Flight(ATL,ORD,94));
        flights.add(new Flight(ATL,YYZ,263));
        flights.add(new Flight(HND,LHR,635));
        flights.add(new Flight(HND,ORD,935));
        flights.add(new Flight(HND,PVG,443));
        flights.add(new Flight(LHR,ATL,796));
        flights.add(new Flight(LHR,PVG,530));
        flights.add(new Flight(LHR,YYZ,599));
        flights.add(new Flight(ORD,ATL,109));
        flights.add(new Flight(ORD,HND,971));
        flights.add(new Flight(ORD,PEK,748));
        flights.add(new Flight(PEK,HND,300));
        flights.add(new Flight(PEK,LHR,324));
        flights.add(new Flight(PEK,PVG,230));
        flights.add(new Flight(PEK,YYZ,803));
        flights.add(new Flight(PVG,PEK,230));
        flights.add(new Flight(YYZ,ATL,265));
        flights.add(new Flight(YYZ,LHR,567));
        flights.add(new Flight(YYZ,ORD,172));
        
        
        FlightShopper shopper = new FlightShopper(airports, flights);
        
        Itinerary i = shopper.price(YYZ, ORD);
        System.out.println(i.toString());
    
        Itinerary j = shopper.price(YYZ, PEK);
        System.out.println(j.toString());
    }
    
}

