/*
 * Copyright 2019 Uppsala University Library
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

import java.util.Collection;

import se.uu.ub.cora.bookkeeper.data.DataAttribute;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class AlvinCoraToFedoraPlaceConverter implements AlvinCoraToFedoraConverter {

	private HttpHandlerFactory httpHandlerFactory;
	private static String fedoraURL;
	private XMLXPathParser parser;

	public static AlvinCoraToFedoraPlaceConverter usingHttpHandlerFactoryAndFedoraUrl(
			HttpHandlerFactory httpHandlerFactory, String fedoraURL) {
		AlvinCoraToFedoraPlaceConverter.fedoraURL = fedoraURL;
		return new AlvinCoraToFedoraPlaceConverter(httpHandlerFactory);
	}

	private AlvinCoraToFedoraPlaceConverter(HttpHandlerFactory httpHandlerFactory) {
		this.httpHandlerFactory = httpHandlerFactory;
	}

	@Override
	public String toXML(DataGroup record) {
		String recordId = getIdFromRecord(record);
		String url = fedoraURL + "objects/" + recordId + "/datastreams/METADATA/content";
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("GET");
		String fedoraXML = httpHandler.getResponseText();
		parser = XMLXPathParser.forXML(fedoraXML);
		String defaultNameFromPlaceRecord = getDefaultNameFromPlaceRecord(record);
		setStringFromDocumentUsingXPath("/place/defaultPlaceName/name", defaultNameFromPlaceRecord);
		return parser.getDocumentAsString("/");
	}

	private String getIdFromRecord(DataGroup record) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void setStringFromDocumentUsingXPath(String xpathString, String newValue) {
		parser.setStringInDocumentUsingXPath(xpathString, newValue);
	}

	private String getDefaultNameFromPlaceRecord(DataGroup record) {
		Collection<DataGroup> nameGroups = record.getAllGroupsWithNameInDataAndAttributes("name",
				DataAttribute.withNameInDataAndValue("type", "authorized"));
		DataGroup nameGroup = nameGroups.iterator().next();
		Collection<DataGroup> defaultNames = nameGroup.getAllGroupsWithNameInDataAndAttributes(
				"namePart", DataAttribute.withNameInDataAndValue("type", "defaultName"));
		DataGroup defaultName = defaultNames.iterator().next();
		return defaultName.getFirstAtomicValueWithNameInData("value");
	}

}
