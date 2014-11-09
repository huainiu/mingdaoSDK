package com.mingdao.api.htf.core;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.MissingResourceException;
import java.io.UnsupportedEncodingException;


public class LoadProperties {
    public static final int FILE_NAME = 1;
    public static final int BASE_NAME = 2;
    private final static String DEFAULT_BUNDLE = "ApplicationResources";
    private ResourceBundle properties = null;

    /**
     * 构造函数
     */
    public LoadProperties()
            throws MissingResourceException {
        properties = ResourceBundle.getBundle(DEFAULT_BUNDLE);
    }

    /**
     * @function 指定配置文件名的构造函数
     * @param
     */
    public LoadProperties(String baseName)
            throws MissingResourceException
    {
            properties = ResourceBundle.getBundle(baseName);
        
    }

    public LoadProperties(String resourceName, int nameModel)
            throws MissingResourceException, IOException
    {
        if (nameModel == BASE_NAME) {
            //先在classpath目录下获取资源
                properties = ResourceBundle.getBundle(resourceName);
        }
        else if (nameModel == FILE_NAME) {
            //然后通过文件输入流获取资源
                InputStream in = new BufferedInputStream(new FileInputStream(
                        resourceName));
                properties = new PropertyResourceBundle(in);
                in.close();
        }
        else {
            //默认在classpath目录下获取资源
                properties = ResourceBundle.getBundle(resourceName);
            }
    }

    /**
     * @function 从指定的配置文件中得到指定键值的属性
     * @param key The key of properties
     * @return the value of the key
     */
    public String getProperties(String key)
            throws MissingResourceException, UnsupportedEncodingException
    {
        String value = properties.getString(key);
        if (value != null) {
            value = new String(value.getBytes("ISO8859_1"), "GBK");
        }
        return value;
    }

    /**
     * @function 从指定的配置文件中得到指定键值的属性,并提供一个缺省的属性值
     * @param key The key of properties
     * @param defaultValue The default value
     * @return the value of the key
     */
    public String getProperties(String key, String defaultValue) {
        try {
            String value = properties.getString(key);
            if (value == null) {
                return defaultValue;
            }
            else {
                return value;
            }
        }
        catch (MissingResourceException exp) {
            return defaultValue;
        }
    }

  /**
   * 取得classes的上下文路径（可以提供给WEB端JSP和class调用）
   * @throws Exception
   * @return String 取得classes的上下文路径（D:/bea/user_projects/domains/bbdomain/applications/km/）
   */
  public String getServletClassesPath() throws Exception {

        String servletClassesPath = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        //java.net.URL url = classLoader.getResource("/");//在WebLogic下没问题,WebSphere61下取不出来
        java.net.URL url = classLoader.getResource("ApplicationResources.properties");
        if (url == null) {
            servletClassesPath = ".";
        } else {
            servletClassesPath = url.getPath();
        }
        /*if (!servletClassesPath.endsWith(java.io.File.separator)) {
            servletClassesPath += java.io.File.separator;
        }*/
        if (servletClassesPath.indexOf("WEB-INF") != -1) {
            servletClassesPath = servletClassesPath.substring(0,servletClassesPath.indexOf("WEB-INF"));
        }
        //Updated By LiJH on 2007/05/23 在JBOSS环境下取servletClassesPath时要去掉最前面的斜杠符号"/"
        //servletClassesPath = "/C:/Training/RMS1.1.0/server/default/./deploy/km.war/"
        if (servletClassesPath.startsWith("/")) {
            servletClassesPath = servletClassesPath.substring(1);
            //servletClassesPath = servletClassesPath.substring(1,servletClassesPath.length());
        }
        return servletClassesPath;
  }
}
