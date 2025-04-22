package com.owpk.drawio;

import static com.owpk.drawio.Utils.addAttr;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.owpk.core.SourceCodeExtractor;
import com.owpk.drawio.exception.DiagramGenerationException;
import com.owpk.drawio.model.UmlElement;
import com.owpk.model.ClassType;
import com.owpk.model.ClassUml;

/**
 * UmlDrawEngine is responsible for generating DrawIO compatible XML files from
 * Java source code.
 * It processes extracted class information and creates visual class diagrams
 * with proper relationships.
 * <p>
 * The engine handles:
 * <ul>
 * <li>Creating class boxes with appropriate styling based on class type
 * (interface, abstract, enum, etc.)</li>
 * <li>Establishing relationship lines between classes (inheritance,
 * implementation)</li>
 * <li>Automatic layout of elements in the diagram</li>
 * <li>Generating the final XML file in DrawIO format</li>
 * </ul>
 */
public class UmlDrawEngine {
    public static final int ELEMENT_GAP = 50;
    public static final int INITIAL_OFFSET = 10;
    public static final int MAX_DIAGRAM_WIDTH = 1400;

    /** Path where the .drawio file will be created */
    private Path drawioPath;

    /** List of class information extracted from source code */
    private List<ClassUml> classBoxes;

    /** Registry for creating and managing XML elements */
    private XmlElementRegistry xmlElementRegistry;

    /** Map to track parent-child relationships between classes */
    private Map<String, List<String>> parentToChildRelations = new HashMap<>();

    /** XML document object */
    private Document document;

    /** Root element of the XML document */
    private Element root;

    /** Base node ID for XML elements */
    private String nodeId;

    /**
     * Constructs a UmlDrawEngine with source code information and output path.
     *
     * @param sourceCodeExtractor The extractor that provides class information from
     *                            source code
     * @param drawioPath          The path where the generated .drawio file will be
     *                            saved
     */
    public UmlDrawEngine(SourceCodeExtractor sourceCodeExtractor, Path drawioPath) {
        this.classBoxes = sourceCodeExtractor.extractSource();
        this.drawioPath = drawioPath;
        this.document = createXmlDocument();
        this.root = initXMLfile(document);
        this.nodeId = "a_1";
        this.xmlElementRegistry = new DefaultElementRegistry(root, document, nodeId);
    }

    /**
     * Main method for creating a .drawio XML file.
     * <p>
     * This method processes the extracted class information to:
     * <ol>
     * <li>Create the base XML structure</li>
     * <li>Generate class boxes for each class</li>
     * <li>Create relationship lines between classes</li>
     * <li>Write the final XML to the specified file path</li>
     * </ol>
     */
    public void createDrawio() {

        var biggestBox = document.createElement("mxCell");
        addAttr(biggestBox, "id", "a_0");
        root.appendChild(biggestBox);

        var biggerBox = document.createElement("mxCell");
        addAttr(biggerBox, "id", nodeId);
        addAttr(biggerBox, "parent", "a_0");
        root.appendChild(biggerBox);

        iterateOverClasses(classBoxes);

        for (var entry : parentToChildRelations.entrySet()) {
            var parentId = entry.getKey();
            var childIds = entry.getValue();

            for (var childId : childIds)
                xmlElementRegistry.createLine(0, parentId, childId);
        }

        // transform the DOM Object to an XML File
        writeeXMLFile(drawioPath);
    }

    /**
     * Processes the list of class UML models to create visual elements.
     * <p>
     * This method:
     * <ul>
     * <li>Creates visual elements for each class</li>
     * <li>Organizes elements by type (interface, abstract class, etc.)</li>
     * <li>Calculates positions for each element in the diagram</li>
     * </ul>
     *
     * @param classUmls List of class UML models to process
     */
    private void iterateOverClasses(List<ClassUml> classUmls) {
        Map<String, UmlElement> visited = new HashMap<>();
        for (var uml : classUmls) {
            iterateOverElement(uml, visited);
        }

        TreeMap<Integer, List<UmlElement>> sortedClasses = visited.values()
                .stream()
                .collect(Collectors.groupingBy(it -> it.getSorting(), TreeMap::new, Collectors.toList()));

        // start y, start x
        int y = INITIAL_OFFSET, x = INITIAL_OFFSET, maxY = 0;

        for (var uml : sortedClasses.entrySet()) {
            for (var element : uml.getValue()) {
                element.setX(x);
                element.setY(y);
                var nextX = x + element.getWidth() + ELEMENT_GAP;
                maxY = Math.max(maxY, element.getHeight());
                if (nextX > MAX_DIAGRAM_WIDTH) {
                    x = INITIAL_OFFSET;
                    y += maxY + ELEMENT_GAP;
                    maxY = 0;
                }
                x += element.getWidth() + ELEMENT_GAP;
            }
            x = INITIAL_OFFSET;
            y += maxY + ELEMENT_GAP;
        }
    }

