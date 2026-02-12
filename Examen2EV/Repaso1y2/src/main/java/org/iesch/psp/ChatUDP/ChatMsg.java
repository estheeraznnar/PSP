package org.iesch.psp.ChatUDP;

import java.io.Serializable;

public class ChatMsg implements Serializable {
    public enum MsgType {
        ENTER, MSG, QUIT
    }

    private String name;
    private MsgType type;
    private String msg;

    public ChatMsg(String name, MsgType type, String msg) {
        this.name = name;
        this.type = type;
        this.msg = msg;
    }

    public ChatMsg(String name, MsgType type) {
        if (type.equals(MsgType.MSG)) {
            throw new IllegalArgumentException(MsgType.MSG + " type is not valid with empty message");
        }
        this.name = name;
        this.type = type;
        this.msg = "";
    }

    public ChatMsg(String name, String msg) {
        this.name = name;
        this.type = MsgType.MSG;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public MsgType getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}