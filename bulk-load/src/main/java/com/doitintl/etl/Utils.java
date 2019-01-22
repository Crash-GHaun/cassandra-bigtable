package com.doitintl.etl;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.google.protobuf.ByteString;

class Utils {
	static ByteString getByteStringValue(Row row, String columnName, DataType dataType){
		ByteString out;

		if(row.isNull(columnName)){
			out = ByteString.EMPTY;
		}
		else{
			switch (dataType.getName()){
				case ASCII:
				case TEXT:
				case VARCHAR:
					out = ByteString.copyFrom(row.getString(columnName).getBytes());
					break;
				case BIGINT:
				case COUNTER:
					out = ByteString.copyFromUtf8(String.valueOf(row.getLong(columnName)));
					break;
				case BLOB:
					out = ByteString.copyFrom(row.getBytes(columnName));
					break;
				case BOOLEAN:
					if(row.getBool(columnName)){
						out = ByteString.copyFromUtf8("True");
					}else{
						out = ByteString.copyFromUtf8("False");
					}
					break;
				case CUSTOM:
					out = ByteString.EMPTY;
					break;
				case DECIMAL:
					out = ByteString.copyFromUtf8(row.getDecimal(columnName).toString());
					break;
				case DOUBLE:
					out = ByteString.copyFromUtf8(String.valueOf(row.getDouble(columnName)));
					break;
				case FLOAT:
					out = ByteString.copyFromUtf8(String.valueOf(row.getFloat(columnName)));
					break;
				case INET:
					out = ByteString.copyFromUtf8(row.getInet(columnName).toString());
					break;
				case INT:
					out = ByteString.copyFromUtf8(String.valueOf(row.getInt(columnName)));
					break;
				case LIST:
				case MAP:
				case SET:
					out = ByteString.EMPTY;
					break;
				case TIMESTAMP:
					out = ByteString.copyFromUtf8(row.getDate(columnName).toString());
					break;
				case TIMEUUID:
				case UUID:
					out = ByteString.copyFromUtf8(row.getUUID(columnName).toString());
					break;
				case VARINT:
					out = ByteString.copyFromUtf8(row.getVarint(columnName).toString());
					break;
				default:
					out = ByteString.EMPTY;
					break;
			}
		}

		return out;
	}
}
