package org.mio.demo.aop;

import org.mio.demo.aop.annotation.AfterExecutionUDP;
import org.mio.demo.aop.annotation.AroundExecutionUDP;
import org.mio.demo.aop.annotation.BeforeExecutionUDP;

public class App {

	public static void main(String[] args) throws Exception {
		App app = new App();
		app.start();
		app.test1();
		app.test2();
	}

	@BeforeExecutionUDP(message = "<BEFORE CALL>", address = "127.0.0.1", port = 9999)
	@AfterExecutionUDP(message = "<AFTER CALL>", address = "127.0.0.1", port = 9999)
	public void start() {
		System.out.println("CALL - start");
	}

	@AroundExecutionUDP(message = "<AROUND 1 CALL>", address = "127.0.0.1", port = 9999)
	public void test1() {
		System.out.println("CALL - test1");
	}

	@AroundExecutionUDP(message = "<AROUND 2 CALL>", address = "127.0.0.1", port = 9999)
	public void test2() throws InterruptedException {
		System.out.println("CALL - test2");
		Thread.sleep((long) (Math.random() * 1000));
	}
}
