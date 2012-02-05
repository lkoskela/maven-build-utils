package com.lassekoskela.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.lassekoskela.maven.buildevents.BuildEventListener;
import com.lassekoskela.maven.buildevents.BuildEventLog;
import com.lassekoskela.maven.buildevents.ExecutionListenerChain;
import com.lassekoskela.maven.logging.ConsoleLog;
import com.lassekoskela.maven.logging.FileLog;
import com.lassekoskela.maven.logging.Log;

import static org.apache.commons.lang3.StringUtils.*;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "buildevents")
public class BuildEventsExtension extends AbstractMavenLifecycleParticipant {

	static final String OUTPUT_MODE = "duration.output";
	static final String OUTPUT_FILE = "duration.output.file";
	static final String DEFAULT_FILE_DESTINATION = "target/durations.log";
    static final String ACTIVATION_PROFILE_KEY = "maven-build-utils.activationProfiles";
    
	@Requirement
	Logger logger;

	@Override
	public void afterProjectsRead(MavenSession session)
			throws MavenExecutionException {
		Log log = resolveLogDestination(session);

        if (!isActivationProfilesPropertySet(session))
            log.info("Use " + ACTIVATION_PROFILE_KEY + " property to set in which profiles to run maven-build-utils.");

        if (shouldBeActive(session)) {
            BuildEventListener listener = createListener(log);
            registerExecutionListener(session, listener);
        }
    }

    private boolean shouldBeActive(MavenSession session) {
        boolean shouldBeActive = false;

        List<String> activationProfiles = listActivationProfiles(session);
        for (String currentlyActive : getAllActiveProfileNames(session)) if (activationProfiles.contains(currentlyActive)) shouldBeActive = true;
        return shouldBeActive;
    }

    protected List<String> listActivationProfiles(MavenSession session) {
        return Arrays.asList(split(deleteWhitespace(getActivationProfilesProperty(session)), ','));
    }
    
    protected static String getActivationProfilesProperty(MavenSession session) {
        return session.getCurrentProject().getProperties().getProperty(ACTIVATION_PROFILE_KEY, "default");
    }

    protected static boolean isActivationProfilesPropertySet(MavenSession session) {
        return !isBlank(session.getCurrentProject().getProperties().getProperty(ACTIVATION_PROFILE_KEY));
    }

    protected List<String> getAllActiveProfileNames(MavenSession session) {
        List<String> names = new ArrayList<String>();
        for (Profile profile : session.getCurrentProject().getActiveProfiles()) {
            names.add(profile.getId());
        }
        return names;
    }
    
	protected BuildEventListener createListener(Log log) {
		return new BuildEventListener(new BuildEventLog(log));
	}

	protected Log resolveLogDestination(MavenSession session) {
		String output = getProperty(session, OUTPUT_MODE, "console");
		if (output.equals("file")) {
			return createFileLog(session);
		}
		if (output.equals("console")) {
			return new ConsoleLog(logger);
		}
		logger.error("Invalid configuration: " + output);
		return new ConsoleLog(logger);
	}

	protected Log createFileLog(MavenSession session) {
		String file = getProperty(session, OUTPUT_FILE,
				DEFAULT_FILE_DESTINATION);
		if (new File(file).isAbsolute()) {
			return new FileLog(new File(file));
		}
		String buildDir = session.getExecutionRootDirectory();
		return new FileLog(new File(buildDir, file));
	}

	private String getProperty(MavenSession s, String key, String defaultValue) {
		return s.getUserProperties().getProperty(key, defaultValue);
	}

	protected void registerExecutionListener(MavenSession session,
			ExecutionListener listener) {
		MavenExecutionRequest request = session.getRequest();
		ExecutionListener original = request.getExecutionListener();
		ExecutionListener chain = new ExecutionListenerChain(original, listener);
		request.setExecutionListener(chain);
	}
}
