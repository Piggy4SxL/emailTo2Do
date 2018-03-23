package sxl.mp.et2d.util;


import javax.servlet.http.HttpServletRequest;

/**
 * @author SxL
 *         Created on 2017/10/31.
 */
public class HttpServletRequestUtil {
    public static int getInt(HttpServletRequest request, String key){
        try {
            return Integer.decode(request.getParameter(key));
        } catch (Exception e) {
            return -1;
        }
    }

    public static long getLong(HttpServletRequest request, String key){
        try {
            return Long.valueOf(request.getParameter(key));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static double getDouble(HttpServletRequest request, String key){
        try {
            return Double.valueOf(request.getParameter(key));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean getBoolean(HttpServletRequest request, String key){
        try {
            return Boolean.valueOf(request.getParameter(key));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getString(HttpServletRequest request, String key){
        try {
            String result = request.getParameter(key);

            if (request != null)
                return result.trim();
            if ("".equals(result))
                return null;
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}
