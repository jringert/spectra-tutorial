package traffic;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import tau.smlab.syntech.controller.executor.ControllerExecutor;
import tau.smlab.syntech.controller.jit.BasicJitController;

public class TrafficSimulator extends JComponent {

	private static final long serialVersionUID = 1L;
	ControllerExecutor executor;
	boolean carA;
	boolean carB;

	int carA_x;
	int carA_y;
	int carB_x;
	int carB_y;
	boolean turnA;
	boolean turnB;

	boolean greenA;
	boolean greenB;

	BufferedImage carA_img;
	BufferedImage carA_turn_left_img;
	BufferedImage carB_img;
	BufferedImage carB_turn_right_img;
	BufferedImage background;
	BufferedImage red_img;
	BufferedImage green_img;

	Thread thread;

	public TrafficSimulator() {
		Thread animationThread = new Thread(new Runnable() {
			public void run() {

				Random rand = new Random();

				Map<String, String> inputs = new HashMap<>();

				try {

					inputs.put("carA", "true");
					inputs.put("carB", "false");

					// Instantiate a new controller executor
					executor = new ControllerExecutor(new BasicJitController(), "out/");
					executor.initState(inputs);

					greenA = Boolean.parseBoolean(executor.getCurrValue("greenA"));
					greenB = Boolean.parseBoolean(executor.getCurrValue("greenB"));

				} catch (Exception e) {
					e.printStackTrace();
				}

				while (true) {

					inputs.clear();

					try {

						carA = rand.nextBoolean();
						carB = rand.nextBoolean();
						inputs.put("carA", String.valueOf(carA));
						inputs.put("carB", String.valueOf(carB));

						executor.updateState(inputs);

						greenA = Boolean.parseBoolean(executor.getCurrValue("greenA"));
						greenB = Boolean.parseBoolean(executor.getCurrValue("greenB"));

					} catch (Exception e) {
						// The above inputs violate the assumptions
						System.err.println(e.getMessage());
					}

					try {

						carA_x = 75;
						carA_y = 450;
						carB_x = 310;
						carB_y = 450;

						if (greenA & carA) {

							for (int i = 0; i < 10; i++) {
								turnA = false;
								repaint();
								Thread.sleep(150);
								carA_y = -28 * i + 450;
							}
							for (int i = 0; i < 5; i++) {
								turnA = true;
								repaint();
								Thread.sleep(150);
								carA_x = 22 * i + 80;
							}
							for (int i = 0; i < 5; i++) {
								turnA = false;
								repaint();
								Thread.sleep(150);
								carA_y = -65 * i + 170;
							}

							repaint();
							Thread.sleep(1000);

						} else if (greenB & carB) {

							for (int i = 0; i < 10; i++) {
								turnB = false;
								repaint();
								Thread.sleep(50);
								carB_y = -28 * i + 450;
							}
							for (int i = 0; i < 5; i++) {
								turnB = true;
								repaint();
								Thread.sleep(50);
								carB_x = -22 * i + 305;
							}
							for (int i = 0; i < 5; i++) {
								turnB = false;
								repaint();
								Thread.sleep(50);
								carB_y = -65 * i + 170;
							}

							repaint();
							Thread.sleep(1000);

						} else {
							repaint();
							Thread.sleep(2000);
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		});

		try {
			carA_img = ImageIO.read(new File("img/180/car0.png"));
			carA_turn_left_img = ImageIO.read(new File("img/270/car0.png"));
			carB_img = ImageIO.read(new File("img/180/car1.png"));
			carB_turn_right_img = ImageIO.read(new File("img/90/car1.png"));
			background = ImageIO.read(new File("img/backgroundWithRedLights.png"));
			red_img = ImageIO.read(new File("img/redlight.png"));
			green_img = ImageIO.read(new File("img/greenlight.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		animationThread.start();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {

		g.drawImage(background, 0, 0, null);

		if (carA) {
			if (turnA) {
				g.drawImage(carA_turn_left_img, carA_x, carA_y, 94, 51, null);
			} else {
				g.drawImage(carA_img, carA_x, carA_y, 51, 94, null);
			}
		}

		if (carB) {
			if (turnB) {
				g.drawImage(carB_turn_right_img, carB_x, carB_y, 94, 51, null);
			} else {
				g.drawImage(carB_img, carB_x, carB_y, 51, 94, null);
			}
		}

		if (greenA) {
			g.drawImage(green_img, 140, 308, 42, 92, null);
		}

		if (greenB) {
			g.drawImage(green_img, 368, 308, 42, 92, null);
		}

	}

	public static void main(String args[]) throws Exception {
		JFrame f = new JFrame("Traffic Simulator");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(480, 552);
		TrafficSimulator traffic = new TrafficSimulator();
		f.setContentPane(traffic);
		f.setVisible(true);
	}

}
