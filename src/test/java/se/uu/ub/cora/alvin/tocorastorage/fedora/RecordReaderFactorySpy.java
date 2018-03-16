package se.uu.ub.cora.alvin.tocorastorage.fedora;

import se.uu.ub.cora.sqldatabase.RecordReader;
import se.uu.ub.cora.sqldatabase.RecordReaderFactory;

public class RecordReaderFactorySpy implements RecordReaderFactory {

	public boolean factorWasCalled = false;
	public RecordReader factored;

	@Override
	public RecordReader factor() {
		factorWasCalled = true;
		factored = new RecordReaderSpy();
		return factored;
	}

}
