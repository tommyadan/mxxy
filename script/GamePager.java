package panel;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mxxy.game.base.Panel;
import com.mxxy.game.event.PanelEvent;
import com.mxxy.game.handler.AbstractPanelHandler;
import com.mxxy.game.ui.GamePanel;
import com.mxxy.game.utils.SpriteFactory;
import com.mxxy.game.widget.ImageComponentButton;
import com.mxxy.game.widget.Label;

/**
 * GamePager (游戏)
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
final public class GamePager extends AbstractPanelHandler{

	private Label label;
	private ImageComponentButton playerCharacter;
	private Label sceneXY,sceneName,sceneTimer;
	private SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
	@Override
	public void init(PanelEvent evt) {
		super.init(evt);
	}
	
	@Override
	protected void initView() {
		label=findViewById("leftdown");
		playerCharacter=findViewById("PlayerAttribute");
		addComponent();
		period=100;
		setAutoUpdate(true);
	}

	@Override
	public void update(PanelEvent evt) {
		updateSceneXY();
	}

	/**
	 * 更新场景坐标
	 */
	public void updateSceneXY(){
		Point sceneLocation = player.getSceneLocation();
		String XY="X:"+sceneLocation.x +" Y:"+sceneLocation.y;
		sceneXY.setText(XY);
	}

	/**
	 * 添加组件
	 */
	public void addComponent(){
		Date currentTime = new Date();
		String format2 = format.format(currentTime);
		sceneTimer=new Label(format2, null, Label.LEFT);
		sceneTimer.setBounds(30, 2,100,15);
		GamePanel panel2 = (GamePanel) context.getWindows().getPanel();
		sceneName=new Label(panel2.getSceneName(), null, Label.LEFT);
		sceneName.setBounds(36, 15,100,15);
		playerCharacter.init(SpriteFactory.loadSprite("/wzife/photo/facesmall/"+player.getCharacter()+".tcp"));
		sceneXY=new Label(null, null,Label.LEFT);
		sceneXY.setBounds(25, 65,100,15);
		panel.add(label,0);
		panel.add(sceneTimer,0);
		panel.add(sceneName,0);
		panel.add(sceneXY,0);
	}

	/**
	 * 小地图
	 * @param e
	 */
	public void openSmap(ActionEvent e){
		Panel oldpanel = uihelp.getPanel(e);
		showOrHide(oldpanel);
	}
	/**
	 * 道具 坐骑
	 * @param e
	 */
	public void openProp(ActionEvent e){
		Panel proppager = uihelp.getPanel(e);
		Panel palyer=uihelp.getPanel("PlayerPager");
		if(proppager.isShowing()){
			uihelp.hidePanel(proppager);
			uihelp.hidePanel(palyer);
		}else{
			uihelp.showPanel(proppager);
			uihelp.showPanel(palyer);
		}
	}
	/**
	 * 宠物
	 * @param e
	 */
	public void openPet(ActionEvent e){
		Panel openPet = uihelp.getPanel(e);
		showOrHide(openPet);
	}
	/**
	 * 点击宠物头像
	 * @param e
	 */
	public void openSummon(ActionEvent e){
		Panel openHeadPet = uihelp.getPanel(e);
		showOrHide(openHeadPet);
	}

	/**
	 * 好友列表
	 * @param e
	 */
	public void openFriend(ActionEvent e){
		Panel openHeadPet = uihelp.getPanel(e);
		showOrHide(openHeadPet);
	}

	/**
	 * 表情动作
	 * @param e
	 */
	public void openPlayerEmote(ActionEvent e){
		Panel openHeadPet = uihelp.getPanel(e);
		showOrHide(openHeadPet);
	}

	/**
	 * 设置面板
	 * @param e
	 */
	public void openSetting(ActionEvent e){
		Panel oldpanel = uihelp.getPanel(e);
		oldpanel.setSceneId(context.getScene());
		showOrHide(oldpanel);
	}
	
	/**
	 * 打开世界地图
	 */
	public void openWordMap(ActionEvent e){
		Panel oldpanel = uihelp.getPanel(e);
		showOrHide(oldpanel);
	}

	/**
	 * 人物属性面板
	 * @param e
	 */
	public void openPlayerCharacter(ActionEvent e){
		Panel oldpanel = uihelp.getPanel(e);
		showOrHide(oldpanel);
	}
	/**
	 * 符号表情
	 * @param e
	 */
	public void openEmote(ActionEvent e){
		Panel emoticon = uihelp.getPanel(e);
		showOrHide(emoticon);
	}
}
