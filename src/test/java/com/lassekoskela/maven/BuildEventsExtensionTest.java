package com.lassekoskela.maven;

import static com.lassekoskela.maven.BuildEventsExtension.DEFAULT_FILE_DESTINATION;
import static com.lassekoskela.maven.BuildEventsExtension.OUTPUT_FILE;
import static com.lassekoskela.maven.BuildEventsExtension.OUTPUT_MODE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.buildevents.BuildEventListener;
import com.lassekoskela.maven.logging.ConsoleLog;
import com.lassekoskela.maven.logging.FileLog;
import com.lassekoskela.maven.logging.Log;

public class BuildEventsExtensionTest {

	private static final String BUILD_OUTPUT_DIR = "target";
	private MavenSession session;
	protected Log log;
	private BuildEventsExtension extension;
	private Properties properties;

	@Before
	public void setUp() throws Exception {
		properties = new Properties();
		session = createFakeMavenSession(properties);
		extension = new BuildEventsExtension() {
			@Override
			protected BuildEventListener createListener(Log configuredLog) {
				log = configuredLog;
				return super.createListener(log);
			}
		};
		extension.logger = mock(Logger.class);
	}

	private MavenSession createFakeMavenSession(Properties properties) {
		MavenProject project = createFakeMavenProject();
		MavenSession session = mock(MavenSession.class);
		when(session.getUserProperties()).thenReturn(properties);
		when(session.getRequest())
				.thenReturn(mock(MavenExecutionRequest.class));
		when(session.getCurrentProject()).thenReturn(project);
		when(session.getExecutionRootDirectory()).thenReturn(abspath("."));
		return session;
	}

	protected String abspath(String path) {
		try {
			return new File(path).getCanonicalFile().getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private MavenProject createFakeMavenProject() {
		MavenProject project = mock(MavenProject.class);
		Build build = mock(Build.class);
		when(project.getBuild()).thenReturn(build);
		when(build.getDirectory()).thenReturn(BUILD_OUTPUT_DIR);
		return project;
	}

	@Test
	public void logsToConsoleByDefault() throws Exception {
		extension.afterProjectsRead(session);
		assertThat(log, is(instanceOf(ConsoleLog.class)));
	}

	@Test
	public void canBeExplicitlyToldToLogToConsole() throws Exception {
		properties.setProperty(OUTPUT_MODE, "console");
		extension.afterProjectsRead(session);
		assertThat(log, is(instanceOf(ConsoleLog.class)));
	}

	@Test
	public void canBeToldToLogToDefaultFilesystemLocation() throws Exception {
		properties.setProperty(OUTPUT_MODE, "file");
		extension.afterProjectsRead(session);
		assertThat(log, is(instanceOf(FileLog.class)));
		assertThat(log.destination(), is(abspath(DEFAULT_FILE_DESTINATION)));
	}

	@Test
	public void canBeToldToLogToSpecificFilesystemLocation() throws Exception {
		properties.setProperty(OUTPUT_MODE, "file");
		properties.setProperty(OUTPUT_FILE, "tmp/whatever/specific.log");
		extension.afterProjectsRead(session);
		assertThat(log, is(instanceOf(FileLog.class)));
		assertThat(log.destination(), is(abspath("tmp/whatever/specific.log")));
	}

	@Test
	public void invalidConfigurationDefaultsToConsole() throws Exception {
		properties.setProperty(OUTPUT_MODE, "invalidValue");
		extension.afterProjectsRead(session);
		assertThat(log, is(instanceOf(ConsoleLog.class)));
		verify(extension.logger).error("Invalid configuration: invalidValue");
	}
}
