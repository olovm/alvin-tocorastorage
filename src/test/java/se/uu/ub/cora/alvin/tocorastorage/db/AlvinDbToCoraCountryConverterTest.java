package se.uu.ub.cora.alvin.tocorastorage.db;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorastorage.ConversionException;
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

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting country to Cora country: Map does not contain value for alpha2code")
	public void testMapWithEmptyValueThrowsError() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "");
		rowFromDb.put("defaultName", "Sverige");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting country to Cora country: Map does not contain value for alpha2code")
	public void testMapWithNonEmptyValueANDEmptyValueThrowsError() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha3code", "SWE");
		rowFromDb.put("alpha2code", "");
		rowFromDb.put("defaultName", "Sverige");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = "Error converting country to Cora country: Map does not contain value for alpha2code")
	public void mapDoesNotContainAlpha2Value() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("defaultName", "Sverige");
		DataGroup country = converter.fromMap(rowFromDb);
		assertEquals(country.getNameInData(), "country");

	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = "Error converting country to Cora country: Map does not contain value for defaultName")
	public void mapDoesNotContainDefaultNameValue() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "someAlpha2Code");
		DataGroup country = converter.fromMap(rowFromDb);
		assertEquals(country.getNameInData(), "country");

	}

	@Test
	public void testMapContainsValueReturnsDataGroupWithCorrectRecordInfo() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("alpha2code", "someAlpha2Code");
		rowFromDb.put("defaultName", "Sverige");
		DataGroup country = converter.fromMap(rowFromDb);
		assertEquals(country.getNameInData(), "country");

		assertCorrectRecordInfoWithId(country, "someAlpha2Code");

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
		rowFromDb.put("alpha2code", "SE");
		rowFromDb.put("defaultName", "Sverige");
		// rowFromDb.put("alpha3code", "SWE");
		// rowFromDb.put("numericalcode", "752");
		DataGroup country = converter.fromMap(rowFromDb);

		assertEquals(country.getFirstAtomicValueWithNameInData("alpha2code"), "SE");
		DataGroup text = country.getFirstGroupWithNameInData("textId");
		assertEquals(text.getFirstAtomicValueWithNameInData("linkedRecordType"), "coraText");
		assertEquals(text.getFirstAtomicValueWithNameInData("linkedRecordId"), "countrySEText");
		// assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Alpha3"),
		// "SWE");
		// assertEquals(country.getFirstAtomicValueWithNameInData("iso31661Numeric"),
		// "752");

	}

}
