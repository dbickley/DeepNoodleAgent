package com.deepnoodle.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.deepnoodle.agent.dao.BaseDao;
import com.deepnoodle.agent.entity.HitEntity;
import com.deepnoodle.agent.metrics.MetricsCollector;
import com.deepnoodle.agent.webserver.AgentWebServer;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class DeepNoodleAgent {
	private static int HEARTBEAT = 10;
	private static AgentWebServer webServer;

	public static void premain(String args, Instrumentation instrumentation) throws SQLException {
		go(args, instrumentation);

	}

	public static void agentmain(String args, Instrumentation instrumentation) throws SQLException {
		go(args, instrumentation);
	}

	private static void go(String args, Instrumentation instrumentation) throws SQLException {
		System.out.println("[Agent] Start agent during JVM startup using argument '-javaagent'");
		BaseDao.init();

		//Capture threads every HEARTBEAT seconds starting now
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

			@Override

			public void run() {
				try {
					heartbeat();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}, 0, HEARTBEAT, TimeUnit.SECONDS);

		new AgentBuilder.Default()
				.type(ElementMatchers.any())
				.transform(new MetricsTransformer())
				.with(AgentBuilder.Listener.StreamWriting.toSystemOut())
				//.ignore(ElementMatchers.noneOf(Thread.class))
				//.with(RedefinitionStrategy.RETRANSFORMATION)
				//.with(CircularityLock.Global.class)
				.with(InitializationStrategy.NoOp.INSTANCE)
				.with(TypeStrategy.Default.REDEFINE)
				.with(AgentBuilder.RedefinitionStrategy.REDEFINITION)

				.installOn(instrumentation);

		if (webServer == null) {
			try {
				webServer = new AgentWebServer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	protected static void heartbeat() throws SQLException {
		//ThreadUtil.captureThreads();
	}

	static class MetricsTransformer implements Transformer {
		@Override
		public DynamicType.Builder<?> transform(
				DynamicType.Builder<?> builder,
				final TypeDescription typeDescription,
				final ClassLoader classLoader,
				final JavaModule module) {
			if (!typeDescription.getName().startsWith(this.getClass().getPackage().getName())
					&& (typeDescription.getName().startsWith("com.conenza")
							|| typeDescription.getName().startsWith("com.liferay")
							|| typeDescription.getName().startsWith("com.deepnoodle"))) {
				final AsmVisitorWrapper constructorVistor = Advice
						.to(EnterAdviceConstructors.class, ExitAdviceConstructors.class)
						.on(ElementMatchers.isConstructor());
				builder = builder.visit(constructorVistor);

				final AsmVisitorWrapper methodsVisitorReturnVoid = Advice
						.to(EnterAdviceMethods.class, ExitAdviceMethodsReturnVoid.class)
						.on(ElementMatchers.isMethod()
								.and(ElementMatchers.not(ElementMatchers.isStatic()))
								.and(ElementMatchers.returns(TypeDescription.VOID)));
				builder = builder.visit(methodsVisitorReturnVoid);

				final AsmVisitorWrapper methodsVisitorReturnObject = Advice
						.to(EnterAdviceMethods.class, ExitAdviceMethodsReturnObject.class)
						.on(ElementMatchers.isMethod()
								.and(ElementMatchers.not(ElementMatchers.isStatic()))
								.and(ElementMatchers.not(ElementMatchers.returns(TypeDescription.VOID))));
				builder = builder.visit(methodsVisitorReturnObject);

				final AsmVisitorWrapper methodsVisitorStaticReturnVoid = Advice
						.to(EnterAdviceMethodStatic.class, ExitAdviceMethodStaticReturnVoid.class)
						.on(ElementMatchers.isMethod()
								.and(ElementMatchers.isStatic())
								.and(ElementMatchers.returns(TypeDescription.VOID)));
				builder = builder.visit(methodsVisitorStaticReturnVoid);

				final AsmVisitorWrapper methodsVisitorStaticReturnObject = Advice
						.to(EnterAdviceMethodStatic.class, ExitAdviceMethodStaticReturnObject.class)
						.on(ElementMatchers.isMethod().and(ElementMatchers.isStatic())
								.and(ElementMatchers.not(ElementMatchers.returns(TypeDescription.VOID))));
				builder = builder.visit(methodsVisitorStaticReturnObject);

				//final AsmVisitorWrapper threadVistor = Advice.to(advice)

			} else if (typeDescription.getName().equals(Thread.class.getName())) {
				builder = builder.visit(Advice.to(ThreadStartInterceptor.class).on(ElementMatchers.named("start")));
			} else {

				System.out.println("ignoring:" + typeDescription.getName());
			}

			//			else {
			//				final AsmVisitorWrapper methodsVisitor = Advice.to(EnterAdvice.class, ExitAdviceMethods.class)
			//						.on(ElementMatchers.isMethod().and(ElementMatchers.none()));
			//				return builder.visit(methodsVisitor);
			//			}

			return builder;
		}

		private static class EnterAdviceConstructors {
			@Advice.OnMethodEnter
			static MethodEnterReturnModel enter(
					//					@Advice.This Object obj,
					@Advice.Origin final Executable executable,
					@Advice.AllArguments Object[] args) {
				HitEntity hit = MetricsCollector.createHit("EnterAdviceConstructors", executable);
				return new MethodEnterReturnModel(System.nanoTime(), hit, args);
				//return System.nanoTime();
			}

		}

		private static class ExitAdviceConstructors {
			@Advice.OnMethodExit
			static void exit(
					//					@Advice.Origin final Executable executable,
					//					@Advice.Origin String method,
					//					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel

					//@Advice.This final Object obj,
					@Advice.AllArguments final Object[] args,
					//@Advice.Return final Object returned,
					@Advice.Origin final Executable executable,
					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel
			//@Advice.Thrown final Throwable throwable
			) {
				final long duration = System.nanoTime() - methodEnterReturnModel.getStartTime();

				HitEntity hit = methodEnterReturnModel.getHit();
				MetricsCollector.endHitConstructor("ExitAdviceConstructors", hit, (Constructor) executable, args,
						methodEnterReturnModel, duration);

			}
		}

		private static class EnterAdviceMethods {
			@Advice.OnMethodEnter
			static MethodEnterReturnModel enter(
					@Advice.This final Object obj,
					@Advice.Origin final String method,
					@Advice.Origin final Executable executable,
					@Advice.AllArguments final Object[] args

			) {

				HitEntity hit = MetricsCollector.createHit("EnterAdviceMethods", executable);
				return new MethodEnterReturnModel(System.nanoTime(), hit, args);
				//return System.nanoTime();
			}

		}

		private static class ExitAdviceMethodsReturnVoid {
			@Advice.OnMethodExit(onThrowable = Throwable.class)
			static void exit(
					@Advice.This final Object obj,
					@Advice.AllArguments final Object[] args,
					@Advice.Origin final Executable executable,
					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel
			//@Advice.Thrown final Throwable throwable
			) {

				final long duration = System.nanoTime() - methodEnterReturnModel.getStartTime();
				HitEntity hit = methodEnterReturnModel.getHit();
				MetricsCollector.endHitMethod("ExitAdviceMethodsReturnVoid", hit, (Method) executable, obj, args, null,
						null,
						methodEnterReturnModel, duration);
				//MetricsCollector.hitMethod(executable.toGenericString(), duration);
			}
		}

		private static class ExitAdviceMethodsReturnObject {
			@Advice.OnMethodExit(onThrowable = Throwable.class)
			static void exit(
					@Advice.This final Object obj,
					@Advice.AllArguments final Object[] args,
					@Advice.Return final Object returned,
					@Advice.Origin final Executable executable,
					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel
			//@Advice.Thrown final Throwable throwable
			) {

				final long duration = System.nanoTime() - methodEnterReturnModel.getStartTime();
				HitEntity hit = methodEnterReturnModel.getHit();
				MetricsCollector.endHitMethod("ExitAdviceMethodsReturnObject", hit, (Method) executable, obj, args,
						returned,
						null,
						methodEnterReturnModel, duration);
			}

		}

		private static class EnterAdviceMethodStatic {
			@Advice.OnMethodEnter
			static MethodEnterReturnModel enter(
					//					@Advice.This Object obj,
					@Advice.Origin final Executable executable,
					@Advice.Origin String method,
					@Advice.AllArguments Object[] args) {

				HitEntity hit = MetricsCollector.createHit("EnterAdviceMethodStatic", executable);
				return new MethodEnterReturnModel(System.nanoTime(), hit, args);
			}

		}

		private static class ExitAdviceMethodStaticReturnObject {
			@Advice.OnMethodExit
			static void exit(
					//					@Advice.Origin final Executable executable,
					//					@Advice.Origin String method,
					//					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel

					//@Advice.This Object obj,
					@Advice.AllArguments Object[] args,
					@Advice.Return Object returned,
					@Advice.Origin final Executable executable,
					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel
			//@Advice.Thrown final Throwable throwable
			//@Advice.Enter final long startTime) throws SQLException
			) {

				final long duration = System.nanoTime() - methodEnterReturnModel.getStartTime();
				HitEntity hit = methodEnterReturnModel.getHit();
				MetricsCollector.endHitMethod("ExitAdviceMethodStaticReturnObject", hit, (Method) executable, null,
						args,
						returned,
						null,
						methodEnterReturnModel, duration);
			}
		}

		private static class ExitAdviceMethodStaticReturnVoid {
			@Advice.OnMethodExit
			static void exit(
					//					@Advice.Origin final Executable executable,
					//					@Advice.Origin String method,
					//					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel

					//@Advice.This Object obj,
					@Advice.AllArguments Object[] args,
					@Advice.Origin final Executable executable,
					@Advice.Enter final MethodEnterReturnModel methodEnterReturnModel
			//@Advice.Thrown final Throwable throwable
			) {

				final long duration = System.nanoTime() - methodEnterReturnModel.getStartTime();
				HitEntity hit = methodEnterReturnModel.getHit();
				MetricsCollector.endHitMethod("ExitAdviceMethodStaticReturnVoid", hit, (Method) executable, null, args,
						null,
						null,
						methodEnterReturnModel, duration);

			}
		}

		public static class ThreadStartInterceptor {

			@Advice.OnMethodEnter
			static void intercept(@Advice.Origin final Executable executable,
					@Advice.Origin String method) {
				System.out
						.println("ThreadStartInterceptor:" + method + " thread Id:"
								+ Thread.currentThread().getName());

			}
		}

	}
}
