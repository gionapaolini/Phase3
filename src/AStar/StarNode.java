/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AStar;

import com.jme3.math.Vector3f;

/**
 *
 * @author giogio
 */
public class StarNode {
    Vector3f position;
    StarNode cameFrom;
    int index;
    float g;
    float h;
    float f;

    public StarNode(Vector3f position, int index) {
        this.position = position;
        this.index = index;
    }

    
    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setG(float g) {
        this.g = g;
    }

    public void setH(float h) {
        this.h = h;
    }

    public void setF() {
        f = g+h;
    }

    public Vector3f getPosition() {
        return position;
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

    public StarNode getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(StarNode cameFrom) {
        this.cameFrom = cameFrom;
    }
    
    
    
     
    
}
