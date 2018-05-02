/*
 * Copyright 2018 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.alvin.tocorastorage.fedora;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.uu.ub.cora.alvin.tocorastorage.ParseException;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinFedoraToCoraPlaceConverter implements AlvinFedoraToCoraConverter {

	private XMLXPathParser parser;

	@Override
	public DataGroup fromXML(String xml) {
		try {
			parser = XMLXPathParser.forXML(xml);
			return tryToCreateDataGroupFromDocument();
		} catch (Exception e) {
			throw ParseException.withMessageAndException(
					"Error converting place to Cora place: " + e.getMessage(), e);
		}
	}

	private DataGroup tryToCreateDataGroupFromDocument() {
		DataGroup place = DataGroup.withNameInData("authority");
		createRecordInfoAndAddToPlace(place);
		createDefaultNameAndAddToPlace(place);
		possiblyCreateCoordinatesAndAddToPlace(place);
		possiblyCreateCountryAndAddToPlace(place);
		possiblyCreateAlternativelNamesAndAddToPlace(place);

		return place;
	}

	private void possiblyCreateAlternativelNamesAndAddToPlace(DataGroup place) {
		NodeList placeNames = parser
				.getNodeListFromDocumentUsingXPath("/place/placeNameForms/entry");
		extractAlternativeNameFromNodesInListAndAddToPlace(place, placeNames);
	}

	private void extractAlternativeNameFromNodesInListAndAddToPlace(DataGroup place,
			NodeList placeNames) {
		for (int idx = 0; idx < placeNames.getLength(); idx++) {
			Node placeName = placeNames.item(idx);
			extractAlternativeNameFromNodeAndAddToPlace(place, placeName, idx);
		}
	}

	private void extractAlternativeNameFromNodeAndAddToPlace(DataGroup place,
			Node alternativeNameXML, int repeatId) {
		DataGroup localName = createDataGroupWithRepeatId(repeatId);
		convertLanguagePart(alternativeNameXML, localName);
		convertNamePart(alternativeNameXML, localName);
		place.addChild(localName);
	}

	private void convertNamePart(Node alternativeNameXML, DataGroup localName) {
		DataGroup namePart = createNamePart();
		extractNameValueAndAddToNamePart(alternativeNameXML, namePart);
		localName.addChild(namePart);
	}

	private DataGroup createNamePart() {
		DataGroup namePart = DataGroup.withNameInData("namePart");
		namePart.addAttributeByIdWithValue("type", "defaultName");
		return namePart;
	}

	private void extractNameValueAndAddToNamePart(Node alternativeNameXML, DataGroup namePart) {
		String nameValue = extractValueFromNode(alternativeNameXML, "placeNameForm/name");
		DataAtomic alternativeName = DataAtomic.withNameInDataAndValue("value", nameValue);
		namePart.addChild(alternativeName);
	}

	private void convertLanguagePart(Node placeName, DataGroup localName) {
		String languageValue = extractValueFromNode(placeName, "placeNameForm/language/alpha3Code");
		DataAtomic alternativeLanguage = DataAtomic.withNameInDataAndValue("language",
				languageValue);
		localName.addChild(alternativeLanguage);
	}

	private DataGroup createDataGroupWithRepeatId(int repeatId) {
		DataGroup localName = DataGroup.withNameInData("name");
		localName.setRepeatId(String.valueOf(repeatId));
		localName.addAttributeByIdWithValue("type", "alternative");
		return localName;
	}

	private String extractValueFromNode(Node placeName, String propertyXPath) {
		return parser.getStringFromNodeUsingXPath(placeName, propertyXPath);
	}

	private void createRecordInfoAndAddToPlace(DataGroup place) {
		DataGroup recordInfo = AlvinFedoraToCoraRecordInfoConverter.createRecordInfo(parser);
		place.addChild(recordInfo);
	}

	private String getStringFromDocumentUsingXPath(String xpathString) {
		return parser.getStringFromDocumentUsingXPath(xpathString);
	}

	private void createDefaultNameAndAddToPlace(DataGroup place) {
		DataGroup defaultName = DataGroup.withNameInData("name");
		place.addChild(defaultName);
		defaultName.addAttributeByIdWithValue("type", "authorized");
		createDefaultNamePartAndAddToName(defaultName);
	}

	private void createDefaultNamePartAndAddToName(DataGroup defaultName) {
		DataGroup defaultNamePart = DataGroup.withNameInData("namePart");
		defaultName.addChild(defaultNamePart);
		defaultNamePart.addAttributeByIdWithValue("type", "defaultName");
		defaultNamePart.addChild(DataAtomic.withNameInDataAndValue("value",
				getStringFromDocumentUsingXPath("/place/defaultPlaceName/name/text()")));
	}

	private void possiblyCreateCoordinatesAndAddToPlace(DataGroup place) {
		String latitude = getStringFromDocumentUsingXPath("/place/latitude/text()");
		String longitude = getStringFromDocumentUsingXPath("/place/longitude/text()");
		if (xmlContainsACompleteCoordinate(latitude, longitude)) {
			createCoordinatesAndAddToPlace(place, latitude, longitude);
		}
	}

	private boolean xmlContainsACompleteCoordinate(String latitude, String longitude) {
		return valueExists(latitude) && valueExists(longitude);
	}

	private void createCoordinatesAndAddToPlace(DataGroup place, String latitude,
			String longitude) {
		DataGroup coordinates = DataGroup.withNameInData("coordinates");
		coordinates.addChild(DataAtomic.withNameInDataAndValue("latitude", latitude));
		coordinates.addChild(DataAtomic.withNameInDataAndValue("longitude", longitude));
		place.addChild(coordinates);
	}

	private void possiblyCreateCountryAndAddToPlace(DataGroup place) {
		String alpha2Code = getStringFromDocumentUsingXPath("/place/country/alpha2Code/text()");
		if (valueExists(alpha2Code)) {
			createCountryAndAddToPlace(place, alpha2Code);
		}
	}

	private boolean valueExists(String alpha2Code) {
		return !"".equals(alpha2Code);
	}

	private void createCountryAndAddToPlace(DataGroup place, String alpha2Code) {
		DataGroup country = DataGroup.withNameInData("country");
		country.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "country"));
		country.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", alpha2Code));
		place.addChild(country);
	}

}
