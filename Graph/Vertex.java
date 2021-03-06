/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giogio
 */
public class Vertex {
    
    List<Vertex> neighbours;
    Vector3f position;
    float g;
    float h;
    float f;
    float distanceFromOrigin;
    boolean underMountain;
    
    public Vertex(Vector3f pos){
        position = pos;
        distanceFromOrigin = position.subtract(new Vector3f(0,0,0)).length();
        neighbours = new ArrayList<>();
        underMountain = false;
    }

    public boolean isUnderMountain() {
        return underMountain;
    }

    public void setUnderMountain(boolean underMountain) {
        this.underMountain = underMountain;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void addNeighbour(Vertex n){
        neighbours.add(n);
    }

    public List<Vertex> getNeighbours() {
        return neighbours;
    }

    public float getG() {
        return g;
    }

    public float getH() {
        return h;
    }

    public float getF() {
        return f;
    }

    public float getDistanceFromOrigin() {
        return distanceFromOrigin;
    }
    
    
    
    
    
}
