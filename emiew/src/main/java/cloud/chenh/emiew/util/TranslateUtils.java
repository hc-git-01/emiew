package cloud.chenh.emiew.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TranslateUtils {

    public static void main(String[] args) throws IOException {
        clean("C:\\Users\\chenh\\Downloads\\db.text.json", "C:\\Users\\chenh\\Downloads\\db.thin.json");
    }

    public static void clean(String src, String dst) throws IOException {
        String content = FileUtils.readFileToString(new File(src), StandardCharsets.UTF_8);
        JSONObject json = JSONObject.parseObject(content);
        removeKey(json, "intro", "links", "head", "frontMatters", "repo", "version");
        FileUtils.writeStringToFile(new File(dst), json.toJSONString(), StandardCharsets.UTF_8);
    }

    public static void removeKey(JSON json, String... keys) {
        if (json instanceof JSONArray) {
            JSONArray array = (JSONArray) json;
            array.forEach(obj -> {
                if (obj instanceof JSON) {
                    removeKey((JSON) obj, keys);
                }
            });
        } else {
            JSONObject object = (JSONObject) json;
            for (String key : keys) {
                object.remove(key);
            }

            object.forEach((k, v) -> {
                if (v instanceof JSON) {
                    removeKey((JSON) v, keys);
                }
            });
        }
    }

}
