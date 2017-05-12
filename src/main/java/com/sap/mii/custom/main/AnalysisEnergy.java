package com.sap.mii.custom.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.xmii.xacute.actions.ActionReflectionBase;
import com.sap.xmii.xacute.core.ILog;
import com.sap.xmii.xacute.core.Transaction;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by I302473 on 5/12/2017.
 */
public class AnalysisEnergy {
    public static final String NODE_ENERGY = "Energy";
    public static final String NODE_RESULTSET = "ResultSet";
    public static final String SPLIT_TAG = ",";
    public static final String NODE_RESULT = "Result";
    public static final String NODE_NODENAME = "NodeName";
    public static final String NODE_VALUE = "Value";
    public static final String NODE_ROWSET = "Rowset";
    public static final String NODE_ROW = "Row";
    public static final String NODE_DESC = "Description";
    public static final String NODE_GROUPNAME = "GroupName";
    public static final String EMPTYSTR = "";

    /*
 * This will take the First Input in the Transaction
 */
    private String strJSONString;
    /*
     * This will take the Second Input in the Transaction
     */
    private String strNodeNames;

    private String strTreeXML;
    /*
     * This will give the result.
     */
    private String strOutXML;
    /*
     * Constructor
     */
    public AnalysisEnergy() {
        // Initialise all attributes in the Cunstructor
        strJSONString = new String(AnalysisEnergy.EMPTYSTR);
        strNodeNames = new String(AnalysisEnergy.EMPTYSTR);
        strTreeXML = new String(AnalysisEnergy.EMPTYSTR);
        strOutXML = new String(AnalysisEnergy.EMPTYSTR);
    }

    /*
     * This method contains the actual business logic for the
     * Action Block
     */
    public void Invoke() throws DocumentException {
        Document outXML = DocumentHelper.createDocument();
        outXML.addElement(NODE_ENERGY)
                .addElement(NODE_RESULTSET);

        SAXReader saxReader = new SAXReader();
        Document treeXML = saxReader.read(new ByteArrayInputStream(strTreeXML.getBytes()));
        Map<String, String> treeMap = buildTreeMap(treeXML);

        Element resultSetElement = outXML.getRootElement().element(NODE_RESULTSET);
        String[] nodeNames = strNodeNames.split(SPLIT_TAG);
        for(String nodeName : nodeNames){
            Map<String,BigDecimal> energyMap = buildEnergyConsumptionMap(strJSONString);
            String nodeCode = treeMap.get(nodeName);
            if(energyMap.containsKey(nodeCode)) {
                Element resultElement = resultSetElement.addElement(NODE_RESULT);
                resultElement.addElement(NODE_NODENAME).addText(nodeName);
                resultElement.addElement(NODE_VALUE).addText(energyMap.get(nodeCode).toString());
            }
        }
        strOutXML = outXML.asXML();
    }

    public Map<String, BigDecimal> buildEnergyConsumptionMap(String jsonString) {
        JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonObject meterObjs = obj.get("meters").getAsJsonObject();
        Set<Map.Entry<String,JsonElement>> metersEntrySet = meterObjs.entrySet();
        Map<String, BigDecimal> resultMap = new HashMap<>();
        for(Map.Entry<String,JsonElement> entry: metersEntrySet){
            JsonObject node = entry.getValue().getAsJsonObject();
            resultMap.put(entry.getKey(), node.get("value").getAsBigDecimal());
        }
        return resultMap;
    }


    public Map<String,String> buildTreeMap(Document treeXML) {
        Map<String, String> treeMap = new HashMap<>();
        Element rootElement = treeXML.getRootElement();
        List<Element> rowElements = rootElement.element(NODE_ROWSET).elements(NODE_ROW);
        for(Element rowElement : rowElements) {
            treeMap.put(rowElement.elementText(NODE_GROUPNAME), rowElement.elementText(NODE_DESC));
        }
        return treeMap;
    }

    // Getter for the Fisrt Input

    public String getJSONString() {
        return strJSONString;
    }

// Setter for the first Input

    public void setJSONString(String strJSONString) {
        this.strJSONString = strJSONString;
    }

    //Getter for the Second Input
    public String getNodeNames() {
        return strNodeNames;
    }

// Setter for the Second Input

    public void setNodeNames(String strNodeNames) {
        this.strNodeNames = strNodeNames;
    }


    public String getTreeXML() {
        return strTreeXML;
    }

    public void setTreeXML(String strTreeXML) {
        this.strTreeXML = strTreeXML;
    }
// getter for the Output . Note there is no setter as this is output property

    public String getOutXML() {
        return strOutXML;
    }
}
