/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author giogio
 */
public class Agent {
    
    protected Vector3f position;
    protected Vector3f velocity;
    protected float maxVelocity;
    protected float wanderAngle;
    protected final int SLOWING_RADIOUS = 10;
    protected final int CIRCLE_DISTANCE = 5;
    protected final int CIRCLE_RADIUS = 5;
    protected final int ANGLE_CHANGE = 1;

    Vector3f wanderTarget;
    Geometry body;
    
    public Agent(){
        position = new Vector3f();
        velocity = new Vector3f(1,0,1);
        wanderAngle = 40;
        maxVelocity = 10;
    }
    public void move(float ftp, boolean isPlanet, Geometry planet){
        velocity = truncate(velocity);
        position = position.add(velocity.mult(ftp));
        body.setLocalTranslation(position);
        if(!isPlanet)
            return;
       
        Ray r = new Ray(planet.getLocalTranslation(),position.subtract(planet.getLocalTranslation()).normalize());
      
        CollisionResults results = new CollisionResults();
        planet.collideWith(r, results);
        if(results.size()<=0)
            return;
        
        Vector3f contactPoint = results.getFarthestCollision().getContactPoint();
        Vector3f normalPoint = results.getFarthestCollision().getContactNormal();
        
        reposition(contactPoint, normalPoint);
        
        
    }
    
    public Vector3f seekForce(Vector3f target){
        Vector3f desidered_Vel = target.subtract(position).normalize().mult(maxVelocity);
        return desidered_Vel.subtract(velocity);
        
    }
    
    public Vector3f fleeForce(Vector3f target){
        Vector3f desidered_Vel = position.subtract(target).normalize().mult(maxVelocity);
        return desidered_Vel.subtract(velocity);
    }
    
    public Vector3f arrivalForce(Vector3f target){
        // Calculate the desired velocity
        Vector3f desired_velocity = target.subtract(position);
        float distance = desired_velocity.length();

        // Check the distance to detect whether the character
        // is inside the slowing area
        if (distance < SLOWING_RADIOUS) {
            // Inside the slowing area
            desired_velocity =desired_velocity.normalize().mult(maxVelocity * (distance / SLOWING_RADIOUS));
        } else {
            // Outside the slowing area.
           desired_velocity =desired_velocity.normalize().mult(maxVelocity);
        }

        // Set the steering based on this
        return desired_velocity.subtract(velocity);
    }
    
    public Vector3f wanderForce(){
        Vector3f circleCenter = velocity.clone();
        circleCenter = circleCenter.normalize();
        circleCenter = circleCenter.mult(CIRCLE_DISTANCE);
        
        
        
        Vector3f displacement = new Vector3f(0,0,-1);
        displacement = displacement.mult(CIRCLE_RADIUS);

        // Randomly change the vector direction
        // by making it change its current angle
        setAngle(displacement, velocity, wanderAngle);
        

        //
        // Change wanderAngle just a bit, so it
        // won't have the same value in the
        // next game frame.
        wanderAngle += (Math.random() * ANGLE_CHANGE) - (ANGLE_CHANGE * .5);    

        return circleCenter.add(displacement);
   
    
    
    }
    
    public void setAngle(Vector3f vector, Vector3f direction, float value){
        float len = vector.length();
        float yAngle = getAngle(direction, new Vector3f(0,1,0));
        
  
       vector.x = (float) (Math.cos(value) * len);
       vector.z = (float) (Math.sin(value) * len); 
       vector.y = 0;
         /*
        vector.x = (float) (Math.cos(value)*Math.cos(yAngle))*len;
        vector.z = (float) (Math.sin(value)*Math.cos(yAngle))*len;
        vector.y = (float) Math.sin(yAngle)*len;
        */
       
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(float maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public float getWanderAngle() {
        return wanderAngle;
    }

    public void setWanderAngle(float wanderAngle) {
        this.wanderAngle = wanderAngle;
    }

    public Vector3f getWanderTarget() {
        return wanderTarget;
    }

    public void setWanderTarget(Vector3f wanderTarget) {
        this.wanderTarget = wanderTarget;
    }

    public Vector3f getPosition() {
        return position;
    }

    
    
    public Geometry getBody() {
        return body;
    }

    public void setBody(Geometry body) {
        this.body = body;
    }
    
    public void setPosition(Vector3f pos){
        body.setLocalTranslation(pos);
        position = pos;
    }
    
    public void applyForce(Vector3f force){
        velocity= velocity.add(force);
    }

    private Vector3f truncate(Vector3f velocity) {
        if(velocity.length()>maxVelocity)
            velocity = velocity.normalize().mult(maxVelocity);
        return velocity;
    }
    
    private float getAngle(Vector3f v1, Vector3f v2){
        float dot = v1.dot(v2);
        float len1 = v1.length();
        float len2 = v2.length();
        
        float angle = (float) Math.acos(dot/(len1*len2));
        return angle;
    }

    private void reposition(Vector3f contactPoint, Vector3f normalPoint){
         
        float length = contactPoint.length();
        Vector3f newLocation = contactPoint.normalize().mult(length+2);
        setPosition(newLocation);
       
        Vector3f rotation = getProjectionOntoPlane(normalPoint,velocity);
        Quaternion rotationQuat = new Quaternion();
        rotationQuat.lookAt(rotation,normalPoint);
        velocity = rotationQuat.getRotationColumn(2);
        
    }
    
     private Vector3f getProjectionOntoPlane(Vector3f n, Vector3f v){
        
    
        Vector3f projection = n.cross(v.cross(n));
              
        return projection;
    }
    
    
}