package sr.ice.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Demo.CalcPrx;
import Demo.CalcPrxHelper;
import Demo.Operation;

public class Client {
	private static final List<Operation> VALUES = Collections.unmodifiableList(Arrays.asList(Operation.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	private static final CallbackCompute callback = new CallbackCompute();

	public static void main(String[] args) {
		int status = 0;
		Ice.Communicator communicator = null;

		try {
			communicator = Ice.Util.initialize(args);

			// Ice.ObjectPrx base = communicator.propertyToProxy("Calc1.Proxy");
			Ice.ObjectPrx base1 = communicator.stringToProxy(
					"K1/calcK1:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");

			Ice.ObjectPrx base2 = communicator.stringToProxy(
					"K2/calc11:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");

			Ice.ObjectPrx base31 = communicator.stringToProxy(
					"K3/calc31:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");
			Ice.ObjectPrx base32 = communicator.stringToProxy(
					"K3/calc32:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");
			Ice.ObjectPrx base33 = communicator.stringToProxy(
					"K3/calc33:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");

			Ice.ObjectPrx base4 = communicator.stringToProxy(
					"K4/calc11:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");

			Ice.ObjectPrx base51 = communicator.stringToProxy(
					"K5/calc51:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");
			Ice.ObjectPrx base52 = communicator.stringToProxy(
					"K5/calc52:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");
			Ice.ObjectPrx base53 = communicator.stringToProxy(
					"K5/calc53:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");

			CalcPrx calc1 = CalcPrxHelper.checkedCast(base1);

			CalcPrx calc2 = CalcPrxHelper.checkedCast(base2);

			CalcPrx calc31 = CalcPrxHelper.checkedCast(base31);
			CalcPrx calc32 = CalcPrxHelper.checkedCast(base32);
			CalcPrx calc33 = CalcPrxHelper.checkedCast(base33);

			CalcPrx calc4 = CalcPrxHelper.checkedCast(base4);

			CalcPrx calc51 = CalcPrxHelper.checkedCast(base51);
			CalcPrx calc52 = CalcPrxHelper.checkedCast(base52);
			CalcPrx calc53 = CalcPrxHelper.checkedCast(base53);

			if (calc1 == null || calc2 == null || calc31 == null || calc32 == null || calc33 == null || calc4 == null
					|| calc51 == null || calc52 == null || calc53 == null)
				throw new Error("Invalid proxy");

			Thread K1 = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						simpleTask(calc1, "K1");
					}
				}
			});
			Thread K2 = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						simpleTask(calc2, "K2");
					}
				}
			});
			Thread K3 = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 3; i++) {
						simpleTask(new CalcPrx[] { calc31, calc32, calc33 }, "K3");
					}
				}
			});
			Thread K4 = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						simpleTask(calc4, "K4");
					}
				}
			});
			Thread K5 = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						simpleTask(new CalcPrx[] {calc51, calc52, calc53}, "K5");
					}
				}
			});

			K1.start();
			K2.start();
			K3.start();
			K4.start();
			K5.start();

			K1.join();
			K2.join();
			K3.join();
			K4.join();
			K5.join();

		} catch (Ice.LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		if (communicator != null) {
			// Clean up
			//
			try {
				communicator.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		System.exit(status);
	}

	private static void simpleTask(CalcPrx calc, String category) {
		try {
			Thread.sleep(RANDOM.nextInt(500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Operation op = VALUES.get(RANDOM.nextInt(SIZE));
		int a = RANDOM.nextInt(10);
		int b = RANDOM.nextInt(10);

		System.out.print("Client.simpleTask()\t");
		System.out.print(category + "\t");
		System.out.println(a + " " + op.toString() + " " + b);

		calc.begin_compute(a, b, op, callback);
	}

	private static void simpleTask(CalcPrx[] calcs, String category) {
		for (CalcPrx calcPrx : calcs) {
			simpleTask(calcPrx, category);
		}
	}
}
