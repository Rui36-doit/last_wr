package Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;


public class BaseServelt extends HttpServlet {
    //调用方法
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String methodName = req.getHeader("X-Action");
        /*
        String json = JSONUtils.getJSONstr(req);
        System.out.println(methodName+json);
         */
        System.out.println(methodName);
        try{
            if(methodName != null) {
                Class<? extends BaseServelt> actionClass = this.getClass();
                Method method = actionClass.getMethod(methodName,
                        HttpServletRequest.class, HttpServletResponse.class);
                method.invoke(this, req, resp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);

    }

}
