package view;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import control.PlayerBehavior;
import control.PuckBehavior;
import model.Goal;
import model.Paddle;
import model.Puck;
import model.Rink;
import model.ScoreBoard;
import model.Walls;



public class Game extends Applet implements KeyListener, ActionListener{
	
	protected BoundingSphere bounds = new BoundingSphere(new Point3d(0.36f,0.0,0.48f), 50.0f);
	BranchGroup rootPuckBranchGrp, rootPaddleBranchGrp, rootGoalBranchGrp, rootScoreboardBranchGrp;
	
	public static TransformGroup puckXfmGrp = new TransformGroup();
	public static TransformGroup player1XfmGrp = new TransformGroup();
	public static TransformGroup player2XfmGrp = new TransformGroup();
	public static TransformGroup p1ScoreboardXfmGrp = new TransformGroup();
	public static TransformGroup p2ScoreboardXfmGrp = new TransformGroup();

	
	public static Transform3D puckPos, player1Pos, player2Pos, player1Scoreboard, player2Scoreboard;
	public static ScoreBoard scoreboard;
	public static Text3D scoreboardText;
	
	public Vector3f puckOrigin = new Vector3f();
	public Vector3f player1Origin = new Vector3f();
	public Vector3f player2Origin = new Vector3f();
	public Vector3f p1ScoreboardOrigin = new Vector3f();
	public Vector3f p2ScoreboardOrigin = new Vector3f();
	public Canvas3D cv;
	
	public static PointSound p1Sound = new PointSound();
	public static PointSound p2Sound = new PointSound();
	public static PointSound g1Sound = new PointSound();		
	public static PointSound g2Sound = new PointSound();
	public static PointSound wallSound = new PointSound(); 	//CenterPoint sound source for simplicity
	
	public static boolean p1Input, p1Up, p1Down, p1Left, p1Right, p2Input, p2Up, p2Down, p2Left, p2Right;
	public static boolean gameRunning = true;
	
	public static int mixerCount = 0;   // limit amount of mixers created for views;
	
	public static void main(String[] args) {
		new MainFrame(new Game(), 1600, 1000);
	}

	public void init() {
		Frame title = (Frame) this.getParent();
		title.setTitle("Air Hockey 3-D");
		
		this.setLayout(new GridLayout(1,2));
		//Canvas 
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		cv = new Canvas3D(gc);	
		
		//Virtual Universe
		SimpleUniverse su = new SimpleUniverse(cv);
        su.getViewingPlatform().setNominalViewingTransform();
		
        //Create Player1 View
        cv = new Canvas3D(gc);
        add(cv);
        BranchGroup viewContents = buildView(cv, new Point3d(Rink.width/2,0.80f,-0.65f), 
        		new Point3d(Rink.width/2,0.0f,Rink.length/2), new Vector3d(0f,1.0f,0f));
		su.addBranchGraph(viewContents);
	    cv.addKeyListener(this);
	
		//Create Player2 View
		cv = new Canvas3D(gc);
	    add(cv);
	    viewContents = buildView(cv, new Point3d(Rink.width/2,0.80f,Rink.length+0.65f),
	    		new Point3d(Rink.width/2,0.0f,Rink.length/2),new Vector3d(0f,1.0f,0f));
	    su.addBranchGraph(viewContents);
	    cv.addKeyListener(this);
        
	    //Build SceneGraph
      	BranchGroup	contents = buildContent();
      	contents.compile();
      	su.addBranchGraph(contents);    				
	}
	
