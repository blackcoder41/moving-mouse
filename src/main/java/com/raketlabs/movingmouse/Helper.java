package com.raketlabs.movingmouse;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.util.Random;

import javax.swing.ImageIcon;

public class Helper {

	public static Image loadIcon(String path) {
		return new ImageIcon(Helper.class.getResource(path)).getImage();
	}

	public static String textTime(int ms) {
		final int sec = 1000;
		final int min = 60 * sec;
		final int hrs = 60 * min;
		final int day = 24 * hrs;

		if (ms >= day) {
			return (ms / day) + " d " + textTime(ms - (day * (ms / day)));
		}

		if (ms >= hrs) {
			return (ms / hrs) + " h " + textTime(ms - (hrs * (ms / hrs)));
		}

		if (ms >= min) {
			return (ms / min) + " m " + textTime(ms - (min * (ms / min)));
		}

		return (ms / sec) + " s";
	}

	public static double easeInOut(double t) {
		return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
	}

	public static void moveMouseTo(int destX, int destY) throws AWTException {
		Robot robot = new Robot();
		Random random = new Random();
		int startX = (int) MouseInfo.getPointerInfo().getLocation().getX();
		int startY = (int) MouseInfo.getPointerInfo().getLocation().getY();

		double speed = 0.07; // movement speed

		for (double t = 0; t < 1; t += speed) {
			int currentX = (int) (startX + Helper.easeInOut(t) * (destX - startX));
			int currentY = (int) (startY + Helper.easeInOut(t) * (destY - startY));

			// Add a sine wave for curve effect
			double curveAmplitude = 10;
			currentX += (int) (curveAmplitude * Math.sin(Math.PI * t));
			currentY += (int) (curveAmplitude * Math.sin(Math.PI * t));

			robot.mouseMove(currentX, currentY);

			try {
				Thread.sleep(30 + random.nextInt(20));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		robot.mouseMove(destX, destY);
	}

	public static void moveMouseBy(int x, int y) throws AWTException {
		int startX = (int) MouseInfo.getPointerInfo().getLocation().getX() + x;
		int startY = (int) MouseInfo.getPointerInfo().getLocation().getY() + y;
		moveMouseTo(startX, startY);
	}

	public static void click() throws AWTException {
		Robot robot = new Robot();
		robot.mousePress(BUTTON1_DOWN_MASK);
		robot.mouseRelease(BUTTON1_DOWN_MASK);
	}

}
