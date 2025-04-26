package Model.op;

public class Systeminfor {
    public int id;
    public int user_id;
    public String content;
    public String type;
    public Systeminfor(){

    }
    public Systeminfor(int id, int user_id, String content, String type){
        this.id = id;
        this.user_id = user_id;
        this.content = content;
        this.type = type;
    }
    public Systeminfor(int user_id, String content, String type){
        this.user_id = user_id;
        this.content = content;
        this.type = type;
    }

}
