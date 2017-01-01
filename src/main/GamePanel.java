package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.JPanel;

import gamestate.PlayState;
import manager.GameStatesManager;
import manager.Keys;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable {

	private static final int MAX_NO_YIELDS = 20;
	private static final int MAX_SKIP_FRAMES = 10;

	// needed to be recalculated
	public static final int PWIDTH = PlayState.GAME_X_END + PlayState.INSET;
	public static final int PHEIGHT = PlayState.GAME_Y_END + PlayState.INSET;

	private Thread animator = null;
	private Image dbImage = null;
	private Graphics dbg = null;

	// game component
	GameStatesManager gsm = new GameStatesManager();

	public GamePanel() {
		// intialize the panel
		this.setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		// intialize the listener
		this.setFocusable(true);
		this.requestFocus();
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Keys.setCurCode(e.getKeyCode());
			}
		});
	}

	@Override
	public void addNotify() {

		super.addNotify();
		if (animator == null) {
			animator = new Thread(this);
			animator.start();
		}
	}

	private void gameUpdate() {
		Keys.update(); // update the internal timer of keys
		gsm.update();
		gsm.handleInput();
	}

	private void gameRender() {
		if (dbImage == null) {
			dbImage = this.createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) {
				System.err.println("cannot create dbgImage");
				return;
			} else {
				dbg = dbImage.getGraphics();
			}
		}

		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);

		gsm.draw(dbg);
	}

	private void paintScreen() {
		Graphics mainG = this.getGraphics(); // get the pen for panel
		if (mainG != null && dbImage != null) {
			mainG.drawImage(dbImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync();
			mainG.dispose();
		}
	}

	@Override
	public void run() {
		long timeDiff, afterTime, sleepTime, oversleepTime = 0L;
		int no_yields = 0;
		long excess = 0L;

		while (true) {
			long beforeTime = System.nanoTime();

			gameUpdate();
			gameRender();
			paintScreen();

			// Calculate time to finish the current rendering
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;

			// Calculate thread sleep time to give a stable FPS
			sleepTime = GameFrame.PERIOD * 1000000L - timeDiff - oversleepTime;

			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime / 1000000L);
					oversleepTime = (System.nanoTime() - afterTime) - sleepTime;
				} catch (Exception e) {
					Logger.getAnonymousLogger().warning(e.getMessage());
				}
			} else {
				oversleepTime = 0L;
				excess -= sleepTime / 1000000L; // nano -> ms
				no_yields++;
			}

			// Voluntarily yield the thread to allow for garbage collector
			if (no_yields >= MAX_NO_YIELDS) {
				Thread.yield();
				no_yields = 0;
			}

			// excess time ??
			int skips = 0;
			while ((excess >= GameFrame.PERIOD) && (skips < MAX_SKIP_FRAMES)) {
				gameUpdate();
				excess -= GameFrame.PERIOD;
				skips++;
			}
		}
	}

}
