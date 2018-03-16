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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.tocorastorage.NotImplementedException;
import se.uu.ub.cora.alvin.tocorastorage.fedora.AlvinToCoraConverter;
import se.uu.ub.cora.alvin.tocorastorage.fedora.AlvinToCoraConverterFactory;
import se.uu.ub.cora.alvin.tocorastorage.fedora.AlvinToCoraConverterFactoryImp;
import se.uu.ub.cora.alvin.tocorastorage.fedora.AlvinToCoraPlaceConverter;

public class AlvinToCoraConverterFactoryTest {
	private AlvinToCoraConverterFactory alvinToCoraConverterFactoryImp;

	@BeforeMethod
	public void beforeMethod() {
		alvinToCoraConverterFactoryImp = new AlvinToCoraConverterFactoryImp();
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "No converter implemented for: someType")
	public void factorUnknownTypeThrowsException() throws Exception {
		alvinToCoraConverterFactoryImp.factor("someType");
	}

	@Test
	public void testFactoryPlace() throws Exception {
		AlvinToCoraConverter converter = alvinToCoraConverterFactoryImp.factor("place");
		assertTrue(converter instanceof AlvinToCoraPlaceConverter);
	}
}
