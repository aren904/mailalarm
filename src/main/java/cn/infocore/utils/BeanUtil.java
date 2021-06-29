package cn.infocore.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: mailalarm
 * @Package: cn.infocore.utils
 * @ClassName: BeanUtil
 * @Author: aren904
 * @Description:
 * @Date: 2021/6/22 20:09
 * @Version: 1.0
 */
@Service
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
