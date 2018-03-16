package se.uu.ub.cora.alvin.tocorastorage.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.sqldatabase.RecordReader;

public class RecordReaderSpy implements RecordReader {

	public String usedTableName = "";
	public List<Map<String, String>> returnedList;

	@Override
	public List<Map<String, String>> readAllFromTable(String tableName) {
		usedTableName = tableName;
		returnedList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("someKey", "someValue");
		returnedList.add(map);
		return returnedList;
	}

}
