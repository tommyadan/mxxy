package com.mxxy.extendpackage;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import com.mxxy.game.event.PanelEvent;
import com.mxxy.game.handler.AbstractPanelHandler;
import com.mxxy.game.modler.SettingModeler;
import com.mxxy.game.utils.ComponentFactory;
import com.mxxy.game.utils.MP3Player;
import com.mxxy.game.utils.SpriteFactory;
import com.mxxy.game.widget.GameSlider;
import com.mxxy.game.widget.ImageCheckBox;
/**
 * SettingsPage (设置)
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
final public class SettingsPage extends AbstractPanelHandler<SettingModeler>{
	private ImageCheckBox  musicCheckBox,windowsCheckBox;
	
	private GameSlider gameSlider;
	@Override
	public void init(PanelEvent evt) {
		super.init(evt);
	}
	
	@Override
	protected void initView() {
		musicCheckBox=findViewById("music");
		windowsCheckBox=findViewById("windows");
		windowsCheckBox.setSelected(true);
		musicCheckBox.setSelected(MP3Player.isPlayer);
		musicCheckBox.addItemListener(this);
		initJSlider();
	}
	public void initJSlider(){
		Image ball1 = SpriteFactory.loadImage("componentsRes/ball1.png");
		Image ball2 = SpriteFactory.loadImage("componentsRes/ball2.png");
		Image ball3 = SpriteFactory.loadImage("componentsRes/ball3.png");
		gameSlider=ComponentFactory.createSlider(0, 100, 0,ball1, ball2, ball3,
				SpriteFactory.loadImage("componentsRes/pos1.png"), SpriteFactory.loadImage("componentsRes/pos2.png"),
				this, SwingConstants.HORIZONTAL);
		gameSlider.setBounds(112, 127, 200, 15);
		panel.add(gameSlider,0);

	}

	String state = null;
	@Override
	public void itemStateChanged(ItemEvent e) {
		//保存状态
		JCheckBox checkBox=(JCheckBox) e.getSource();
		boolean on = false;
		if(checkBox==musicCheckBox){
			on=checkBox.isSelected();
			state="music";
			modler.stopMusic(on,panel.getScene());
		}
	}
	/**
	 * 退出游戏
	 * @param e
	 */
	public void exit(ActionEvent e){
		application.exitGame();
	}

	/**
	 * 保存设置
	 * @param e
	 */
	public void saveSetting(ActionEvent e){
		modler.saveSetting(propertiesConfigManager,state,musicCheckBox.isSelected());
	}
	/**
	 * 重启TODO
	 * @param e
	 */
	public void restartGame(ActionEvent e){
		// 		RuntimeUtil.exec("java "+RuntimeUtil.getJarExecPath(SwingApplication.class)+"com/mxxy/game/base/SwingApplication");
		// 		application.exitGame();
	}

	@Override
	protected String setConfigFileName() {
		return "save/setting.properties";
	}
}
