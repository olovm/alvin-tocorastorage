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
package se.uu.ub.cora.alvin.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.alvin.tocorastorage.ParseException;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinDbToCoraPlaceConverter implements AlvinDbToCoraConverter {

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

		createCoordinatesAndAddToPlace(place);
		return place;
	}

	private void createRecordInfoAndAddToPlace(DataGroup place) {
		DataGroup recordInfo = AlvinDbToCoraRecordInfoConverter.createRecordInfo(parser);
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

	private void createCoordinatesAndAddToPlace(DataGroup place) {
		DataGroup coordinates = DataGroup.withNameInData("coordinates");
		place.addChild(coordinates);
		coordinates.addChild(DataAtomic.withNameInDataAndValue("latitude",
				getStringFromDocumentUsingXPath("/place/latitude/text()")));
		coordinates.addChild(DataAtomic.withNameInDataAndValue("longitude",
				getStringFromDocumentUsingXPath("/place/longitude/text()")));
	}

	@Override
	public DataGroup fromMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		return null;
	}
}
