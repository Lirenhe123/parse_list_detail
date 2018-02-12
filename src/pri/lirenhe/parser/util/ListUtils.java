package pri.lirenhe.parser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ListUtils {

	public static String resolveId(Element aTag, String idStartToTrim, String idEndToTrim) {
		String id = aTag.attr("href").trim();

		while (id.startsWith(".")) {
			for (; id.startsWith(".");) {
				id = id.substring(id.indexOf(".") + 1, id.length());
			}
			if (id.startsWith("/")) {
				id = id.substring(id.indexOf("/") + 1, id.length());
			}
		}

		for (; id.endsWith(".");) {
			id = id.substring(0, id.lastIndexOf("."));
		}

		// re_id
		if (!id.contains("?") && !id.startsWith("/")) {
			id = "/" + id;
		}
		int idStart = 0;
		int idEnd = 0;
		if (idStartToTrim.equals("")) {
			idStart = 0;
		} else {
			idStart = id.indexOf(idStartToTrim) + idStartToTrim.length();
		}
		if (idEndToTrim.equals("")) {
			idEnd = id.length();
		} else {
			idEnd = id.lastIndexOf(idEndToTrim);
		}
		id = id.substring(idStart, idEnd);

		return id;
	}
	
	public static String resolveUrl(String newPathPrefix, String additionalLinkParamStr, String id) {
		String url = "";
		url = newPathPrefix + "/?" + additionalLinkParamStr + "&iw-cmd=Y00229_Detail&" + "iw_ir_1=" + id;
		return url;
	}
	
	public static String resolveTitle(Element aTag, String regexOfTitle) {
		String title = null;
		if (!regexOfTitle.equals("")) {
			Element title_html = null;
			title_html = aTag.select(regexOfTitle).last();
			if (title_html != null) {
				title = title_html.text();
			}
		} else {
			title = aTag.attr("title").trim();
			if (title == null || title.equals("null") || title.equals("")) {
				title = aTag.text().trim();
			}
		}
		title = title.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
		// matchesTitle()
		String regex = "(.*)(\\[|.)(20\\d{2}\\D[01][0-9]\\D[0123][0-9])";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(title);
		if (matcher.find()) {
			title = matcher.group(1);
		}
		if (title.contains("[") || title.contains("]")) {
			title = title.substring(title.indexOf("]") + 1, title.length());
		}
		for (; title.endsWith(".");) {
			title = title.substring(0, title.lastIndexOf("."));
		}
		return title;
	}
	public static String resolveDate(String regexOfDate, Element row) {
		String date = null;
		Elements date_htmls = row.select(regexOfDate);
		String regex = "(.*)(20\\d{2})(\\D)([01][0-9])(\\D)([0123][0-9])(.*)";
		Pattern pattern = Pattern.compile(regex);
		String year = null;
		String month = null;
		String day = null;
		if (date_htmls != null) {
			for (Element date_html : date_htmls) {
				date = date_html.text().trim();
				Matcher matcher = pattern.matcher(date);
				if (matcher.find()) {
					year = matcher.group(2);
					month = matcher.group(4);
					day = matcher.group(6);
					if (month.length() < 2) {
						month = "0" + month;
					}
					if (day.length() < 2) {
						day = "0" + day;
					}
					break;
				}
			}
			date = year + "-" + month + "-" + day;
		}
		return date;
	}
	
	public static String resolvePage(Element allFatherTagOfPage, String pageNumRegex, int groupId) {
		String page = null;
		String allFatherTagOfPage_str = null;
		allFatherTagOfPage_str = allFatherTagOfPage.text()
				.replaceAll("[\u00a0\u1680\u180e\u2000-\u200a\u2028\u2029\u202f\u205f\u3000\ufeff\\s+]", "");
		;
		page = allFatherTagOfPage_str;
		String regex = "";
		int groupIdd = 0;
		if (!pageNumRegex.equals("")) {
			groupIdd = groupId;
			regex = pageNumRegex;
		} else {
			if (page.contains("/")) {
				regex = "(.*)/(\\d+)(.*)";
				groupIdd = 2;
			} else if (page.contains("共")) {
				regex = "(.*)共(\\d+)页(.*)";
				groupIdd = 2;
			}
		}
		if (!regex.equals("")) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(page);
			if (matcher.find()) {
				page = matcher.group(groupIdd);

			}
		}
		if (page.equals(allFatherTagOfPage_str)) {
			return null;
		}
		return page;
	}
	
	public boolean rowFilter(Element row) {
		boolean shouldBeFilter = false;
		if (row.attr("class").contains("page") || row.attr("class").contains("age")) {
			shouldBeFilter = true;
		}
		return shouldBeFilter;
	}
	
	// re_stake
		public static Object getStake(String syntax, Element element, int regPartNum, boolean returnAllElement)
				throws Exception {
			String regLeft = null; // jsoup 的selector的syntax
			String regRight = null; // 准确地位元素element的index
			Elements regParts = null;
			Element regPart = null;

			// 解析syntax
			if (syntax.contains("::")) {
				regLeft = syntax.substring(syntax.indexOf("{") + 1, syntax.lastIndexOf("}"));
				regRight = syntax.substring(syntax.lastIndexOf("::") + 2, syntax.length());
				if (regRight.startsWith("<")) {
					regRight = regRight.substring(regRight.indexOf("<") + 1, regRight.length());
				}
			} else {
				regLeft = syntax;
				regRight = "1";
			}
			// 解析regLeft，使用jsoup的selector
			if (regLeft != null && !regLeft.equals("")) {
				regParts = element.select(regLeft);
			} else {
				System.out.println("regLeft is null");
			}
			if (regParts != null && !regParts.toString().equals("")) {
				if (returnAllElement) {// 当returnAllElement为true时，返回所有定位的元素（集合Elements）
					return regParts;
				}
				if (regParts.size() == 1) {
					regPart = regParts.first();
					for (int i = 1; i <= new Integer(regRight) - 1; i++) {
						regPart = regPart.parent();
					}
				} else {
					regPart = regParts.get(regPartNum - 1);// regPartNum确定当定位的元素element多个时，到底使用第几个element，一般使用第一个元素（first）
					for (int i = 1; i <= new Integer(regRight) - 1; i++) {
						regPart = regPart.parent();
					}
				}
			} else {
				throw new Exception("regParts is null, please check your syntax=" + syntax);
			}

			return regPart;
		}
}
