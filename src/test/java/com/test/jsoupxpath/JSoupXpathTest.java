package com.test.jsoupxpath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
import com.beust.jcommander.internal.Sets;
import com.tool.kj.jsoupxpath.KjJSoupXpath;

public class JSoupXpathTest {

	public static void main(String[] args) throws FileNotFoundException, IOException, XpathSyntaxErrorException {
		String xpath = "//div[@class='groups-list-tree-container']/ul/li/div/div[3]/a/@href";
		//String xpath = "//li[@class='project-row']/div/a/@href";

		Set<String> sets = Sets.newHashSet();
		for (File file : new File("/Users/kuojian21/git/html").listFiles()) {
			List<Object> objs = KjJSoupXpath.parse(new FileReader(file)).sel(xpath);
			for (Object obj : objs) {
				sets.add(obj.toString());
			}
		}
		for (String s : sets.stream().sorted().collect(Collectors.toList())) {
			System.out.println(s.substring(1));
		}
	}

}
