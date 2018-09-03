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

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/**
 * Provides a way to find all methods, classes and
 * fields annotated with a specific annotation
 * within a specified package.
 *
 * @param <T> the annotation type.
 */
public class AnnotatedTypeFinder<T extends Annotation> {

    /**
     * Reflections API object.
     */
    private final Reflections reflections;

    /**
     * Flags that determine whether or not
     * to search for methods, classes and fields.
     */
    private boolean findMethods = false, findClasses = false, findFields = false;

    /**
     * Constructs a new annotated type finder.
     *
     * @param _package the package to search.
     * @param types the ElementTypes (class, field, method) to search for.
     */
    public AnnotatedTypeFinder(String _package, ElementType... types){
        ArrayList<Scanner> scanners = new ArrayList<>();

        for(ElementType type : types){
            switch(type){
                case METHOD:
                    scanners.add(new MethodAnnotationsScanner());
                    findMethods = true;
                    break;
                case TYPE:
                    scanners.add(new TypeAnnotationsScanner());
                    findClasses = true;
                    break;
                case FIELD:
                    findFields = true;
                    scanners.add(new FieldAnnotationsScanner());
                    break;
            }
        }

        reflections = new Reflections(new ConfigurationBuilder().setUrls(
                ClasspathHelper.forPackage( _package)).setScanners(scanners.toArray(new Scanner[0])));
    }

    /**
     * Finds all annotated types requested
     * and constructs a new AnnotatedTypes object
     * containing the methods, classes and fields
     * that were annotated.
     *
     * @param annotation the annotation to search for.
     * @return the newly constructed AnnotatedTypes object
     * containing the annotated types.
     */
    public AnnotatedTypes<T> find(Class<T> annotation){
        Set<Method> methods = null;
        Set<Field> fields = null;
        Set<Class<?>> types = null;

        if(findMethods)
            methods = reflections.getMethodsAnnotatedWith(annotation);

        if(findFields)
            fields = reflections.getFieldsAnnotatedWith(annotation);

        if(findClasses)
            types = reflections.getTypesAnnotatedWith(annotation);

        return new AnnotatedTypes<>(methods, fields, types, annotation);
    }
}
