package com.sap.mii.custom.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.mii.custom.main.AnalysisEnergy;
import com.sap.xmii.xacute.actions.ActionReflectionBase;
import com.sap.xmii.xacute.core.ILog;
import com.sap.xmii.xacute.core.Transaction;
import org.dom4j.Document;
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
public class AnalysisEnergyAction extends ActionReflectionBase {

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
    public AnalysisEnergyAction() {
        // Initialise all attributes in the Cunstructor
        strJSONString = new String(AnalysisEnergy.EMPTYSTR);
        strNodeNames = new String(AnalysisEnergy.EMPTYSTR);
        strTreeXML = new String(AnalysisEnergy.EMPTYSTR);
        strOutXML = new String(AnalysisEnergy.EMPTYSTR);
    }

    /**
     * This will take the Icon to display in the BLS
     */
    public String GetIconPath() {
        return "/elec.png";
    }

    /*
     * This method contains the actual business logic for the
     * Action Block
     */
    public void Invoke(Transaction trx, ILog ilog)
    {
        try{
            Document outXML = DocumentHelper.createDocument();
            outXML.addElement(AnalysisEnergy.NODE_ENERGY)
                    .addElement(AnalysisEnergy.NODE_RESULTSET);

            SAXReader saxReader = new SAXReader();
            Document treeXML = saxReader.read(new ByteArrayInputStream(strTreeXML.getBytes()));
            Map<String, String> treeMap = buildTreeMap(treeXML);

            Element resultSetElement = outXML.getRootElement().element(AnalysisEnergy.NODE_RESULTSET);
            String[] nodeNames = strNodeNames.split(AnalysisEnergy.SPLIT_TAG);
            for(String nodeName : nodeNames){
                Map<String,BigDecimal> energyMap = buildEnergyConsumptionMap(strJSONString);
                String nodeCode = treeMap.get(nodeName);
                if(energyMap.containsKey(nodeCode)) {
                    Element resultElement = resultSetElement.addElement(AnalysisEnergy.NODE_RESULT);
                    resultElement.addElement(AnalysisEnergy.NODE_NODENAME).addText(nodeName);
                    resultElement.addElement(AnalysisEnergy.NODE_VALUE).addText(energyMap.get(nodeCode).toString());
                }
            }
            strOutXML = outXML.asXML();
          /*
           // This varaible is defined in ActionReflectionBase class
            */
            _success=true;
        }catch (Exception e) {
            _success=false;// Set _success to false if any exception is cought
            ilog.error(e);
        }
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

    /**
     * This is required to make the Configure Button Disabled
     * Note: If you want to have Custom ConfigureDialog, you need not put this method.

     */
    public boolean isConfigurable(){
        return false;
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
        List<Element> rowElements = rootElement.element(AnalysisEnergy.NODE_ROWSET).elements(AnalysisEnergy.NODE_ROW);
        for(Element rowElement : rowElements) {
            treeMap.put(rowElement.elementText(AnalysisEnergy.NODE_GROUPNAME), rowElement.elementText(AnalysisEnergy.NODE_DESC));
        }
        return treeMap;
    }
}
