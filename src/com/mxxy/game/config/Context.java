package com.mxxy.game.config;

import com.mxxy.game.sprite.Players;
import com.mxxy.game.ui.IWindows;
import com.mxxy.net.IClient;

public class Context {
	private Players player;
	private String scene;
	private IWindows windows;
	private IClient client;
	public void setWindows(IWindows windows) {
		this.windows = windows;
	}
	
	public IClient getClient() {
		return client;
	}
	public void setClient(IClient client) {
		this.client = client;
	}
	public IWindows getWindows() {
		return windows;
	}
	public void setPlayer(Players player) {
		this.player = player;
	}
	public Players getPlayer() {
		return player;
	}
	public void setScene(String scene) {
		this.scene = scene;
	}
	public String getScene() {
		return scene;
	}
}
