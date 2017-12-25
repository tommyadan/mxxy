package com.mxxy.game.base;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import com.mxxy.game.sprite.Players;

@SuppressWarnings("serial")
abstract public class AbstactPanel extends DrawPaneImp {
	/**
	 *drawPlayer (绘制人物)
	 */
	public void drawPlayers(Graphics2D g, long elapsedTime){
		Players players=getPlayer();
		if(players!=null){
			players.setHover(isHover(players));
			Point p = players.getLocation();
			p = localToView(p);
			players.update(elapsedTime);
			players.draw(g, p.x, p.y);
		}
	}
	/**
	 * drawNPC (绘制NPC)
	 */
	public void drawNpc(Graphics2D g, long elapsedTime){
		if(npcList==null){
			return;
		}
		for (int i = 0; i < npcList.size(); i++) {
			Players npc = (Players) npcList.get(i);
			npc.setHover(isHover(npc));
			Point p = npc.getLocation();
			p = localToView(p);
			npc.update(elapsedTime);
			npc.draw(g, p.x, p.y);
		}
	}
	/**
	 * judge mouse hover  (判断鼠标悬停)
	 * @param players
	 * @return
	 */
	public boolean isHover(Players players){
		Point point=getPointCursor();
		if (point == null) {
			return false;
		}
		Point vp = localToView(players.getLocation());
		boolean hover = players.contains(point.x - vp.x, point.y - vp.y);
		return hover;
	}

	public void romveNpc(int index){
		npcList.remove(index);
	}
	public void clearNpc(){
		npcList.clear();
	}
//	private TalkAction talkAction =new TalkAction();
	/**
	 * addNpcAndListener
	 * @param players
	 */
	public void addNpc(Players players){
		npcList.add(players);
		players.removeAllListeners();
//		players.addPlayerListener(talkAction);
	}
	
	public List<Players> getNpcList() {
		return npcList;
	}
}

