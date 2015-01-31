import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;

public class SnakeCanvas extends Canvas implements Runnable, KeyListener{

	private final int BOX_HEIGHT = 15;
	private final int BOX_WIDTH = 15;
	private final int GRID_WIDTH = 25;
	private final int GRID_HEIGHT = 25;
	
	private LinkedList<Point> snake;
	private Point fruit;
	private int direction = Direction.NO_DIRECTION;
	
	private Thread runThread;
	private Graphics globalGraphics;
	private int score = 0;
	private String highScore = "";
	
	
	public void init() {
		
	}
	
	
	public void paint(Graphics g) {
		this.setPreferredSize(new Dimension(640,480));
		this.addKeyListener(this);
		
		if (snake == null) {  // initialize the snake
			snake = new LinkedList<Point>();
			GenerateDefaultSnake();
			PlaceFruit();
		}
		if (runThread == null){  // initialize the runThread
			runThread = new Thread(this);
			runThread.start();
		}
		if (highScore.equals("")) {
			// initialize the highScore
			highScore = this.GetHighScore();	
		}
		
		DrawFruit(g);
		DrawGrid(g);
		DrawSnake(g);
		DrawScore(g);
		
	}
	
	public void update(Graphics g) {
		// this is the default update method which will contain our double buffering
		Graphics offScreenGraphics; // these are the graphics we will use to draw offscreen
		BufferedImage offscreen = null;
		Dimension d = this.getSize();
		
		offscreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics = offscreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		offScreenGraphics.setColor(this.getForeground());
		paint (offScreenGraphics);
		
		// flip
		g.drawImage(offscreen, 0, 0, this);
		
	}
	
	
	public void GenerateDefaultSnake() {
		score = 0;
		snake.clear();
		
		snake.add(new Point(0,2));
		snake.add(new Point(0,1));
		snake.add(new Point(0,0));
		direction = Direction.NO_DIRECTION;
	}
	
	
	public void Move() {
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch (direction) {
			case Direction.NORTH;
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction.SOUTH;
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction.WEST;
				newPoint = new Point(head.x - 1, head.y);
				break;
			case Direction.EAST;
				newPoint = new Point(head.x + 1, head.y);
				break;
		}
		
		snake.remove(snake.peekLast());
		
		if (newPoint.equals(fruit)) {
			// the snake has hit fruit
			score += 10;
			Point addPoint = (Point) newPoint.clone();
			switch (direction) {
				case Direction.NORTH;
					newPoint = new Point(head.x, head.y - 1);
					break;
				case Direction.SOUTH;
					newPoint = new Point(head.x, head.y + 1);
					break;
				case Direction.WEST;
					newPoint = new Point(head.x - 1, head.y);
					break;
				case Direction.EAST;
					newPoint = new Point(head.x + 1, head.y);
					break;
			}
			snake.push(addPoint);
			PlaceFruit();
		}
		
		else if (newPoint.x < 0 || newPoint.x > (GRID_WIDTH -1)) {
			// we went out of boundaries, reset game
			CheckScore();
			GenerateDefaultSnake();
			return;
		}
		else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT - 1)) {
			// we went out of boundaries, reset game
			CheckScore();
			GenerateDefaultSnake();
			return;
		}
		else if (snake.contains(newPoint)) {
			// we ran into ourselves, reset game
			CheckScore();
			GenerateDefaultSnake();
			return;
		}
		// if we reach this point in code, we are good
		snake.push(newPoint);
	}
	
	public void DrawScore(Graphics g) {
		g.drawString("Score: " + score, 0, BOX_HEIGHT * GRID_HEIGHT + 10);
		g.drawString("Highscore: " + highScore, 0, BOX_HEIGHT * GRID_HEIGHT + 20);
	}
	
	public void CheckScore() {
		if (highScore.equals(""))
			return;
		// format  Todor:100
		if (score > Integer.parseInt(highScore.split(":")[1])) {
			// user has set a new record
			String name = JOptionPane.showInputDialog("You have set a new highscore. " +
					                                          "What is your name?");
			highScore = name + ":" + score;
			
			File scoreFile = new File("highscore.dat");
			if (!scoreFile.exists()) {
				try {
					scoreFile.createNewFile();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try {
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highScore);
			}
			catch (Exception e) {
				// errors
			}
			finally {
				try {
					if (writer != null)
						writer.close();
				}
				catch (Exception e) {
					
				}
			}
		}
	}
	
	public void DrawGrid(Graphics g) {
		// drawing an outside rectangle
		g.drawRect(0, 0, GRID_WIDTH *BOX_WIDTH, GRID_HEIGHT * BOX_HEIGHT);
		// drawing the vertical lines
		for (int x = BOX_WIDTH; x < GRID_WIDTH * BOX_WIDTH; x += BOX_WIDTH) { //165
			g.drawLine(x, 0, x, BOX_HEIGHT * GRID_HEIGHT);
		}
	}
	
	public void DrawSnake(Graphics g) {
		g.setColor(Color.GREEN);
		for (Point p : snake) {
			g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
		g.setColor(Color.BLACK);
	}
		
	public void DrawFruit(Graphics g) {
		g.setColor(Color.RED); // this is line 191
		g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		g.setColor(Color.BLACK);
	}
	
	public void PlaceFruit() {
		Random rand = new Random();
		int randomX = rand.nextInt(GRID_WIDTH);
		int randomY = rand.nextInt(GRID_HEIGHT);
		Point randomPoint = new Point(randomX, randomY);
		while(snake.contains(randomPoint)){
			randomX = rand.nextInt(GRID_WIDTH);
			randomY = rand.nextInt(GRID_HEIGHT);
			randomPoint = new Point(randomX, randomY);
		}
		fruit = randomPoint;
	}
		
	@Override
	public void run() {
		while(true){
			// runs indefinitly
			Move();
			repaint(); // recalls the update paint method once again
			
			try {
				Thread.currentThread();
				Thread.sleep(100);
			}
			catch (Exception e) { // line num 222
				e.printStackTrace();
			}
		}
	}
	
	public String GetHighScore() {
		// format:  Todor:100
		FileReader readFile = null;
		BufferedReader reader = null;
		try {
			readFile = new FileReader("highscore.dat"); // it can be a .txt file, but it is editable
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		catch (Exception e) {
			return "Nobody:0";
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP;
			if (direction != Direction.SOUTH)
				direction = Direction.NORTH;
			break;
		case KeyEvent.VK_DOWN;
			if (direction != Direction.NORTH)
				direction = Direction.SOUTH;
			break;
		case KeyEvent.VK_RIGHT;
		}
	}
	
	
	
	
 	
}
