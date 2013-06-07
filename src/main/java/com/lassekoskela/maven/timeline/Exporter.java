/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package com.lassekoskela.maven.timeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.codehaus.plexus.logging.Logger;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.lassekoskela.maven.BuildTimelineExtension;
import com.lassekoskela.maven.bean.Timeline;
import com.lassekoskela.maven.timeline.GoalOrganizer.DisplayableGoal;

public class Exporter {

	private static final String TEMPLATE_TIMELINE = "timeline.mustache";
	private static final String TEMPLATE_BUILDITEM = "buildItem.mustache";
	private static final String EXPORT_FILE = "timeline.html";
	
	private final GoalOrganizer goalOrganizer;
	private final Mustache timelineMustache;
	private final Mustache buildItemMustache;
	
	public Exporter(Logger logger) throws FileNotFoundException {
		goalOrganizer = new GoalOrganizer(logger);
		ClassLoader classLoader = BuildTimelineExtension.class.getClassLoader();
		MustacheFactory mustacheFactory = new DefaultMustacheFactory();
		timelineMustache = compileTemplate(classLoader, mustacheFactory, TEMPLATE_TIMELINE);
		buildItemMustache = compileTemplate(classLoader, mustacheFactory, TEMPLATE_BUILDITEM);
	}

	private Mustache compileTemplate(ClassLoader ccl, MustacheFactory mf, String template) {
		return mf.compile(new InputStreamReader(ccl.getResourceAsStream(template)), template);
	}
	
	public File export(Timeline timeline) throws TimelineExportException {
		try {
			return serializeTimelineToFile(serializeBuildItems(timeline));
		} catch (Exception e) {
			throw new TimelineExportException("Cannot export the timeline as an HTML page", e);
		}
	}

	private File serializeTimelineToFile(String serializedBuildItems) throws IOException {
		File exportFile = getExportFile(EXPORT_FILE);
		timelineMustache.execute(new FileWriter(exportFile), buildTimelineModel(serializedBuildItems)).flush();
		return exportFile;
	}

	private String serializeBuildItems(Timeline timeline) throws IOException {
		Writer buildItemsWriter = new StringWriter();
		for (DisplayableGoal displayableGoal : goalOrganizer.organize(timeline)) {
			buildItemMustache.execute(buildItemsWriter, buildItemModel(displayableGoal));
		}
		buildItemsWriter.flush();
		return buildItemsWriter.toString();
	}

	@VisibleForTesting File getExportFile(String filePath) {
		File exportFile = new File(filePath);
		if (exportFile.exists() && !exportFile.delete()) {
			throw new IllegalStateException("Cannot delete existing export file");
		}
		return exportFile;
	}

	@VisibleForTesting Map<String, String> buildTimelineModel(String serializedBuildItems) {
		return ImmutableMap.of("timeline", serializedBuildItems);
	}
	
	@VisibleForTesting Map<String, String> buildItemModel(DisplayableGoal goal) {
		return ImmutableMap.of(
				"projectId", goal.getProjectId(),
				"phaseId", goal.getPhaseId(),
				"goalId", goal.getGoalId(),
				"projectDeps", goal.getDependencies(),
				"cssStyle", cssStyle(goal));
	}

	@VisibleForTesting String cssStyle(DisplayableGoal goal) {
		return String.format("height:%dpx;top:%dpx;left:%dpx;",
				goal.getHeightPosition(), goal.getTopPosition(), goal.getLeftPosition());
	}
}
