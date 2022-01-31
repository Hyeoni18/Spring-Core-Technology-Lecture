package hello.corespring;

public class MyEvent {

    private int date;

    private Object source; //이벤트를 발생시킨 소스를 갖고 싶다면 적어줘도 됨.

    public MyEvent(Object source, int date) {
        this.source = source;
        this.date = date;
    }

    public Object getSource() {
        return source;
    }

    public int getDate() {
        return date;
    }
}
