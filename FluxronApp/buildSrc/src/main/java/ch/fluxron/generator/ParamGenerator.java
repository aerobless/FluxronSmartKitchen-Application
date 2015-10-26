package ch.fluxron.generator;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate a map of parameters based on the .eds file.
 */
public class ParamGenerator {
    final static String PACKAGE_NAME = "ch.fluxron.fluxronapp.data";
    final static String CLASS_NAME = "ParamManager";

    public static void main(String[] args){
        System.out.println("Generating classes..");
        ParamGenerator generator = new ParamGenerator();
        Map<String, DeviceParameter> paramMap = generator.loadParameters(args[0]);
        /*for(DeviceParameter p:paramMap.values()){
            System.out.println(p.toString());
        }*/
        generator.generateParameterManager(args[0] + "/app/src/main/java/ch/fluxron/fluxronapp/data/generated/" + CLASS_NAME + ".java", paramMap);
        generator.generateDeviceParameter(args[0]);
        System.out.println("SIZE"+paramMap.size());
    }

    private void generateDeviceParameter(String rootPath){
        Path srcLocation = Paths.get(rootPath+"/buildSrc/src/main/java/ch/fluxron/generator/DeviceParameter.java");
        Path dstLocation = Paths.get(rootPath+"/app/src/main/java/ch/fluxron/fluxronapp/data/generated/DeviceParameter.java");

        try{
            Files.createDirectories(dstLocation.getParent());
            BufferedReader br = new BufferedReader(new FileReader(srcLocation.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(dstLocation.toFile()));

            String line;
            boolean firstline = true;
            while((line = br.readLine()) != null)
            {
                if(firstline) {
                    bw.write("package ch.fluxron.fluxronapp.data.generated;");
                    firstline = false;
                } else {
                    bw.write(line);
                    bw.write("\n");
                }
            }
            br.close();
            bw.close();
        }
        catch(Exception e){
            System.out.println("Exception caught : " + e);
        }
    }

    private void generateParameterManager(String outputPath, Map<String, DeviceParameter> paramMap){
        StringBuilder paramManager = new StringBuilder();

        paramManager.append("package ch.fluxron.fluxronapp.data.generated;\n\n");
        //Imports
        paramManager.append("import java.util.Map;\n");
        paramManager.append("import java.util.HashMap;\n");
        paramManager.append("\n");

        //Class
        paramManager.append("//Generated Class\n");
        paramManager.append("public class "+CLASS_NAME+" {\n\n");
        for (DeviceParameter p:paramMap.values()) {
            paramManager.append("    public final static String F_"+p.getName().toUpperCase()+p.getId().toUpperCase()+" = \""+p.getId()+"\";\n");
        }

        paramManager.append("    Map<String, DeviceParameter> paramMap;\n\n");

        paramManager.append("    public "+CLASS_NAME+"() {\n");
        paramManager.append("    paramMap = new HashMap<String, DeviceParameter>();\n");
        for (DeviceParameter p:paramMap.values()) {
            paramManager.append("            paramMap.put(\""+p.getId()+
                    "\", new DeviceParameter(\""+p.getName()+ "\",\""+p.getId()+"\"," +
                    " "+p.getObjectType()+", "+p.getDataType()+", \""+p.getAccessType()+"\", \""+p.getDefaultValue()+
                    "\", "+p.getSubNumber()+", new byte[]{(byte)"+(p.getIndex()[0] & 0xff) +", (byte)"+(p.getIndex()[1] & 0xff)+"}, (byte)"+p.getSubindex()+"));\n");
        }

        paramManager.append("    }\n");

        paramManager.append("    public Map<String, DeviceParameter> getParamMap() {\n" +
                "        return paramMap;\n" +
                "    }\n");

        paramManager.append("}\n");

        try {
            Path pathToFile = Paths.get(outputPath);
            Files.createDirectories(pathToFile.getParent());
            FileWriter fw = new FileWriter(outputPath);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(paramManager.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read device parameters from a .eds file, containing information such as index, subindex etc.
     */
    public Map<String, DeviceParameter> loadParameters(String rootPath){
        Map<String, DeviceParameter> parameterList = new HashMap<String, DeviceParameter>();

        try {
            InputStream is = new FileInputStream("/"+rootPath+"/buildSrc/build/resources/main/ch/fluxron/generator/FLUXRON_parameter.eds");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder responseData = new StringBuilder();
            DeviceParameter param = null;
            while((line = in.readLine()) != null) {
                responseData.append(line);
                if(line.matches("\\s*\\[[0-9A-Z]{1,4}(sub[0-9]{1,4})?\\]")){ //match [1000] and [1000sub1]
                    param = new DeviceParameter();
                    line = line.replaceAll("\\s|\\[|\\]",""); //remove spaces, []
                    String[] parts = line.split("sub");
                    byte[] index = new byte[2];
                    index[0] = Integer.decode("0x" + parts[0].substring(0, 2)).byteValue();
                    if(parts[0].length()>=4){
                        index[1] = Integer.decode("0x" + parts[0].substring(2, 4)).byteValue();
                    }
                    param.setIndex(index);
                    if(parts.length>1){
                        param.setSubindex(Byte.decode("0x" + parts[1]));
                    }
                    param.setId(line);
                    parameterList.put(line, param);
                } else if (line.contains("ParameterName=") && (param!=null)){
                    param.setName(line.substring(line.lastIndexOf("=") + 1).replaceAll(" |-", "_").replaceAll("\\(|\\)|\\[|\\]",""));
                } else if (line.contains("ObjectType=") && (param!=null)){
                    param.setObjectType(Integer.decode(line.substring(line.lastIndexOf("=") + 1)));
                } else if (line.contains("DataType=") && (param!=null)){
                    param.setDataType(Integer.decode(line.substring(line.lastIndexOf("=") + 1)));
                } else if (line.contains("AccessType=") && (param!=null)){
                    param.setAccessType(line.substring(line.lastIndexOf("=") + 1));
                } else if (line.contains("DefaultValue=") && (param!=null)){
                    param.setDefaultValue(line.substring(line.lastIndexOf("=") + 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parameterList;
    }
}