package alvintocorastorage;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DocumentCreator {

	public Document createDocumentFromXML(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder dBuilder = createDocumentBuilder();
		return readXMLUsingBuilderAndXML(dBuilder, xml);
	}

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		ErrorHandler errorHandlerWithoutSystemOutPrinting = new DefaultHandler();
		dBuilder.setErrorHandler(errorHandlerWithoutSystemOutPrinting);
		return dBuilder;
	}

	private Document readXMLUsingBuilderAndXML(DocumentBuilder dBuilder, String xml)
			throws SAXException, IOException {
		StringReader reader = new StringReader(xml);
		InputSource in = new InputSource(reader);
		Document doc = dBuilder.parse(in);
		doc.getDocumentElement().normalize();
		return doc;
	}
}