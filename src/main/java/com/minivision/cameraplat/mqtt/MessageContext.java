package com.minivision.cameraplat.mqtt;

import java.security.SecureRandom;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.minivision.cameraplat.mqtt.ex.MqttOpException;
import com.minivision.cameraplat.mqtt.message.Packet.Head.Type;

@Component
public class MessageContext {
  private static final Logger logger = LoggerFactory.getLogger(MessageContext.class);

  private SecureRandom r = new SecureRandom();
  private AtomicInteger seq = new AtomicInteger(Math.abs(r.nextInt() / 10));
  
  private ConcurrentMap<Long, RequestFuture<?>> reqCache = new ConcurrentHashMap<>();
  
  private long timeout = 10l * 1000 * 1000000;// nano
  
  
  public long getNextId() {
    int id = seq.incrementAndGet();
    if (id < 0 || id == Integer.MAX_VALUE) {
        seq.compareAndSet(id, Math.abs(r.nextInt() / 10));
    }
    return id;
  }
  
  @Scheduled(fixedRate = 5000)
  public void checkExpire() {
    long start = System.nanoTime();
    for (Entry<Long, RequestFuture<?>> entry : reqCache.entrySet()) {
        RequestFuture<?> req = entry.getValue();
        long aliveTime = System.nanoTime() - req.getBuildNanoTime();
        if (req != null && aliveTime > timeout) {
            req = reqCache.remove(entry.getKey());
            // check again in case of anyone else removed it
            if (req != null) {
                req.fail(new MqttOpException(Type.RESPONSE_TIMEOUT, "Request timeout"));
                logger.debug("--- {} timeout after {}ns.", req, aliveTime);
            }
        }
    }
    long duration = System.nanoTime() - start;
    logger.trace("@@@ TimeoutChecker finished in {}ns.", duration);
  }
  
  
  public void add(RequestFuture<?> req) {
      long id = req.getRequest().getHead().getId();
      if (reqCache.putIfAbsent(id, req) != null) {
          throw new IllegalArgumentException("Request with same id " + req.getRequest().getHead().getId()
                  + " alreadly exists");
      }
  }

  public RequestFuture<?> remove(long id) {
      return reqCache.remove(id);
  }
}
