package io.zl;

public class SimpleStringResponse implements Response {


    public static final SimpleStringResponse OK = new SimpleStringResponse("OK");

    String message;
    public SimpleStringResponse(String string){
        this.message = string;
    }
    @Override
    public String toString() {
        return message;
    }

    @Override
    public ResponseType type() {
        return ResponseType.SIMPLE_STRING;
    }
}
