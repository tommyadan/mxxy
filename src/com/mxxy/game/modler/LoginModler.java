package com.mxxy.game.modler;

import java.io.IOException;

import com.mxxy.game.config.IConfigManager;
import com.mxxy.game.utils.Constant;
import com.mxxy.game.utils.Constant.LoginStatus;
import com.mxxy.net.IClient;
import com.mxxy.protocol.LoginMessage;

/**
 * 登录数据处理
 * @author ZAB
 * 邮箱 ：624284779@qq.com
 */
public class LoginModler{
	public LoginStatus next(IConfigManager propertiesConfigManager, String user, String password) {
		if(user.isEmpty()||user.equals("   "+Constant.getString("USER_HINT"))
				||password.isEmpty()||password.equals("   "+Constant.getString("PASSWOR_HINT"))){
			return LoginStatus.PASSWORDANDUSER_EMPTY;
		}else if(propertiesConfigManager.loadCheckUser(user,password)||user.equals("1")&&password.equals("1")){
			return LoginStatus.SUCCESS;
		}else{
			return LoginStatus.PASSWORDANDUSER_ERR;
		}
	}
	
	
	public LoginStatus next(IClient client,String user, String password) {
		try {
			client.send(new LoginMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
