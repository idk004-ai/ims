package com.group1.interview_management.common;

import java.lang.reflect.Field;
import java.util.Collection;

public class DTOConverter {

     public static <T, U extends T> U convert(T parentObject, Class<U> childClass, Collection<Object> childValues) throws Exception {
          U childObject = childClass.getDeclaredConstructor().newInstance();

          // copy properties from parent to child
          for (Field field : parentObject.getClass().getDeclaredFields()) {
               field.setAccessible(true);
               Field childField = getField(childClass, field.getName());
               if (childField != null) {
                    childField.setAccessible(true);
                    childField.set(childObject, field.get(parentObject));
               }
          }

          // set additional properties
          int index = 0;
          for (Field field : childClass.getDeclaredFields()) {
               if (!isFieldInParent(parentObject.getClass(), field.getName())) {
                    field.setAccessible(true);
                    field.set(childObject, childValues.toArray()[index++]);
               }
          }
          return childObject;
     }

     private static Field getField(Class<?> clazz, String fieldName) {
          try {
               return clazz.getDeclaredField(fieldName);
          } catch (NoSuchFieldException e) {
               return null;
          }
     }

     private static boolean isFieldInParent(Class<?> parentClass, String fieldName) {
          try {
               parentClass.getDeclaredField(fieldName);
               return true;
          } catch (NoSuchFieldException e) {
               return false;
          }
     }
}
