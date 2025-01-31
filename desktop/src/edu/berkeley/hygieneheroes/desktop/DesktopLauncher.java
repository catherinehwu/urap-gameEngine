package edu.berkeley.hygieneheroes.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import edu.berkeley.hygieneheroes.BoardGameEngine;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Hygiene Heroes";
		config.width = 800;
		config.height = 480;

		new LwjglApplication(new BoardGameEngine(), config);
	}
}
