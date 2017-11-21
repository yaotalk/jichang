package com.minivision.cameraplat.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minivision.cameraplat.mqtt.ex.MqttOpException;
import com.minivision.cameraplat.mqtt.message.Packet;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Type;


public class RequestFuture<T> {
  private static final Logger logger = LoggerFactory.getLogger(RequestFuture.class);
  private Packet<?> request;
  Class<T> responseBodyType;
  private Packet<T> response;
  private long buildNanoTime;
  private long sendNanoTime;
  private long responseNanoTime;
  private volatile boolean done;
  private Throwable throwable;

  public RequestFuture(Packet<?> request, Class<T> responseBodyType) {
    this.request = request;
    this.responseBodyType = responseBodyType;
    this.buildNanoTime = System.nanoTime();
  }

  public boolean isDone() {
    return done;
  }

  public Packet<?> getRequest() {
    return request;
  }

  public void setRequest(Packet<?> request) {
    this.request = request;
  }

  public Packet<T> getResponse() throws MqttOpException {
    waitResponse();
    if (throwable != null) {
      throw new MqttOpException("Invalid response", throwable);
    }
    if (!Type.isResponseOk(response)) {
      throw new MqttOpException(response.getHead().getType(), response.getHead().getMsg());
    }
    return response;
  }

  public Packet<T> getResponse(long timeout) throws MqttOpException {
    waitResponse(timeout);
    if (throwable != null) {
      throw new MqttOpException("Invalid response", throwable);
    }
    if (!Type.isResponseOk(response)) {
      throw new MqttOpException(response.getHead().getType(), response.getHead().getMsg());
    }
    return response;
  }

  private void waitResponse(long timeout) {
    long startWait = System.currentTimeMillis();
    long limitTime = timeout;
    synchronized (this) {
      while (!done) {
        try {
          this.wait(limitTime);
          limitTime = timeout - System.currentTimeMillis() + startWait;
          if (limitTime <= 0) {
            this.responseNanoTime = System.nanoTime();
            this.throwable =
                new MqttOpException(Type.RESPONSE_TIMEOUT, "Request timeout : " + timeout);
            this.done = true;
          }
        } catch (InterruptedException e) {
          logger.error("Interrupted while waiting response.", e);
        }
      }
    }
  }

  private void waitResponse() {
    synchronized (this) {
      while (!done) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          logger.error("Interrupted while waiting response.", e);
        }
      }
    }
  }

  public void setResponse(Packet<T> response) {
    synchronized (this) {
      this.responseNanoTime = System.nanoTime();
      this.response = response;
      this.done = true;
      this.notifyAll();
    }
  }

  public void fail(Throwable t) {
    synchronized (this) {
      this.responseNanoTime = System.nanoTime();
      this.throwable = t;
      this.done = true;
      this.notifyAll();
    }
  }

  public long getBuildNanoTime() {
    return buildNanoTime;
  }

  public long getSendNanoTime() {
    return sendNanoTime;
  }

  public void setSendNanoTime(long sendNanoTime) {
    this.sendNanoTime = sendNanoTime;
  }

  public long getResponseNanoTime() {
    return responseNanoTime;
  }

  public Class<T> getResponseBodyType() {
    return responseBodyType;
  }

  public void setResponseBodyType(Class<T> responseBodyType) {
    this.responseBodyType = responseBodyType;
  }

}
