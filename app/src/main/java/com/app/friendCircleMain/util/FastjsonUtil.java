package com.app.friendCircleMain.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.util.ArrayList;
import java.util.Date;

/**
 * @ClassName FastjsonUtil
 * @Description json转化工具
 * @author d_sun dreamfly@126.com
 * @date 2014年6月18日 上午9:26:22
 */
public class FastjsonUtil {
	private static SerializeConfig mapping = new SerializeConfig();

	/**
	 * Object转化成json 默认处理时间
	 * 
	 * @param jsonText
	 * @return
	 */
	public static String object2json(Object jsonText) {
		return JSON.toJSONString(jsonText,
				SerializerFeature.WriteDateUseDateFormat);
	}

	/**
	 * Object转化成json 自定义时间格式
	 * 
	 * @param jsonText
	 * @return
	 */
	public static String object2json(String dateFormat, Object jsonText) {
		mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
		return JSON.toJSONString(jsonText, mapping);
	}

	/**
	 * JSON转化成Object
	 * 
	 * @param jsonText
	 * @param clazz
	 * @return
	 */
	public static <T> T json2object(String jsonText, Class<T> clazz) {
		return (T) JSON.parseObject(jsonText, clazz);
	}

	public static ArrayList json2list(String jsonString, Class clazz) {
		ArrayList list = new ArrayList();
		try {
			list = (ArrayList) JSON.parseArray(jsonString, clazz);
		} catch (Exception e) {
		}
		return list;
	}
}