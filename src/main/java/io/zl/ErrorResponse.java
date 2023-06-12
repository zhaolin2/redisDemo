package io.zl;

public class ErrorResponse implements Response {


//    public static final ErrorResponse OK = new ErrorResponse("OK");

    String message;
    public ErrorResponse(String string){
        this.message = string;
    }
    @Override
    public String toString() {
        return message;
    }

    @Override
    public ResponseType type() {
        return ResponseType.ERROR;
    }
}
