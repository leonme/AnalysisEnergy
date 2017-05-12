package com.sap.mii.custom.main;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.FileReader;

/**
 * Created by I302473 on 5/12/2017.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        SAXReader sr = new SAXReader();
        Document treeDoc = sr.read("C:\\Users\\i302473\\workspace\\AnalysisEnergy\\src\\main\\resources\\xml\\tree.xml");

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader("C:\\Users\\i302473\\workspace\\AnalysisEnergy\\src\\main\\resources\\xml\\api.json"));
        JsonObject apiJson = jsonElement.getAsJsonObject();
        String strJson = apiJson.toString();

        String nodeNames = "E|TOTAL,E|COMMON,E|AP30,DTY23-AP30-F01,DTY23-AP30-F02,POY-AP30-F01,POY-AP30-F01|DTY";

        String outXML = "";

        System.out.println("Input Params:");
        System.out.println("Tree: " + treeDoc.asXML() + "\n" +
                "Json: " + strJson + "\n" +
                "NodeNames" + nodeNames);
        AnalysisEnergy ae= new AnalysisEnergy();
        ae.setTreeXML(treeDoc.asXML());
        ae.setJSONString(strJson);
        ae.setNodeNames(nodeNames);
        ae.Invoke();
        outXML = ae.getOutXML();
        System.out.println("OutPut: \n" + outXML);

    }
}