	//Build content tree
	private BranchGroup buildContent() {
		BranchGroup root = new BranchGroup();
		buildLighting(root);
		TransformGroup goalXfmGrp = new TransformGroup();
		rootPuckBranchGrp = new BranchGroup();
		rootPaddleBranchGrp = new BranchGroup();
		rootGoalBranchGrp = new BranchGroup();
		rootScoreboardBranchGrp = new BranchGroup();
		scoreboard = new ScoreBoard();
		
		//ScoreBoard
		Node scoreBoardNode = buildScoreBoard(scoreboard);
		rootScoreboardBranchGrp.addChild(scoreBoardNode);	
		
		//Build rink and walls
		Node rink = buildRink();
		rootPuckBranchGrp.addChild(rink);		
		Node rightWall = buildWalls(Walls.Side.RIGHT);
		rootPuckBranchGrp.addChild(rightWall);		
		Node leftWall = buildWalls(Walls.Side.LEFT);
		rootPuckBranchGrp.addChild(leftWall);		
		Node nearWall = buildWalls(Walls.Side.NEAR);
		rootPuckBranchGrp.addChild(nearWall);		
		Node farWall = buildWalls(Walls.Side.FAR);
		rootPuckBranchGrp.addChild(farWall);
		
		//Puck 
		Node nPuck = buildPuck();
		rootPuckBranchGrp.addChild(nPuck);
		
		//Player 1 paddle, goal, and behavior
		boolean p1 = true;
		Node player1 = buildPaddle(p1);
		addPaddleSound(p1, player1XfmGrp, 0.97f);
		rootPaddleBranchGrp.addChild(player1);
		
		Node p1Goal = buildGoal(p1);
		addGoalSound(p1,goalXfmGrp);
		rootGoalBranchGrp.addChild(p1Goal);
		
		PlayerBehavior playerB1 = new PlayerBehavior(bounds,player1Origin,p1);
		playerB1.setSchedulingBounds(bounds);
		rootPaddleBranchGrp.addChild(playerB1);
		
		//Player 2 paddle, goal, and behavior
		p1 = false;
		Node player2 = buildPaddle(p1);
		addPaddleSound(p1, player2XfmGrp, 0.97f);
		rootPaddleBranchGrp.addChild(player2);
		Node p2Goal = buildGoal(p1);
		addGoalSound(p1,goalXfmGrp);
		rootGoalBranchGrp.addChild(p2Goal);
		
		PlayerBehavior playerB2 = new PlayerBehavior(bounds,player2Origin,p1);
		playerB2.setSchedulingBounds(bounds);
		rootPaddleBranchGrp.addChild(playerB2);
		
		//Puck Behavior 
		PuckBehavior puckStep = new PuckBehavior(bounds, puckOrigin);
		puckStep.setSchedulingBounds(bounds);
		rootPuckBranchGrp.addChild(puckStep);
		
		root.addChild(rootScoreboardBranchGrp);
		root.addChild(rootGoalBranchGrp);
		root.addChild(rootPuckBranchGrp);
		root.addChild(rootPaddleBranchGrp);
		root.addChild(goalXfmGrp);
		addBackgroundNoise(root);
		addWallSound(root);
		return root;
	}
	
	//Build View
	private BranchGroup buildView(Canvas3D cv, Point3d lens, Point3d lookat, Vector3d up) {
		View view = new View();
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		view.addCanvas3D(cv);
		  
		ViewPlatform plat = new ViewPlatform();
		view.attachViewPlatform(plat);
		PhysicalEnvironment env = new PhysicalEnvironment();
		view.setPhysicalBody(new PhysicalBody());
		view.setPhysicalEnvironment(env);				
		
		Transform3D trans = new Transform3D();
		trans.lookAt(lens, lookat, up);
		trans.invert();
		
		TransformGroup tg = new TransformGroup(trans);
		tg.addChild(plat);
		
		BranchGroup bView = new BranchGroup();
		bView.addChild(tg);
		if(mixerCount ==0) {							//Limit session to one audio mixer 
			JavaSoundMixer mixer = new JavaSoundMixer(env);
			mixer.initialize();	
			mixerCount++;
		}
		return bView;
	}
	
