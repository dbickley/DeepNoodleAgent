package com.deepnoodle.agent.metrics;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.deepnoodle.agent.MethodEnterReturnModel;
import com.deepnoodle.agent.dao.ClassDao;
import com.deepnoodle.agent.dao.HitDao;
import com.deepnoodle.agent.dao.MethodDao;
import com.deepnoodle.agent.dao.PackageDao;
import com.deepnoodle.agent.dao.ThreadDao;
import com.deepnoodle.agent.entity.ClassEntity;
import com.deepnoodle.agent.entity.HitEntity;
import com.deepnoodle.agent.entity.MethodEntity;
import com.deepnoodle.agent.entity.PackageEntity;
import com.deepnoodle.agent.entity.ThreadEntity;

public class MetricsCollector {
	static ReentrantLock stackTraceHitEntityMapLock = new ReentrantLock();
	private static ThreadLocal<Map<String, HitEntity>> stackTraceHitEntityMapThreadLocal = new InheritableThreadLocal<>();
	//private final static LinkedList<HitEntity> parentList = new LinkedList<>();

	public static void endHitMethod(String callingMethod, HitEntity hit, Method executable, Object obj, Object[] args,
			Object returned,
			Throwable throwable,
			MethodEnterReturnModel methodEnterReturnModel, long duration) {

		String fullClassName = executable.getDeclaringClass().getName();
		String modifiers = Modifier.toString(executable.getModifiers());
		String methodName = executable.getName();
		Annotation[] annotations = executable.getAnnotations();

		//System.out.println(callingMethod + " | " + fullClassName);
		endHit(hit, true, args, returned, duration, fullClassName, modifiers, methodName, annotations);

	}

	@SuppressWarnings("rawtypes")
	public static void endHitConstructor(String callingMethod, HitEntity hit, Constructor executable, Object[] args,
			MethodEnterReturnModel methodEnterReturnModel, long duration) {

		String fullClassName = executable.getDeclaringClass().getName();
		String modifiers = Modifier.toString(executable.getModifiers());
		String methodName = executable.getName();
		Annotation[] annotations = executable.getAnnotations();

		//System.out.println(callingMethod + " | " + fullClassName);
		endHit(hit, false, args, null, duration, fullClassName, modifiers, methodName, annotations);
	}

	public static HitEntity createHit(String callingMethod, Executable executable) {
		HitEntity hit = new HitEntity();
		if (executable == null) {
			System.out.println(callingMethod + " send a null executable");
		} else {
			Thread currentThread = Thread.currentThread();
			long threadId = currentThread.getId();
			ThreadEntity threadEntity = ThreadDao.instance.findByThreadId(threadId);
			if (threadEntity == null) {
				threadEntity = new ThreadEntity();
				threadEntity.setThreadId(threadId);
				threadEntity.setName(currentThread.getName());
				threadEntity.setClassName(currentThread.getClass().getName());
				threadEntity.setState(currentThread.getState().toString());
				threadEntity = ThreadDao.instance.insert(threadEntity);
			}
			hit.setThreadId(threadEntity.getId());

			Class<?> clazz = executable.getDeclaringClass();
			String className = clazz.getSimpleName();

			String[] packageNames = clazz.getPackage().getName().split("\\.");
			Long parentPackageId = null;
			for (String packageName : packageNames) {
				PackageEntity packageEntity = PackageDao.instance.findByNameAndParent(packageName, parentPackageId);
				if (packageEntity == null) {
					packageEntity = new PackageEntity();
					packageEntity.setName(packageName);
					packageEntity.setParentPackageId(parentPackageId);
					packageEntity = PackageDao.instance.insert(packageEntity);
				}
				parentPackageId = packageEntity.getId();
			}
			hit.setPackageId(parentPackageId);
			ClassEntity classEntity = ClassDao.instance.findByNameAndPackageId(className, parentPackageId);
			if (classEntity == null) {
				classEntity = new ClassEntity();
				classEntity.setName(className);
				classEntity.setPackageId(parentPackageId);
				ClassDao.instance.insert(classEntity);
			}
			hit.setClassId(classEntity.getId());
			String methodName = executable.getName();
			MethodEntity methodEntity = MethodDao.instance.findByMethodNameAndClassId(methodName, classEntity.getId());
			if (methodEntity == null) {
				methodEntity = new MethodEntity();
				methodEntity.setName(methodName);
				methodEntity.setClassId(classEntity.getId());
				//	methodEntity.setMethod(isMethod);
				//	methodEntity.setAnnotations(annotations);
				methodEntity.setModifiers(Modifier.toString(executable.getModifiers()));
				MethodDao.instance.insert(methodEntity);
			}

			hit.setMethodId(methodEntity.getId());

			//hit = calculateCallingHitId(hit, currentThread);
		}
		return hit;
	}

