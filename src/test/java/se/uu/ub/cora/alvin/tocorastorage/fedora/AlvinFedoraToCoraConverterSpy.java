package se.uu.ub.cora.alvin.tocorastorage.fedora;

import se.uu.ub.cora.alvin.tocorastorage.fedora.AlvinFedoraToCoraConverter;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class AlvinFedoraToCoraConverterSpy implements AlvinFedoraToCoraConverter {

	public String xml;
	public DataGroup convertedDataGroup;

	@Override
	public DataGroup fromXML(String xml) {
		this.xml = xml;
		convertedDataGroup = DataGroup.withNameInData("Converted xml");
		return convertedDataGroup;
	}

}
