package org.mkdev.ut.bean2string;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2015-02-01
 */
public class BeanToStringHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanToStringHelper.class);

    private Class bean;
    private Set<Method> propertiesToMethods = new LinkedHashSet<>();

    private static final String COMMA = ",";
    private static final String[] propertyMethodPrefix = new String[]{"is", "get"};

    public BeanToStringHelper(Class bean) {
        this.bean = bean;
    }

    public BeanToStringHelper addProperties(String propertyNamesCSV) {
        BeanToStringHelper tempThis = this;

        if (propertyNamesCSV != null && !propertyNamesCSV.isEmpty()) {
            for (String propertyName : Arrays.asList(propertyNamesCSV.split(COMMA))) {
                LOGGER.debug("looking up property: {}", propertyName.trim());
                tempThis = tempThis.addProperty(propertyName.trim());
            }
        }

        return tempThis;
    }

    public BeanToStringHelper addProperty(String propertyName) {

        String propertyNameToProcess = propertyName.toLowerCase();

        for (Method method : bean.getDeclaredMethods()) {
            String methodNameToProcess = method.getName().toLowerCase();

            if (methodNameToProcess.contains("is" + propertyNameToProcess) || methodNameToProcess.contains("get" + propertyNameToProcess)) {
                this.propertiesToMethods.add(method);

                LOGGER.debug("found method [{}] for property: {}", propertyName.trim());

                break;
            }
        }

        return this;
    }

    public String process(Object objectInstance, String separator) {
        StringBuilder result = new StringBuilder();

        int count = 0;
        for (Method method: propertiesToMethods) {
            try {
                result.append(method.invoke(objectInstance, (Object[]) null));
                if (count != propertiesToMethods.size()-1) {
                    result.append(separator);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            count++;
        }

        return result.toString();
    }
}
