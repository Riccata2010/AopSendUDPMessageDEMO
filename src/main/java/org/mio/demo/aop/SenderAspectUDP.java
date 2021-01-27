package org.mio.demo.aop;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.mio.demo.aop.annotation.AfterExecutionUDP;
import org.mio.demo.aop.annotation.AroundExecutionUDP;
import org.mio.demo.aop.annotation.BeforeExecutionUDP;

@Aspect
public class SenderAspectUDP {

	private static final Log LOGGER = LogFactory.getLog(SenderAspectUDP.class);
	private DatagramSocket socket = null;

	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Optional.ofNullable(socket).ifPresent(DatagramSocket::close);
		}));
	}

	private DatagramSocket ds() throws SocketException {
		return Objects.isNull(socket) ? socket = new DatagramSocket() : socket;
	}

	public void send(String message, String address, int port) {
		try {
			byte[] ms = message.getBytes(Charset.defaultCharset());
			ds().send(new DatagramPacket(ms, ms.length, InetAddress.getByName(address), port));
		} catch (IOException exc) {
			LOGGER.error(exc);
		}
	}

	private void send(BeforeExecutionUDP anno) {
		send(anno.message(), anno.address(), anno.port());
	}

	private void send(AfterExecutionUDP anno) {
		send(anno.message(), anno.address(), anno.port());
	}

	private void send(AroundExecutionUDP anno) {
		send(AroundExecutionUDP.prop.getMessage(), anno.address(), anno.port());
	}

	private AroundExecutionUDP decorator(AroundExecutionUDP anno, long time) {
		AroundExecutionUDP.prop.setMessage(anno.message() + " in [" + (System.currentTimeMillis() - time) + " ms]");
		return anno;
	}

	private Method getMethods(JoinPoint point) {
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		return method;
	}

	@Before("execution(@org.mio.demo.aop.annotation.BeforeExecutionUDP * *(..))")
	public void before(JoinPoint point) throws Throwable {
		Stream.of(getMethods(point).getAnnotation(BeforeExecutionUDP.class)).forEach(this::send);
	}

	@After("execution(@org.mio.demo.aop.annotation.AfterExecutionUDP * *(..))")
	public void after(JoinPoint point) throws Throwable {
		Stream.of(getMethods(point).getAnnotation(AfterExecutionUDP.class)).forEach(this::send);
	}

	@Around("execution(@org.mio.demo.aop.annotation.AroundExecutionUDP * *(..))")
	public Object aroundAspect(ProceedingJoinPoint point) throws Throwable {
		Method method = getMethods(point);
		long time = System.currentTimeMillis();
		Object proceed = point.proceed();
		Stream.of(method.getAnnotation(AroundExecutionUDP.class)).map(a -> decorator(a, time)).forEach(this::send);
		return proceed;
	}
}
