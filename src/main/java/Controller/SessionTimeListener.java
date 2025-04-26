package Controller;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
@WebListener
public class SessionTimeListener implements HttpSessionListener {
    //定时移除会话
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        Map<String, HttpSession> onlineUsers = (Map<String, HttpSession>) context.getAttribute("onlineusers");
        onlineUsers.remove(se.getSession().getId());
    }
}
