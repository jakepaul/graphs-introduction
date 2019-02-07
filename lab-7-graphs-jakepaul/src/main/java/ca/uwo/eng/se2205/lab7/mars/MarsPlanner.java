package ca.uwo.eng.se2205.lab7.mars;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Calculates the best location to land a rover.
 */
@ParametersAreNonnullByDefault
public class MarsPlanner {
    
    int [][] marsSurface;
    List<int[]> sitesToVisit;
    /**
     * Initializes the planner with the topology of the land and the landing sites.
     *
     * @param topology Two dimensional set of heights
     * @param sites {@code List} of coordinates that must be visited
     */
    public MarsPlanner(int[][] topology, List<int[]> sites) {
        // DO NOT CHANGE THE METHOD SIGNATURE
        marsSurface=topology;
        sitesToVisit= sites;
    }
    
    /**
     * Calculates the best landing spot in the topology.
     *
     * @param fuelAvailable How much fuel is available daily when travelling
     * @return Coordinates for the best landing spot
     */
    public int[] bestLandingSpot(int fuelAvailable) {
        // DO NOT CHANGE THE METHOD SIGNATURE
        if(sitesToVisit.size() == 1){
            return sitesToVisit.get(0);
        }
        int halfFuel= fuelAvailable/2;
        ArrayList<int[]> min= new ArrayList<>();
        for(int i=0; i<marsSurface.length; i++){
            for(int t=0; t<marsSurface[0].length; t++){
                int [][] path= new int[marsSurface.length][marsSurface[0].length];
                for(int m=0; m<marsSurface.length; m++){
                    Arrays.fill(path[m], -10);
                }
                path[i][t]= halfFuel;
                validPath(i, t, halfFuel, path);
                boolean pointCheck= true;
                int totalWeight=0;
                for(int[] array: sitesToVisit){
                    int pointWeight= path[array[1]][array[0]];
                    totalWeight+=pointWeight;
                    if(pointWeight < 0){
                        pointCheck=false;
                        break;
                    }
                }
                if (pointCheck){
                    int[] beingChecked= new int[]{t, i, totalWeight};
                    min.add(beingChecked);
                }
            }
        }
        //System.out.println(min.get(0));
        int[] bestSpot= new int[]{-1,-1};
        int mostFuelLeft=-1;
        for(int[] a: min){
            if(a[2]>mostFuelLeft){
                mostFuelLeft=a[2];
                bestSpot[0]= a[0];
                bestSpot[1]= a[1];
            }
        }
        
        return bestSpot;
    }
    
    private int[][] validPath(int y, int x, int fuelLeft, int[][] path){
        if(fuelLeft<=0){
            return path;
        }
        int posY= y+1;
        if(0<=posY && posY<marsSurface.length){
            int fuelLim= fuelLeft - Math.abs(marsSurface[y][x]-marsSurface[posY][x]);
            if(path[posY][x]< fuelLim) {
                path[posY][x] = fuelLim;
                validPath(posY, x, fuelLim, path);
            }
        }
        int posX= x+1;
        if(0<=posX && posX<marsSurface[0].length){
            int fuelLim= fuelLeft - Math.abs(marsSurface[y][x]-marsSurface[y][posX]);
            if(path[y][posX]< fuelLim){
                path[y][posX]= fuelLim;
                validPath(y, posX, fuelLim, path);
            }
            
        }
        posY= y-1;
        if(0<=posY && posY<marsSurface.length){
            int fuelLim= fuelLeft - Math.abs(marsSurface[y][x]-marsSurface[posY][x]);
            if(path[posY][x]< fuelLim){
                path[posY][x]= fuelLim;
                validPath(posY, x, fuelLim, path);
            }
            
        }
        posX= x-1;
        if(0<=posX && posX<marsSurface[0].length){
            int fuelLim= fuelLeft - Math.abs(marsSurface[y][x]-marsSurface[y][posX]);
            if(path[y][posX]< fuelLim){
                path[y][posX]= fuelLim;
                validPath(y, posX, fuelLim, path);
            }
            
        }
        return path;
    }
}

