package Controller;

import Model.op.History;
import Model.op.MessageManager;
import Model.op.Order;
import Model.op.Systeminfor;
import Model.utils.Historymanager;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/historyservelt")
public class HistoryServelt extends BaseServelt{
    //获取用户的历史记录
    public void showhistory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String idstr = JSONUtils.getStr(req);
            System.out.println(idstr);
            int id = Integer.valueOf(idstr);
            String json = Historymanager.showuserhistory(id);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("数据流出错了");
            writer.write("no");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("数据转换出错");
            writer.write("no");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("获取数据库资源出错");
            writer.write("no");
        }
    }
    //添加历史记录
    public void addhistory(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            Order order = JSONUtils.getObjectbyjson(json, Order.class);
            boolean flag = Historymanager.addhistory(order);
            //添加消息
            String infor = req.getHeader("Content");
            System.out.println("infore = " + infor);
            if(infor != null){
                String content = MessageManager.getcontent(infor, order);
                Systeminfor message = new Systeminfor(order.buyer_id, content,"未读");
                //发送给用户
                if(infor.equals("senting") || infor.equals("waitingget") || infor.equals("waitingsend") || infor.equals("backmoney")) {
                    Integer id = order.buyer_id;
                    String userid = id.toString();
                    MessageManager.addmessage(order.buyer_id, "未读", order, infor);
                    SystemWebSocket.sendMessage(userid, message);
                }else if(infor.equals("hasget") || infor.equals("backshop") || infor.equals("backorder")){
                    Integer id = order.seller_id;
                    String userid = id.toString();
                    MessageManager.addmessage(order.seller_id, "未读", order, infor);
                    SystemWebSocket.sendMessage(userid, message);
                }
            }
            if(flag){
                writer.write("yes");
            }else{
                writer.write("no");
            }
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("no");
            System.out.println("IO出错了");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("no");
            System.out.println("数据库连接出错了");
        }
    }
}
