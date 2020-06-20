package com.jayfella.devkit.service;

import javafx.fxml.Initializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * The service manager holds single instances of service such as JmeEngineService.
 * Allows us to easily access these services from various areas of the SDK without having to pass references.
 */
public class ServiceManager {

    private static Map<Class<? extends Service>, Service> services = new HashMap<>();

    public static <T extends Service> T getService(Class<T> serviceClass) {
        // return (T)services.get(serviceClass);

       Service service = services.entrySet().stream()
                .filter(entry -> isAssignable(entry.getValue().getClass(),serviceClass))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);

        return (T) service;
    }

    public static <T extends Service> T registerService(Class<T> serviceClass) {

        try {
            Constructor<T> constructor = serviceClass.getConstructor();
            T service = constructor.newInstance();
            services.put(serviceClass, service);
            return service;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T extends Service> T registerService(Service service) {
        return (T) services.put(service.getClass(), service);
    }

    public static void stop() {
        services.values().forEach(Service::stop);
    }

    private static boolean isAssignable(Class<?> clazz, Class<?> typeClass) {

        while (clazz != null) {

            if (clazz.isAssignableFrom(typeClass)) {
                return true;
            }

            Class<?>[] interfaces = clazz.getInterfaces();

            for (Class<?> interfaceClass : interfaces) {

                // lots of things implement savable. We don't want to match that.
                if (interfaceClass == Service.class || interfaceClass == Initializable.class) {
                    continue;
                }

                if (interfaceClass.isAssignableFrom(typeClass)) {
                    return true;
                }
            }

            clazz = clazz.getSuperclass();

            // everything is assignable from object.
            if (clazz == Object.class) {
                break;
            }
        }

        return false;
    }

}
