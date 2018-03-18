package se.uu.ub.cora.alvin.tocorastorage.db;

import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.alvin.tocorastorage.ConversionException;
import se.uu.ub.cora.alvin.tocorastorage.ParseException;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
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
		checkMapContainsRequiredValues(map);

		DataGroup country = createCountryDataGroupWithRecordInfo(map);
		// TODO: loopa värden eller plocka ut de vi vet att vi vill ha?
		for (Entry<String, String> entry : map.entrySet()) {
			checkNonEmptyValue(entry);
			DataAtomic dataAtomicChild = DataAtomic.withNameInDataAndValue(entry.getKey(),
					entry.getValue());
			country.addChild(dataAtomicChild);
		}
		// TODO: lägg till en länk till texten
		return country;
	}

	private void checkNonEmptyValue(Entry<String, String> entry) {
		if ("".equals(entry.getValue())) {
			throw ParseException.withMessageAndException(
					"Error converting country to Cora country: " + "Column must contain value.",
					null);

		}
	}

	private DataGroup createCountryDataGroupWithRecordInfo(Map<String, String> map) {
		DataGroup country = DataGroup.withNameInData("country");
		DataGroup recordInfo = createAndAddRecordInfo(map);
		country.addChild(recordInfo);
		return country;
	}

	private void checkMapContainsRequiredValues(Map<String, String> map) {
		if (!map.containsKey("iso31661Alpha2")) {
			throw ConversionException.withMessageAndException(
					"Error converting country to Cora country: Map does not contain value for iso31661Alpha2",
					null);
		}
	}

	private DataGroup createAndAddRecordInfo(Map<String, String> map) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", map.get("iso31661Alpha2")));
		DataGroup type = createLinkWithNameInDataTypeAndId("type", "recordType", "country");
		recordInfo.addChild(type);
		DataGroup dataDivider = createLinkWithNameInDataTypeAndId("dataDivider", "system", "alvin");
		recordInfo.addChild(dataDivider);
		return recordInfo;
		// createdby, updatedby osv?
	}

	private DataGroup createLinkWithNameInDataTypeAndId(String nameInData, String linkedRecordType,
			String linkedRecordId) {
		DataGroup type = DataGroup.withNameInData(nameInData);
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return type;
	}

}
