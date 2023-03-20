package com.example.samplegateway.utils;

import com.example.samplegateway.config.ApplicationContextProvider;

public class BeanUtils {

   public static Object getBean(String beanName) {
      return ApplicationContextProvider.getContext().getBean(beanName);

   }

   public static <T> Object getBean(Class<T> clz) {
      return ApplicationContextProvider.getContext().getBean(clz);

   }

}
