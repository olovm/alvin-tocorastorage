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
import static org.testng.Assert.assertFalse;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorastorage.ParseException;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinFedoraToCoraPlaceConverterTest {

	private AlvinFedoraToCoraPlaceConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		converter = new AlvinFedoraToCoraPlaceConverter();
	}

	@Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting place to Cora place: Can not read xml: "
			+ "The element type \"pid\" must be terminated by the matching end-tag \"</pid>\".")
	public void parseExceptionShouldBeThrownOnMalformedXML() throws Exception {
		String xml = "<pid></notPid>";
		converter.fromXML(xml);
	}

	@Test
	public void convertFromXML() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(TestDataProvider.place22XML);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "place");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "alvin");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "alvin-place:22");

		DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2014-12-18 20:20:38.346");

		DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2014-12-18 20:21:20.880");

		DataGroup defaultName = placeDataGroup.getFirstGroupWithNameInData("name");
		assertEquals(defaultName.getAttribute("type"), "authorized");
		DataGroup defaultNamePart = defaultName.getFirstGroupWithNameInData("namePart");
		assertEquals(defaultNamePart.getAttribute("type"), "defaultName");
		assertEquals(defaultNamePart.getFirstAtomicValueWithNameInData("value"), "Linköping");

		DataGroup coordinates = placeDataGroup.getFirstGroupWithNameInData("coordinates");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("latitude"), "58.42");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("longitude"), "15.62");

		DataGroup country = placeDataGroup.getFirstGroupWithNameInData("country");
		assertEquals(country.getFirstAtomicValueWithNameInData("linkedRecordType"), "country");
		assertEquals(country.getFirstAtomicValueWithNameInData("linkedRecordId"), "SE");

	}

	@Test
	public void convertFromXML24() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(TestDataProvider.place24XML);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "place");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "alvin");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "alvin-place:24");

		DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2014-12-18 22:16:44.623");

		DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2016-02-12 10:29:43.147");

		DataGroup defaultName = placeDataGroup.getFirstGroupWithNameInData("name");
		assertEquals(defaultName.getAttribute("type"), "authorized");
		DataGroup defaultNamePart = defaultName.getFirstGroupWithNameInData("namePart");
		assertEquals(defaultNamePart.getAttribute("type"), "defaultName");
		assertEquals(defaultNamePart.getFirstAtomicValueWithNameInData("value"), "Lund");

		DataGroup coordinates = placeDataGroup.getFirstGroupWithNameInData("coordinates");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("latitude"), "55.7");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("longitude"), "13.18");

		DataGroup country = placeDataGroup.getFirstGroupWithNameInData("country");
		assertEquals(country.getFirstAtomicValueWithNameInData("linkedRecordType"), "country");
		assertEquals(country.getFirstAtomicValueWithNameInData("linkedRecordId"), "SE");
	}

	@Test
	public void convertFromXMLNoCountry() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(TestDataProvider.place22_noCountry_XML);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "place");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "alvin");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), "alvin-place:22_2");

		DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2014-12-18 20:20:38.346");

		DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2014-12-18 20:21:20.880");

		DataGroup defaultName = placeDataGroup.getFirstGroupWithNameInData("name");
		assertEquals(defaultName.getAttribute("type"), "authorized");
		DataGroup defaultNamePart = defaultName.getFirstGroupWithNameInData("namePart");
		assertEquals(defaultNamePart.getAttribute("type"), "defaultName");
		assertEquals(defaultNamePart.getFirstAtomicValueWithNameInData("value"), "Linköping");

		DataGroup coordinates = placeDataGroup.getFirstGroupWithNameInData("coordinates");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("latitude"), "58.42");
		assertEquals(coordinates.getFirstAtomicValueWithNameInData("longitude"), "15.62");

		assertFalse(placeDataGroup.containsChildWithNameInData("country"));
	}

	@Test
	public void convertFromXMLNoCoordinates() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(TestDataProvider.place24NoCoordinates);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"),
				"alvin-place:24_noCoordinates");

		assertFalse(placeDataGroup.containsChildWithNameInData("coordinates"));
	}

	@Test
	public void convertFromXMLNoLatitude() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(TestDataProvider.place24NoLatitudeXML);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"),
				"alvin-place:24_noLatitude");

		assertFalse(placeDataGroup.containsChildWithNameInData("coordinates"));
	}

	@Test
	public void convertFromXMLNoLongitude() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(TestDataProvider.place24NoLongitudeXML);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"),
				"alvin-place:24_noLongitude");

		assertFalse(placeDataGroup.containsChildWithNameInData("coordinates"));
	}

	@Test
	public void convertFromXMLNoUpdatedInfoSetsToSameAsCreatedInfo() throws Exception {
		DataGroup placeDataGroup = converter.fromXML(TestDataProvider.place5NoTsUpdated);
		assertEquals(placeDataGroup.getNameInData(), "authority");
		DataGroup recordInfo = placeDataGroup.getFirstGroupWithNameInData("recordInfo");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"),
				"alvin-place:5");

		DataGroup createdBy = recordInfo.getFirstGroupWithNameInData("createdBy");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(createdBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsCreated"),
				"2017-10-27 22:36:51.991");

		DataGroup updatedBy = recordInfo.getFirstGroupWithNameInData("updatedBy");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordType"), "user");
		assertEquals(updatedBy.getFirstAtomicValueWithNameInData("linkedRecordId"), "12345");

		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("tsUpdated"),
				"2017-10-27 22:36:51.991");
	}

}
