package com.mxxy.extendpackage;


import java.awt.event.ActionEvent;

import com.mxxy.game.event.PanelEvent;
import com.mxxy.game.handler.AbstractPanelHandler;
import com.mxxy.game.utils.SpriteFactory;
import com.mxxy.game.widget.Label;

/**
 * FishingPager (钓鱼)
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
final public class FishingPager extends AbstractPanelHandler{

	private Label fishingRod;
	@Override
	public void init(PanelEvent evt) {
		super.init(evt);
	}
	@Override
	protected void initView() {
		Label label = findViewById("head");
		fishingRod = findViewById("fishing_rod");
		Label name = findViewById("name");
		name.setText(player.getPersonName());
		label.setAnim(SpriteFactory.loadAnimation("/wzife/photo/facesmall/"+player.getCharacter()+".tcp"));
	}
	
	public void grod(ActionEvent e){
		fishingRod.setAnim(SpriteFactory.loadAnimation("/wzife/fishing/fishermen3.tcp"));
	}
}