	public static HitEntity calculateCallingHitId(HitEntity hit, Thread currentThread) {
		Long callingHitId = null;
		StackTraceElement[] stacktrace = currentThread.getStackTrace();
		if (stacktrace.length > 3) {
			stackTraceHitEntityMapLock.lock();
			try {
				Map<String, HitEntity> stackTraceHitEntityMap = stackTraceHitEntityMapThreadLocal.get();

				if (stackTraceHitEntityMap != null) {

					HitEntity childHit = null;
					int i = 3;
					while (childHit == null && i < stacktrace.length) {
						StackTraceElement parentStackTraceElement = stacktrace[i++];
						String key = parentStackTraceElement.getClassName() + "."
								+ parentStackTraceElement.getMethodName();
						//System.out.println("finding:" + key);
						childHit = stackTraceHitEntityMap.get(key);
						if (childHit != null) {
							callingHitId = childHit.getId();
						}
					}

				}
			} finally {
				stackTraceHitEntityMapLock.unlock();
			}
		}

		StackTraceElement thisStackTraceElement = null;
		if (stacktrace.length > 2) {
			thisStackTraceElement = stacktrace[2];
		}
		hit = HitDao.instance.insertHit(hit);

		if (thisStackTraceElement != null) {
			stackTraceHitEntityMapLock.lock();
			try {
				Map<String, HitEntity> stackTraceHitEntityMap = stackTraceHitEntityMapThreadLocal.get();
				if (stackTraceHitEntityMap == null) {
					stackTraceHitEntityMap = new HashMap<>();
					stackTraceHitEntityMapThreadLocal.set(stackTraceHitEntityMap);
				}
				String key = thisStackTraceElement.getClassName() + "." + thisStackTraceElement.getMethodName();
				//System.out.println("adding:" + key);
				stackTraceHitEntityMap.put(key, hit);
			} finally {
				stackTraceHitEntityMapLock.unlock();
			}
		}

		hit.setCallingHitId(callingHitId);
		return hit;
	}

	public static void endHit(HitEntity hit, boolean isMethod, Object[] args, Object returned, long duration,
			String fullClassName,
			String modifiers,
			String methodName, Annotation[] annotations) {
		//Thread currentThread = Thread.currentThread();
		//long threadId = currentThread.getId();

		//		String[] splitClassName = fullClassName.split("\\.");

		//TODO add some safety
		//		String className = splitClassName[splitClassName.length - 1];
		//		String packageName = String.join(".", Arrays.copyOfRange(splitClassName, 0, splitClassName.length - 1));

		//		hit.setThreadId(threadId);
		//		hit.setPackageName(packageName);
		//		hit.setClassName(className);
		//		hit.setMethodName(methodName);
		//		hit.setMethod(isMethod);
		//		hit.setModifiers(modifiers);
		//		hit.setAnnotations(JsonUtil.toJson(annotations));
		//hit.setArgs(JsonUtil.toJson(args));
		//hit.setReturned(JsonUtil.toJson(returned));

		hit.setDuration(duration);
		hit.setEndTime(System.nanoTime());

		HitDao.instance.updateHit(hit);

	}

}