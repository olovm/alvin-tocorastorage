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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorastorage.NotImplementedException;
import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class AlvinFedoraToCoraRecordStorageTest {
	private AlvinFedoraToCoraRecordStorage alvinToCoraRecordStorage;
	private HttpHandlerFactorySpy httpHandlerFactory;
	private AlvinFedoraToCoraConverterFactorySpy converterFactory;
	private String baseURL = "http://alvin-cora-fedora:8088/fedora/";
	private String fedoraUsername = "fedoraUser";
	private String fedoraPassword = "fedoraPassword";

	@BeforeMethod
	public void BeforeMethod() {
		httpHandlerFactory = new HttpHandlerFactorySpy();
		converterFactory = new AlvinFedoraToCoraConverterFactorySpy();
		alvinToCoraRecordStorage = AlvinFedoraToCoraRecordStorage
				.usingHttpHandlerFactoryAndConverterFactoryAndFedoraBaseURLAndFedoraUsernameAndFedoraPassword(
						httpHandlerFactory, converterFactory, baseURL, fedoraUsername,
						fedoraPassword);
	}

	@Test
	public void testInit() throws Exception {
		assertNotNull(alvinToCoraRecordStorage);
	}

	@Test
	public void alvinToCoraRecordStorageImplementsRecordStorage() throws Exception {
		assertTrue(alvinToCoraRecordStorage instanceof RecordStorage);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "read is not implemented for type: null")
	public void readThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.read(null, null);
	}

	@Test
	public void readPlaceCallsFedoraAndReturnsConvertedResult() throws Exception {
		httpHandlerFactory.responseText = "Dummy response text";
		DataGroup readPlace = alvinToCoraRecordStorage.read("place", "alvin-place:22");
		assertEquals(httpHandlerFactory.urls.get(0),
				baseURL + "objects/alvin-place:22/datastreams/METADATA/content");
		assertEquals(httpHandlerFactory.factoredHttpHandlers.size(), 1);
		HttpHandlerSpy httpHandler = (HttpHandlerSpy) httpHandlerFactory.factoredHttpHandlers
				.get(0);
		assertEquals(httpHandler.requestMetod, "GET");

		assertEquals(converterFactory.factoredToCoraConverters.size(), 1);
		assertEquals(converterFactory.factoredToCoraTypes.get(0), "place");
		AlvinFedoraToCoraConverterSpy alvinToCoraConverter = (AlvinFedoraToCoraConverterSpy) converterFactory.factoredToCoraConverters
				.get(0);
		assertEquals(alvinToCoraConverter.xml, httpHandlerFactory.responseText);
		assertEquals(readPlace, alvinToCoraConverter.convertedDataGroup);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "create is not implemented")
	public void createThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.create(null, null, null, null, null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "deleteByTypeAndId is not implemented")
	public void deleteByTypeAndIdThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.deleteByTypeAndId(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "linksExistForRecord is not implemented")
	public void linksExistForRecordThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.linksExistForRecord(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "update is not implemented for type: someNotImplementedType")
	public void updateThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.update("someNotImplementedType", null, null, null, null, null);
	}

	@Test
	public void updateUpdatesRecordInStoragesName() throws Exception {
		httpHandlerFactory.responseText = "Dummy response text";
		DataGroup record = DataGroup.withNameInData("authority");

		DataGroup collectedTerms = DataGroup.withNameInData("collectedData");
		collectedTerms.addChild(DataAtomic.withNameInDataAndValue("type", "place"));
		collectedTerms.addChild(DataAtomic.withNameInDataAndValue("id", "alvin-place:22"));

		DataGroup storageTerms = DataGroup.withNameInData("storage");
		collectedTerms.addChild(storageTerms);

		DataGroup collectedRecordLabel = DataGroup.withNameInData("collectedDataTerm");
		storageTerms.addChild(collectedRecordLabel);
		collectedRecordLabel.setRepeatId("someRepeatId");
		collectedRecordLabel.addChild(
				DataAtomic.withNameInDataAndValue("collectTermId", "recordLabelStorageTerm"));
		collectedRecordLabel.addChild(
				DataAtomic.withNameInDataAndValue("collectTermValue", "SomePlaceCollectedName"));

		DataGroup linkList = null;
		String dataDivider = null;

		alvinToCoraRecordStorage.update("place", "alvin-place:22", record, collectedTerms, linkList,
				dataDivider);

		assertEquals(httpHandlerFactory.factoredHttpHandlers.size(), 1);
		assertEquals(httpHandlerFactory.urls.get(0), baseURL
				+ "objects/alvin-place:22/datastreams/METADATA?format=?xml&controlGroup=M"
				+ "&logMessage=coraWritten&checksumType=SHA-512&dsLabel=SomePlaceCollectedName");

		HttpHandlerSpy httpHandler = (HttpHandlerSpy) httpHandlerFactory.factoredHttpHandlers
				.get(0);
		assertEquals(httpHandler.requestMetod, "PUT");
		String encoded = Base64.getEncoder().encodeToString(
				(fedoraUsername + ":" + fedoraPassword).getBytes(StandardCharsets.UTF_8));
		assertEquals(httpHandler.requestProperties.get("Authorization"), "Basic " + encoded);

		assertEquals(converterFactory.factoredToFedoraConverters.size(), 1);
		assertEquals(converterFactory.factoredToFedoraTypes.get(0), "place");
		AlvinCoraToFedoraConverterSpy converterSpy = (AlvinCoraToFedoraConverterSpy) converterFactory.factoredToFedoraConverters
				.get(0);
		assertSame(converterSpy.record, record);
		assertEquals(converterSpy.returnedXML, httpHandler.outputStrings.get(0));
	}

	@Test
	public void updateIsMissingRecordLabelInCollectedStorageTerms() throws Exception {
		httpHandlerFactory.responseText = "Dummy response text";
		DataGroup record = DataGroup.withNameInData("authority");

		DataGroup collectedTerms = DataGroup.withNameInData("collectedData");
		collectedTerms.addChild(DataAtomic.withNameInDataAndValue("type", "place"));
		collectedTerms.addChild(DataAtomic.withNameInDataAndValue("id", "alvin-place:22"));

		DataGroup storageTerms = DataGroup.withNameInData("storage");
		collectedTerms.addChild(storageTerms);

		DataGroup collectedRecordLabel = DataGroup.withNameInData("collectedDataTerm");
		storageTerms.addChild(collectedRecordLabel);
		collectedRecordLabel.setRepeatId("someRepeatId");
		collectedRecordLabel.addChild(
				DataAtomic.withNameInDataAndValue("collectTermId", "NOTrecordLabelStorageTerm"));
		collectedRecordLabel.addChild(
				DataAtomic.withNameInDataAndValue("collectTermValue", "SomePlaceCollectedName"));

		DataGroup linkList = null;
		String dataDivider = null;

		alvinToCoraRecordStorage.update("place", "alvin-place:22", record, collectedTerms, linkList,
				dataDivider);

		assertEquals(httpHandlerFactory.factoredHttpHandlers.size(), 1);
		assertEquals(httpHandlerFactory.urls.get(0), baseURL
				+ "objects/alvin-place:22/datastreams/METADATA?format=?xml&controlGroup=M"
				+ "&logMessage=coraWritten&checksumType=SHA-512&dsLabel=LabelNotPresentInStorageTerms");
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "readList is not implemented for type: null")
	public void readListThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.readList(null, null);
	}

	@Test(expectedExceptions = ReadFedoraException.class, expectedExceptionsMessageRegExp = ""
			+ "Unable to read list of places: Can not read xml: "
			+ "The element type \"someTag\" must be terminated by the matching end-tag \"</someTag>\".")
	public void readListThrowsParseExceptionOnBrokenXML() throws Exception {
		httpHandlerFactory.responseText = "<someTag></notSameTag>";
		alvinToCoraRecordStorage.readList("place", DataGroup.withNameInData("filter"));
	}

	@Test
	public void readPlaceListCallsFedoraAndReturnsConvertedResult() throws Exception {
		httpHandlerFactory.responseText = createXMLForPlaceList();
		Collection<DataGroup> readPlaceList = alvinToCoraRecordStorage.readList("place",
				DataGroup.withNameInData("filter")).listOfDataGroups;
		assertEquals(httpHandlerFactory.urls.get(0), baseURL
				+ "objects?pid=true&maxResults=100&resultFormat=xml&query=pid%7Ealvin-place:*");
		assertEquals(httpHandlerFactory.factoredHttpHandlers.size(), 7);
		HttpHandlerSpy httpHandler = (HttpHandlerSpy) httpHandlerFactory.factoredHttpHandlers
				.get(0);
		assertEquals(httpHandler.requestMetod, "GET");

		assertEquals(httpHandlerFactory.urls.get(1),
				baseURL + "objects/alvin-place:22/datastreams/METADATA/content");
		assertEquals(httpHandlerFactory.urls.get(2),
				baseURL + "objects/alvin-place:24/datastreams/METADATA/content");
		assertEquals(httpHandlerFactory.urls.get(3),
				baseURL + "objects/alvin-place:679/datastreams/METADATA/content");
		assertEquals(httpHandlerFactory.urls.get(4),
				baseURL + "objects/alvin-place:692/datastreams/METADATA/content");
		assertEquals(httpHandlerFactory.urls.get(5),
				baseURL + "objects/alvin-place:15/datastreams/METADATA/content");
		assertEquals(httpHandlerFactory.urls.get(6),
				baseURL + "objects/alvin-place:1684/datastreams/METADATA/content");

		assertEquals(converterFactory.factoredToCoraConverters.size(), 6);
		assertEquals(converterFactory.factoredToCoraTypes.get(0), "place");
		AlvinFedoraToCoraConverterSpy alvinToCoraConverter = (AlvinFedoraToCoraConverterSpy) converterFactory.factoredToCoraConverters
				.get(0);
		assertEquals(alvinToCoraConverter.xml, httpHandlerFactory.responseText);
		assertEquals(readPlaceList.size(), 6);
		Iterator<DataGroup> readPlaceIterator = readPlaceList.iterator();
		assertEquals(readPlaceIterator.next(), alvinToCoraConverter.convertedDataGroup);
	}

	private String createXMLForPlaceList() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<result xmlns=\"http://www.fedora.info/definitions/1/0/types/\" xmlns:types=\"http://www.fedora.info/definitions/1/0/types/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/types/ http://localhost:8088/fedora/schema/findObjects.xsd\">\n"
				+ "  <resultList>\n" + "  <objectFields>\n" + "      <pid>alvin-place:22</pid>\n"
				+ "  </objectFields>\n" + "  <objectFields>\n" + "      <pid>alvin-place:24</pid>\n"
				+ "  </objectFields>\n" + "  <objectFields>\n"
				+ "      <pid>alvin-place:679</pid>\n" + "  </objectFields>\n"
				+ "  <objectFields>\n" + "      <pid>alvin-place:692</pid>\n"
				+ "  </objectFields>\n" + "  <objectFields>\n" + "      <pid>alvin-place:15</pid>\n"
				+ "  </objectFields>\n" + "  <objectFields>\n"
				+ "      <pid>alvin-place:1684</pid>\n" + "  </objectFields>\n"
				+ "  </resultList>\n" + "</result>";
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "readAbstractList is not implemented")
	public void readAbstractListThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.readAbstractList(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "readLinkList is not implemented")
	public void readLinkListThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.readLinkList(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "generateLinkCollectionPointingToRecord is not implemented")
	public void generateLinkCollectionPointingToRecordThrowsNotImplementedException()
			throws Exception {
		alvinToCoraRecordStorage.generateLinkCollectionPointingToRecord(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "recordsExistForRecordType is not implemented")
	public void recordsExistForRecordTypeThrowsNotImplementedException() throws Exception {
		alvinToCoraRecordStorage.recordsExistForRecordType(null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "recordExistsForAbstractOrImplementingRecordTypeAndRecordId is not implemented")
	public void recordExistsForAbstractOrImplementingRecordTypeAndRecordIdThrowsNotImplementedException()
			throws Exception {
		alvinToCoraRecordStorage.recordExistsForAbstractOrImplementingRecordTypeAndRecordId(null,
				null);
	}
}
