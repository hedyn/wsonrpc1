/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public final class RemoteInvoker {
    
    public static RemoteInvoker create(Remote remote) {
        return new RemoteInvoker(remote);
    }
    
    private final Remote remote;
    private String serviceName;
    private ClassLoader classLoader;
    private int timeout;
    
    private RemoteInvoker(Remote remote) {
        this.remote = remote;
    }
    
    /**
     * 
     * @param serviceName
     * @return
     */
    public RemoteInvoker serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    /**
     * 
     * @param classLoader
     * @return
     */
    public RemoteInvoker classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    /**
     * 设置超时时间，0表示永不超时。单位为TimeUnit.MILLISECONDS
     * 
     * @param timeout
     * @return
     */
    public RemoteInvoker timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 
     * @param serviceClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> serviceClass) {
        if (classLoader == null) {
            classLoader = serviceClass.getClassLoader();
        }
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return proxyObjectMethods(method, proxy, args);
                }
                Class<?> returnType = method.getReturnType();
                if (returnType == void.class) {
                    remote.invoke(serviceName, method.getName(), args);
                    return null;
                }
                return remote.invoke(serviceName, method.getName(), args, returnType, timeout);
            }
        };
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[] { serviceClass }, handler);
    }

    private static Object proxyObjectMethods(Method method, Object proxyObject, Object[] args) {
        String name = method.getName();
        if (name.equals("toString")) {
            return proxyObject.getClass().getName() + "@" + System.identityHashCode(proxyObject);
        }
        if (name.equals("hashCode")) {
            return System.identityHashCode(proxyObject);
        }
        if (name.equals("equals")) {
            return proxyObject == args[0];
        }
        throw new RuntimeException(method.getName() + " is not a member of java.lang.Object");
    }

}
