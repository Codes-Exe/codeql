import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.InputSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;

public class XsltInjection {
  public void testStreamSourceInputStream(Socket socket) throws Exception {
    StreamSource source = new StreamSource(socket.getInputStream());
    TransformerFactory.newInstance().newTransformer(source).transform(null, null);
  }

  public void testStreamSourceReader(Socket socket) throws Exception {
    StreamSource source = new StreamSource(new InputStreamReader(socket.getInputStream()));
    TransformerFactory.newInstance().newTemplates(source).newTransformer().transform(null, null);
  }

  public void testStreamSourceInjectedParam(@RequestParam String param) throws Exception {
    String xslt = "<xsl:stylesheet [...]" + param + "</xsl:stylesheet>";
    StreamSource source = new StreamSource(new StringReader(xslt));
    TransformerFactory.newInstance().newTransformer(source).transform(null, null);
  }

  public void testSAXSourceInputStream(Socket socket) throws Exception {
    SAXSource source = new SAXSource(new InputSource(socket.getInputStream()));
    TransformerFactory.newInstance().newTemplates(source).newTransformer().transform(null, null);
  }

  public void testSAXSourceReader(Socket socket) throws Exception {
    SAXSource source = new SAXSource(null, new InputSource(new InputStreamReader(socket.getInputStream())));
    TransformerFactory.newInstance().newTransformer(source).transform(null, null);
  }

  public void testStAXSourceEventReader(Socket socket) throws Exception {
    StAXSource source = new StAXSource(XMLInputFactory.newInstance().createXMLEventReader(socket.getInputStream()));
    TransformerFactory.newInstance().newTransformer(source).transform(null, null);
  }

  public void testStAXSourceEventStream(Socket socket) throws Exception {
    StAXSource source = new StAXSource(XMLInputFactory.newInstance().createXMLStreamReader(null, new InputStreamReader(socket.getInputStream())));
    TransformerFactory.newInstance().newTemplates(source).newTransformer().transform(null, null);
  }

  public void testDOMSource(Socket socket) throws Exception {
    DOMSource source = new DOMSource(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(socket.getInputStream()));
    TransformerFactory.newInstance().newTransformer(source).transform(null, null);
  }

  public void testDisabledXXE(Socket socket) throws Exception {
    StreamSource source = new StreamSource(socket.getInputStream());
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    factory.newTransformer(source).transform(null, null);
  }

  public void testFeatureSecureProcessingDisabled(Socket socket) throws Exception {
    StreamSource source = new StreamSource(socket.getInputStream());
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
    factory.newTransformer(source).transform(null, null);
  }

  public void testSaxon(Socket socket) throws Exception {
    StreamSource source = new StreamSource(socket.getInputStream());
    XsltCompiler compiler = new Processor(true).newXsltCompiler();
    
    compiler.compile(source).load().transform();
    compiler.compile(source).load30().transform(null, null);
    compiler.compile(source).load30().applyTemplates((Source) null);
    compiler.compile(source).load30().applyTemplates((Source) null, null);
    compiler.compile(source).load30().applyTemplates((XdmValue) null);
    compiler.compile(source).load30().applyTemplates((XdmValue) null, null);
    compiler.compile(source).load30().callFunction(null, null);
    compiler.compile(source).load30().callFunction(null, null, null);
    compiler.compile(source).load30().callTemplate(null);
    compiler.compile(source).load30().callTemplate(null, null);
  }

  public void testSaxonXsltPackage(@RequestParam String param, Socket socket) throws Exception {
    URI uri = new URI(param);
    StreamSource source = new StreamSource(socket.getInputStream());
    XsltCompiler compiler = new Processor(true).newXsltCompiler();
    
    compiler.loadExecutablePackage(uri).load().transform();
    compiler.compilePackage(source).link().load().transform();
    compiler.loadLibraryPackage(uri).link().load().transform();
  }

  public void testOkFeatureSecureProcessing(Socket socket) throws Exception {
    StreamSource source = new StreamSource(socket.getInputStream());
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.newTransformer(source).transform(null, null);
  }

  public void testOkSaxon(Socket socket) throws Exception {
    StreamSource source = new StreamSource(socket.getInputStream());
    XsltCompiler compiler = new Processor(true).newXsltCompiler();
    
    compiler.compile(source).load().close();
    compiler.compile((Source) new Object()).load().transform();
  }
}