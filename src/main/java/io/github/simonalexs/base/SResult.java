package io.github.simonalexs.base;

public class SResult {
    private Integer code;
    private String msg;

    private SResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static SResult success() {
        return new SResult(0, "");
    }

    public static SResult error(String msg) {
        return error(-1, msg);
    }

    public static SResult error(Integer code, String msg) {
        return new SResult(code, msg);
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public boolean isError() {
        return !isSuccess();
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
