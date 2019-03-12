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
package se.uu.ub.cora.alvin.tocorastorage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class ResourceReader {

	public static String readResourceAsString(String resourceFile) {
		StringBuilder data = new StringBuilder();
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		// try (Stream<String> lines = Files.lines(Paths.get(
		// contextClassLoader.getResource(resourceFile).toURI()),
		// StandardCharsets.UTF_8);) {
		try (Stream<String> lines = new BufferedReader(new InputStreamReader(
				contextClassLoader.getResourceAsStream(resourceFile), StandardCharsets.UTF_8))
						.lines();) {

			lines.forEach(line -> data.append(line).append("\n"));
			removeAddedExtraLineBreakAtEnd(data);
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to read resource to string for file: " + resourceFile, e);
		}
		return data.toString();
	}

	private static void removeAddedExtraLineBreakAtEnd(StringBuilder data) {
		data.replace(data.lastIndexOf("\n"), data.length(), "");
	}

}
