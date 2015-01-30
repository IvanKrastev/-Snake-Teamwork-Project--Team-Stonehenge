import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;


public class snakeCanvas extends Canvas implements Runnable {
	private final int BOX_HEIGHT = 5;
	private final int BOX_WIDTH = 5;
	private final int GRID_WIDTH = 30;
	private final int GRID_HEIGHT = 30;
	
	private LinkedList<Point> snake;
	private Point fruit;
	
	private Thread runThread;
	private Graphics globalGraphics; 
	
	public void paint(Graphics g){
		snake = new LinkedList<Point>();
		fruit = new Point();
		globalGraphics = g.create();
		if(runThread == null) {
			runThread = new Thread(this);
			runThread.start();
		}
	}
		
	public void Draw (Graphics g) {
		DrawGrid(g);
		DrawSnake(g);
		DrawFruit(g);
	}
	
	public void Move() {
		
	}
	
	public void DrawGrid (Graphics g) {
		//drawing an outside rectangle
		g.drawRect(0, 0, GRID_WIDTH*BOX_WIDTH, GRID_HEIGHT*BOX_HEIGHT);
		//drawing of the vertical lines
		for (int x = BOX_WIDTH; x < GRID_WIDTH*BOX_WIDTH; x+= BOX_WIDTH) {
			g.drawLine(x, 0, x, BOX_HEIGHT * GRID_HEIGHT);
		}
		//drawing the horizontal lines
		for (int y = BOX_HEIGHT; y < GRID_HEIGHT*BOX_HEIGHT; y+= BOX_HEIGHT) {
			g.drawLine(0,  y, BOX_WIDTH * GRID_WIDTH, y);
		}
	}
	
	public void DrawSnake (Graphics g) {
		g.setColor(Color.GREEN);
		for (Point p : snake) {
			g.fillRect(p.x, p.y, BOX_WIDTH, BOX_HEIGHT);
		}
		g.setColor(Color.BLACK);
	}
	
	public void DrawFruit (Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(fruit.x, fruit.y, BOX_WIDTH, BOX_HEIGHT);
	}

	@Override
	public void run() {
		while (true) {
			//runs indefinitely
			Move();
			Draw(globalGraphics);
			
			try {
				Thread.currentThread();
				Thread.sleep(100);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
