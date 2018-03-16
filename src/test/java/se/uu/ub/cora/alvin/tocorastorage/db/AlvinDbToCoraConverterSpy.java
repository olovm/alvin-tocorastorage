package se.uu.ub.cora.alvin.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinDbToCoraConverterSpy implements AlvinDbToCoraConverter {

	public String xml;
	public DataGroup convertedDataGroup;
	public Map<String, String> mapToConvert;
	public DataGroup convertedDbDataGroup;

	@Override
	public DataGroup fromXML(String xml) {
		this.xml = xml;
		convertedDataGroup = DataGroup.withNameInData("Converted xml");
		return convertedDataGroup;
	}

	@Override
	public DataGroup fromMap(Map<String, String> map) {
		mapToConvert = map;
		convertedDbDataGroup = DataGroup.withNameInData("from Db converter");
		return convertedDbDataGroup;
	}

}
