package mygame;

import Logic.Agent;
import Logic.Planet;
import Logic.Settings;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    List<Agent> agents;
    Planet planet;
    Sphere sphere, planetSphere;
    Box cube;
    Material red, blue, green,matWireframe, mat;
    Settings settings;
    Node fovs;
    //Setting
    int n_agents = 5;
    boolean onPlanet = true; 
    boolean planetVisible = false;
    boolean meshVisible = true;
    boolean FOVvisible = true;
    
    
    
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(800,400);
        app.setShowSettings(false); // splashscreen
        app.setSettings(settings);
            
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        initializeAppSetting();
        initializeMaterials();
        if(onPlanet)
            initializePlanet();
        else
            initializeTerrain();
        initializeAgents();
        
        if(FOVvisible){
            fovs = new Node();
            rootNode.attachChild(fovs);
        }
        

        
    }
    
    @Override
    public void simpleUpdate(float ftp){
       moveAgents(ftp);
       if(!FOVvisible)
           return;
       
       displayFOV();
    }

    private void initializeAppSetting() {
        setDisplayFps(false);
        setDisplayStatView(false);
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(0,40,140));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(40f);
        
    }

    private void initializeMaterials() {
        settings = new Settings();
        
        sphere = new Sphere(5,5,1);
        cube = new Box(40,0.1f,40);
        
        red = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        red.setColor("Color", ColorRGBA.Red);
        blue = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blue.setColor("Color", ColorRGBA.Blue);
        green = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        green.setColor("Color", ColorRGBA.Green);
        
        matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);
        
        mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseVertexColor", true);
    }
    private void initializeTerrain() {
        Geometry floor = new Geometry("floor",cube);
        floor.setLocalTranslation(Vector3f.ZERO);
        floor.setMaterial(red);
        rootNode.attachChild(floor);
    }

    private void initializeAgents() {
        agents = new ArrayList();
        for(int i=0;i<n_agents;i++){
            Agent wanderer = new Agent();
            wanderer.setBody(new Geometry("Sphere", sphere));
            wanderer.getBody().setMaterial(green);
            
            float r1 = (float) (Math.random()*100);
            if(Math.random()<0.5)
                r1-=200;
            else
                r1+=200;
            float r2 = (float) (Math.random()*100);
            if(Math.random()<0.5)
                r2-=200;
            else
                r2+=200;
            float r3 = (float) (Math.random()*100);
            if(Math.random()<0.5)
                r3-=200;
            else
                r3+=200;
            
            wanderer.setPosition(new Vector3f(r1,r2,r3));
            rootNode.attachChild(wanderer.getBody());
            agents.add(wanderer);
        }
    }
    
    private void moveAgents(float ftp){
        for(Agent wanderer: agents){
            
            wanderer.applyForce(wanderer.wanderForce());
            wanderer.move(ftp, true, planet.getPlanet());
        }
    }

    private void initializePlanet() {
        
        DirectionalLight sun = new DirectionalLight();
        DirectionalLight sun2 = new DirectionalLight();
        DirectionalLight sun3 = new DirectionalLight();
        
        sun.setDirection(new Vector3f(0,1,0));
        sun2.setDirection(new Vector3f(0,0,-1));
        sun3.setDirection(new Vector3f(0,-1,0));
      
        rootNode.addLight(sun);
        rootNode.addLight(sun2);
        rootNode.addLight(sun3);
        
        planet = new Planet(settings);
        planet.getPlanet().setMaterial(mat);
        rootNode.attachChild(planet.getPlanet());
        
        if(!meshVisible)
            return;
        planetSphere = new Sphere(150, 150, settings.getRadius()+1.4f);
        Geometry navMesh = new Geometry("navMesh", planetSphere);
        navMesh.setMaterial(matWireframe);
        planet.setNavMesh(navMesh);
        updateMesh();
    }
    
    
  
    
    
    public void updateMesh() {
          
        Mesh mesh = planet.getNavMesh().getMesh(); 
         
        Mesh target = new Mesh();
        VertexBuffer sourcePos = mesh.getBuffer(Type.Position);
        VertexBuffer sourceNorms = mesh.getBuffer(Type.Normal);

        VertexBuffer targetPos = matchBuffer(sourcePos, target);
        VertexBuffer targetNorms = matchBuffer(sourceNorms, target);

        // Make sure we also have an index and texture buffer that matches
        // ...even though we don't transform them we still need copies of
        // them.  We could just reference them but then our other buffers
       // might get out of sync
        matchBuffer(mesh.getBuffer(Type.Index), target);
        matchBuffer(mesh.getBuffer(Type.TexCoord), target);

        morph(sourcePos, sourceNorms, targetPos, targetNorms);
        target.updateBound();
        Geometry newMesh = new Geometry("new",target);
     //matWireframe.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
       
        newMesh.setMaterial(matWireframe);
        rootNode.detachAllChildren();
        if(planetVisible)
            rootNode.attachChild(planet.getPlanet());
        planet.setNavMesh(newMesh);
        rootNode.attachChild(newMesh);
        
    }
     protected VertexBuffer matchBuffer( VertexBuffer source, Mesh mesh ) {
        if( source == null )
            return null;

        VertexBuffer target = mesh.getBuffer(source.getBufferType());
        if( target == null || target.getData().capacity() < source.getData().limit() ) {
            target = source.clone();
            mesh.setBuffer(target);
        } else {
            
            target.getData().limit(source.getData().limit());
        }
        return target;
    }
     protected void morph( VertexBuffer sourcePos, VertexBuffer sourceNorms,
                          VertexBuffer targetPos, VertexBuffer targetNorms ) {
        FloatBuffer sp = (FloatBuffer)sourcePos.getData();
        sp.rewind();

        FloatBuffer sn = (FloatBuffer)sourceNorms.getData();
        sn.rewind();

        FloatBuffer tp = (FloatBuffer)targetPos.getData();
        tp.rewind();

        FloatBuffer tn = (FloatBuffer)targetNorms.getData();
        tn.rewind();

        morph(sp, sn, tp, tn);

        sp.rewind();
        sn.rewind();

        tp.rewind();
        targetPos.updateData(tp);
        tn.rewind();
        targetNorms.updateData(tn);
    }
     
      protected void morph( FloatBuffer sourcePos, FloatBuffer sourceNorms,
                          FloatBuffer targetPos, FloatBuffer targetNorms ) {

        int count = sourcePos.limit() / 3;
        Vector3f v = new Vector3f();
        Vector3f normal = new Vector3f();
        Node node = new Node();
        node.attachChild(planet.getPlanet());
        node.attachChild(planet.getNavMesh());
        for( int i = 0; i < count; i++ ) {
            System.out.println(i+" out of "+count);
            v.x = sourcePos.get();
            v.y = sourcePos.get();
            v.z = sourcePos.get();
            normal.x = sourceNorms.get();
            normal.y = sourceNorms.get();
            normal.z = sourceNorms.get();
            
            
            Vector3f origin = planet.getPlanet().getLocalTranslation();
        
            Ray r = new Ray(origin, v.subtract(origin).normalize());

            CollisionResults res = new CollisionResults();
            node.collideWith(r, res);
            
            if(res.getClosestCollision().getGeometry().getName().equals("navMesh")){
                Vector3f realP = res.getCollision(1).getContactPoint();
                System.out.println(realP);
                 v.x = realP.x;
                 v.y = realP.y;
                 v.z = realP.z;
                
            }
            

            targetPos.put(v.x).put(v.y).put(v.z);
            targetNorms.put(normal.x).put(normal.y).put(normal.z);
        }
        node.detachAllChildren();
    }
      
      
      
    public void displayFOV(){
         fovs.detachAllChildren();
        for(Agent agent: agents){
            
           
            
            Ray r = new Ray(planet.getPlanet().getLocalTranslation(),agent.getPosition().subtract(planet.getPlanet().getLocalTranslation()).normalize());
      
            CollisionResults results = new CollisionResults();
            planet.getPlanet().collideWith(r, results);
            if(results.size()<=0)
                return;

            
            Vector3f contactPointNorm = results.getFarthestCollision().getContactNormal();
            
            Vector3f[] points = buildPyramid(agent.getPosition(), agent.getVelocity(),contactPointNorm);
            
            Vector3f eyePosition = agent.getPosition();
            Vector3f NTL =  points[0];
            Vector3f NTR = points[1];
            Vector3f NBL = points[2];
            Vector3f NBR = points[3];
            Vector3f FTL =  points[4];
            Vector3f FTR = points[5];
            Vector3f FBL = points[6];
            Vector3f FBR = points[7];

         
            Geometry line1 = new Geometry("line1", new Line(eyePosition, FTL));
            Geometry line2 = new Geometry("line2", new Line(eyePosition, FTR));
            Geometry line3 = new Geometry("line3", new Line(eyePosition, FBL));
            Geometry line4 = new Geometry("line4", new Line(eyePosition, FBR));

            Geometry line5 = new Geometry("line5", new Line(FTL, FTR));
            Geometry line6 = new Geometry("line6", new Line(FTL, FBL));
            Geometry line7 = new Geometry("line7", new Line(FBL, FBR));
            Geometry line8 = new Geometry("line8", new Line(FBR, FTR));

            Geometry line9 = new Geometry("line9", new Line(NTL, NTR));
            Geometry line10 = new Geometry("line10", new Line(NTL, NBL));
            Geometry line11 = new Geometry("line11", new Line(NBL, NBR));
            Geometry line12 = new Geometry("line12", new Line(NBR, NTR));

         

            line1.setMaterial(blue);
            line2.setMaterial(blue);
            line3.setMaterial(blue);
            line4.setMaterial(blue);
            line5.setMaterial(blue);
            line6.setMaterial(blue);
            line7.setMaterial(blue);
            line8.setMaterial(blue);
            line9.setMaterial(blue);
            line10.setMaterial(blue);
            line11.setMaterial(blue);
            line12.setMaterial(blue);


            fovs.attachChild(line1);
            fovs.attachChild(line2);
            fovs.attachChild(line3);
            fovs.attachChild(line4);
            fovs.attachChild(line5);
            fovs.attachChild(line6);
            fovs.attachChild(line7);
            fovs.attachChild(line8);
            fovs.attachChild(line9);
            fovs.attachChild(line10);
            fovs.attachChild(line11);
            fovs.attachChild(line12);
        }
    }
      
      
      
   
    public Vector3f[] buildPyramid(Vector3f eyePosition,Vector3f direction,Vector3f upVector){
         
         direction.normalize();
         
         Vector3f rightVector = direction.cross(upVector);
         float nearDistance = 10;
         float farDistance = 10;
         float fov = 45;
         float aspectRatio = 16/12;
         
         float Hnear = (float) (2 * Math.tan(fov/2) * nearDistance);
         float Wnear = Hnear*aspectRatio;
         
         float Hfar = (float) (2 * Math.tan(fov / 2) * farDistance);
         float Wfar = Hfar * aspectRatio;
         
         Vector3f Cnear = eyePosition.add(direction.mult(nearDistance));
         Vector3f Cfar = eyePosition.add(direction.mult(farDistance));
         
         Vector3f NTL,NTR,NBL,NBR,FTL,FTR,FBL,FBR;
         
         NTL = Cnear.add(upVector.mult(Hnear/2)).subtract(rightVector.mult(Wnear/2));
         NTR = Cnear.add(upVector.mult(Hnear/2)).add(rightVector.mult(Wnear/2));
         NBL = Cnear.subtract(upVector.mult(Hnear/2)).subtract(rightVector.mult(Wnear/2));
         NBR = Cnear.subtract(upVector.mult(Hnear/2)).add(rightVector.mult(Wnear/2));
         
         FTL = Cfar.add(upVector.mult(Hfar/2)).subtract(rightVector.mult(Wfar/2));
         FTR = Cfar.add(upVector.mult(Hfar/2)).add(rightVector.mult(Wfar/2));
         FBL = Cfar.subtract(upVector.mult(Hfar/2)).subtract(rightVector.mult(Wfar/2));
         FBR = Cfar.subtract(upVector.mult(Hfar/2)).add(rightVector.mult(Wfar/2));
         
         Vector3f [] v = {NTL,NTR,NBL,NBR,
                          FTL,FTR,FBL,FBR};
         return v;
        
         
     }

}
