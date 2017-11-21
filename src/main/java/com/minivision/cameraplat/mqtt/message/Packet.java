package com.minivision.cameraplat.mqtt.message;

import com.fasterxml.jackson.annotation.JsonView;

public class Packet<T> {
  @JsonView(MqttMessageView.class)
  private Head head;
  @JsonView(MqttMessageView.class)
  private T body;

  public Packet() {}

  public Packet(Head head) {
    setHead(head);
  }

  public Packet(Head head, T body) {
    setHead(head);
    setBody(body);
  }

  public Head getHead() {
    return head;
  }

  public void setHead(Head head) {
    this.head = head;
  }

  public T getBody() {
    return body;
  }

  public void setBody(T body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return "Packet [head=" + head + ", body=" + body + "]";
  }

  public static final class Head {
    @JsonView(MqttMessageView.class)
    private int version = 1;
    @JsonView(MqttMessageView.class)
    private long id;
    @JsonView(MqttMessageView.class)
    private String from;
    @JsonView(MqttMessageView.class)
    private int code;
    @JsonView(MqttMessageView.class)
    private int type;
    @JsonView(MqttMessageView.class)
    private String msg;

    public Head() {}

    public Head(long id, int code, int type) {
      this(id, code, type, null);
    }

    public Head(long id, int code, int type, String msg) {
      setId(id);
      setCode(code);
      setType(type);
      setMsg(msg);
    }

    public int getVersion() {
      return version;
    }

    public void setVersion(int version) {
      this.version = version;
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getFrom() {
      return from;
    }

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }

    public int getType() {
      return type;
    }

    public void setType(int type) {
      this.type = type;
    }

    public String getMsg() {
      return msg;
    }

    public void setMsg(String msg) {
      this.msg = msg;
    }

    @Override
    public String toString() {
      return "Head [version=" + version + ", id=" + id + ", from=" + from + ", code=" + code
          + ", type=" + type + ", msg=" + msg + "]";
    }
    
    public static class Code{
      public static final int SYNC_DEVICE = 0;
      public static final int STRATEGY_INFO = 1;
      public static final int UPDATE_CAMERA = 2;
      public static final int DEL_CAMERA = 3;
      public static final int STATUS_INFO = 4;
      
      //public static final int STRATEGY_ADD = 5;
      //public static final int STRATEGY_DELETE = 6;
      
      public static final int REC_INFO = 7;
      public static final int REC_COMPLETE = 8;
      
      public static final int MULTI_FACE = 9;
    }
    
    public static class Type{
      public static final int NOTIFY = 0;
      public static final int REQUEST = 1;
      public static final int RESPONSE_OK = 2;
      public static final int RESPONSE_BAD_REQ = 3;
      public static final int RESPONSE_TIMEOUT = 4;
      public static final int RESPONSE_PROCESS_FAIL = 5;
      // boundary of vendor specified error
      public static final int RESPONSE_VENDOR_SPEC_ERROR = 10;
      public static String getSysDesc(int errType) {
        return null;
      }
      
      public static boolean isResponse(int type) {
        return type >= RESPONSE_OK;
      }
  
      public static boolean isResponseOk(Packet<?> p) {
          return p.getHead().getType() == RESPONSE_OK;
      }
    }

  }

}

