/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

/**
 *
 * @author giogio
 */
public class Settings {
   int typeOfAlgorithm;
   
   int radius, heightmapwidth, iterations, numIslands, seed;
   float islandRadius, displacement, smoothing, amplitude, x_scale, y_scale, z_scale;
   
   int n_escapers, n_pursuers, n_pursuer_powers, n_pursuers_malus, n_escapers_powers, n_escapers_malus;

   public Settings(){
       typeOfAlgorithm = 1;
       radius = 50;  //10 to 50 
       heightmapwidth = 600; //300 to 800
       iterations = 25000; // 15000 to 50000
       numIslands = 35;   //1 to 35
       seed = 19380400;   // 100000 to 40000000
       islandRadius=90;  // 20 to 90
       displacement = 1f;  // 0 to 1 maybe -1
       smoothing = .3f;     // 0 to 1
       amplitude = 10.0f;   // 1 to 25
       x_scale = 100.0f;    // 1 to 100
       y_scale = 100.0f;    // 1 to 100
       z_scale = 100.0f;    // 1 to 100
       n_escapers=10;
       n_pursuers=10;
       
   }
   
    public int getTypeOfAlgorithm() {
        return typeOfAlgorithm;
    }

    public void setTypeOfAlgorithm(int typeOfAlgorithm) {
        this.typeOfAlgorithm = typeOfAlgorithm;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getHeightmapwidth() {
        return heightmapwidth;
    }

    public void setHeightmapwidth(int heightmapwidth) {
        this.heightmapwidth = heightmapwidth;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getNumIslands() {
        return numIslands;
    }

    public void setNumIslands(int numIslands) {
        this.numIslands = numIslands;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public float getIslandRadius() {
        return islandRadius;
    }

    public void setIslandRadius(float islandRadius) {
        this.islandRadius = islandRadius;
    }

    public float getDisplacement() {
        return displacement;
    }

    public void setDisplacement(float displacement) {
        this.displacement = displacement;
    }

    public float getSmoothing() {
        return smoothing;
    }

    public void setSmoothing(float smoothing) {
        this.smoothing = smoothing;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public float getX_scale() {
        return x_scale;
    }

    public void setX_scale(float x_scale) {
        this.x_scale = x_scale;
    }

    public float getY_scale() {
        return y_scale;
    }

    public void setY_scale(float y_scale) {
        this.y_scale = y_scale;
    }

    public float getZ_scale() {
        return z_scale;
    }

    public void setZ_scale(float z_scale) {
        this.z_scale = z_scale;
    }

    public int getN_escapers() {
        return n_escapers;
    }

    public void setN_escapers(int n_escapers) {
        this.n_escapers = n_escapers;
    }

    public int getN_pursuers() {
        return n_pursuers;
    }

    public void setN_pursuers(int n_pursuers) {
        this.n_pursuers = n_pursuers;
    }

    public int getN_pursuer_powers() {
        return n_pursuer_powers;
    }

    public void setN_pursuer_powers(int n_pursuer_powers) {
        this.n_pursuer_powers = n_pursuer_powers;
    }

    public int getN_pursuers_malus() {
        return n_pursuers_malus;
    }

    public void setN_pursuers_malus(int n_pursuers_malus) {
        this.n_pursuers_malus = n_pursuers_malus;
    }

    public int getN_escapers_powers() {
        return n_escapers_powers;
    }

    public void setN_escapers_powers(int n_escapers_powers) {
        this.n_escapers_powers = n_escapers_powers;
    }

    public int getN_escapers_malus() {
        return n_escapers_malus;
    }

    public void setN_escapers_malus(int n_escapers_malus) {
        this.n_escapers_malus = n_escapers_malus;
    }
   
    public void printSettings(){
        System.out.println("N_Pursuers: "+n_pursuers);
        System.out.println("N_Escapers: "+n_escapers);
        System.out.println("Radius: "+radius);
        System.out.println("Heightmapwidth: "+heightmapwidth);
        System.out.println("Seed: "+seed);

        if(typeOfAlgorithm==1){
            System.out.println("iterations: "+iterations);
            System.out.println("numIslands: "+numIslands);
            System.out.println("islandRadius: "+islandRadius);
            System.out.println("displacement: "+displacement);
            System.out.println("smoothing: "+smoothing);
        
        }else if(typeOfAlgorithm==2){
            System.out.println("amplitude: "+amplitude);
            System.out.println("x_scale: "+x_scale);
            System.out.println("y_scale: "+y_scale);
            System.out.println("z_scale: "+z_scale);
    
        }
    }
    
}
