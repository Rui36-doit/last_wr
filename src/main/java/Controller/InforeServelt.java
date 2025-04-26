package Controller;

import Model.op.Systeminfor;
import Model.utils.Box;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/inforservelt")
public class InforeServelt extends BaseServelt{
    //获取消息
    public void showinfore(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        PrintWriter writer = resp.getWriter();
        String sql = "select id, content, user_id, type from system_infores where user_id = ?";
        Box box = JDBCUtil.search(sql, id);
        ArrayList<Systeminfor> list = new ArrayList<>();
        ResultSet resultSet = box.getResult();
        while(resultSet.next()){
            Systeminfor infor = new Systeminfor(resultSet.getInt("id"), resultSet.getInt("user_id"),
                    resultSet.getString("content"), resultSet.getString("type"));
            list.add(infor);
        }
        String json = JSONUtils.toJSON(list);
        System.out.println(json);
        writer.write(json);
    }

    //保存所以的
    public void updatainfore(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        String sql = "update system_infores set type = '已读' where user_id = ?";
        int t = JDBCUtil.update(sql, id);
    }
}
