package Model.op;

import Model.utils.JDBCUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Shops {
    public int id;
    public String name;
    public double price;
    public String kind;
    public int sellerid;
    public int number;
    public String photo;
    public LocalDateTime time;
    public String describe;
    public String type;
    public String sellername;

    public Shops(){

    }
    public Shops(int id, String name, double price, String kind, int sellerid, int number,
                 String photo, LocalDateTime time, String describe, String type, String sellername){
        this.id = id;
        this.name = name;
        this.price = price;
        this.kind = kind;
        this.sellerid = sellerid;
        this.number = number;
        this.photo = photo;
        this.time = time;
        this.describe = describe;
        this.type = type;
        this.sellername = sellername;
    }
    public Shops(String name, double price, String kind, int sellerid, int number,
                 String photo, LocalDateTime time, String describe, String type, String sellername){
        this.name = name;
        this.price = price;
        this.kind = kind;
        this.sellerid = sellerid;
        this.number = number;
        this.photo = photo;
        this.time = time;
        this.describe = describe;
        this.type = type;
        this.sellername = sellername;
    }
    //保存数据
    public void save() throws SQLException {
        String sql = "update shops set name = ?, price = ?, photo = ?, `describe`= ? where id = ?";
        JDBCUtil.update(sql, name, price, photo, describe, id);
    }
}
