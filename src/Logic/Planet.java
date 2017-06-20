/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author giogio
 */
public class Planet {
    Geometry planet;
    Geometry navMesh;
    Settings settings;    
    float radius;
    
    public Planet(Settings setting){
        settings = setting;
        planet = new Geometry("Planet");
        PlanetMeshGen planetMeshGen = new PlanetMeshGen();
        planetMeshGen.generateHeightmap(setting);
        planet.setMesh(planetMeshGen.generateMesh(setting));
        radius = setting.getRadius();

    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
    
 
    

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
    
    
    public Geometry getPlanet() {
        return planet;
    }

    public Geometry getNavMesh() {
        return navMesh;
    }

    public void setNavMesh(Geometry navMesh) {
        this.navMesh = navMesh;
    }
    
    
    
}
