package com.mxxy.game.base;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.mxxy.game.config.DataStoreManager;
import com.mxxy.game.config.PlayerVO;
import com.mxxy.game.config.PropertiseConfigImpl;
import com.mxxy.game.handler.GamePanelController;
import com.mxxy.game.ui.GameFrame;
import com.mxxy.game.ui.GamePanel;
import com.mxxy.game.ui.IWindows;
import com.mxxy.game.ui.LoadingPanel;
import com.mxxy.game.ui.LoginPanel;
import com.mxxy.game.utils.PanelManager;

public class SwingApplication extends Application {

	private GameFrame gameFrame;

	private LoadingPanel loadingPanel;

	@Override
	protected IWindows createWindows() {
		gameFrame=new GameFrame();
		gameFrame.initContent(context);
		loadingPanel=new LoadingPanel(312, 104);
		return gameFrame;
	}
	private PropertiseConfigImpl config;
	@Override
	protected void loadeResourceProgress(int i) {
		loadingPanel.getjProgressBar().setMinimum(0);
		config = (PropertiseConfigImpl) objects[1];
		loadingPanel.getjProgressBar().setMaximum(config.getPropertiseSize());
		loadingPanel.getjProgressBar().setValue(i);
	}
	@Override
	protected void loadingpanel() {
		gameFrame.showPanel(loadingPanel);
	}

	@Override
	protected void loadeResourceEnd() {
		showHomePager();
	}

	public void showHomePager(){
		LoginPanel loginPanel=new LoginPanel();
		Panel panel = PanelManager.getPanel("HomePager");
		loginPanel.setUIhelp(getUiHelp());
		loginPanel.setConfigManager(config);
		loginPanel.playMusic();
		gameFrame.showPanel(loginPanel);
		getUiHelp().showPanel(panel);
	}

	
	@Override
	public void enterGame(PlayerVO data) {
		gameFrame.setIsfristApplication(false);
		GamePanel gamePanel=new GamePanel();
		Panel gamePager = PanelManager.getPanel("GamePager");
		gamePanel.setConfigManager(config);
		/** 由于是覆盖在上面的 所以需要将事件传递给父容器*/
		gamePager.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Container parent = gamePager.getParent();
				Point p = e.getPoint();
				int x = gamePager.getX();
				int y = gamePager.getY();
				MouseEvent event = new MouseEvent(parent, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), e
						.getModifiers(), x + p.x, y + p.y, e.getClickCount(), false);
				parent.dispatchEvent(event);
			}
		});
		DataStoreManager dataStore = (DataStoreManager)objects[0];
		dataStore.initData(data);
		dataStore.loadSceneNpc();
		dataStore.loadSceneTeleporter();
		gamePanel.setContext(dataStore.getContext());
		gamePanel.setDataStore(dataStore);
		gamePanel.initGameDate();
		gamePanel.setUIhelp(getUiHelp());
		gamePanel.playMusic();
		gameFrame.showPanel(gamePanel);
		new GamePanelController(gamePanel);
		getUiHelp().showPanel(gamePager);
	}

	/**
	 * main 程序入口
	 */
	public static void main(String[] args) {
		Application application=new SwingApplication();
		application.startGame();
	}
}

