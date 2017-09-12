package com.linghua.hdds.common;

import java.nio.ByteBuffer;
import java.util.Map.Entry;

import org.apache.avro.util.Utf8;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.linghua.hdds.meta.Hcolumn.Type;



public class ByteUtils {

	public static byte[] toBytes(Object o) {

		Class<?> clazz = o.getClass();

		if (clazz.equals(Byte.TYPE) || clazz.equals(Byte.class))
			return new byte[] { (byte) o };
		else if (clazz.equals(String.class))
			return Bytes.toBytes((String) o);
		else if (clazz.equals(Integer.TYPE) || clazz.equals(Integer.class))
			return Bytes.toBytes((Integer) o);
		else if (clazz.equals(Float.TYPE) || clazz.equals(Float.class))
			return Bytes.toBytes((Float) o);
		else if (clazz.equals(Double.TYPE) || clazz.equals(Double.class))
			return Bytes.toBytes((Double) o);
		else if (clazz.equals(Boolean.TYPE) || clazz.equals(Boolean.class))
			return Bytes.toBytes((Boolean) o);
		else if (clazz.equals(Long.TYPE) || clazz.equals(Long.class))
			return Bytes.toBytes((Long) o);
		else if (clazz.equals(Character.TYPE) || clazz.equals(Character.class))
			return Bytes.toBytes((Character) o);
		else if (clazz.equals(Short.TYPE) || clazz.equals(Short.class))
			return Bytes.toBytes((Short) o);
		else if (clazz.isArray())
			return (byte[]) o;
		else if (clazz.isEnum())
			return new byte[] { (byte) ((Enum<?>) o).ordinal() };

		throw new RuntimeException("can't parse data as clazz " + clazz);
	}

	public static Object fromBytes(byte[] val, Type t) {

		switch (t) {
		
		case STRING:
			return Bytes.toString(val);
		case BYTES:
			return ByteBuffer.wrap(val);
		case INT:
			return Bytes.toInt(val);
		case LONG:
			return Bytes.toLong(val);
		case FLOAT:
			return Bytes.toFloat(val);
		case DOUBLE:
			return Bytes.toDouble(val);
		case BOOLEAN:
			return val[0] != 0;
			
		default:throw new RuntimeException("unknow type:"+t.getName());
		}

	}

	public static void main(String[] args) {

	}

}
