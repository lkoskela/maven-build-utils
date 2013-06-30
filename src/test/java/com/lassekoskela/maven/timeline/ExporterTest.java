package com.lassekoskela.maven.timeline;

import static com.lassekoskela.maven.timeline.ObjectBuilder.goal;
import static com.lassekoskela.maven.timeline.ObjectBuilder.phase;
import static com.lassekoskela.maven.timeline.ObjectBuilder.project;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLUnit.buildControlDocument;
import static org.custommonkey.xmlunit.XMLUnit.buildTestDocument;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lassekoskela.maven.bean.Project;
import com.lassekoskela.maven.bean.Timeline;
import com.lassekoskela.maven.timeline.GoalOrganizer.DisplayableGoal;

public class ExporterTest {

	private String filePath;
	private Exporter exporter;

	@Before
	public void configureXmlUnit() throws Exception {
		XMLUnit.setIgnoreWhitespace(true);
	}

	@Before
	public void deletePreviousExportFile() throws Exception {
		filePath = "timeline.html";
		File exportFile = new File(filePath);
		if (exportFile.exists() && exportFile.isDirectory()) {
			for (File innerFile : exportFile.listFiles()) {
				innerFile.delete();
			}
			exportFile.delete();
		}
	}

	@Before
	public void setUp() throws FileNotFoundException {
		exporter = new Exporter(new ConsoleLogger());
	}

	@Test
	public void buildItemModelProducesCorrectCSS() {
		DisplayableGoal goal = new DisplayableGoal("project1", "phase1", "goal1", "dep1 dep2", 100, 200, 300);
		assertEquals(exporter.buildItemModel(goal), ImmutableMap.of("projectId", "project1", "phaseId", "phase1",
				"goalId", "goal1", "projectDeps", "dep1 dep2", "cssStyle", "height:300px;top:200px;left:100px;"));
	}

	@Test
	public void exportFileIsNotCreatedUntilWeMakeAnExport() {
		File exportFile = exporter.getExportFile(filePath);
		assertFalse(exportFile.exists());
	}

	@Test
	public void anExistingExportFileIsDeleted() throws IOException {
		File file = new File(filePath);
		file.createNewFile();
		assertTrue(file.exists());

		File exportFile = exporter.getExportFile(filePath);
		assertFalse(exportFile.exists());
	}

	@Test(expected = TimelineExportException.class)
	public void shouldThrowExceptionWhenExportFileAlreadyExistsAsNonEmptyDirectory() throws Exception {
		File directory = new File(filePath);
		directory.mkdir();
		new File(directory, "child.txt").createNewFile();

		exporter.export(new Timeline(ImmutableSet.<Project> of()));
	}

	@Test
	public void exportTimelineOfTwoProjects() throws Exception {
		Timeline timeline = new Timeline(ImmutableSet.of(
				project("project1",
						phase("validate", goal("goal0", 2000, 0),
								goal("goal1", 80000, 2800, "project1-validate-goal0"),
								goal("goal2", 40000, 85000, "project1-validate-goal0 project1-validate-goal1")),
						phase("compile", goal("goal1", 12000, 0),
								goal("goal2", 100000, 20000, "project1-validate-goal0", "project1-compile-goal1"))),
				project("project2",
						phase("test", goal("goal1", 2000, 83000), goal("goal2", 8000, 86000, "project2-test-goal1")),
						phase("install", goal("goal1", 12000, 30000, "project2-install-goal2", "project2-test-goal2"),
								goal("goal2", 4000, 28000)))));

		File htmlFile = exporter.export(timeline);

		Document htmlDoc = buildControlDocument(inputSource(htmlFile));
		Document expectedHtmlDoc = buildTestDocument(inputSource("timeline_two_projects.html"));

		assertXMLEqual(expectedHtmlDoc, htmlDoc);
	}

	private InputSource inputSource(File file) throws FileNotFoundException {
		return new InputSource(new FileInputStream(file));
	}

	private InputSource inputSource(String locationRelativeToClasspath) {
		return new InputSource(ClassLoader.getSystemResourceAsStream(locationRelativeToClasspath));
	}
}
