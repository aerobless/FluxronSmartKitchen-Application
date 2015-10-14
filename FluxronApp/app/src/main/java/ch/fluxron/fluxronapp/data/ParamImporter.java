package ch.fluxron.fluxronapp.data;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Used to import .od XML files.
 */
public class ParamImporter {
    Context context;

    public ParamImporter(Context context) {
        this.context = context;
    }

    public void loadOD(){
        Log.d("FLUXRON", "running xpath");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            Document doc = builder.parse(context.getAssets().open("FLUXRON_parameter.od"));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            try {
                XPathExpression expr = xpath.compile("/PyObject/attr");
                NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                printNodeList(nl, "");

            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printNodeList(NodeList nl, String space) {
        for(int i=0; i<nl.getLength(); i++){
            Node n = nl.item(i);
            printNode(space, n);
            if(n.getAttributes() != null) {
                printAttributes(n.getAttributes(), space + "++");
            }
            if(n.getChildNodes() != null){
                printNodeList(n.getChildNodes(), space + "--");
            }
        }
    }

    private void printNode(String space, Node n) {
        Log.d("FLUXRON XML", space + "Name: " + n.getNodeName()
                   + "NodeType: " + n.getNodeType()
                   + "Value: " + n.getNodeValue()
                   +"TextContent: " + n.getTextContent());
    }

    private void printAttributes(NamedNodeMap nmap, String space) {
        for (int i = 0; i < nmap.getLength(); i++) {
            Node n = nmap.item(i);
            Log.d("FLUXRON XML", "Attribute " + i + ": " + n.getNodeName() + " " + n.getTextContent());
            //printNode(space, n);
        }
    }
}
