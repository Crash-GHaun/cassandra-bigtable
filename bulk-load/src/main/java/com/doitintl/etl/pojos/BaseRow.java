package com.doitintl.etl.pojos;

import com.google.bigtable.v2.Mutation;
import com.google.bigtable.v2.Mutation.SetCell;
import com.google.protobuf.ByteString;
import lombok.Data;
import org.apache.beam.sdk.values.KV;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseRow implements Serializable {
	private static final long serialVersionUID = 5388597170603971222L;

	/**
	 * Every class that extends this class will have to implement it's own createBigTableRow().
	 * This method will create a BigTable row represent the POJO.
	 * @return KV element that represent a key and it's mutations
	 */
	abstract public KV<ByteString, Iterable<Mutation>> createBigTableRow();

	/**
	 * Each class should define it's own cell's timestamp
	 * @return
	 */
	abstract public Date cellTimeStamp();

	/**
	 * Generate a BigTable row mutation
	 * @param family Column family for the cell
	 * @param columnQualifier Qualifier family for the cell
	 * @param value Cell's value
	 * @return A BigTable row mutation
	 */
	Mutation getMutation(String family, String columnQualifier, String value){
		return Mutation
				.newBuilder()
				.setSetCell(getCell(family,columnQualifier, value))
				.build();
	}

	/**
	 * Generate a SetCell
	 * @param family Column family for the cell
	 * @param columnQualifier Qualifier family for the cell
	 * @param value Cell's value
	 * @return a SetCell
	 */
	private SetCell getCell(String family, String columnQualifier, String value){
		return SetCell.newBuilder()
				.setFamilyName(family)
				.setColumnQualifier(ByteString.copyFromUtf8(columnQualifier))
				.setTimestampMicros(this.cellTimeStamp().getTime()*1000)
				.setValue(ByteString.copyFromUtf8(value))
				.build();
	}
}
