import java.awt.*;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;


class Line {
    Integer x = 0;
    Integer y = 0;
    Integer x1 = 0;
    Integer y1 = 0;
    double k = Double.valueOf(1);
    double b = Double.valueOf(0);

    Line(Integer X, Integer Y, Integer X1, Integer Y1) {
        x = X;
        y = Y;
        x1 = X1;
        y1 = Y1;
        k = (double) (Y1 - Y) / (X1 - X);
        b = Y - X * k;
    }

    public static void Sol(Line d1, Line d2) {
        if (d1.k == d2.k && d1.b != d2.b) {
            System.out.println("line 1: y = " + d1.k + "x + " + d1.b + "; line 2: y = " + d2.k + "x + " + d2.b + "; are ||");
        } else if (d1.k == d2.k && d1.b == d2.b) {
            System.out.println("line 1: y = " + d1.k + "x + " + d1.b + "; line 2: y = " + d2.k + "x + " + d2.b + "; are equal");
        } else {
            double x = (d2.b - d1.b) / (d1.k - d2.k);
            double y = x * d1.k + d1.b;
            System.out.println("line 1: y = " + d1.k + "x + " + d1.b + "; line 2: y = " + d2.k + "x + " + d2.b + "; (" + x + "; " + y + ")");
        }
        return;
    }

    public static String SolXML(Line d1, Line d2) {
        String answer = "";
        if (d1.k == d2.k && d1.b != d2.b) {
            return  "are ||";
        } else if (d1.k == d2.k && d1.b == d2.b) {
            return "are equal";
        } else {
            double x = (d2.b - d1.b) / (d1.k - d2.k);
            double y = x * d1.k + d1.b;
            return "(" + x + "; " + y + ")";
        }
    }
}




public class Main {
    private static Node getLanguage(Document doc, String name1, String name2,String dot) {
        Element lines = doc.createElement("Lines");

        lines.appendChild(getLanguageElements(doc, lines, "First", name1));

        lines.appendChild(getLanguageElements(doc, lines, "Second", name2));

        lines.appendChild(getLanguageElements(doc, lines, "dot", dot));
        return lines;
    }

    private static Node getLanguageElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        TreeMap<Integer, Line> map = new TreeMap<>();
        ArrayList<Line> Lines = new ArrayList<Line>();
        ArrayList<Integer> dots = new ArrayList<Integer>();
        Integer x1, y1, x2, y2;
        int k = 1;
        int a = 0;

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse("input.xml");

        Node root = document.getDocumentElement().getFirstChild();
        Node line = root.getNextSibling();
        while(line != null)
        {
            String str = line.getTextContent();
            String regex = "[^\\d]";

            String[] reg = str.split(regex);

            for(String word : reg){
                try {
                    Integer rez = Integer.valueOf(word);
                    dots.add(rez);
                } catch (NumberFormatException e) {
                }
            }
            map.put(k, new Line(dots.get(0), dots.get(1), dots.get(2), dots.get(3)));
            dots.clear();
            line = line.getNextSibling().getNextSibling();
            k++;
        }
        for(int i = 1; i < map.size(); i++) {
            for (int j = i + 1; j <= map.size(); j++) {
                map.get(i).Sol(map.get(i), map.get(j));
            }
        }



        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document document2 = builder.newDocument();
            Element element = document2.createElementNS("lines", "answer");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transform = transformerFactory.newTransformer();
            transform.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document2);
            StreamResult result = new StreamResult(new File("out.xml"));
            String answer = map.get(1).SolXML(map.get(1), map.get(2));
            String line1 = "y = " + map.get(1).k + "x + " + map.get(1).b;
            String line2 = "y = " + map.get(2).k + "x + " + map.get(2).b;
            document2.appendChild(element);
            element.appendChild(getLanguage(document2, line1, line2, answer));
            for(int i = 1; i <= map.size() - 1; i++)
            {
                for(int j = i + 1; j <= map.size(); j++)
                {
                    answer = map.get(i).SolXML(map.get(i), map.get(j));
                    line1 = "y = " + map.get(i).k + "x + " + map.get(i).b;
                    line2 = "y = " + map.get(j).k + "x + " + map.get(j).b;
                    element.appendChild(getLanguage(document2, line1, line2, answer));
                }
            }
            transform.transform(source, result);
        } catch(Exception e) {
            e.getStackTrace();
        }
    }
}
