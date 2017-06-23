/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AStar;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;

/**
 *
 * @author giogio
 */
public class AStarAlgorithm {
   
    ArrayList<StarNode> totalPath;
    public ArrayList<StarNode> closedSet;
    ArrayList<StarNode> openSet; 
    Vector3f[] points;
    float[] ratios;
    boolean[][] edges;
    StarNode goal;
    StarNode start;
    
    public AStarAlgorithm(int startIndex, int  goalIndex, Vector3f[] points, float[] ratios, boolean[][] edges){
        openSet = new ArrayList<>();
        totalPath = new ArrayList<>();
        closedSet = new ArrayList<>();
        this.points = points;
        this.ratios = ratios;
        this.edges = edges;
        start = new StarNode(points[startIndex],startIndex);
        goal = new StarNode(points[goalIndex],goalIndex);
        openSet.add(start);

        start.setG(0);
        heuristicEstimate(start);
        this.totalPath = new ArrayList<StarNode>();
        
    }
    
    public ArrayList<StarNode> pathFinding(){
        while(openSet.size()!=0){
            StarNode current = lowestF(openSet);
            if(current.equals(goal)){
                return reconstructPath(current);
            }
            
            openSet.remove(current);
            closedSet.add(current);
            
            
               
            ArrayList<StarNode> neighbors = getNeighbours(current);
            float tGscore;
            for(StarNode neighbor: neighbors){
               
                if(!closedSet.contains(neighbor)){

                    float distance = neighbor.getPosition().subtract(current.getPosition()).length();
                    tGscore = current.getG() + distance;
                    if(!openSet.contains(neighbor)){
                        heuristicEstimate(neighbor);
                        openSet.add(neighbor);
                        neighbor.setCameFrom(current);
                        neighbor.setG(tGscore);
                        
                    }
                    else{
                        if(tGscore <= neighbor.getG()){
                            neighbor.setCameFrom(current);
                            neighbor.setG(tGscore);             
                            heuristicEstimate(neighbor);
                           
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    //add heuristic here? height/distance?
    public void heuristicEstimate(StarNode x){
       
        float ratio = ratios[x.getIndex()]*50;
        float height = x.getPosition().length()*2;
        float distance = x.getPosition().subtract(goal.getPosition()).length();
        
        x.setH(ratio+height+distance);
        x.setF();
        
       
    }
    
    public StarNode lowestF (ArrayList<StarNode> openSet){
        StarNode current = openSet.get(0);
        for(int i=1; i<openSet.size(); i++){
            if(current.getF() > openSet.get(i).getF()){
                current = openSet.get(i);
            }
        }
        return current;
    }
    
    public ArrayList<StarNode> reconstructPath(StarNode current){
        totalPath.add(current);
        while(!current.equals(start)){
            
            current = current.getCameFrom();
            
            totalPath.add(current);            
        }
        ArrayList<StarNode> path = new ArrayList();
      
        for(int i=totalPath.size()-1; i>=0; i--){
            path.add(totalPath.get(i));
        }
        return path;
    }
    
    public ArrayList<StarNode> getNeighbours(StarNode node){
        ArrayList<StarNode> list = new ArrayList();
     
        for(int i=0;i<points.length;i++){
                   
           if(edges[node.getIndex()][i]){

               list.add(getNodeFromIndex(i));
           }
        }
        return list;
    }
    
    public StarNode getNodeFromIndex(int i){
        for(StarNode s: openSet){
            if(s.getIndex()==i)
                return s;
        }
        for(StarNode s: closedSet){
            if(s.getIndex()==i)
                return s;
        }
        if(i==goal.getIndex())
            return goal;
            
        return new StarNode(points[i],i);
    }
    
    
    
}
