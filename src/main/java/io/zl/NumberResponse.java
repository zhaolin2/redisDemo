package io.zl;

public class NumberResponse implements Response {


//    public static final ErrorResponse OK = new ErrorResponse("OK");

    Number message;
    public NumberResponse(Number string){
        this.message = string;
    }

    @Override
    public String toString() {
        return message.toString();
    }

    @Override
    public ResponseType type() {
        return ResponseType.NUMBER;
    }
}
