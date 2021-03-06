/*
 * Copyright 2018, 2019 Uppsala University Library
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
import java.util.List;

public class AlvinFedoraToCoraConverterFactorySpy implements AlvinFedoraConverterFactory {

	List<AlvinFedoraToCoraConverter> factoredToCoraConverters = new ArrayList<>();
	List<String> factoredToCoraTypes = new ArrayList<>();
	List<AlvinCoraToFedoraConverter> factoredToFedoraConverters = new ArrayList<>();
	List<String> factoredToFedoraTypes = new ArrayList<>();

	@Override
	public AlvinFedoraToCoraConverter factorToCoraConverter(String type) {
		factoredToCoraTypes.add(type);
		AlvinFedoraToCoraConverter converter = new AlvinFedoraToCoraConverterSpy();
		factoredToCoraConverters.add(converter);
		return converter;
	}

	@Override
	public AlvinCoraToFedoraConverter factorToFedoraConverter(String type) {
		factoredToFedoraTypes.add(type);
		AlvinCoraToFedoraConverter converter = new AlvinCoraToFedoraConverterSpy();
		factoredToFedoraConverters.add(converter);
		return converter;
	}

}
