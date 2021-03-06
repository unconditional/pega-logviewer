/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;

public class PEGA0037ReportModel extends AlertMessageReportModel {

	private static final long serialVersionUID = -8889727175209305065L;

	private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0037ReportModel.class);

	private List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList;

	private Pattern pattern;

	public PEGA0037ReportModel(long thresholdKPI, String kpiUnit, AlertLogEntryModel alertLogEntryModel) {

		super("PEGA0037", thresholdKPI, kpiUnit, alertLogEntryModel);

		String regex = "Assembled class name = (.*);Assembled class size";
		pattern = Pattern.compile(regex);
	}

	@Override
	protected List<AlertBoxAndWhiskerReportColumn> getAlertMessageReportColumnList() {

		if (alertMessageReportColumnList == null) {
			alertMessageReportColumnList = new ArrayList<AlertBoxAndWhiskerReportColumn>();

			String displayName;
			int prefColWidth;
			int hAlignment;
			boolean filterable;
			AlertBoxAndWhiskerReportColumn amReportColumn = null;

			// first column data is the key
			displayName = "FUA Keys/Assembled Class Name";
			prefColWidth = 500;
			hAlignment = SwingConstants.LEFT;
			filterable = true;
			amReportColumn = new AlertBoxAndWhiskerReportColumn(AlertBoxAndWhiskerReportColumn.KEY, displayName, prefColWidth, hAlignment, filterable);

			alertMessageReportColumnList.add(amReportColumn);

			List<AlertBoxAndWhiskerReportColumn> defaultAlertMessageReportColumnList = AlertBoxAndWhiskerReportColumn.getDefaultAlertMessageReportColumnList();

			alertMessageReportColumnList.addAll(defaultAlertMessageReportColumnList);
		}

		return alertMessageReportColumnList;
	}

	@Override
	public String getAlertMessageReportEntryKey(AlertLogEntry alertLogEntry, ArrayList<String> logEntryValueList) {

		String alertMessageReportEntryKey = null;

		AlertLogEntryModel alertLogEntryModel = getAlertLogEntryModel();

		List<String> logEntryColumnList = alertLogEntryModel.getLogEntryColumnList();

		int messageIndex = logEntryColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());
		String message = logEntryValueList.get(messageIndex);

		int lastIndex = message.lastIndexOf("=");
		int length = message.length();

		if (lastIndex != -1) {

			String fuaKeys = message.substring(lastIndex + 1, length);
			fuaKeys = fuaKeys.trim();

			if ("".equals(fuaKeys)) {

				Matcher patternMatcher = pattern.matcher(message);
				boolean matches = patternMatcher.find();

				if (matches) {
					alertMessageReportEntryKey = patternMatcher.group(1).trim();
				}

			} else {
				alertMessageReportEntryKey = fuaKeys;
			}

		}

		if (alertMessageReportEntryKey == null) {
			LOG.info("PEGA0037ReportModel - Could'nt match - [" + message + "]");
		}

		return alertMessageReportEntryKey;
	}

	public static void main(String[] args) {

		// case 1
		long before = System.currentTimeMillis();
		String message1 = "Rule assembly process has exceeded the threshold of 400 ms: 417 ms. Details: ;Total Rule assembly process: ELAPSED time = 417;CPU time = 0;Delta Java assembly process: Delta Assembly ELAPSED time = 53;Delta Assembly CPU time = 0;Delta Compile process: Delta Compile ELAPSED time = 364;Delta Compile CPU time = 0;Personal Ruleset =  ;AccessGroup = ProdMgmtMB:Mgr_BackOffice;Application = ProdMgmtMB 01.06;FuaKeys = PXINSHANELD=RULE-OBJ-WHEN WORK- PZSHOWACTIONASBUTTONS #20130919T005132.921 GMT";

		String regex = ".*=(.*)";

		Pattern pattern = Pattern.compile(regex);

		Matcher patternMatcher = pattern.matcher(message1);
		boolean matches = patternMatcher.find();
		System.out.println(matches);

		if (matches) {
			System.out.println(patternMatcher.groupCount());
			System.out.println(patternMatcher.group(1));
		}

		long after = System.currentTimeMillis();

		System.out.println("case 1: " + (after - before));

		// case 2
		before = System.currentTimeMillis();
		int lastIndex = message1.lastIndexOf("=");
		int length = message1.length();

		if (lastIndex != -1) {
			String key = message1.substring(lastIndex + 1, length);
			System.out.println(key);
		}

		after = System.currentTimeMillis();

		System.out.println("case 2: " + (after - before));

		// case 3
		before = System.currentTimeMillis();

		message1 = "Rule assembly process has exceeded the threshold of 400 ms: 717 ms. Details: ;Total Rule assembly process: ELAPSED time = 717;CPU time = 0;Delta Java assembly process: Delta Assembly ELAPSED time = 468;Delta Assembly CPU time = 0;Delta Compile process: Delta Compile ELAPSED time = 249;Delta Compile CPU time = 0;Assembled class name = com.pegarules.generated.html_section.ra_stream_pyactionarea_af7431ae43e91f2c3e3813daeeb5643d;Assembled class size = 31,528;Rules count = 11;Personal Ruleset = ;AccessGroup = CPMS:UserAdministration;Application = UserAdministrator 01.01.01;FuaKeys =";

		regex = "Assembled class name = (.*);Assembled class size";
		pattern = Pattern.compile(regex);

		patternMatcher = pattern.matcher(message1);
		matches = patternMatcher.find();

		System.out.println(matches);

		if (matches) {
			System.out.println(patternMatcher.groupCount());
			System.out.println(patternMatcher.group(1));
		} else {

		}

		after = System.currentTimeMillis();

		System.out.println("case 3: " + (after - before));
	}

}
