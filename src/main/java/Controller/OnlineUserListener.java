package Controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WebListener
public class OnlineUserListener implements ServletContextListener, HttpSessionListener {
    //初始化
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<String, HttpSession> onlineusers = new ConcurrentHashMap<>();
        sce.getServletContext().setAttribute("onlineusers", onlineusers);
        System.out.println("初始化成功");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            Map<String, HttpSession> onlineUsers = getOnlineUsers(session);
            onlineUsers.remove(userId);
            System.out.println("用户[" + userId + "]会话结束，已从在线列表移除");
        }
    }

    public Map<String, HttpSession> getOnlineUsers(HttpSession session){
        return (Map<String, HttpSession>) session.getServletContext().getAttribute("onlineusers");
    }

}
