package ch.fluxron.fluxronapp.data;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Used to import .od XML and .eds text files.
 */
public class ParamManager {
    private Context context;
    private List<DeviceParameter> parameterList;

    public ParamManager(Context context) {
        this.context = context;
        parameterList = loadParameters();
    }

    public List<DeviceParameter> getParameterList() {
        return parameterList;
    }

    /**
     * Read device parameters from a .eds file, containing information such as index, subindex etc.
     */
    public List<DeviceParameter> loadParameters(){
        List<DeviceParameter> parameterList = new ArrayList<DeviceParameter>();

        try {
            InputStream is = context.getAssets().open("FLUXRON_parameter.eds");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder responseData = new StringBuilder();
            DeviceParameter param = null;
            while((line = in.readLine()) != null) {
                responseData.append(line);
                if(line.matches("\\s*\\[[0-9]{1,4}(sub[0-9]{1,4})?\\]")){ //match [1000] and [1000sub1]
                    param = new DeviceParameter();
                    line = line.replaceAll("\\s|\\[|\\]",""); //remove spaces, []
                    String[] parts = line.split("sub");
                    byte[] index = new byte[2];
                    index[0] = Byte.decode("0x"+parts[0].substring(0, 2));
                    index[1] = Byte.decode("0x"+parts[0].substring(3, 4));
                    param.setIndex(index);
                    if(parts.length>1){
                        param.setSubindex(Byte.decode("0x"+parts[1]));
                    }
                    parameterList.add(param);
                } else if (line.contains("ParameterName=") && (param!=null)){
                    param.setName(line.substring(line.lastIndexOf("=") + 1));
                } else if (line.contains("ObjectType=") && (param!=null)){
                    param.setObjectType(Integer.decode(line.substring(line.lastIndexOf("=") + 1)));
                } else if (line.contains("DataType=") && (param!=null)){
                    param.setDataType(Integer.decode(line.substring(line.lastIndexOf("=") + 1)));
                } else if (line.contains("AccessType=") && (param!=null)){
                    param.setAccessType(line.substring(line.lastIndexOf("=") + 1));
                } else if (line.contains("DefaultValue=") && (param!=null)){
                    param.setDefaultValue(line.substring(line.lastIndexOf("=") + 1));
                } else if (line.contains("PDOMapping=") && (param!=null)){
                    param.setPdoMapping(Integer.parseInt(line.substring(line.lastIndexOf("=") + 1)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parameterList;
    }

    public void loadOD(){
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
