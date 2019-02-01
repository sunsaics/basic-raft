package xyz.imcoder.raft.core.utils;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;

import java.io.*;

/**
 * @Author sunsai
 * @Date 2019/1/12 3:18 PM
 **/
public class Utils {

    private static final DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());

    public static byte[] toByteArray (Object obj) {
        JsonWriter writer = dslJson.newWriter();
        try {
            dslJson.serialize(writer, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toByteArray();
    }

    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public static Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public static <T> T toObject(byte[] bytes, Class<T> tClass) {
        T result = null;
        try {
            result = dslJson.deserialize(tClass, bytes, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] concat(byte[] first, byte[] second) {
        byte [] result = new byte[first.length + second.length];
        for (int i = 0; i < first.length; i++) {
            result[i] = first[i];
        }
        for (int i = 0; i < second.length; i++) {
            result[i + first.length] = second[i];
        }
        return result;
    }

}
