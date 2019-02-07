package ca.uwo.eng.se2205.lab7.graphs;

import java.util.*;

/**
 * Created by jacob on 2017-04-10.
 */
public class UndirectedGraph <V,E> implements Graph<V,E> {
    
    private HashSet<Vertex<V,E>> vertices;
    private ArrayList<Edge<E,V>> edges;
    
    public UndirectedGraph(){
        vertices= new HashSet<>();
        edges= new ArrayList<>();
    }
    
    private class UndirectedVertex<V, E> implements Vertex<V, E>{
        V elem;
        HashSet<Edge<E,V>> edgesOfVertex;
        
        UndirectedVertex(){
            this.elem = null;
            edgesOfVertex = new HashSet<>();
        }
        
        UndirectedVertex(V element){
            elem = element;
            edgesOfVertex = new HashSet<>();
        }
        /**
         * Get the owning {@link Graph} of this {@link Edge}.
         *
         * @return Non-{@code null} graph instance
         */
        @Override
        public Graph<V, E> graph() {
            return (Graph<V, E>) UndirectedGraph.this;
        }
        
        /**
         * Returns the stored element
         *
         * @return Non-{@code null} element stored
         */
        @Override
        public V getElement() {
            if(elem==null){
                throw new NullPointerException();
            }
            return elem;
        }
        
        /**
         * Set the element in the Vertex
         *
         * @param element Non-{@code null} element
         * @return The previous element
         */
        @Override
        public V setElement(V element) {
            if(element==null){
                throw new NullPointerException();
            }
            V temp= elem;
            elem=element;
            return temp;
        }
        
        void addEdge(Edge<E, V> e){
            edgesOfVertex.add(e);
        }
        
        /**
         * Gets all of the edges of a {@link Vertex}
         *
         * @return Non-{@code null} collection of {@link Edge}s, but possibly empty
         * @throws ClassCastException if the {@link Vertex} implementation is not correct
         */
        @Override
        public Collection<? extends Edge<E, V>> incidentEdges() {
            return new AbstractSet<Edge<E, V>>() {
                @Override
                public Iterator<Edge<E, V>> iterator() {
                    return new Iterator<Edge<E,V>>() {
                        Iterator<Edge<E, V>> iter1= edgesOfVertex.iterator();
                        Edge<E, V> temp;
                        
                        @Override
                        public boolean hasNext() {
                            return iter1.hasNext();
                        }
                        
                        @Override
                        public Edge<E, V> next() {
                            temp= iter1.next();
                            return temp;
                        }
                        
                        @Override
                        public void remove() {
                            
                            Edge<E,V> removedEdge= temp;
                            edgesOfVertex.remove(removedEdge);
                            removedEdge.v().incidentEdges().remove(removedEdge);
                        }
                    };
                }
                @Override
                public int size() {
                    return edgesOfVertex.size();
                }
            };
        }
        public int hashCode(){
            int out= elem.hashCode();
            for(Edge<E, V> e: edgesOfVertex){
                out= 31*out + e.hashCode();
            }
            return out;
        }
    }
    
    private class UndirectedEdge<E, V> implements Edge<E, V>{
        E weight;
        UndirectedVertex<V, E> u, v;
        
        public UndirectedEdge(UndirectedVertex<V,E> v1, UndirectedVertex<V,E> v2, E weight){
            this.weight= weight;
            u=v1;
            v= v2;
            u.addEdge(this);
            v.addEdge(this);
        }
        /**
         * Get the owning {@link Graph} of this {@link Edge}.
         *
         * @return Non-{@code null} graph instance
         */
        @Override
        public Graph<V, E> graph() {
            return null;
        }
        
        /**
         * Get the weight
         *
         * @return the weight
         */
        @Override
        public E getWeight() {
            return weight;
        }
        
        /**
         * Set the weight
         *
         * @param weight New weight
         * @throws UnsupportedOperationException if immutable
         */
        @Override
        public void setWeight( E weight) {
            this.weight= weight;
        }
        
        /**
         * Get {@code u}, the first {@link Vertex}. If the edge is {@link Undirected} the order does
         * not matter.
         *
         * @return First {@code Vertex}
         */
        @Override
        public Vertex<V, E> u() {
            return u;
        }
        
        /**
         * Get {@code v}, the second {@link Vertex}. If the edge is {@link Undirected} the order does
         * not matter.
         *
         * @return Second {@code Vertex}
         */
        @Override
        public Vertex<V, E> v() {
            return v;
        }
        
        /**
         * Get the {@link Vertex} opposite the passed {@link Vertex}.
         *
         * @param vertex Vertex within the edge
         * @return Opposite vertex to the passed parameter
         * @throws IllegalArgumentException if {@code v} is not attached to the {@code Edge}
         */
        @Override
        public Vertex<V, E> opposite(Vertex<V, E> vertex) {
            if(vertex==v){
                return u;
            }
            else if(vertex==u){
                return v;
            }
            throw new IllegalArgumentException("vertex is not attached to this edge");
        }
        
        /**
         * Returns {@code true} if the vertex is in this edge
         *
         * @param vertex The vertex to check against
         * @return
         * @throws NullPointerException if vertex is {@code null}
         */
        @Override
        public boolean contains(Vertex<V, E> vertex) {
            if(vertex==null){
                throw new NullPointerException("vertex cannot be null");
            }
            else return (vertex==u || vertex==v);
        }
        
        public int hashCode(){
            int out= u.getElement().hashCode();
            out = 31*out + v.getElement().hashCode();
            out = 31*out + weight.hashCode();
            return out;
        }
    }
    
