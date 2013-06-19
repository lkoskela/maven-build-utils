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
