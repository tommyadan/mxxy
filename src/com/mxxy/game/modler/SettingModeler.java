package com.mxxy.game.modler;

import com.mxxy.game.config.IConfigManager;
import com.mxxy.game.utils.MP3Player;

public class SettingModeler {

	public void saveSetting(IConfigManager propertiesConfigManager,String settingName,boolean on) {
		propertiesConfigManager.put(settingName, String.valueOf(on));
		propertiesConfigManager.saveConfig();
	}

	public void stopMusic(boolean on,String string) {
		if(!on){
			MP3Player.stopLoop();
		}else{
			if(!MP3Player.isPlayer)
				MP3Player.loop("music/"+string+".mp3");
		}
	}
}
