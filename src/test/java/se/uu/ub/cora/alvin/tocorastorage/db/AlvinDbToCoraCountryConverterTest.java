package se.uu.ub.cora.alvin.tocorastorage.db;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
		rowFromDb.put("someEmptyColumnName", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting country to Cora country: Column must contain value.")
	public void testMapWithNonEmptyValueANDEmptyValueThrowsError() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("someColumnName", "someColumnValue");
		rowFromDb.put("someEmptyColumnName", "");
		converter.fromMap(rowFromDb);
	}

	@Test
	public void testMapContainsValueReturnsDataGroup() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("someColumnName", "someColumnValue");
		DataGroup country = converter.fromMap(rowFromDb);
		assertEquals(country.getNameInData(), "country");
	}

}
