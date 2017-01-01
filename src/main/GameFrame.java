package main;

import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;

import loader.FontLoader;
import loader.ImagesLoader;

public class GameFrame extends JFrame {

	public static int FPS = 20;
	public static int PERIOD = 50; // ms

	public GameFrame() {
		super("Magic Tower");

		ImagesLoader.init();
		FontLoader.init();
		initGUI();
		initLocation();

		this.setResizable(false);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private void initGUI() {
		GamePanel gamePanel = new GamePanel();
		Container c = getContentPane();
		c.add(gamePanel);
		// debugFrame debugFrame = new debugFrame();
		// c.add(debugFrame);
	}

	private void initLocation() {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		Rectangle screenRect = gc.getBounds();
		this.setLocation(screenRect.width / 4, screenRect.height / 12);
	}

	public static void main(String[] args) {
		new GameFrame();
	}
}
