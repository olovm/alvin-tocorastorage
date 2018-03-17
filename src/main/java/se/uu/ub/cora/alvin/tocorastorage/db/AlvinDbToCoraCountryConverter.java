package se.uu.ub.cora.alvin.tocorastorage.db;

import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.alvin.tocorastorage.ParseException;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinDbToCoraCountryConverter implements AlvinDbToCoraConverter {

	@Override
	public DataGroup fromXML(String xml) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup fromMap(Map<String, String> map) {
		if (map.isEmpty()) {
			return null;
		}

		for (Entry<String, String> entry : map.entrySet()) {
			if ("".equals(entry.getValue())) {
				throw ParseException.withMessageAndException(
						"Error converting country to Cora country: " + "Column must contain value.",
						null);

			}
		}
		return DataGroup.withNameInData("country");
	}

}
