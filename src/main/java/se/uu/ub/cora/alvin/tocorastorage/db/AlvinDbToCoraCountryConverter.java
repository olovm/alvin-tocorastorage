package se.uu.ub.cora.alvin.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.alvin.tocorastorage.ConversionException;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinDbToCoraCountryConverter implements AlvinDbToCoraConverter {

	private static final String COUNTRY_STRING = "country";
	private static final String ALPHA2CODE = "alpha2code";

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
		checkMapContainsRequiredValue(map, ALPHA2CODE);
		checkMapContainsRequiredValue(map, "defaultName");

		DataGroup country = createCountryDataGroupWithRecordInfo(map);

		String alpha2 = map.get(ALPHA2CODE);
		DataAtomic isoAlpha2 = DataAtomic.withNameInDataAndValue(ALPHA2CODE, alpha2);
		country.addChild(isoAlpha2);
		// TODO: loopa värden eller plocka ut de vi vet att vi vill ha?
		// for (Entry<String, String> entry : map.entrySet()) {
		// checkNonEmptyValue(entry);
		// DataAtomic dataAtomicChild =
		// DataAtomic.withNameInDataAndValue(entry.getKey(),
		// entry.getValue());
		// country.addChild(dataAtomicChild);
		// }
		DataGroup text = createText(alpha2);
		country.addChild(text);
		// TODO: ska vi i detta läge skapa texten??
		return country;
	}

	private DataGroup createText(String alpha2) {
		DataGroup text = DataGroup.withNameInData("textId");
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "coraText"));
		String linkedRecordId = COUNTRY_STRING + alpha2 + "Text";
		text.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return text;
	}

	private void checkNonEmptyValue(String value) {
		if ("".equals(value)) {
			throw ConversionException.withMessageAndException(
					"Error converting country to Cora country: " + "Column must contain value.",
					null);

		}
	}

	private DataGroup createCountryDataGroupWithRecordInfo(Map<String, String> map) {
		DataGroup country = DataGroup.withNameInData(COUNTRY_STRING);
		DataGroup recordInfo = createAndAddRecordInfo(map);
		country.addChild(recordInfo);
		return country;
	}

	private void checkMapContainsRequiredValue(Map<String, String> map, String valueToGet) {
		if (!map.containsKey(valueToGet) || "".equals(map.get(valueToGet))) {
			throw ConversionException.withMessageAndException(
					"Error converting country to Cora country: Map does not contain value for "
							+ valueToGet,
					null);
		}
	}

	private DataGroup createAndAddRecordInfo(Map<String, String> map) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", map.get(ALPHA2CODE)));
		DataGroup type = createLinkWithNameInDataTypeAndId("type", "recordType", COUNTRY_STRING);
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
