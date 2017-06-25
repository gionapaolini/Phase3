package mygame;

import AStar.AStarAlgorithm;
import AStar.StarNode;

import Logic.Agent;
import Logic.Planet;
import Logic.Settings;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    ArrayList<Agent> pursuers;
    ArrayList<Agent> evaders;
    Thread timeIncreaser;
    Planet planet;
    Sphere sphere;
    Material red, blue, green, white, matWireframe, mat;
    Settings settings;
    Node fovs;
    Node[] bb;
    BitmapText[] times;
    
    Vector3f[] randomPosition;
    float[] visibilityRatio;
    int[] timePosition;
    boolean[] chosenPositionPursuer;
    boolean[] chosenPositionEvaders;
    
    
    boolean end =  false;
    //Setting
    int n_pursuers = 3;
    int n_evaders = 3;
    
    int pursuerType = 3;
    int evaderType = 3;
  
    boolean planetVisible = true;
   
    boolean FOVvisible = false;
    boolean catchingActive = true;
    boolean showLines = false;
  
    boolean useAgents = true;
    boolean displayTime = true;
    boolean initializeRandomLocations = true;
    boolean readFile = true;
    boolean usePatrolling = true;
    private int NUM_VIS_RAYS = 1000;
    private int NUM_POS_RAYS = 10000;
    private boolean useHighestVisibilityPoints = true;
    private boolean[][] edges;

    
    
    
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
        initializePlanet();

        if(initializeRandomLocations){
        
            
            if(readFile) {
                try{
                    ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("randomPosition"));
                    randomPosition = (Vector3f[])inputStream.readObject();
                } catch(Exception e) {}
                try{
                    ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("visibilityRatio"));
                    visibilityRatio = (float[])inputStream.readObject();
                } catch(Exception e) {}
            }
            else {
                initializeRandomPositions();
            }
            
                    
            
            initializeEdges();
            
           
        }
        
       
        if(usePatrolling) {
            initializeTimesOnSamples();
            initializeTimer();
        }

            
        if(useAgents){
            initializeAgents();
            chosenPositionPursuer = new boolean[NUM_POS_RAYS];
            chosenPositionEvaders = new boolean[NUM_POS_RAYS];
        }
        
       
      
       
        
        if(FOVvisible){
            fovs = new Node();
            rootNode.attachChild(fovs);
        }
        
        

        
    }
    
    @Override
    public void simpleUpdate(float ftp){
       if(useAgents)
           moveAgents(ftp);
       
       
       if(catchingActive)
           checkDeaths();
       if(displayTime)
           updateTimes();
      
       if(FOVvisible)
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
    
        
        red = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        red.setColor("Color", ColorRGBA.Red);
        blue = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blue.setColor("Color", ColorRGBA.Blue);
        green = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        green.setColor("Color", ColorRGBA.Green);
        white = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        white.setColor("Color", ColorRGBA.White);
        
        matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);
        
        mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseVertexColor", true);
    }
   

    private void initializeAgents() {
        pursuers = new ArrayList();
        evaders = new ArrayList();

        for(int i=0;i<n_pursuers;i++){
            Agent agent = new Agent(0,pursuerType, evaders);
            
             agent.setBody(new Geometry("Sphere", sphere));
             agent.getBody().setMaterial(red);
            

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

            agent.setPosition(new Vector3f(r1,r2,r3));
         
            rootNode.attachChild(agent.getBody());
            pursuers.add(agent);
        }
        
        for(int i=0;i<n_evaders;i++){
            Agent agent = new Agent(1,evaderType, pursuers);
                        
             agent.setBody(new Geometry("Sphere", sphere));
             agent.getBody().setMaterial(white);
            
     


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

            agent.setPosition(new Vector3f(r1,r2,r3));
            
            rootNode.attachChild(agent.getBody());
            evaders.add(agent);
        }
    }
    
    private void moveAgents(float ftp){
        
        for(Agent wanderer: pursuers){
           switch(pursuerType){
               //wandering random
               case 1:
                   wanderer.applyForce(wanderer.wanderForce());
                   break;
                   
               //pursue force toward evaders
               case 2:
                   for(Agent a: evaders){
                        if(Math.random()>9)
                            break;

                            wanderer.applyForce(wanderer.pursueForce(a));
                             

                    }
                                   
                   break;
               
               //patrolling highest visibility areas
               case 3:
                     patrollingForcePursuer(wanderer);

           }
           wanderer.move(ftp, planet);
 
        }
        for(Agent wanderer: evaders){
           
           switch(evaderType){
               //wandering random
               case 1:
                   wanderer.applyForce(wanderer.wanderForce());
                   break;
                   
               //evade force away from evaders
               case 2:
                   for(Agent a: pursuers){
                       
                        if(Math.random()>9)
                            break;

                        wanderer.applyForce(wanderer.evadeForce(a));     

                    }
                                   
                   break;
               
               //stays on lowest visibility areas
               case 3:
                      addForcesToEvader(wanderer);
           }
           wanderer.move(ftp, planet);
           
        }
        
        resetChosenPositionEvader();
        resetChosenPositionPursuer();

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
        
        if(planetVisible)
            rootNode.attachChild(planet.getPlanet());            

        
    }
    
    
  
    
    
   
      
      
    public void displayFOV(){
         fovs.detachAllChildren();
        for(Agent agent: pursuers){
            
           
        
            Vector3f[] points = buildPyramid(agent.getPosition(), agent.getVelocity(),agent.getCurrentNormal());
            
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
         float nearDistance = 0.1f;
         float farDistance = 100;
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
    
    
    

    
    
    private boolean isInSight(Vector3f[] points, Vector3f eyePosition, Vector3f object){
        
        
        Vector3f[] pointsPyramid = {eyePosition, points[4],points[5],points[6],points[7]};
        
        int cnt = 0;
        Ray ray = new Ray(object, Vector3f.UNIT_Y);
     
     
        if ( ray.intersectWherePlanar(pointsPyramid[0], pointsPyramid[1], pointsPyramid[3], null) ) cnt++;
        if ( ray.intersectWherePlanar(pointsPyramid[0], pointsPyramid[1], pointsPyramid[2], null) ) cnt++;
        if (cnt>1) return false;
        if ( ray.intersectWherePlanar(pointsPyramid[0], pointsPyramid[2], pointsPyramid[4], null) ) cnt++;
        if (cnt>1) return false;
        if ( ray.intersectWherePlanar(pointsPyramid[0], pointsPyramid[3], pointsPyramid[4], null) ) cnt++;
        if (cnt>1) return false;
   
        
        if ( ray.intersectWherePlanar(pointsPyramid[1], pointsPyramid[2], pointsPyramid[3], null) ) cnt++;
        if (cnt>1) return false;
        if ( ray.intersectWherePlanar(pointsPyramid[2], pointsPyramid[3], pointsPyramid[4], null) ) cnt++;
        if (cnt>1) return false;
        if (cnt==1)
            return true;
        else //->(cnt==0 || cnt>1)
            return false;
        
    }

    private void checkDeaths() {
        
        
        for(Agent pursuer: pursuers){
            Vector3f[] pyramid = buildPyramid(pursuer.getPosition(),pursuer.getVelocity(),pursuer.getCurrentNormal());
            pursuer.setPyramidSight(pyramid);
            
            for(Agent evader: evaders){
                
                if(!evader.isCanMove())
                    continue;
                
                Ray r = new Ray(pursuer.getPosition(),evader.getPosition().subtract(pursuer.getPosition()).normalize());
                CollisionResults res = new CollisionResults();
                planet.getPlanet().collideWith(r, res);
                if(res.size()<=0){
                    
                    
                    if(isInSight(pyramid, pursuer.getPosition(),evader.getPosition())){
                        evader.getBody().setMaterial(blue);
                        evader.setCanMove(false);
                        
                        if(showLines){
                            Line l = new Line(pursuer.getPosition(),evader.getPosition());
                            Geometry line = new Geometry("Fck", l);
                            line.setMaterial(blue);
                            rootNode.attachChild(line);
                        }
                    }
                }
                    
            }
        }
        
        
    }

    
   
    
     public void initializeTimesOnSamples(){
        bb = new Node[randomPosition.length];
        BillboardControl[] control = new BillboardControl[randomPosition.length];
     
         
        BitmapFont newFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        times = new BitmapText[randomPosition.length];
        for(int i=0;i<times.length;i++){
            if(visibilityRatio[i]<0.6f)
                continue;
            times[i] = new BitmapText(newFont, false);
            times[i].setSize(1.5f);
            times[i].setText(""+visibilityRatio[i]);
            times[i].setLocalTranslation(new Vector3f(0,0,0));
            control[i]=new BillboardControl();
            bb[i] = new Node("node"+i);
            bb[i].setLocalTranslation(randomPosition[i].normalize().mult(randomPosition[i].length()+2));
            bb[i].addControl(control[i]);
            bb[i].attachChild(times[i]);
            rootNode.attachChild(bb[i]);
        }
    }
    
    public void updateTimes(){
        
        for(int i=0;i<times.length;i++){
           if(visibilityRatio[i]<0.6f)
                continue;
            times[i].setText(""+timePosition[i]);
          
        }
            
    }
    public void initializeTimer(){
        
        timePosition = new int[NUM_POS_RAYS];
        
        timeIncreaser = new Thread() {
            public void run() {
                 while(!end){

                    
                     try {
                         Thread.sleep(1000);
                       
                         for(int i=0;i<NUM_POS_RAYS;i++){
                                     
                            if(visibilityRatio[i]<0.6f)
                                continue;
                            
                            timePosition[i]++;
                            
            
                         }

                     } catch (InterruptedException ex) {
                         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                     }
                    

                 }

            }  
            public void setEnd(){
                end = true;
            }
        };
        timeIncreaser.start();
    }
    
    
    @Override

    public void destroy(){

        super.destroy();

        end=true;

    }
  
    
    public float evaluatePositionVisibility(Vector3f position) {
        Vector3f rand;
        int count = 0;
        
        
        double time = System.currentTimeMillis();
                
        for(int i=0; i < NUM_VIS_RAYS; i++) {
            rand = new Vector3f((float)Math.random()-0.5f,(float)Math.random()-0.5f,(float)Math.random()-0.5f);
            //System.out.println(rand);
            Ray r = new Ray(position,rand);
            CollisionResults res = new CollisionResults();
            planet.getPlanet().collideWith(r, res);
            if(res.size()<=0)
                count++;
        }
        
        System.out.println(System.currentTimeMillis()-time);
        
        return count/(float)NUM_VIS_RAYS;
    }
    
    public void initializeRandomPositions() {

        randomPosition = new Vector3f[NUM_POS_RAYS];
        visibilityRatio = new float[NUM_POS_RAYS];
        

        Sphere s = new Sphere(5, 5, 0.2f);
        Vector3f rand;
        
        for(int i=0; i < NUM_POS_RAYS; i++) {
            
           
            rand = new Vector3f((float)Math.random()-0.5f,(float)Math.random()-0.5f,(float)Math.random()-0.5f);
            Ray r = new Ray(new Vector3f(0,0,0),rand);
            CollisionResults res = new CollisionResults();
            planet.getPlanet().collideWith(r, res);
                
            
            Geometry n = new Geometry("n", s);
            n.setMaterial(green);
            
            Vector3f position = res.getFarthestCollision().getContactPoint();
         
            n.setLocalTranslation(position);
            //rootNode.attachChild(n);
            
            visibilityRatio[i] = evaluatePositionVisibility(position.normalize().mult(position.length()+0.5f));
            randomPosition[i] = position.normalize().mult(position.length()+0.5f);
            
            System.out.println(i);

        }
        try { 
            String visFilename = "visibilityRatio";
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(visFilename));
            outputStream.writeObject(visibilityRatio);
            
        } catch(Exception e) {}

        try { 
            String posFilename = "randomPosition";
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(posFilename));
            outputStream.writeObject(randomPosition);
            
        } catch(Exception e) {}

        
    }
    
    public void initializeEdges(){
        edges= new boolean[NUM_POS_RAYS][NUM_POS_RAYS];
        for(int i=0;i<NUM_POS_RAYS;i++){
            for(int j=i+1;j<NUM_POS_RAYS;j++){

                if(randomPosition[i].subtract(randomPosition[j]).length()<6){
                                       
                    edges[i][j]=true;
                    edges[j][i]=true;

                }
                    
            }
        }
      
      displaybestPath();
    }
    
    
    public void addForcesToEvader(Agent p){
        Vector3f sum = new Vector3f(0,0,0);
        double time = System.currentTimeMillis();
        
        float best = 10000;
        int besti = -1;
                
        for(int i=0;i<visibilityRatio.length;i++){
                  
            if(visibilityRatio[i]>0.5f || chosenPositionEvaders[i])
                continue;
        
            float diff = p.getPosition().subtract(randomPosition[i]).length()/100;
            
            float closestValue = 100000;
            for(Agent agent:pursuers){
               
                float current = agent.getPosition().subtract(randomPosition[i]).length();
                if(current<closestValue){
                    closestValue = current;
                }
            }
            
            float totalVal = visibilityRatio[i] + diff - (closestValue/50);
                      
            
            
            if( totalVal < best) {
                besti = i;
                best = totalVal;
            }
                
               
        }
        chosenPositionEvaders[besti] = true;
        p.applyForce(p.seekForce(randomPosition[besti]));
    }
    
    
    public void patrollingForcePursuer(Agent p){
        
     
        int highestTime = -1;
        int besti = -1;
        
       
        
        for(int i=0;i<visibilityRatio.length;i++){
                   
            if(visibilityRatio[i]<0.6f || chosenPositionPursuer[i])
                continue;
            
            if(p.getPosition().subtract(randomPosition[i]).length()<5){
                timePosition[i] = 0;
            }
            
            
            if(timePosition[i]>highestTime){
                highestTime = timePosition[i];
                besti = i;
            }
            
            
        }
       
       chosenPositionPursuer[besti] = true;
       p.applyForce(p.seekForce(randomPosition[besti]));
        
      
    }
    
    public void resetChosenPositionPursuer(){
         for(int i=0;i<chosenPositionPursuer.length;i++){
                   
           chosenPositionPursuer[i] = false;
            
        }
    }
    
    public void resetChosenPositionEvader(){
         for(int i=0;i<chosenPositionEvaders.length;i++){
                   
           chosenPositionEvaders[i] = false;
            
            
        }
    }
    
    
    public void attachBaaaallsOnRandom(){
       
         
         
         
        for(int i=0;i<NUM_POS_RAYS;i++){
            for(int j=i+1;j<NUM_POS_RAYS;j++){

                if(edges[i][j]){
                    Line l = new Line(randomPosition[i],randomPosition[j]);
                    Geometry line = new Geometry("Fck", l);
                    line.setMaterial(blue);
                    rootNode.attachChild(line);
                }
                    
            }
        }
         
         
       
     }
    
    public void attachBall(){
        int target = 9566;
        
        Sphere s = new Sphere(5,5,2f);
         Geometry mainS = new Geometry("Main", s);
         mainS.setLocalTranslation(randomPosition[target]);
         mainS.setMaterial(blue);
         rootNode.attachChild(mainS);
         
         
         for(int i=0;i<NUM_POS_RAYS;i++){
            if(edges[target][i]){
              Geometry n = new Geometry("n", s);
              n.setMaterial(red);
              n.setLocalTranslation(randomPosition[i]);

              rootNode.attachChild(n);
            }

         }
        
    }
    
    
    public void displaybestPath(){
        float bestVis1 = 999999;
        int index1 = -1;
        float bestVis2 = 99999;
        int index2 = -1;
        
        for(int i=0;i<NUM_POS_RAYS;i++){
            if(visibilityRatio[i]<bestVis1){
                bestVis1 = visibilityRatio[i];
                index1 = i;
            }
        }
        
        for(int i=0;i<NUM_POS_RAYS;i++){
            
            if(i==index1 || randomPosition[index1].subtract(randomPosition[i]).length()<40)
                continue;
            
            if(visibilityRatio[i]<bestVis2){
                bestVis2 = visibilityRatio[i];
                index2 = i;
            }
        }
        
        AStarAlgorithm algo = new AStarAlgorithm(index1,index2,randomPosition,visibilityRatio, edges); 
        ArrayList<StarNode> list1 = algo.pathFinding();
        ArrayList<StarNode> list = algo.closedSet;        
        Sphere s = new Sphere(5,5,0.5f);
        Sphere big = new Sphere(5,5,7f);
        
        for(StarNode node: list1){
                
                     
                 Geometry n = new Geometry("n", s);
                if(node.getIndex() == index1 || node.getIndex() == index2)
                    n.setMaterial(blue);
                else
                    n.setMaterial(red);

                 n.setLocalTranslation(node.getPosition());

                 rootNode.attachChild(n);
                 
            
              
         }
        
        
        
    }
    
    
    
   
}
