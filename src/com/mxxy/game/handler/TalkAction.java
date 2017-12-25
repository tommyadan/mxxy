package com.mxxy.game.handler;

import com.mxxy.game.event.PlayerEvent;
import com.mxxy.game.event.PlayerListenerAdapter;
import com.mxxy.game.sprite.Players;
import com.mxxy.game.ui.TalkPanel;

public class TalkAction  extends PlayerListenerAdapter{
	@Override
	public void talk(PlayerEvent evt) {
		Players player = evt.getPlayer();
		showTalkPanel(player.getCharacter());
	}
	public void showTalkPanel(String charcater){
//		if(charcater.equals("3017")){
//			PalettePanel palettePanel=new PalettePanel();
//			palettePanel.setCharacter(GameMain.getDataStore().getCharacter());
//			palettePanel.setColorations(GameMain.getDataStore().getColorations());
//			palettePanel.onCreate();
//			GameMain.getContainer().add(palettePanel);
//			return;
//		}
		TalkPanel talkPanel =null;
		talkPanel.stopDraw();
		talkPanel.setScreenSize(482, 167);
//		DataStore dataStore = GameMain.getDataStore();
		talkPanel.setCharacter(charcater);
		talkPanel.setLocation(180, 320);
//		talkPanel.initTalk(dataStore.findNpcChat(charcater));
	}
}