	//Build Scoreboard 
	private Node buildScoreBoard(ScoreBoard scoreboard) {
		BranchGroup scoreboardGroup = new BranchGroup();
		
		Appearance ap = new Appearance();
		Color3f ambient = new Color3f(0.8f,0.8f,0.8f);
		Color3f emissiveColour = new Color3f(0.1f, 0.1f, 0.1f);
		Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
		Color3f diffuseColour = new Color3f(1.0f, 1.0f, 1.0f);
		float shimmer = 25.0f;
		ap.setMaterial(new Material(ambient, emissiveColour,
		        diffuseColour, specularColour, shimmer));
		ap.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0f));
		
		String scores = "Score: " + scoreboard.getPlayer1score() + " - " + scoreboard.getPlayer2score();
		Font3D font = new Font3D(new Font("Serif", Font.BOLD, 1),new FontExtrusion());
		scoreboardText = new Text3D(font,scores,new Point3f(Rink.width/2,Rink.depth*4,Rink.length+0.6f), Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT);
		
		scoreboardText.setCapability(Text3D.ALLOW_STRING_WRITE);
		Shape3D p1Score = new Shape3D(scoreboardText,ap);
		Shape3D p2Score = new Shape3D(scoreboardText,ap);
		
		player1Scoreboard = new Transform3D();
		p1ScoreboardOrigin = new Vector3f(Rink.width/2,Rink.depth*4,Rink.length+0.6f);
		player1Scoreboard.rotY(Math.PI);
		player1Scoreboard.setScale(0.08);
		player1Scoreboard.setTranslation(p1ScoreboardOrigin);
		TransformGroup scoreXfmGrp = new TransformGroup(player1Scoreboard);
		scoreXfmGrp.addChild(p1ScoreboardXfmGrp);
		p1ScoreboardXfmGrp.addChild(p1Score);
		scoreboardGroup.addChild(scoreXfmGrp);
		
		scoreXfmGrp = new TransformGroup();
		player2Scoreboard = new Transform3D();
		p2ScoreboardOrigin = new Vector3f(Rink.width/2,Rink.depth*4,-0.6f);
		player2Scoreboard.setScale(0.08);
		player2Scoreboard.setTranslation(p2ScoreboardOrigin);
		scoreXfmGrp.addChild(p2ScoreboardXfmGrp);
		p2ScoreboardXfmGrp.setTransform(player2Scoreboard);
		p2ScoreboardXfmGrp.addChild(p2Score);
		scoreboardGroup.addChild(scoreXfmGrp);
		
		return scoreboardGroup;
	}
	
	//Create a puck
	private Node buildPuck() {
		BranchGroup puckGroup = new BranchGroup();
		TransformGroup puckTrans = new TransformGroup();
		
		Appearance ap = new Appearance();
		Color3f ambColor = new Color3f(0.0f,0.0f,0.0f);
		Color3f emitColor = new Color3f(0.1f, 0.1f, 0.1f);
		Color3f specColor = new Color3f(1.0f,1.0f,1.0f);
		Color3f diffColor = new Color3f(0.0f, 0.0f, 0.0f);
		float shine = 128.0f;
		ap.setMaterial(new Material(ambColor, emitColor,
		        diffColor, specColor, shine));
		ap.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0f));
		
		Puck puck = new Puck(ap);
		puckPos = new Transform3D();
		puckOrigin = new Vector3f(0.36f,0.0075f,0.48f);
		puckPos.setTranslation(puckOrigin);
		
		puckTrans = new TransformGroup();
		puckTrans.addChild(puckXfmGrp);
		puckXfmGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		puckXfmGrp.addChild(puck);
		
		
		puckGroup.addChild(puckTrans);
		return puckGroup;
	}

	//Build goal
	private Node buildGoal(boolean p1) {
		BranchGroup branchGoal = new BranchGroup();
		TransformGroup goalXfmGrp = new TransformGroup();
		
		Appearance ap = new Appearance();
		Color3f ambColor = new Color3f(0.25f, 0.21f, 0.0f);
	    Color3f emitColor = new Color3f(0.1f, 0.1f, 0.1f);
	    Color3f specColor = new Color3f(1.0f, 1.0f, 1.0f);
	    Color3f diffColor = new Color3f(1.0f, 0.84f, 0.0f);
	    float shine = 128.0f;
	    ap.setMaterial(new Material(ambColor, emitColor,
	        diffColor, specColor, shine));
	    ap.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0f));
		
	    Goal goal = new Goal(p1);
	    goal.setAppearance(ap);
	    goalXfmGrp.addChild(goal);
	    branchGoal.addChild(goalXfmGrp);  
		return branchGoal;
	}

	//Build walls on Rink
	private Node buildWalls(Walls.Side side) {
		BranchGroup wallGroup = new BranchGroup();
		TransformGroup wallXfmGrp = new TransformGroup();
		
		Appearance ap = new Appearance();
		Color3f ambientColour = new Color3f(0.21f, 0.18f, 0.13f);
	    Color3f emissiveColour = new Color3f(0.1f, 0.1f, 0.1f);
	    Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
	    Color3f diffuseColour = new Color3f(0.43f, 0.36f, 0.26f);
	    float shimmer = 40.0f;
	    ap.setMaterial(new Material(ambientColour, emissiveColour,
	        diffuseColour, specularColour, shimmer));
	    ap.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0f));
		Walls wall = new Walls(side);
		wall.setAppearance(ap);

		wallXfmGrp.addChild(wall);
		wallGroup.addChild(wallXfmGrp);
		return wallGroup;
	}

	//Lights
	private void buildLighting(BranchGroup root) {
		Background background = new Background(0.5f, 0.5f, 0.5f);
	    background.setApplicationBounds(bounds);
	    root.addChild(background);
	    
		Color3f color = new Color3f(Color.WHITE);
		AmbientLight ambLight = new AmbientLight(true,color);
		ambLight.setInfluencingBounds(bounds);
		root.addChild(ambLight);
		
		Vector3f lightDir = new Vector3f(1.0f,1.0f,0.0f);
		DirectionalLight dirLight = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f),lightDir);
		dirLight.setInfluencingBounds(bounds);
		root.addChild(dirLight);
		
		PointLight pLight = new PointLight(new Color3f(Color.white), new Point3f(0.0f,5.0f,0f),
			      new Point3f(1f,0f,0f));
		pLight.setInfluencingBounds(bounds);
		root.addChild(pLight);
	}

	//Create player paddles
	private Node buildPaddle(boolean player1) {
		BranchGroup paddleGroup = new BranchGroup();
		
		Appearance ap = new Appearance();
	    ap.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0f));
	    
	    if(player1) {
	    	Color3f ambientColour = new Color3f(0.3f, 0.0f, 0.0f);
		    Color3f emissiveColour = new Color3f(0.1f, 0.1f, 0.1f);
		    Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
		    Color3f diffuseColour = new Color3f(1.0f, 0.0f, 0.0f);
		    float shimmer = 128.0f;
		    ap.setMaterial(new Material(ambientColour, emissiveColour,
			        diffuseColour, specularColour, shimmer));
		    
		    Paddle paddle = new Paddle(player1, ap);
		    
	    	TransformGroup paddle1Trans = new TransformGroup();
	    	player1Pos = new Transform3D();
	    	player1Origin = new Vector3f(0.36f,0.0075f,0.15f);
	    	player1Pos.setTranslation(player1Origin);
	    	paddle1Trans.addChild(player1XfmGrp);
	    	player1XfmGrp.setTransform(player1Pos);
	    	player1XfmGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    	player1XfmGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    	player1XfmGrp.addChild(paddle);
	  		paddleGroup.addChild(paddle1Trans); 
	    }else {
	    	Color3f ambientColour = new Color3f(0.0f, 0.0f, 0.3f);
		    Color3f emissiveColour = new Color3f(0.1f, 0.1f, 0.1f);
		    Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
		    Color3f diffuseColour = new Color3f(0.0f, 0.0f, 1.0f);
		    float shimmer = 128.0f;
		    ap.setMaterial(new Material(ambientColour, emissiveColour,
			        diffuseColour, specularColour, shimmer));
		    
		    Paddle paddle = new Paddle(player1, ap);
		    
	    	TransformGroup paddle2Trans = new TransformGroup();
	    	player2Pos = new Transform3D();
	    	player2Origin = new Vector3f(0.36f,0.0075f,0.81f);
	    	player2Pos.setTranslation(player2Origin);
	    	paddle2Trans.addChild(player2XfmGrp);
	    	player2XfmGrp.setTransform(player2Pos);
	    	player2XfmGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    	player2XfmGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    	player2XfmGrp.addChild(paddle);
	    	paddleGroup.addChild(paddle2Trans);
	    }
		return paddleGroup;
	}

	//Create rink shape
	private Node buildRink() {
		BranchGroup rinkGroup = new BranchGroup();
		
		Appearance ap = new Appearance();
		Color3f ambColor = new Color3f(0.1f, 0.1f, 0.1f);
	    Color3f emitColor = new Color3f(0.1f, 0.1f, 0.1f);
	    Color3f specColor = new Color3f(1.0f, 1.0f, 1.0f);
	    Color3f difColor = new Color3f(1.0f, 1.0f, 1.0f);
	    float shine = 128.0f;
		URL texture =  getClass().getClassLoader().getResource("images/GSULogo-01.jpg");
	    TextureLoader loader = new TextureLoader(texture, this);
	    ImageComponent2D image = loader.getImage();
	    Texture2D tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,image.getWidth(),image.getHeight());
		tex.setImage(0, image);
		tex.setEnable(true);
		tex.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		ap.setTexture(tex);
		
		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.COMBINE);
		ap.setTextureAttributes(ta);
		
	    ap.setMaterial(new Material(ambColor, emitColor, difColor, specColor, shine));
	    ap.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0f));
	    
	    Rink rink = new Rink();
        rink.setAppearance(ap);
	    Transform3D rinkTranslation = new Transform3D();
	    
        TransformGroup rinkTrans = new TransformGroup();
        rinkTrans.setTransform(rinkTranslation);
	    rinkTrans.addChild(rink);
	    rinkGroup.addChild(rinkTrans);   
		return rinkGroup;
	}
	
	//Add audio to scene 
	public void addGoalSound(boolean g1, TransformGroup goal) {
		MediaContainer sound = new MediaContainer(new String("file:GoalHit.wav"));
		if(g1) {
			g1Sound.setSoundData(sound);
			g1Sound.setInitialGain(0.2f);
			g1Sound.setPosition(new Point3f(Rink.width/2,0f,0f));
			g1Sound.setCapability(PointSound.ALLOW_ENABLE_READ);
			g1Sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
			g1Sound.setSchedulingBounds(bounds);
			g1Sound.setEnable(false);
			g1Sound.setLoop(0);
		
			goal.addChild(g1Sound);
		}else {
			g2Sound.setSoundData(sound);
			g2Sound.setInitialGain(0.2f);
			g2Sound.setPosition(new Point3f(Rink.width/2,0f,Rink.length));
			g2Sound.setCapability(PointSound.ALLOW_ENABLE_READ);
			g2Sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
			g2Sound.setSchedulingBounds(bounds);
			g2Sound.setEnable(false);
			g2Sound.setLoop(0);
			
			goal.addChild(g2Sound);
		}	
	}
	
	public void addPaddleSound(boolean p1, TransformGroup player, float edgeInf) {
		Transform3D current  = new Transform3D();
		Vector3f position = new Vector3f();
		
		if(p1) {
			player.getTransform(current);
			current.get(position);
	
			MediaContainer sound = new MediaContainer(new String("file:PaddleLong.wav"));
			p1Sound.setSoundData(sound);
			p1Sound.setReleaseEnable(true);				/////////////////
			p1Sound.setInitialGain(1.0f);
			p1Sound.setPosition(new Point3f(position));
			p1Sound.setCapability(PointSound.ALLOW_ENABLE_READ);
			p1Sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
			p1Sound.setSchedulingBounds(bounds);
			p1Sound.setEnable(false);    						//Toggle
			p1Sound.setLoop(0);
			player.addChild(p1Sound);
		}else {
			player.getTransform(current);
			current.get(position);
	
			MediaContainer sound = new MediaContainer(new String("file:PaddleLong.wav"));
			p2Sound.setSoundData(sound);
			p2Sound.setReleaseEnable(true);
			p2Sound.setInitialGain(1.0f);
			p2Sound.setPosition(new Point3f(position));
			p2Sound.setCapability(PointSound.ALLOW_ENABLE_READ);
			p2Sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
			p2Sound.setSchedulingBounds(bounds);
			p2Sound.setEnable(false);    					
			p2Sound.setLoop(0);
			player.addChild(p2Sound);
		}
	}
	
	public void addWallSound(BranchGroup bg) {
		MediaContainer sound = new MediaContainer(new String("file:WallHitLong.wav"));
		wallSound.setSoundData(sound);
		wallSound.setInitialGain(1.0f);
		wallSound.setPosition(new Point3f(0.36f, 0.0f, 0.48f));
		wallSound.setCapability(PointSound.ALLOW_ENABLE_READ);
		wallSound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
		wallSound.setSchedulingBounds(bounds);
		wallSound.setEnable(false);
		wallSound.setLoop(0);	
		bg.addChild(wallSound);
	}
	
	public void addBackgroundNoise(BranchGroup bg) {
		MediaContainer sound = new MediaContainer(new String("file:Ambience.wav"));
		BackgroundSound source = new BackgroundSound(sound, 0.0f);	
		source.setSchedulingBounds(bounds);
		source.setEnable(true);
		source.setLoop(BackgroundSound.INFINITE_LOOPS);
		bg.addChild(source);
	}
	
	//Event Listeners
	@Override
	public void actionPerformed(ActionEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode) {
			case 65: 						//A
				p1Left = true; break;				
			case 68: 						//D
				p1Right = true; break;
			case 87:						//W
				p1Up = true; break;
			case 83: 						//S
				p1Down = true; break;
			case 37: 						//LEFT_ARROW
				p2Left = true; break;
			case 39:						//RIGHT_ARROW
				p2Right = true; break;
			case 38:						//UP_ARROW
				p2Up = true; break;
			case 40:						//DOWN_ARROW
				p2Down = true; break;
			case 32:						//SPACEBAR : Start game
				gameRunning = true; break;
		}	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode) {
			case 65: 
				p1Left = false; break;
			case 68: 
				p1Right = false; break;
			case 87:
				p1Up = false; break;
			case 83: 
				p1Down = false; break;
			case 37: 
				p2Left = false; break;
			case 39:
				p2Right = false; break;
			case 38:
				p2Up = false; break;
			case 40:
				p2Down = false; break;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
