/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giogio
 */
public class Graph {
    List<Vertex> vertices;
    
    public Graph(Mesh m){
        vertices = new ArrayList<>();
        makeGraph(m);
    }
    
    public void markSafeTriangles(ArrayList<Vertex> vertices, float radius){
    
        for(Vertex v: vertices){
            for(Vertex v2: v.getNeighbours()){ 
                if(v2.isUnderMountain())
                    continue;
                if(v.getDistanceFromOrigin()-v2.getDistanceFromOrigin() >= 1.5f){
                    v2.setUnderMountain(true);
                }
            }
        }
         
    }
    
  
    public void makeGraph(Mesh m){
      int l = m.getTriangleCount();
      System.out.println("Triangles num: "+l);
      Triangle[] triangleList = new Triangle[l];
      for(int i=0;i<l;i++){
          triangleList[i] = new Triangle();
          m.getTriangle(i, triangleList[i]);
      }
      
     this.vertices = new ArrayList();

       for(int i=0;i<triangleList.length;i++){
           System.out.println("Triangle "+i);
            Triangle current = triangleList[i];
            Vertex currentVertex;
            if(notExist(current)){
                
                 currentVertex = new Vertex(current.getCenter());
                 vertices.add(currentVertex);
                 
            }else{
                currentVertex = getVertexFromTriangle(current);
            }
           
            for(int j=i+1;j<triangleList.length;j++){
                Triangle secondTriangle = triangleList[j];
                Vertex secondVertex;
                if(notExist(secondTriangle)){
                    secondVertex = new Vertex(secondTriangle.getCenter());
                    vertices.add(secondVertex);
                 
                }else{
                    secondVertex = getVertexFromTriangle(secondTriangle);
                }
                 
                if(isNeighbour(current,secondTriangle)){
                    currentVertex.addNeighbour(secondVertex);
                    secondVertex.addNeighbour(currentVertex);

                }
                
            }
            
        }
             System.out.println("HERE2");
    }
    
    
    public boolean notExist(Triangle current){
     
        //System.out.println(current.get1()+","+current.get2()+","+current.get3());
        
        for(Vertex v: vertices){
            if(v.getPosition().x == current.getCenter().x &&
               v.getPosition().y == current.getCenter().y &&
               v.getPosition().z == current.getCenter().z)
                    
                return false;
        }
        
        return true;
    }   
    
    public Vertex getVertexFromTriangle(Triangle current){
        for(Vertex v: vertices){
            
             if(v.getPosition().x == current.getCenter().x &&
                v.getPosition().y == current.getCenter().y &&
                v.getPosition().z == current.getCenter().z)
                 return v;
        }
        return null;
   }
    
     private boolean isNeighbour(Triangle first, Triangle second){
        if(first.get1().equals(second.get1()) || first.get1().equals(second.get2()) || first.get1().equals(second.get3())
                || first.get2().equals(second.get1()) || first.get2().equals(second.get2()) || first.get2().equals(second.get3())
                    || first.get3().equals(second.get1()) || first.get3().equals(second.get2()) || first.get3().equals(second.get3())){
            return true;
        }
        return false;         
    }
    
     public ArrayList<Vertex> getVerticesList(){
         return (ArrayList<Vertex>) vertices;
     }
}
