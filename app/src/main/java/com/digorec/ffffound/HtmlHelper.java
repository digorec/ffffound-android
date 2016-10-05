package com.digorec.ffffound;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlHelper {
    TagNode rootNode;

    public HtmlHelper(URL htmlPage) throws IOException {
        HtmlCleaner cleaner = new HtmlCleaner();
        rootNode = cleaner.clean(htmlPage);
    }

    List<TagNode> getLinksByClass(String CSSClassname)  {
        List<TagNode> linkList = new ArrayList<TagNode>();
        TagNode linkElements[] = rootNode.getElementsByName("a", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++) {
            String classType = linkElements[i].getAttributeByName("class");
            if (classType != null && classType.equals(CSSClassname)) {
                linkList.add(linkElements[i]);
            }
        }
        return linkList;
    }

    List<TagNode> getTagsByClass(String Tag, String CSSClassname)  {
        List<TagNode> linkList = new ArrayList<TagNode>();
        TagNode linkElements[] = rootNode.getElementsByName(Tag, true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++) {
            String classType = linkElements[i].getAttributeByName("class");
            if (classType != null && classType.equals(CSSClassname)) {
                linkList.add(linkElements[i]);
            }
        }
        return linkList;
    }
}