    /**
     * Returns a collection of {@link Vertex} that works with the
     * {@code Graph}. Any change to the {@link Collection} will affect
     * the {@code Graph} itself.
     *
     * @return {@code Graph}-backed {@code Collection}
     */
    @Override
    public Collection<? extends Vertex<V,E>> vertices() {
        return new AbstractSet<Vertex<V, E>>() {
            /**
             * Returns an iterator over the elements contained in this collection.
             *
             * @return an iterator over the elements contained in this collection
             */
            @Override
            public Iterator<Vertex<V,E>> iterator() {
                return new Iterator<Vertex<V,E>>() {
                    Iterator<Vertex<V, E>> newIter= vertices.iterator();
                    @Override
                    public boolean hasNext() {
                        return newIter.hasNext();
                    }
                    
                    @Override
                    public Vertex<V, E> next() {
                        return newIter.next();
                    }
                    
                    @Override
                    public void remove(){
                        Vertex<V,E> deletedVertex= newIter.next();
                        vertices.remove(deletedVertex);
                        for(Edge<E,V> e: deletedVertex.incidentEdges()){
                            e.opposite(deletedVertex).incidentEdges().remove(e);
                            edges.remove(e);
                        }
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
    
    /**
     * Creates a new {@link Vertex} instance adding it to the {@code Graph}
     *
     * @param element The stored element
     * @return New non-{@code null} {@code Vertex}
     */
    @Override
    public Vertex<V, E> newVertex(V element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }
        UndirectedVertex<V,E> newVertex = new UndirectedVertex<>(element);
        for(Vertex<V, E> vert: vertices){
            if(vert.getElement().toString().equals(element.toString())){
                return vert;
            }
        }
        vertices.add(newVertex);
        return newVertex;
    }
    
    /**
     * Gets a collection of {@link Edge}s that is backed by the
     * {@code Graph}. Any change to the {@code Collection} will affect the
     * {@code Graph} itself.
     *
     * @return {@code Graph}-backed {@code Collection}
     */
    @Override
    public Collection<? extends Edge<E, V>> edges() {
        return new AbstractSet<Edge<E, V>>() {
            @Override
            public Iterator<Edge<E, V>> iterator() {
                return new Iterator<Edge<E, V>>() {
                    int counter=0;
                    @Override
                    public boolean hasNext() {
                        return counter<edges.size();
                    }
                    
                    @Override
                    public Edge<E, V> next() {
                        counter++;
                        return edges.get(counter-1);
                    }
                    
                    @Override
                    public void remove(){
                        counter--;
                        Edge<E,V> removedEdge= edges.remove(counter);
                        Vertex<V,E> out= removedEdge.u();
                        Vertex<V,E> in= removedEdge.v();
                        in.incidentEdges().remove(removedEdge);
                        out.incidentEdges().remove(removedEdge);
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
        };
    }
    
    /**
     * Create a new {@link Edge} instance or returns the existing {@code Edge}
     *
     * @param u
     * @param v
     * @param weight
     * @return New non-{@code null} {@code Edge.Weighted}
     * @throws ClassCastException if the {@link Vertex} implementation is not correct
     */
    @Override
    public Edge<E, V> newEdge(Vertex<V, E> u, Vertex<V, E> v, E weight) {
        if(u==null || v==null){
            throw new NullPointerException("Edge cannot be connected to null vertex");
        }
        else if (weight == null) {
            throw new NullPointerException("Weight cannot be null");
        }
        UndirectedEdge<E,V> newEdge = new UndirectedEdge<E,V>((UndirectedVertex<V,E>)u,(UndirectedVertex<V,E>)v,weight);
        edges.add(newEdge);
        return newEdge;
    }
    
    /**
     * Returns an edge between {@param u} and {@param v}. If the {@code Graph}
     * is Directed the order matters.
     *
     * @param u First {@code Vertex}
     * @param v Second {@code Vertex}
     * @return Edge between two {@link Vertex}, {@code null} if it doesn't exist
     * @throws ClassCastException if the {@link Vertex} implementation is not correct
     */
    //@Nullable
    @Override
    public Edge<E, V> getEdge(Vertex<V, E> u, Vertex<V, E> v) {
        if (v== null || u == null) {
            throw new NullPointerException("Vertex cannot be null");
        }
        UndirectedVertex<V, E> check= (UndirectedVertex<V, E>)u;
        for(Edge<E, V> e: check.incidentEdges()){
            if(e.opposite(check)==v){
                return e;
            }
        }
        return null;
    }
    
    /**
     * Gets the edges entering a {@link Vertex}
     *
     * @param v
     * @return Non-{@code null} collection of {@link Edge}s, but possibly empty
     * @throws ClassCastException if the {@link Vertex} implementation is not correct
     */
    @Override
    public Collection<? extends Edge<E, V>> incomingEdges(Vertex v) {
        if(v == null) {
            throw new NullPointerException("Vertex cannot be null");
        }
        return v.incidentEdges();
    }
    
    /**
     * Gets the edges exiting a {@link Vertex}
     *
     * @param v
     * @return Non-{@code null} collection of {@link Edge}s, but possibly empty
     * @throws ClassCastException if the {@link Vertex} implementation is not correct
     */
    @Override
    public Collection<? extends Edge<E, V>> outgoingEdges(Vertex v) {
        if(v == null) {
            throw new NullPointerException("Vertex cannot be null");
        }
        return v.incidentEdges();
    }
    
    /**
     * Gets all of the edges of a {@link Vertex}
     *
     * @param v
     * @return Non-{@code null} collection of {@link Edge}s, but possibly empty
     * @throws ClassCastException if the {@link Vertex} implementation is not correct
     */
    @Override
    public Collection<? extends Edge<E, V>> incidentEdges(Vertex<V, E> v) {
        
        if(v == null) {
            throw new NullPointerException("Vertex cannot be null");
        }
        return v.incidentEdges();
    }
}
