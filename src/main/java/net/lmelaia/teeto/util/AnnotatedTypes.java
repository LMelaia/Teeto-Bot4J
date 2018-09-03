/*
 *  This file is part of TeetoBot4J.
 *
 *  TeetoBot4J is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TeetoBot4J is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TeetoBot4J.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.lmelaia.teeto.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Contains the result from an AnnotatedTypeFinder object.
 * <p>
 * These results include the methods, classes and
 * fields that were annotated with the specified
 * annotated, if the type was requested to be searched
 * for, otherwise the array is null.
 *
 * @param <T> the annotation type.
 */
public class AnnotatedTypes<T extends Annotation> {

    /**
     * List of all annotated methods, or null
     * if methods were not searched for.
     */
    private final Method[] methods;

    /**
     * List of all annotated fields, or null
     * if fields were not searched for.
     */
    private final Field[] fields;

    /**
     * List of all annotated classes, or null
     * if classes were not searched for.
     */
    private final Class<?>[] types;

    /**
     * The annotation searched for.
     */
    private final Class<? extends Annotation> annotation;

    /**
     * Constructs a new annotated types object.
     *
     * @param methods all annotated methods.
     * @param fields all annotated fields.
     * @param types all annotated classes.
     * @param annotation the annotation searched for.
     */
    AnnotatedTypes(Set<Method> methods, Set<Field> fields, Set<Class<?>> types, Class<? extends Annotation> annotation){
        if(methods != null)
            this.methods = methods.toArray(new Method[0]);
        else
            this.methods = null;

        if(fields != null)
            this.fields = fields.toArray(new Field[0]);
        else
            this.fields = null;

        if(types != null)
            this.types = types.toArray(new Class<?>[0]);
        else
            this.types = null;

        this.annotation = annotation;
    }

    /**
     * @return List of all annotated methods, or null
     *         if methods were not searched for.
     */
    public Method[] getMethods() {
        return methods;
    }

    /**
     * @return List of all annotated fields, or null
     *         if fields were not searched for.
     */
    public Field[] getFields() {
        return fields;
    }

    /**
     * @return List of all annotated classes, or null
     *         if classes were not searched for.
     */
    public Class<?>[] getTypes() {
        return types;
    }

    /**
     * Gets the specified annotation of a given method.
     *
     * @param method the method to introspect.
     * @return the annotation instance of the specified
     * method.
     */
    public T getAnnotationFromMethod(Method method){
        for(Annotation annotationX : method.getDeclaredAnnotations())
            if(annotationX.annotationType().equals(this.annotation))
                //Is checked
                //noinspection unchecked
                return (T)annotationX;

        return null;
    }
}
