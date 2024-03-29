package com.mygdx.jkdomino;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.jkdomino.scenes.GameScene;

public class Domino extends Game {
	public static AssetManager assetManager;
	public static float SH = 960;
	public static float SW = 540;

	@Override
	public void create() {
		assetManager = new AssetManager();
		loadAssets();
		assetManager.finishLoading();
		initAssets();
		setScreen(new GameScene(this));
	}

	private void initAssets() {

	}

	private void loadAssets() {
		assetManager.load("badlogic.jpg",Texture.class);
		assetManager.load("domino.png",Texture.class);
	}
}
