package se.uu.ub.cora.alvin.tocorastorage;

public class AlvinToCoraConverterFactoryImp implements AlvinToCoraConverterFactory {

	@Override
	public AlvinToCoraConverter factor(String type) {
		if ("place".equals(type)) {
			return new AlvinToCoraPlaceConverter();
		}
		throw NotImplementedException.withMessage("No converter implemented for: " + type);
	}

}
