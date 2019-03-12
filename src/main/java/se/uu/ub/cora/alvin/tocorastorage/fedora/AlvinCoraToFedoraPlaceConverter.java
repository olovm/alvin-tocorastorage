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

import java.io.StringWriter;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.uu.ub.cora.alvin.tocorastorage.ResourceReader;
import se.uu.ub.cora.bookkeeper.data.DataAttribute;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class AlvinCoraToFedoraPlaceConverter implements AlvinCoraToFedoraConverter {

	private HttpHandlerFactory httpHandlerFactory;
	private String fedoraURL;
	private XMLXPathParser parser;
	private Document document;

	public static AlvinCoraToFedoraPlaceConverter usingHttpHandlerFactoryAndFedoraUrl(
			HttpHandlerFactory httpHandlerFactory, String fedoraURL) {
		return new AlvinCoraToFedoraPlaceConverter(httpHandlerFactory, fedoraURL);
	}

	private AlvinCoraToFedoraPlaceConverter(HttpHandlerFactory httpHandlerFactory,
			String fedoraURL) {
		this.httpHandlerFactory = httpHandlerFactory;
		this.fedoraURL = fedoraURL;
	}

	@Override
	public String toXML(DataGroup record) {
		String recordId = getIdFromRecord(record);
		String fedoraXML = getXMLForRecordFromFedora(recordId);
		parser = XMLXPathParser.forXML(fedoraXML);
		convertDefaultName(record);
		return parser.getDocumentAsString("/");
	}

	private String getXMLForRecordFromFedora(String recordId) {
		String url = fedoraURL + "objects/" + recordId + "/datastreams/METADATA/content";
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("GET");
		return httpHandler.getResponseText();
	}

	private String getIdFromRecord(DataGroup record) {
		DataGroup recordInfo = record.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void convertDefaultName(DataGroup record) {
		String defaultNameFromPlaceRecord = getDefaultNameFromPlaceRecord(record);
		setStringFromDocumentUsingXPath("/place/defaultPlaceName/name", defaultNameFromPlaceRecord);
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

	public HttpHandlerFactory getHttpHandlerFactory() {
		// needed for tests
		return httpHandlerFactory;
	}

	public String getFedorURL() {
		// needed for tests
		return fedoraURL;
	}

	@Override
	public String toNewXML(DataGroup record) {
		String newPlaceTemplate = ResourceReader.readResourceAsString("place/templatePlace.xml");
		parser = XMLXPathParser.forXML(newPlaceTemplate);
		setStringFromDocumentUsingXPath("/place/pid", getIdFromRecord(record));
		convertDefaultName(record);
		return parser.getDocumentAsString("/");
	}

	private Element createPidNodeUsingRecordAndDocument(DataGroup record) {
		String recordId = getIdFromRecord(record);
		Element pid = document.createElement("pid");
		pid.appendChild(document.createTextNode(recordId));
		return pid;
	}

	private Element createDefaultNameElement(DataGroup record) {
		String nameString = extractNameFromDataGroup(record);
		Element defaultName = document.createElement("defaultPlaceName");
		createAndAddDeletedNode(defaultName);
		createAndAddNameNode(nameString, defaultName);
		return defaultName;
	}

	private void createAndAddDeletedNode(Element parentElement) {
		createTextNodeWithTagNameAndAddToParent("deleted", "false", parentElement);
	}

	private void createTextNodeWithTagNameAndAddToParent(String elementTagName,
			String textNodeValue, Element parentElement) {
		Element element = document.createElement(elementTagName);
		element.appendChild(document.createTextNode(textNodeValue));
		parentElement.appendChild(element);
	}

	private void createAndAddNameNode(String nameString, Element parentElement) {
		createTextNodeWithTagNameAndAddToParent("name", nameString, parentElement);
	}

	private String extractNameFromDataGroup(DataGroup record) {
		DataAttribute nameAttribute = DataAttribute.withNameInDataAndValue("type", "authorized");
		Collection<DataGroup> authorizedNames = record
				.getAllGroupsWithNameInDataAndAttributes("name", nameAttribute);

		DataAttribute namePartAttribute = DataAttribute.withNameInDataAndValue("type",
				"defaultName");
		DataGroup authorizedName = authorizedNames.iterator().next();
		Collection<DataGroup> nameParts = authorizedName
				.getAllGroupsWithNameInDataAndAttributes("namePart", namePartAttribute);
		DataGroup defaultNamePart = nameParts.iterator().next();
		return defaultNamePart.getFirstAtomicValueWithNameInData("value");
	}

	private void addDocumentToWriterAndTransform(StringWriter writer, Transformer transformer)
			throws TransformerException {
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(writer);

		transformer.transform(source, result);
	}

	private Transformer createTransformer()
			throws TransformerFactoryConfigurationError, TransformerConfigurationException {
		TransformerFactory transformerFactory2 = TransformerFactory.newInstance();

		// Transformer transformer = transformerFactory.newTransformer();
		Transformer transformer = transformerFactory2.newTransformer();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		return transformer;
	}

}
