package se.uu.ub.cora.alvin.tocorastorage.db;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorastorage.ConversionException;
import se.uu.ub.cora.alvin.tocorastorage.ParseException;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinDbToCoraCountryConverterTest {

	private AlvinDbToCoraCountryConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		converter = new AlvinDbToCoraCountryConverter();
	}

	@Test
	public void testEmptyMap() {
		Map<String, String> rowFromDb = new HashMap<>();
		DataGroup country = converter.fromMap(rowFromDb);
		assertNull(country);
	}

	@Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting country to Cora country: Column must contain value.")
	public void testMapWithEmptyValueThrowsError() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("iso31661Alpha2", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting country to Cora country: Column must contain value.")
	public void testMapWithNonEmptyValueANDEmptyValueThrowsError() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("someColumnName", "someColumnValue");
		rowFromDb.put("iso31661Alpha2", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = "Error converting country to Cora country: Map does not contain value for iso31661Alpha2")
	public void mapDoesNotContainAlpha2Value() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("someColumnName", "someColumnValue");
		DataGroup country = converter.fromMap(rowFromDb);
		assertEquals(country.getNameInData(), "country");

	}

	@Test
	public void testMapContainsValueReturnsDataGroupWithCorrectRecordInfo() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("iso31661Alpha2", "someColumnValue");
		DataGroup country = converter.fromMap(rowFromDb);
		assertEquals(country.getNameInData(), "country");

		assertCorrectRecordInfoWithId(country, "someColumnValue");

	}

	private void assertCorrectRecordInfoWithId(DataGroup country, String id) {
		DataGroup recordInfo = country.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);

		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "country");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "alvin");
	}

	@Test
	public void testMapContainsValueReturnsDataGroupWithCorrectChildren() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("iso31661Alpha2", "SE");
		rowFromDb.put("iso31661Alpha3", "SWE");
		rowFromDb.put("iso31661Numeric", "752");
		DataGroup country = converter.fromMap(rowFromDb);

		assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Alpha2"), "SE");
		assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Alpha3"), "SWE");
		assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Numeric"), "752");

	}

}
