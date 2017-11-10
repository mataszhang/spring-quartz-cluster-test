package com.matas.utils;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * JSON工具类
 * 
 * @author xiongyw
 *
 */
public class JSONUtil {
	private static final ObjectMapper objectMapper;
	static {
		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 获取泛型的Collection Type
	 * 
	 * @param collectionClass
	 *            泛型的Collection
	 * @param elementClasses
	 *            元素类
	 * @return JavaType Java类型
	 * @since 1.0
	 */
	public static JavaType getCollectionType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
		return objectMapper.getTypeFactory().constructParametrizedType(parametrized, parametersFor, parameterClasses);
	}

	/**
	 * 将给定的JSON串，转换为指定的对象
	 * 
	 * @param jsonStr
	 * @param clz
	 * @return
	 */
	public static <T> T jsonToObject(String jsonStr, Class<T> clz) {
		if (StringUtils.isEmpty(jsonStr)) {
			return null;
		}

		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(jsonStr, clz);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 将给定的JSON串，转换为指定的对象
	 * 
	 * <pre>
	 *   示例
	 *   <code>
	 *   	YSJsonResult<YSAccessToken> jsonToObject = JSONUtil.jsonToObject(result, JSONUtil.getCollectionType(YSJsonResult.class, YSJsonResult.class, YSAccessToken.class));
	 *   </code>
	 * </pre>
	 * 
	 * @param jsonStr
	 * @param clz
	 * @return
	 */
	public static <T> T jsonToObject(String jsonStr, JavaType type) {
		if (StringUtils.isEmpty(jsonStr)) {
			return null;
		}
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(jsonStr, type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 将给定的JSON串，转换为指定型的对象
	 * 
	 * <pre>
	 * 示例：
	 * <code>
	 * Map&ltString,User&gt result = JSONUtil.jsonToObject(jsonStr, new TypeReference&ltMap&ltString,User&gt&gt() { });
	 * </code>
	 * </pre>
	 * 
	 * @param jsonStr
	 * @param typeRef
	 *            泛型引用对象
	 * @return
	 */
	public static <T> T jsonToObject(String jsonStr, TypeReference<T> typeRef) {
		if (StringUtils.isEmpty(jsonStr)) {
			return null;
		}
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(jsonStr, typeRef);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 将给定的对象，转换为JSON字符串
	 * 
	 * @param object
	 *            待转换为JSON字符串的对象
	 * @return
	 */
	public static String objectToJson(Object object) {
		if (object == null) {
			return null;
		}

		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
