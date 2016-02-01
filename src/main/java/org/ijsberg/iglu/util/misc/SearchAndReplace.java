package org.ijsberg.iglu.util.misc;

import org.ijsberg.iglu.util.collection.ArraySupport;
import org.ijsberg.iglu.util.io.FSFileCollection;
import org.ijsberg.iglu.util.io.FileCollection;
import org.ijsberg.iglu.util.io.FileFilterRuleSet;
import org.ijsberg.iglu.util.io.FileSupport;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by J Meetsma on 4-12-2015.
 */
public class SearchAndReplace {
/*
	<xs:simpleType name="RijKarakteristiekOmschrijvingType">
		<xs:annotation>
			<xs:documentation>Waardes: Eurocity/Europese Unit Cargo/Goederen/Intercity/Intercity Expres/Internationaal/Meetwagen/Losse Loc/Losse Loc Reizigers/Leeg Materieel/Lege Motorpost/Motorpost/Onderhoud Materieel/Proef Goederen/Snelananas/Stopananas/Stoomananas Materieel/TGV/Ultrasoon/Meetwagen/Werkananas/Thalys/Sprinter/Tram


 */
    private static Properties replacements = new Properties();
    static {
        replacements.put("Trein","Ananas");
        replacements.put("Prorail","Acme");
        replacements.put("ProRail","AcMe");
        replacements.put("Spoorweg", "Transportband");
        replacements.put("Spoor", "Transport");
        replacements.put("Goederen", "Transport");
        replacements.put("Cargo", "Motion");
        replacements.put("Eurocity", "Telstar");
        replacements.put("Tgv", "Btw");
        replacements.put("Thalys", "Aardbei");
        replacements.put("Spoor", "Transport");
        replacements.put("Wagen", "Krat");
        replacements.put("Sprinter", "Colli");
        replacements.put("Intercity", "Pallet");
        replacements.put("Wissel", "Hefboom");
        replacements.put("RijKarakteristiek", "Opname");
        replacements.put("Reiziger", "Fruit");
        replacements.put("Perron", "Steiger");
        replacements.put("Baanvak", "Productielijn");
        replacements.put("Kilometer", "Staffel");
        replacements.put("Kilometrering", "Gradatie");
        replacements.put("Dienst", "Weegschaal");
        replacements.put("Ces_ovgs", "Xyz");
    }

    private static final String BASE_DIR = "C:\\repository\\TibcoBW\\CES_OVGS_TreinNummerReeks_v1.0.2";
    private static final String TARGET_DIR = "C:\\repository\\TibcoBW\\clean";


    private static String[] originalNames;
    private static String[] destinationNames;


    private static void populateKeysAndValues(Properties properties) {
        ArrayList<String> keyList = new ArrayList<String>();
        ArrayList<String> valueList = new ArrayList<String>();
        for(String key : properties.stringPropertyNames()) {
            keyList.add(key);
            keyList.add(key.toLowerCase());
            keyList.add(key.toUpperCase());
            String value = properties.getProperty(key);
            valueList.add(value);
            valueList.add(value.toLowerCase());
            valueList.add(value.toUpperCase());
        }
        originalNames = keyList.toArray(new String[0]);
        destinationNames = valueList.toArray(new String[0]);
    }

    /*
    create new dir struct
     */
    public static void main(String[] args) throws IOException{

        populateKeysAndValues(replacements);

        File newDir = new File(TARGET_DIR);
        newDir.mkdirs();
        FileSupport.emptyDirectory(newDir);

        FileCollection fileCollection = new FSFileCollection(
                BASE_DIR,
                new FileFilterRuleSet(BASE_DIR).setIncludeFilesWithNameMask("*.process|*.wsdl|*.xsd|*.substvar"));
        for(String fileName : fileCollection.getFileNames()) {
            String newFileName = StringSupport.replaceAll(fileName, originalNames, destinationNames);
            String fileContents = FileSupport.getTextFileFromFS(new File(BASE_DIR + "/" + fileName));
            String newFileContents = StringSupport.replaceAll(fileContents, originalNames, destinationNames);
            FileSupport.saveTextFile(newFileContents, FileSupport.createFile(TARGET_DIR + "/" + newFileName));
        }

        FileSupport.zip(newDir.listFiles()[0].getPath(), newDir.listFiles()[0].getPath() + ".zip", "*");
    }

}
