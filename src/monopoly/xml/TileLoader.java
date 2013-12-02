package monopoly.xml;

import monopoly.tiles.*;

//XML DOM Classes
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import monopoly.MonopolyModelState;
import monopoly.Player;

public class TileLoader {
	//
	// Main Functions
	//
	
	public static ITile[] loadFromXML(String xmlFilename) {
		// open the file
		File xmlFile = new File(xmlFilename);
		
		// init the builder factory
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		// init the dBuilder
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			// throw stack trace because we have to catch the exception -_-
			e.printStackTrace();
			return null;
		}
		
		// parse the document with the builder, if there are any errors,
		// print the stack trace and return null
		Document document;
		try {
			document = dBuilder.parse(xmlFile);
		}
		catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Node firstChildNode = document.getFirstChild();
		
		
		// get the property nodes and init the property arrays
		Element propertiesElement = (Element)document.getFirstChild();
		NodeList propertyNodes = propertiesElement.getChildNodes();
		LinkedList<ITile> tileList = new LinkedList<ITile>();
		
		// iterate through every property in the document, converting
		for (int n = 0; n < propertyNodes.getLength(); n++) {
			Node currentNode = propertyNodes.item(n);
			
			// typecast as an element only if it is an element
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				ITile t = XMLToTile((Element)currentNode);
				tileList.add(t);
			}
			
			// otherwise, skip over this one
		}
		
		// now convert to an array and return it
		ITile[] outTiles = tileList.toArray(new ITile[0]);
		return outTiles;
	}
	
	public static ITile XMLToTile(Element e) {
		
		// see what element type this is
		String elementType = e.getNodeName();
		
		ITile outTile = null;
		switch (elementType) {
			
		case "CardTile":
			String type = getChildValue("Type", e);
			if (type.equals("Chance")) {
				outTile = new CardTile(MonopolyModelState.CHANCE);
			}
			else if (type.equals("Community Chest")) {
				outTile = new CardTile(MonopolyModelState.COMMUNITY_CHEST);
			}
			break;
			
		case "FreeParking":
			outTile = new FreeParking();
			break;
			
		case "GoToJailTile":
			// get the destination board location
			String destStr = getChildValue("Destination", e);
			int destination = Integer.parseInt(destStr);
					
			outTile = new GoToJailTile(destination);
			break;
			
		case "Property":
			// get the name
			String name = getChildValue("Name", e);
			
			// get the value
			String strValue = getChildValue("Value", e);
			double value = Double.parseDouble(strValue);
			
			outTile = new Property(name, value);
			break;
			
		case "Railroad":
			// get the name
			name = getChildValue("Name", e);
			
			outTile = new Railroad(name);			
			break;
			
		case "Utility":
			// get the name
			name = getChildValue("Name", e);
			
			outTile = new Utility(name);
			break;
			
		default:
			// assign the proper tile type
			final TileType tileType = TileType.parseType(elementType);
			
			// basic board tile cases.  Implement a basic, do nothing, 
			// return PLAYING landOn() function
			outTile = new ITile() {
				@Override
				public MonopolyModelState landOn(Player p) {
					return MonopolyModelState.PLAYING;
				}
				
				@Override
				public TileType getTileType() {
					return tileType;
				}
			};
			break;
			
		}
		
		return outTile;
	}
	
	public static String getChildValue(String nodeName, Element e) {
		return e.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
	}
}
