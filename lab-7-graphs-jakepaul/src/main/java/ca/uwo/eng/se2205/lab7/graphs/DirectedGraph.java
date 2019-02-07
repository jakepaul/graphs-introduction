package ca.uwo.eng.se2205.lab7.graphs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


/**
 * Created by jacob on 2017-04-04.
 */
public class DirectedGraph<V,E> implements Graph<V,E> {
    
    private List<Vertex<V,E>> vertices;
    //private HashSet<Vertex<V,E>> vertices;
    //private ArrayList<Edge<E,V>> edges;
   
   
    public abstract class MyList<T> extends AbstractList<T> {
        @Override
        public boolean add (T element){
            throw new UnsupportedOperationException();
        }
        @Override
        public T set (int index, T element){
            throw new UnsupportedOperationException();
        }
    }
    
    
    public DirectedGraph(){
    
        this.vertices = new LinkedList<Vertex<V,E>>();
        //this.vertices = new HashSet<Vertex<V,E>>();
        //this.edges = new ArrayList<>();
        
    }
    
    @Override
    public Collection<? extends Vertex<V, E>> vertices() {
        return new AbstractSet<Vertex<V, E>>() {
            /**
             * Returns an iterator over the elements contained in this collection.
             *
             * @return an iterator over the elements contained in this collection
             */
            @Override
            public Iterator<Vertex<V,E>> iterator() {
                return new Iterator<Vertex<V,E>>() {
                    
                    Vertex<V,E> v;
                    
                    Iterator<Vertex<V, E>> newIt = vertices.iterator();
                    
                    
                    @Override
                    public boolean hasNext() {
                        return newIt.hasNext();
                    }
                
                    @Override
                    public Vertex<V, E> next() {
                        v = newIt.next();
                        return v;
                    }
                
                    @Override
                    public void remove(){
                        //Vertex<V,E> deletedVertex= newIt.next();
                        //vertices.remove(deletedVertex);
                        if (v != null) {
                            for(Edge<E,V> e: v.incomingEdges()){
                                ((DirectedVertex<V,E>)e.u()).removeEdge(e);
                            }
                        }
                        newIt.remove();
                    }
                };
            }
        
            @Override
            public int size() {
                return vertices.size();
            }
        
            @Override
            public boolean add(Vertex<V,E> v){
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public Vertex<V, E> newVertex( @Nonnull V element) {
        
        DirectedVertex<V,E> newVertex = new DirectedVertex<V,E>(element, this);
        //if element is already in there return it
        for(Vertex<V, E> e: vertices){
            if(e.getElement().toString().equals(element.toString())){
                return e;
            }
        }
        //if not add it to the list
        vertices.add(newVertex);
        return newVertex;
    }
    
    @Override
    public Collection<? extends Edge<E, V>> edges() {
    
        List<Edge<E,V>> edges = new LinkedList<Edge<E,V>>();
        for (Vertex<V,E> v : vertices){
            for (Edge<E,V> e : v.outgoingEdges()) {
                edges.add(e);
            }
        }
        
        return new AbstractSet<Edge<E, V>>() {
            @Override
            public Iterator<Edge<E, V>> iterator() {
                return new Iterator<Edge<E, V>>() {
                    
                    Iterator<Edge<E,V>> newItr = edges.iterator();
                    
                    //int counter=0;
                    Edge<E,V> edge;
                    @Override
                    public boolean hasNext() {
                        //return counter<edges.size();
                        return newItr.hasNext();
                    }
                
                    @Override
                    public Edge<E, V> next() {
                        //counter++;
                        edge = newItr.next();
                        //return edges.get(counter-1);
                        return edge;
                    }
                
                    @Override
                    public void remove(){
                        newItr.remove();
                        for (Vertex<V,E> v : vertices){
                            if (v.outgoingEdges().contains(edge)){
                                ((DirectedVertex<V,E>)v).removeEdge(edge);
                            }
                        }
                    }
                };
            }
        
            @Override
            public int size() {
                return edges.size();
            }
        
            @Override
            public boolean add(Edge<E,V> e){
                throw new UnsupportedOperationException();
            }
    
            @Override
            public boolean remove(Object o) {
                for (Vertex<V,E> v : vertices){
                    if (v.outgoingEdges().contains(o)){
                        return ((DirectedVertex<V,E>)v).removeEdge((Edge<E,V>)o);
                    }
                }
                return false;
            }
        };
    }
    
    @Override
    public Edge<E, V> newEdge(Vertex<V, E> u, Vertex<V, E> v, E weight) {
        if(u==null || v==null){
            throw new NullPointerException("Vertex cannot be null");
        }
        else if (weight == null) {
            throw new NullPointerException("Weight cannot be null");
        }
        
        return ((DirectedVertex<V,E>)u).addEdge(weight, v);
    }
    
    @Nullable
    @Override
    public Edge<E, V> getEdge(Vertex<V, E> u, Vertex<V, E> v) {
        if (u == null || v == null) {
            throw new NullPointerException("Edge cannot be connected to null vertices");
        }
        
        return ((DirectedVertex<V,E>)u).getEdge(v);
    }
    
    @Override
    public Collection<? extends Edge<E, V>> incomingEdges(Vertex<V, E> v) {
        if (v == null) {
            throw new NullPointerException("Vertex cannot be null");
        }
        LinkedList<Edge<E, V>> incoming = new LinkedList<>();
        for (Vertex<V,E> u : vertices){
            
            if (getEdge(u,v) != null){
                incoming.add(getEdge(u,v));
            }
            //you stopped here
        }
        return incoming;
    }
    
    @Override
    public Collection<? extends Edge<E, V>> outgoingEdges(Vertex<V, E> v) {
        if (v == null) {
            throw new NullPointerException("Vertex cannot be null");
        }
        return v.outgoingEdges();
    }
    
    @Override
    public Collection<? extends Edge<E, V>> incidentEdges(Vertex<V, E> v) {
        if (v == null) {
            throw new NullPointerException("Vertex cannot be null");
        }
        return v.incidentEdges();
    }
    
    //change to private
    private static class DirectedEdge<E,V> implements Edge.Directed<E,V> {
        
        E weight;
        Vertex<V,E> start;
        Vertex<V,E> end;
        Graph<V,E> parentGraph;
        
        
        public DirectedEdge(Vertex<V,E> start, Vertex<V,E> end, Graph<V,E> parentGraph, E weight){
            this.start = start;
            this.end = end;
            this.weight = weight;
            this.parentGraph = parentGraph;
        }
        
        @Override
        public Graph<V, E> graph() {
            return parentGraph;
        }
    
        @Override
        public E getWeight() {
            return weight;
        }
    
        @Override
        public void setWeight(@Nullable E weight) {
            this.weight = weight;
        }
    
        @Override
        public Vertex<V, E> u() {
            return start;
        }
    
        @Override
        public Vertex<V, E> v() {
            return end;
        }
    
        @Override
        public Vertex<V, E> opposite(Vertex<V, E> vertex) {
            if (vertex != start && vertex != end){
                throw new IllegalArgumentException("this vertex does not exist");
            }
            else if (vertex == start){
                return end;
            }else{
                return start;
            }
        }
    
        @Override
        public boolean contains(Vertex<V, E> vertex) {
            if (vertex == null){
                throw new NullPointerException("vertex does not exist");
            }
            else{
               return (vertex == start || vertex == end);
            }
        }
    
        public int hashCode(){
            int out= weight.hashCode();
            out= 31*out + start.getElement().hashCode();
        
            return 31*out +end.getElement().hashCode();
        }
    }
    
    // change back to private
    private static class DirectedVertex<V,E> implements Vertex<V,E>{
        
        List<Edge<E,V>> edges;
        //HashSet<Edge<E,V>> edges;
        V element;
        Graph<V,E> parentGraph;
        
        public DirectedVertex(V elem, Graph<V,E> parent){
            this.edges = new LinkedList<Edge<E,V>>();
            //this.edges = new HashSet<>();
            //should we make hashset with incoming edges and outgoing edges not just edges?
            
            
            
            this.element = elem;
            this.parentGraph = parent;
        }
        
        @Override
        public Graph<V, E> graph() {
            return parentGraph;
        }
    
        @Override
        public V getElement() {
            return element;
        }
    
        @Override
        public V setElement(V element) {
            if (element==null){
                throw new NullPointerException("element can not be null");
            }
            V temp = this.element;
            this.element = element;
            return temp;
        }
    
        @Override
        public Collection<? extends Edge<E, V>> incomingEdges() {
            

            return this.parentGraph.incomingEdges(this);
            
          
        }
        
    
        @Override
        public Collection<? extends Edge<E, V>> outgoingEdges() {
            return edges;
            
        }
    
        @Override
        public Collection<? extends Edge<E, V>> incidentEdges() {
            List<Edge<E,V>> incidentList = new LinkedList<Edge<E, V>>();
            incidentList.addAll(incomingEdges());
            incidentList.addAll(outgoingEdges());
            return incidentList;
           
        }
        
        public int hashCode(){
            int out=element.hashCode();
            for (Edge<E,V> e: incidentEdges()) {
                out= 31*out + e.hashCode();
            }
            return out;
        }
    
        public boolean equals(Object o){
            return this.hashCode()==o.hashCode();
        }
        
        //public void addEdge(Edge<E,V> e){edges.add(e);}
        public Edge<E,V> addEdge(E weight, Vertex<V,E> end){
            Edge<E,V> e = new DirectedEdge<E,V>(this, end, parentGraph, weight);
            edges.add(e);
            return e;
        }
        
        public boolean removeEdge(Edge<E,V> edge){
            return edges.remove(edge);
        }
        
        public Edge<E,V> getEdge(Vertex <V, E> v){
            for (Edge<E,V> e : edges) {
                if (e.v() == v){
                    return e;
                }
            }
            return null;
        }
    }
    
    
    
}