package com.test.jsoupxpath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.seimicrawler.xpath.JXNode;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
import com.beust.jcommander.internal.Sets;
import com.tool.kj.jsoupxpath.KjJSoupXpath;

public class JSoupXpathTest {

	public static void git() throws FileNotFoundException, XpathSyntaxErrorException, IOException {
		String xpath = "//a[@class='project']/@href";
		// String xpath = "//li[@class='project-row']/div/a/@href";

		Set<String> sets = Sets.newHashSet();
		for (File file : new File("/Users/kuojian21/kj/git/html").listFiles()) {
			List<Object> objs = KjJSoupXpath.parse(new FileReader(file)).sel(xpath);
			for (Object obj : objs) {
				sets.add(obj.toString());
			}
		}
		for (String s : sets.stream().sorted().collect(Collectors.toList())) {
			System.out.println(s.substring(1));
		}
	}

	public static void m() throws FileNotFoundException, XpathSyntaxErrorException, IOException {
		String xpath = "//tr";
		// String xpath = "//li[@class='project-row']/div/a/@href";

		Set<String> sets = Sets.newHashSet();
		File file = new File("/Users/kuojian21/kj/kuaishou-promotion-api/m.list");
		List<JXNode> objs = KjJSoupXpath.parse(new FileReader(file)).selN(xpath);
		for (JXNode obj : objs) {
			List<JXNode> o = obj.sel("//td");
			if (o.isEmpty()) {
				continue;
			}
			// System.out.println(o.get(0).getElement().textNodes().get(0).text());
			// System.out.println(o.get(0).getElement().textNodes().get(0).text().trim());
			sets.add(o.get(0).getElement().textNodes().get(0).text().trim() + "/"
					+ o.get(1).getElement().textNodes().get(0).text().trim());
		}
		for (String s : sets.stream().sorted().collect(Collectors.toList())) {
			System.out.println(s);
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, XpathSyntaxErrorException {
		m();
	}

}
