/**
		* Copyright (c) 2009 Vitaliy Pavlenko
		*
		* Permission is hereby granted, free of charge, to any person
		* obtaining a copy of this software and associated documentation
		* files (the "Software"), to deal in the Software without
		* restriction, including without limitation the rights to use,
		* copy, modify, merge, publish, distribute, sublicense, and/or sell
		* copies of the Software, and to permit persons to whom the
		* Software is furnished to do so, subject to the following
		* conditions:
		*
		* The above copyright notice and this permission notice shall be
		* included in all copies or substantial portions of the Software.
		*
		* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
		* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
		* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
		* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
		* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
		* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
		* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
		* OTHER DEALINGS IN THE SOFTWARE.
		*/

		package edu.ufp.inf.sd.rmi.client.frogger;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeoutException;

import edu.ufp.inf.sd.rmi.client.*;
import edu.ufp.inf.sd.rmi.server.DB;
import edu.ufp.inf.sd.rmi.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.server.State.State;
import edu.ufp.inf.sd.rmi.server.State.StateMovimento;
import edu.ufp.inf.sd.rmi.server.State.StateTransito;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;

public class Main extends StaticScreenGame {

	static String host = "localhost";
	static int port = 5672;

	static final int WORLD_WIDTH = (13*32);
	static final int WORLD_HEIGHT = (14*32);
	static final Vector2D FROGGER_START = new Vector2D(6*32,WORLD_HEIGHT-32);

	static final String RSC_PATH = "edu/ufp/inf/sd/rmi/client/frogger/resources/";
	static final String SPRITE_SHEET = RSC_PATH + "frogger_sprites.png";

	static final int FROGGER_LIVES      = 5;
	static final int STARTING_LEVEL     = 1;
	static final int DEFAULT_LEVEL_TIME = 60;

	private FroggerUI ui;
	private WindGust wind;
	private HeatWave hwave;
	private GoalManager goalmanager;

	private AbstractBodyLayer<MovingEntity> movingObjectsLayer;
	private AbstractBodyLayer<MovingEntity> particleLayer;

	private MovingEntityFactory roadLine1;
	private MovingEntityFactory roadLine2;
	private MovingEntityFactory roadLine3;
	private MovingEntityFactory roadLine4;
	private MovingEntityFactory roadLine5;

	private MovingEntityFactory riverLine1;
	private MovingEntityFactory riverLine2;
	private MovingEntityFactory riverLine3;
	private MovingEntityFactory riverLine4;
	private MovingEntityFactory riverLine5;

	private ImageBackgroundLayer backgroundLayer;

	static final int GAME_INTRO        = 0;
	static final int GAME_PLAY         = 1;
	static final int GAME_FINISH_LEVEL = 2;
	static final int GAME_INSTRUCTIONS = 3;
	static final int GAME_OVER         = 4;

	protected int GameState = GAME_INTRO;
	protected int GameLevel = STARTING_LEVEL;

	public int GameLives    = FROGGER_LIVES;
	public int GameScore    = 0;

	public int levelTimer = DEFAULT_LEVEL_TIME;

	private boolean space_has_been_released = false;
	private boolean keyPressed = false;
	private boolean listenInput = true;

	//Cria um ArrayList para guardar para os dois jogadores
	private ArrayList<FroggerCollisionDetection> frogsCol;
	private ArrayList<Frogger> frogs;
	private ArrayList<AudioEfx> audios;
	//

	//Inteiro para identificar o jogador
	private Integer numeroJogador;
	public String username;
	//

	public int dificuldade;

	public FroggerGameRI froggerGameRI = DB.getInstance().game;


	/**
	 * Initialize game objects
	 */
	public Main (Integer nJogador) throws Exception {

		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		gameframe.setTitle("Frogger");

		ResourceFactory.getFactory().loadResources(RSC_PATH, "resources.xml");

		ImageResource bkg = ResourceFactory.getFactory().getFrames(
				SPRITE_SHEET + "#background").get(0);
		backgroundLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH,
				WORLD_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);

