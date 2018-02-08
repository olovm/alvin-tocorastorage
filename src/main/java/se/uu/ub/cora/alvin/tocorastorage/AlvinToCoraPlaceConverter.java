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
package se.uu.ub.cora.alvin.tocorastorage;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinToCoraPlaceConverter implements AlvinToCoraConverter {

	private Document document;
	private XPath xpath;

	@Override
	public DataGroup fromXML(String xml) {
		try {
			document = new DocumentCreator().createDocumentFromXML(xml);
			setupXPath();
			return tryToCreateDataGroupFromDocument();
		} catch (Exception e) {
			throw ParseException.withMessageAndException(
					"Error converting place to Cora place: " + e.getMessage(), e);
		}
	}

	private void setupXPath() {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();
	}

	private DataGroup tryToCreateDataGroupFromDocument() throws XPathExpressionException {
		DataGroup place = DataGroup.withNameInData("authority");
		createRecordInfoAndAddToPlace(place);

		createDefaultNameAndAddToPlace(place);
		return place;
	}

	private void createRecordInfoAndAddToPlace(DataGroup place) throws XPathExpressionException {
		DataGroup recordInfo = createRecordInfoAsPlace();
		place.addChild(recordInfo);

		String pid = getStringFromDocumentUsingXPath("/place/pid/text()");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", pid));

		DataGroup createdBy = createLinkWithNameInDataAndTypeAndId("createdBy", "user", "12345");
		recordInfo.addChild(createdBy);
	}

	private String getStringFromDocumentUsingXPath(String xpathString)
			throws XPathExpressionException {
		XPathExpression expr = xpath.compile(xpathString);
		return (String) expr.evaluate(document, XPathConstants.STRING);
	}

	private DataGroup createRecordInfoAsPlace() {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");

		DataGroup type = createLinkWithNameInDataAndTypeAndId("type", "recordType", "place");
		recordInfo.addChild(type);
		DataGroup dataDivider = createLinkWithNameInDataAndTypeAndId("dataDivider", "system",
				"alvin");
		recordInfo.addChild(dataDivider);

		return recordInfo;
	}

	private DataGroup createLinkWithNameInDataAndTypeAndId(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		DataGroup type = DataGroup.withNameInData(nameInData);
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return type;
	}

	private void createDefaultNameAndAddToPlace(DataGroup place) throws XPathExpressionException {
		DataGroup defaultName = DataGroup.withNameInData("name");
		place.addChild(defaultName);
		defaultName.addAttributeByIdWithValue("type", "authorized");
		createDefaultNamePartAndAddToName(defaultName);
	}

	private void createDefaultNamePartAndAddToName(DataGroup defaultName)
			throws XPathExpressionException {
		DataGroup defaultNamePart = DataGroup.withNameInData("namePart");
		defaultName.addChild(defaultNamePart);
		defaultNamePart.addAttributeByIdWithValue("type", "defaultName");
		defaultNamePart.addChild(DataAtomic.withNameInDataAndValue("value",
				getStringFromDocumentUsingXPath("/place/defaultPlaceName/name/text()")));
	}

}
