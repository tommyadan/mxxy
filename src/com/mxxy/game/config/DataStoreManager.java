package com.mxxy.game.config;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import com.mxxy.game.astar.NpcBean;
import com.mxxy.game.astar.NpcBean.NpcListbean;
import com.mxxy.game.astar.SceneJump;
import com.mxxy.game.astar.SceneJump.SceneJumpBean;
import com.mxxy.game.resources.SceneNpc;
import com.mxxy.game.resources.SceneTeleporter;
import com.mxxy.game.sprite.Players;
import com.mxxy.game.sprite.Sprite;
import com.mxxy.game.utils.Constant;
import com.mxxy.game.utils.JsonUtils;
/**
 * 数据管理
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
public class DataStoreManager  implements IDataManager{

	private Context context;

	private String character;

	private int colorations[];

	public DataStoreManager(Context context) {
		this.context=context;
		JsonUtils.getInstanceJsonUtils();
	}

	public void initData(PlayerVO playerVO) {
		System.out.println("creating game data: " + new Date());
		Players p = createPlayer(playerVO);
		if (p != null) {
			this.context.setPlayer(p);
			this.context.setScene(playerVO.getSceneId());
		}
		System.out.println("create game data: " + new Date());
	}
	
	public Players createPlayer(PlayerVO data){
		Players player = new Players(null, data.getName(), data.getCharacter(),true,true);
		player.showNameFlage=true;
		player.setData(data);
		return player;
	}
	
	/**
	 * @return
	 */
	public Players createPlayer(SceneNpc sceneNpc){
		boolean showWeapon=sceneNpc.getCharacterId().equals("0010")||sceneNpc.getCharacterId().equals("0001");
		Players player = new Players(null,sceneNpc.getName(),sceneNpc.getCharacterId(),showWeapon,true);
		player.showNameFlage=true;
		player.setDescribe(sceneNpc.getDescribe());
		player.setWeaponIndex("59");//设置武器
		player.setSceneLocation(new Point(sceneNpc.getSceneX(),sceneNpc.getSceneY()));
		player.setState(sceneNpc.getState());
		player.setDirection(sceneNpc.getDirection());   //设置方向
		if(showWeapon){
			int[] colorations = new int[3];
			colorations[0] = 4;  //头发颜色
			colorations[1] = 4;  //飘带颜色
			colorations[2] = 6;  //衣服颜色
			player.setColorations(colorations, true);
		}
		player.setNameBackground(Constant.TEXT_NAME_NPC_COLOR);
		return player;
	}

	private Map<String,List<SceneNpc>> sceneNpcMap=new HashMap<String, List<SceneNpc>>();


	private Map<String, List<SceneTeleporter>> jumpMap=new HashMap<String, List<SceneTeleporter>>();

	@Override
	public List<SceneNpc> findSceneNpc(String sceneId) {
		return getSceneNpcList(sceneId);
	}

	/**
	 * 加载对应场景ID的NPC实例
	 */
	public void loadSceneNpc(){
		NpcBean fromJson = JsonUtils.parses(show("npc"), NpcBean.class);
		for (int i = 0; i < fromJson.NpcList.size(); i++) {
			NpcListbean productListbean = fromJson.NpcList.get(i);
			registSceneNpc(String.valueOf(productListbean.sceneId),
					new SceneNpc(
							productListbean.sceneId,
							productListbean.characterId, 
							productListbean.name, 
							productListbean.sceneX, 
							productListbean.sceneY,
							productListbean.state,
							productListbean.describe,
							productListbean.direction));
		}
	}

	@Override
	public List<SceneTeleporter> findJump(String sceneId) {
		return getSceneTeleporter(sceneId);
	}

	/**
	 * loadTriggerJump(加载跳转点)
	 */
	public void loadSceneTeleporter(){
		SceneJump scenejump = JsonUtils.parses(show("scenejump"), SceneJump.class);
		for (int i = 0; i < scenejump.getSceneJump().size(); i++) {
			SceneJumpBean s = scenejump.getSceneJump().get(i);
			registerTrigger(String.valueOf(s.getGoalId()),
					new SceneTeleporter(s.getOriginId(), s.getGoalId(), s.getStartPoint(), s.getEndPoint() ));
		}
	}

	public  String show(String fileName){
		try {
			FileWork work=new FileWork(fileName);
			work.execute();
			return	work.get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加载NPC
	 * @author dell
	 */
	class FileWork extends SwingWorker<String, Void>{
		private String fileName;
		public FileWork(String filename){
			this.fileName=filename;
		}
		StringBuilder stringBuilder=new StringBuilder();
		@Override
		protected String doInBackground() throws Exception {
			try {
				@SuppressWarnings("resource")
				BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream("uiConfig/"+fileName+".json"),"utf-8"));
				String inString=null;
				while((inString=bufferedReader.readLine())!=null){
					stringBuilder.append(inString);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stringBuilder.toString();
		}
	}

	
	public void registerTrigger(String scenceId, SceneTeleporter t) {
		List<SceneTeleporter> listTrigger = this.jumpMap.get(scenceId);
		if (listTrigger == null) {
			listTrigger = new ArrayList<SceneTeleporter>();
			this.jumpMap.put(scenceId, listTrigger);
		}
		listTrigger.add(t);
	}

	
	private void registSceneNpc(String scenceId, SceneNpc key) {
		List<SceneNpc> sceneList = this.sceneNpcMap.get(scenceId);
		if(sceneList==null){
			sceneList=new ArrayList<SceneNpc>();
			this.sceneNpcMap.put(scenceId, sceneList);
		}
		sceneList.add(key);
	}

	
	public List<SceneNpc> getSceneNpcList(String sceneid){
		return sceneNpcMap.get(sceneid);
	}

	
	public List<SceneTeleporter> getSceneTeleporter(String sceneid){
		return jumpMap.get(sceneid);
	}

	private String lastchat="#Y 代码完善中 #29。";
	@Override
	public String findNpcChat(String npcId) {

		return lastchat;
	}

	public Players createPlayer(String character, int[] colorations) {
		Players player = new Players(null, "未命名", character,false,true);
		player.setColorations(colorations, true);
		player.setState(Players.STATE_STAND);
		player.setDirection(Sprite.DIRECTION_BOTTOM_RIGHT);
		return player;
	}
	public Players createElf(String type,String name,int level) {
		Players player = createPlayer(type,(int[])null);
		player.setPersonName(name);
		//初始化怪物属性
		//		PlayerVO data = new PlayerVO("elf-"+(++elfId),name,type);
		//		data.level = level;
		//初始化场景怪物的属性
		//		initElf(data);
		//		player.setData(data);
		//player.setNameForeground(GameMain.TEXT_NAME_NPC_COLOR);
		//		System.out.println("create elf: "+data);
		return player;
	}

	public String getCharacter() {
		return character;
	}

	public int[] getColorations() {
		return colorations;
	}
	
	public Context getContext() {
		return context;
	}
}