		// Used in CollisionObject, basically 2 different collision spheres
		// 30x30 is a large sphere (sphere that fits inside a 30x30 pixel rectangle)
		//  4x4 is a tiny sphere
		PaintableCanvas.loadDefaultFrames("col", 30, 30, 2, JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("colSmall", 4, 4, 2, JIGSHAPE.RECTANGLE, null);

		//Cria os ArrayLists para guardar para os dois jogadores
		frogs = new ArrayList<>();
		frogsCol = new ArrayList<>();
		audios = new ArrayList<>();
		//

		//Para o Jogador 1
		Frogger frog1 = new Frogger(this);
		frogs.add(frog1);
		FroggerCollisionDetection frogCol1 = new FroggerCollisionDetection(frog1);
		frogsCol.add(frogCol1);
		AudioEfx audioEfx1 = new AudioEfx(frogCol1, frog1);
		audios.add(audioEfx1);
		//

		//Para o Jogador 2
		Frogger frog2 = new Frogger(this);
		frogs.add(frog2);
		FroggerCollisionDetection frogCol2 = new FroggerCollisionDetection(frog2);
		frogsCol.add(frogCol2);
		AudioEfx audioEfx2 = new AudioEfx(frogCol2, frog2);
		audios.add(audioEfx2);
		//

		ui = new FroggerUI(this);
		wind = new WindGust();
		hwave = new HeatWave();
		goalmanager = new GoalManager();

		movingObjectsLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();
		particleLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();

		numeroJogador = nJogador;

		Runnable runnable = () -> {
			Receive receive = new Receive();
			try {
				receive.receive(DB.getInstance().game.getObservers().get(numeroJogador));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();


		initializeLevel(1);

	}


	public void initializeLevel(int level) throws Exception {

		/* dV is the velocity multiplier for all moving objects at the current game level */
		double dV = level*0.05 + 1;

		movingObjectsLayer.clear();

		/* River Traffic */
		riverLine1 = new MovingEntityFactory(new Vector2D(-(32*3),2*32),
				new Vector2D(0.06*dV,0));

		riverLine2 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH,3*32),
				new Vector2D(-0.04*dV,0));

		riverLine3 = new MovingEntityFactory(new Vector2D(-(32*3),4*32),
				new Vector2D(0.09*dV,0));

		riverLine4 = new MovingEntityFactory(new Vector2D(-(32*4),5*32),
				new Vector2D(0.045*dV,0));

		riverLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH,6*32),
				new Vector2D(-0.045*dV,0));

		/* Road Traffic */
		roadLine1 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 8*32),
				new Vector2D(-0.1*dV, 0));

		roadLine2 = new MovingEntityFactory(new Vector2D(-(32*4), 9*32),
				new Vector2D(0.08*dV, 0));

		roadLine3 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 10*32),
				new Vector2D(-0.12*dV, 0));

		roadLine4 = new MovingEntityFactory(new Vector2D(-(32*4), 11*32),
				new Vector2D(0.075*dV, 0));

		roadLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 12*32),
				new Vector2D(-0.05*dV, 0));

		goalmanager.init(level);
		for (Goal g : goalmanager.get()) {
			movingObjectsLayer.add(g);
		}

		/* Build some traffic before game starts buy running MovingEntityFactories for fews cycles */
		for (int i=0; i<500; i++)
			cycleTraffic(10);
	}


	/**
	 * Populate movingObjectLayer with a cycle of cars/trucks, moving tree logs, etc
	 *
	 * @param deltaMs
	 */
	public void cycleTraffic(long deltaMs) throws Exception {
		/* Road traffic updates */
		roadLine1.update(deltaMs);
		roadLine2.update(deltaMs);
		roadLine3.update(deltaMs);
		roadLine4.update(deltaMs);
		roadLine5.update(deltaMs);

		/* River traffic updates */
		riverLine1.update(deltaMs);
		riverLine2.update(deltaMs);
		riverLine3.update(deltaMs);
		riverLine4.update(deltaMs);
		riverLine5.update(deltaMs);

		if (numeroJogador == 0) { // Apenas o host da partida cria trafego
			generateTraffic(deltaMs);
		}

		try {
			MovingEntity m;
			// HeatWave
			if ((m = hwave.genParticles(frogs.get(numeroJogador).getCenterPosition())) != null) newStateTransito(m, deltaMs);

			movingObjectsLayer.update(deltaMs);
			particleLayer.update(deltaMs);
		}catch (ConcurrentModificationException e) {
			System.out.println(e.toString());;
		}
	}

	private void generateTraffic(long deltaMs) throws Exception {
		MovingEntity m;
		/* Road traffic updates */
		if ((m = roadLine1.buildVehicle()) != null) newStateTransito(m, deltaMs);
		if ((m = roadLine2.buildVehicle()) != null) newStateTransito(m, deltaMs);
		if ((m = roadLine3.buildVehicle()) != null) newStateTransito(m, deltaMs);
		if ((m = roadLine4.buildVehicle()) != null) newStateTransito(m, deltaMs);
		if ((m = roadLine5.buildVehicle()) != null) newStateTransito(m, deltaMs);
		/* River traffic updates */
		if ((m = riverLine1.buildShortLogWithTurtles(40)) != null) newStateTransito(m, deltaMs);
		if ((m = riverLine2.buildLongLogWithCrocodile(30)) != null) newStateTransito(m, deltaMs);
		if ((m = riverLine3.buildShortLogWithTurtles(50)) != null) newStateTransito(m, deltaMs);
		if ((m = riverLine4.buildLongLogWithCrocodile(20)) != null) newStateTransito(m, deltaMs);
		if ((m = riverLine5.buildShortLogWithTurtles(10)) != null) newStateTransito(m, deltaMs);
		// Do Wind
		if ((m = wind.genParticles(GameLevel)) != null) newStateTransito(m, deltaMs);
	}

	public void froggerKeyboardHandler() throws Exception {
		keyboard.poll();

		boolean keyReleased = false;
		boolean downPressed = keyboard.isPressed(KeyEvent.VK_DOWN);
		boolean upPressed = keyboard.isPressed(KeyEvent.VK_UP);
		boolean leftPressed = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean rightPressed = keyboard.isPressed(KeyEvent.VK_RIGHT);

		if (keyboard.isPressed(KeyEvent.VK_C))
			frogs.get(numeroJogador).cheating = true;
		if (keyboard.isPressed(KeyEvent.VK_V))
			frogs.get(numeroJogador).cheating = false;
		if (keyboard.isPressed(KeyEvent.VK_0)) {
			GameLevel = 10;
			initializeLevel(GameLevel);
		}

		if (downPressed || upPressed || leftPressed || rightPressed)
			keyPressed = true;
		else if (keyPressed)
			keyReleased = true;

		if (listenInput) {
			if (downPressed) {
				newStateMovimento(numeroJogador, 1);
			}
			if (upPressed) {
				newStateMovimento(numeroJogador, 2);
			}
			if (leftPressed) {
				newStateMovimento(numeroJogador, 4);
			}
			if (rightPressed) {
				newStateMovimento(numeroJogador, 3);
			}
			if (keyPressed)
				listenInput = false;
		}

		if (keyReleased) {
			listenInput = true;
			keyPressed = false;
		}

		if (keyboard.isPressed(KeyEvent.VK_ESCAPE))
			GameState = GAME_INTRO;

	}

	public void menuKeyboardHandler() throws Exception {
		keyboard.poll();

		// Following 2 if statements allow capture space bar key strokes
		if (!keyboard.isPressed(KeyEvent.VK_SPACE)) {
			space_has_been_released = true;
		}

		if (!space_has_been_released)
			return;

		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			switch (GameState) {
				case GAME_INSTRUCTIONS:
				case GAME_OVER:
					GameState = GAME_INTRO;
					space_has_been_released = false;
					break;
				default:
					GameLives = FROGGER_LIVES;

					GameScore = 0;
					GameLevel = 1;
					levelTimer = DEFAULT_LEVEL_TIME;

					for(int i=0; i<2; i++) {
						frogs.get(i).setPosition(FROGGER_START.translate(new Vector2D(i * 32, 0)));
					}
					GameState = GAME_PLAY;
					audios.get(numeroJogador).playGameMusic();
					initializeLevel(GameLevel);

			}
		}
		if (keyboard.isPressed(KeyEvent.VK_H))
			GameState = GAME_INSTRUCTIONS;
	}

	public void finishLevelKeyboardHandler() throws Exception {
		keyboard.poll();
		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			GameState = GAME_PLAY;
			audios.get(numeroJogador).playGameMusic();
			initializeLevel(++GameLevel);
		}
	}

	/**
	 * w00t
	 */
	public void update(long deltaMs) {
		switch (GameState) {
			case GAME_PLAY:
				try {
					froggerKeyboardHandler();
				} catch (Exception e) {
					e.printStackTrace();
				}
				wind.update(deltaMs);
				hwave.update(deltaMs);

				//Atualizar para os dois jogadores
				for (int i = 0; i < 2; i++) {
					frogs.get(i).update(deltaMs);
					audios.get(i).update(deltaMs);
					frogsCol.get(i).testCollision(movingObjectsLayer);
				}
				//

				ui.update(deltaMs);
				try {
					cycleTraffic(deltaMs);
				} catch (Exception e) {
					e.printStackTrace();
				}

				//Atualizar para os dois jogadores
				for(int i=0; i<2; i++) {
					// Wind gusts work only when Frogger is on the river
					if (frogsCol.get(i).isInRiver())
						wind.start(GameLevel);
					wind.perform(frogs.get(i), GameLevel, deltaMs);

					// Do the heat wave only when Frogger is on hot pavement
					if (frogsCol.get(i).isOnRoad())
						hwave.start(frogs.get(i), GameLevel);
					hwave.perform(frogs.get(i), deltaMs, GameLevel);
				}

				if (!frogs.get(numeroJogador).isAlive)
					particleLayer.clear();

				goalmanager.update(deltaMs);

				if (goalmanager.getUnreached().size() == 0) {
					GameState = GAME_FINISH_LEVEL;
					audios.get(numeroJogador).playCompleteLevel();
					particleLayer.clear();
				}

				if (GameLives < 1) {
					GameState = GAME_OVER;
				}

				break;

			case GAME_OVER:
			case GAME_INSTRUCTIONS:
			case GAME_INTRO:
				goalmanager.update(deltaMs);
				try {
					menuKeyboardHandler();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					cycleTraffic(deltaMs);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case GAME_FINISH_LEVEL:
				try {
					finishLevelKeyboardHandler();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}

	}

	public void transito(String tipoVeiculo, Coordenadas coordenadas, Coordenadas velocidade, String spriteType, long deltaMs) {
		MovingEntity m;
		Vector2D pos = new Vector2D(coordenadas.getX(), coordenadas.getY());
		Vector2D vel = new Vector2D(velocidade.getX(), velocidade.getY());

		switch (tipoVeiculo) {
			case "Car": {
				m = new Car(pos, vel, spriteType);
				movingObjectsLayer.add(m);
				break;
			}
			case "Crocodile": {
				m = new Crocodile(pos, vel);
				movingObjectsLayer.add(m);
				break;
			}
			case "Particle": {
				m = new Particle(spriteType, pos, vel);
				particleLayer.add(m);
				break;
			}
			case "Truck": {
				m = new Truck(pos, vel);
				movingObjectsLayer.add(m);
				break;
			}
			case "ShortLog": {
				m = new ShortLog(pos, vel);
				movingObjectsLayer.add(m);
				break;
			}
			case "CopCar": {
				m = new CopCar(pos, vel);
				movingObjectsLayer.add(m);
				break;
			}
			case "Turtles": {
				m = new Turtles(pos, vel);
				movingObjectsLayer.add(m);
				break;
			}
			case "LongLog": {
				m = new LongLog(pos, vel);
				movingObjectsLayer.add(m);
				break;
			}
		}
	}



	/**
	 * Rendering game objects
	 */
	public void render(RenderingContext rc) {
		switch(GameState) {
			case GAME_FINISH_LEVEL:
			case GAME_PLAY:
				backgroundLayer.render(rc);

				for (Frogger frog : frogs) {
					if (frog.isAlive) {
						try {
							movingObjectsLayer.render(rc);
						} catch (ConcurrentModificationException ex) {
							System.out.println(ex);
						}
						//frog.collisionObjects.get(0).render(rc);
						frog.render(rc);
					} else {
						frog.render(rc);
						movingObjectsLayer.render(rc);
					}
				}

				particleLayer.render(rc);
				ui.render(rc);
				break;

			case GAME_OVER:
			case GAME_INSTRUCTIONS:
			case GAME_INTRO:
				backgroundLayer.render(rc);
				try {
					movingObjectsLayer.render(rc);
				} catch (ConcurrentModificationException ex) {
					System.out.println(ex);
				}
				ui.render(rc);
				break;
		}
	}

	public static void main (String[] args) throws Exception {
		Main f = new Main(0);
		f.run();
	}

	private void newStateTransito(MovingEntity m, long deltaMs) throws Exception {
		Coordenadas pos = new Coordenadas(m.getPosition().getX(), m.getPosition().getY());
		Coordenadas vel = new Coordenadas(m.getVelocity().getX(), m.getVelocity().getY());

		State gameState = new StateTransito(GameScore, levelTimer, GameLevel, m.getClass().getSimpleName(), pos, vel, m.getName(), deltaMs);
		sendState(gameState);
	}

	private void newStateMovimento(Integer numJog, Integer dir) throws Exception {
		State gameState = new StateMovimento(GameScore, levelTimer, GameLevel, numJog, dir);
		sendState(gameState);
	}

	private void sendState(State state) throws Exception {
		String[] args = new String[1];
		args[0] = state.toString();
		//System.out.println("Main -> sendState(" + state.toString() + ")");
		Emit.main(args);

	}


	public void move(Integer numJog, int direcao) {
		if (direcao == 1) {
			frogs.get(numJog).moveDown();
		} else if (direcao == 2) {
			frogs.get(numJog).moveUp();
		} else if (direcao == 3) {
			frogs.get(numJog).moveRight();
		} else if (direcao == 4) {
			frogs.get(numJog).moveLeft();
		}
	}


}

