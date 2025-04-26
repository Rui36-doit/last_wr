package Model;

import Controller.Infore;
import Model.mysql.Connectpool;
import Model.op.Evaluate;
import Model.op.Order;
import Model.op.Shops;
import Model.op.User;
import Model.utils.Box;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class teast {
    //{"shop_id":2,"seller_id":2,"seller_name":"天天","shop_name":"平板","number":1,"pay_type":"先支付后到货","buyer_id":0,"buyer_name":"李华","type":"待发货","money":99}
    public static void main(String[] args) throws SQLException, IOException {
        String data = "2025-04";
        LocalDateTime dateTime = LocalDateTime.parse(data + "-01T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        System.out.println(year);
        System.out.println(month);
    }
}