    /**
     * Processes a single class UML model and its relationships.
     * <p>
     * Creates a class box for the given class and establishes
     * relationship lines with its parent classes.
     *
     * @param classUml The class UML model to process
     * @param visited  Map of already processed classes to avoid duplication
     */
    private void iterateOverElement(ClassUml classUml, Map<String, UmlElement> visited) {
        var childXml = createClassBox(classUml, visited);

        for (var parent : classUml.getParent()) {
            var parentXml = createClassBox(parent, visited);
            xmlElementRegistry.createLine(0, parentXml.getXmlId(), childXml.getXmlId());
        }
    }

    /**
     * Creates a class box for the given class UML model if it doesn't already
     * exist.
     * <p>
     * This method ensures each class is only processed once by checking the visited
     * map.
     *
     * @param classUml The class UML model to create a box for
     * @param visited  Map of already processed classes
     * @return The UML element representing the class box
     */
    private UmlElement createClassBox(ClassUml classUml, Map<String, UmlElement> visited) {
        return visited.computeIfAbsent(classUml.getClassIdentity(),
                n -> {
                    var classBox = xmlElementRegistry.createClassBox(classUml);
                    classBox.setSorting(getSorting(classUml.getType()));
                    return classBox;
                });
    }

    /**
     * Determines the sorting order for a class based on its type.
     * <p>
     * This affects the visual grouping of elements in the diagram:
     * <ul>
     * <li>Interfaces are displayed first</li>
     * <li>Abstract classes second</li>
     * <li>Enums third</li>
     * <li>Regular classes last</li>
     * </ul>
     *
     * @param classType The type of the class
     * @return An integer representing the sorting order
     */
    private int getSorting(ClassType classType) {
        return (switch (classType) {
            case INTERFACE -> 1;
            case ABSTRACT_CLASS -> 2;
            case ENUM -> 3;
            case CLASS -> 4;
            default -> 0;
        });
    }

    /**
     * Writes the XML document to a file at the specified path.
     *
     * @param drawioPath The path where the XML file will be written
     */
    private void writeeXMLFile(Path drawioPath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(drawioPath.toFile());
            transformer.transform(domSource, streamResult);
            System.out.println("Done creating XML File");
        } catch (TransformerException tfe) {
            throw new DiagramGenerationException("Cannot write xml", tfe);
        }
    }

    /**
     * Creates a new XML document.
     *
     * @return A new XML Document object
     * @throws RuntimeException if there's an error creating the document
     */
    private Document createXmlDocument() {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            return documentBuilder.newDocument();
        } catch (ParserConfigurationException pce) {
            throw new DiagramGenerationException("Cannot build xml", pce);
        }
    }

    /**
     * Initializes the XML file structure with required elements and attributes.
     * <p>
     * This creates the basic structure required for a DrawIO compatible XML file.
     *
     * @param document The XML document to initialize
     * @return The root element of the initialized document
     */
    private Element initXMLfile(Document document) {
        Element diagram = document.createElement("diagram");
        addAttr(diagram, "id", UUID.randomUUID().toString());
        addAttr(diagram, "name", "Page-1"); //
        document.appendChild(diagram);

        // Employee element (Constant)
        Element mxGraphModel = document.createElement("mxGraphModel");
        addAttr(mxGraphModel, "dx", "332");
        addAttr(mxGraphModel, "dy", "241");
        addAttr(mxGraphModel, "grid", "1");
        addAttr(mxGraphModel, "gridSize", "10");
        addAttr(mxGraphModel, "guides", "1");
        addAttr(mxGraphModel, "tooltips", "1");
        addAttr(mxGraphModel, "connect", "1");
        addAttr(mxGraphModel, "arrows", "1");
        addAttr(mxGraphModel, "fold", "1");
        addAttr(mxGraphModel, "page", "1");
        addAttr(mxGraphModel, "pageScale", "1");
        addAttr(mxGraphModel, "pageWidth", "850");
        addAttr(mxGraphModel, "pageHeight", "1100");
        addAttr(mxGraphModel, "math", "0");
        addAttr(mxGraphModel, "shadow", "0");

        diagram.appendChild(mxGraphModel);

        root = document.createElement("root");
        mxGraphModel.appendChild(root);
        return root;
    }
}
