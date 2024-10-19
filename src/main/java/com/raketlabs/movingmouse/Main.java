package com.raketlabs.movingmouse;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {

	private static final int FRAME_RATE = 50;
	private static final int DELAY = 5 * 1000;
	private static final LocalTime SCHEDULE_MOVEMENT = LocalTime.of(21, 9);
	private static final int[] points = new int[] { 100, 100, 200, 200, 300, 300 };

	private final Queue<Runnable> tasks = new LinkedList<>();

	public Main() throws AWTException, InterruptedException {
		Image appIcon = Helper.loadIcon("/icon_google.png");
		JFrame window = new JFrame();
		window.setSize(new Dimension(320, 240));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setIconImage(appIcon);
		window.setVisible(true);

		JPanel container = new JPanel();
		window.setContentPane(container);

		JLabel label = new JLabel();
		Font defaultFont = label.getFont();
		Font biggerFont = new Font(defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() * 4);
		label.setFont(biggerFont);
		container.add(label);

		final AtomicInteger remainingTime = new AtomicInteger(DELAY);

		new Timer(FRAME_RATE, e -> {
			remainingTime.set(remainingTime.get() - FRAME_RATE);

			try {
				Point point = MouseInfo.getPointerInfo().getLocation();
				window.setTitle(String.format("%s, %s", point.x, point.y));
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}
		}).start();

		new Timer(1000, e -> {
			if (remainingTime.get() <= 0) {
				remainingTime.set(DELAY);
				tasks.add(() -> doRandomMouseMovement());
			}
			label.setText(Helper.textTime(remainingTime.get()));
		}).start();

		doWorkOn(SCHEDULE_MOVEMENT, () -> tasks.add(() -> doSpecifiedMouseMovement(points)));

		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		final Runnable r = () -> {
			while (!tasks.isEmpty()) {
				tasks.poll().run();
			}
		};
		executorService.scheduleAtFixedRate(r, 0, 10, MILLISECONDS);
	}

	private void doRandomMouseMovement() {
		Random random = new Random();
		final int min = 200;
		int x = random.nextInt(100) - 50;

		if (x < 0)
			x -= min;
		else
			x += min;

		int y = random.nextInt(100) - 50;

		if (y < 0)
			y -= min;
		else
			y += min;

		try {
			System.out.println("Making a move");
			Helper.moveMouseBy(x, y);
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

	private void doSpecifiedMouseMovement(int... points) {
		for (int f = 0; f < 10; f++)
			for (int i = 0; i < points.length / 2; i++) {
				int a = i * 2;
				int b = a + 1;
				int x = points[a];
				int y = points[b];
				try {
					Helper.moveMouseTo(x, y);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}

	}

	private void doWorkOn(LocalTime schedule, Runnable runnable) {
		final LocalTime now = LocalTime.now();
		final long ms = Duration.between(now, schedule).toMillis();
		if (ms > 0) {
			final int msInt = Math.toIntExact(ms);
			Timer timer = new Timer(msInt, e -> {
				runnable.run();
			});
			timer.setRepeats(false);
			timer.start();
		}
	}

	public static void main(String... args) {
		SwingUtilities.invokeLater(() -> {
			try {
				new Main();
			} catch (AWTException | InterruptedException e) {
				e.printStackTrace();
			}
		});

		System.out.println("end");
	}
}