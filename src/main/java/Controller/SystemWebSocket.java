package Controller;

import Model.op.Systeminfor;
import Model.utils.JSONUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/system_ws/{userid}")
public class SystemWebSocket {
    //存储用户的Session
    private static final Map<String, Session> users = Collections.synchronizedMap(new HashMap<>());

    //在web建立连接时调用
    @OnOpen
    public void onOpen(Session session, @PathParam("userid") String userid){
        users.put(userid, session);
        System.out.println("连接成功" + userid);
    }

    //向指定的用户发送链接请求
    public static boolean sendMessage(String userid, Systeminfor massage) throws IOException {
        Session target = users.get(userid);
        if(target != null && target.isOpen()){
            try {
                String messagestr = JSONUtils.toJSON(massage);
                target.getBasicRemote().sendText(messagestr);
                System.out.println("发送成功");
                return true;
            } catch (IOException e) {
                System.out.println("发送失败");
                users.remove(userid);
                return false;
            }
        }else {
            System.out.println("用户不在线");
        }
        return false;
    }

    //关闭时调用
    @OnClose
    public void onClose(@PathParam("userid") String userid){
        users.remove(userid);
        System.out.println("移除成功" + userid);
    }

}
