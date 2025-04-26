package Model.op;

public class Evaluate {
    public int id;
    public int buyer_id;
    public int seller_id;
    public String content;
    public double score;
    public String buyername;
    public int shop_id;
    public String shopname;
    public String response;

    public Evaluate(){

    }
    public Evaluate(int id, int buyerid, int sellerid, String content, double score,
                    String buyername, int shopid, String shopname, String response){
        this.id = id;
        this.buyer_id = buyerid;
        this.seller_id = sellerid;
        this.content = content;
        this.score = score;
        this.buyername = buyername;
        this.shop_id = shopid;
        this.shopname = shopname;
        this.response = response;
    }
}
