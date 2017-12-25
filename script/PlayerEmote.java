package panel;


import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import com.mxxy.game.event.PanelEvent;
import com.mxxy.game.handler.AbstractPanelHandler;
import com.mxxy.game.widget.ImageComponentButton;

/**
 * PlayerEmote (人物动作)
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
final public class PlayerEmote extends AbstractPanelHandler{

	@Override
	public void init(PanelEvent evt) {
		super.init(evt);
	}
	@Override
	protected void initView() {
		
	}
	public void movement(ActionEvent e){
		ImageComponentButton source = (ImageComponentButton) e.getSource();
		if(!player.isShowMount()){
			player.setState(source.getName());
		}else{
			uihelp.prompt((JComponent) mPanel, "请先下坐骑", 2000);
		}
	}
}
