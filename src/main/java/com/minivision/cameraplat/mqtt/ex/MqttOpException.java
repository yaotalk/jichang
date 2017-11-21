package com.minivision.cameraplat.mqtt.ex;

import com.minivision.cameraplat.mqtt.message.Packet.Head.Type;

public class MqttOpException extends RuntimeException {
	private static final long serialVersionUID = -3232739464550625156L;

	private int errType = Type.RESPONSE_PROCESS_FAIL;

	public MqttOpException(String message) {
		super(message);
	}

	public MqttOpException(int errType, String message) {
		super(message);
		this.errType = errType;
	}

	public MqttOpException(String message, Throwable cause) {
		super(message, cause);
		copyErrType(cause);
	}

	public MqttOpException(int errType, String message, Throwable cause) {
		super(message, cause);
		this.errType = errType;
	}

	private void copyErrType(Throwable cause) {
		if (cause instanceof MqttOpException) {
			MqttOpException c = (MqttOpException) cause;
			this.errType = c.errType;
		}
	}

	@Override
	public String getMessage() {
		return String.format("[%s:%s] ", errType, Type.getSysDesc(errType)) + super.getMessage();
	}

	public int getErrType() {
		return errType;
	}
}
