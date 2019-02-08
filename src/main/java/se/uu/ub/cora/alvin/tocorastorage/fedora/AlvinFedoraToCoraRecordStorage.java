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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.uu.ub.cora.alvin.tocorastorage.NotImplementedException;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.spider.data.SpiderReadResult;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public final class AlvinFedoraToCoraRecordStorage implements RecordStorage {

	private static final String PLACE = "place";
	private HttpHandlerFactory httpHandlerFactory;
	private String baseURL;
	private AlvinFedoraToCoraConverterFactory converterFactory;

	private AlvinFedoraToCoraRecordStorage(HttpHandlerFactory httpHandlerFactory,
			AlvinFedoraToCoraConverterFactory converterFactory, String baseURL) {
		this.httpHandlerFactory = httpHandlerFactory;
		this.converterFactory = converterFactory;
		this.baseURL = baseURL;
	}

	// usingHttpHandlerFactoryAndConverterFactoryAndFedoraBaseURLAndFedoraUsernameAndFedoraPassword
	public static AlvinFedoraToCoraRecordStorage usingHttpHandlerFactoryAndConverterFactoryAndFedoraBaseURLAndFedoraUsernameAndFedoraPassword(
			HttpHandlerFactory httpHandlerFactory,
			AlvinFedoraToCoraConverterFactory converterFactory, String baseURL,
			String fedoraUsername, String fedoraPassword) {
		return new AlvinFedoraToCoraRecordStorage(httpHandlerFactory, converterFactory, baseURL);
	}

	@Override
	public DataGroup read(String type, String id) {
		if (PLACE.equals(type)) {
			return readAndConvertPlaceFromFedora(id);
		}
		throw NotImplementedException.withMessage("read is not implemented for type: " + type);
	}

	private DataGroup readAndConvertPlaceFromFedora(String id) {
		HttpHandler httpHandler = createHttpHandlerForPlace(id);
		AlvinFedoraToCoraConverter toCoraConverter = converterFactory.factor(PLACE);
		return toCoraConverter.fromXML(httpHandler.getResponseText());
	}

	private HttpHandler createHttpHandlerForPlace(String id) {
		String url = baseURL + "objects/" + id + "/datastreams/METADATA/content";
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("GET");
		return httpHandler;
	}

	@Override
	public void create(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		throw NotImplementedException.withMessage("create is not implemented");
	}

	@Override
	public void deleteByTypeAndId(String type, String id) {
		throw NotImplementedException.withMessage("deleteByTypeAndId is not implemented");
	}

	@Override
	public boolean linksExistForRecord(String type, String id) {
		throw NotImplementedException.withMessage("linksExistForRecord is not implemented");
	}

	@Override
	public void update(String type, String id, DataGroup record, DataGroup collectedTerms,
			DataGroup linkList, String dataDivider) {
		// throw NotImplementedException.withMessage("update is not implemented");
		// id = "alvin-place:22";
		String dsLabel = "labelFromCora";
		String url = baseURL + "objects/" + id + "/datastreams/METADATA?format=?xml&controlGroup=M"
				+ "&logMessage=coraWritten&checksumType=SHA-512&dsLabel=" + dsLabel;
		// String encoded = Base64.getEncoder()
		// .encodeToString(("fedoraAdmin:changeit").getBytes(StandardCharsets.UTF_8));
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		// httpHandler.setRequestProperty("Authorization", "Basic " + encoded);
		//
		// httpHandler.setRequestMethod("PUT");
		// httpHandler.setOutput(
		// "<place id=\"1\"><pid>alvin-place:22</pid><dsId>METADATA</dsId><recordInfo
		// id=\"2\"><externalDs>false</externalDs><lastAction>UPDATED</lastAction><created
		// id=\"3\"><date id=\"4\">2014-12-18 20:20:38.346 UTC</date><dateInStorage
		// id=\"5\">2014-12-18 20:20:39.815 UTC</dateInStorage><user class=\"seamUser\"
		// id=\"6\"><lastUpdated class=\"sql-timestamp\" id=\"7\">2014-04-17
		// 08:12:52.806</lastUpdated><id>1</id><userId>test</userId><domain>uu</domain><firstName>Test</firstName><lastName>Testsson</lastName><email>test.testsson@ub.uu.se</email></user><note>Place
		// created through web gui</note><type>CREATED</type></created><updated
		// id=\"8\"><userAction reference=\"3\"/><userAction id=\"9\"><date
		// id=\"10\">2014-12-18 20:21:20.880 UTC</date><user class=\"seamUser\"
		// id=\"11\"><lastUpdated class=\"sql-timestamp\" id=\"12\">2014-04-17
		// 08:12:52.806</lastUpdated><id>1</id><userId>test</userId><domain>uu</domain><firstName>Test</firstName><lastName>Testsson</lastName><email>test.testsson@ub.uu.se</email></user><note>Place
		// updated through web
		// gui</note><type>UPDATED</type></userAction></updated></recordInfo><country
		// class=\"country\"><lastUpdated class=\"sql-timestamp\" id=\"14\">2014-04-17
		// 08:12:48.8</lastUpdated><defaultName>Sverige</defaultName><localisedNames
		// id=\"15\"><entry><string>en</string><string>Sweden</string></entry></localisedNames><alpha2Code>SE</alpha2Code><alpha3Code>SWE</alpha3Code><numericalCode>752</numericalCode><marcCode>sw</marcCode></country><regions
		// id=\"16\"/><defaultPlaceName id=\"17\"><deleted>false</deleted>"
		// + "<name>Linköping from Cora7</name></defaultPlaceName>"
		// + "<placeNameForms id=\"18\"/><identifiers/><localIdentifiers
		// id=\"19\"><localIdentifier><type class=\"localIdentifierType\"><lastUpdated
		// class=\"sql-timestamp\">2014-04-17
		// 08:49:50.65</lastUpdated><defaultName>Waller-id</defaultName><localisedNames/><code>waller</code><id>114</id><internal>false</internal><organisationUnitId>2</organisationUnitId></type><text>1367</text></localIdentifier></localIdentifiers><longitude>15.62</longitude><latitude>58.42</latitude></place>");
		// int responseCode = httpHandler.getResponseCode();
		// int trams = responseCode;
	}

	@Override
	public SpiderReadResult readList(String type, DataGroup filter) {
		if (PLACE.equals(type)) {
			return readAndConvertPlaceListFromFedora();
		}
		throw NotImplementedException.withMessage("readList is not implemented for type: " + type);
	}

	private SpiderReadResult readAndConvertPlaceListFromFedora() {
		try {
			return tryCreateSpiderReadResultFromReadingAndConvertingPlaceListInFedora();
		} catch (Exception e) {
			throw ReadFedoraException
					.withMessageAndException("Unable to read list of places: " + e.getMessage(), e);
		}
	}

	private SpiderReadResult tryCreateSpiderReadResultFromReadingAndConvertingPlaceListInFedora() {
		SpiderReadResult spiderReadResult = new SpiderReadResult();
		spiderReadResult.listOfDataGroups = (List<DataGroup>) tryReadAndConvertPlaceListFromFedora();
		return spiderReadResult;
	}

	private Collection<DataGroup> tryReadAndConvertPlaceListFromFedora() {
		String placeListXML = getPlaceListXMLFromFedora();
		NodeList list = extractNodeListWithPidsFromXML(placeListXML);
		return constructCollectionOfPlacesFromFedora(list);
	}

	private String getPlaceListXMLFromFedora() {
		HttpHandler httpHandler = createHttpHandlerForPlaceList();
		String responseText = httpHandler.getResponseText();
		return responseText;
	}

	private HttpHandler createHttpHandlerForPlaceList() {
		String url = baseURL
				+ "objects?pid=true&maxResults=100&resultFormat=xml&query=pid%7Ealvin-place:*";
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("GET");
		return httpHandler;
	}

	private NodeList extractNodeListWithPidsFromXML(String placeListXML) {
		XMLXPathParser parser = XMLXPathParser.forXML(placeListXML);
		return parser
				.getNodeListFromDocumentUsingXPath("/result/resultList/objectFields/pid/text()");
	}

	private Collection<DataGroup> constructCollectionOfPlacesFromFedora(NodeList list) {
		Collection<DataGroup> placeList = new ArrayList<>();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			String pid = node.getTextContent();
			placeList.add(readAndConvertPlaceFromFedora(pid));
		}
		return placeList;
	}

	@Override
	public SpiderReadResult readAbstractList(String type, DataGroup filter) {
		throw NotImplementedException.withMessage("readAbstractList is not implemented");
	}

	@Override
	public DataGroup readLinkList(String type, String id) {
		throw NotImplementedException.withMessage("readLinkList is not implemented");
	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String type, String id) {
		throw NotImplementedException
				.withMessage("generateLinkCollectionPointingToRecord is not implemented");
	}

	@Override
	public boolean recordsExistForRecordType(String type) {
		throw NotImplementedException.withMessage("recordsExistForRecordType is not implemented");
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String type,
			String id) {
		throw NotImplementedException.withMessage(
				"recordExistsForAbstractOrImplementingRecordTypeAndRecordId is not implemented");
	}

}
