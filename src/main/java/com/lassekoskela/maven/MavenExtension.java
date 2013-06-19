package com.lassekoskela.maven;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Profile;

import com.lassekoskela.maven.buildevents.BuildEventListener;
import com.lassekoskela.maven.buildevents.BuildEventLog;
import com.lassekoskela.maven.buildevents.ExecutionListenerChain;
import com.lassekoskela.maven.logging.Log;

public class MavenExtension extends AbstractMavenLifecycleParticipant {

    public boolean shouldBeActive(MavenSession session, String activationPropertyKey, String activationProfileKey) {
        boolean shouldBeActive = Boolean.valueOf(getActivationProperty(session, activationPropertyKey));
        if (shouldBeActive) {
        	return shouldBeActive;
        }

        List<String> activationProfiles = listActivationProfiles(session, activationProfileKey);
        for (String currentlyActive : getAllActiveProfileNames(session)) if (activationProfiles.contains(currentlyActive)) shouldBeActive = true;
        return shouldBeActive;
    }

    protected List<String> listActivationProfiles(MavenSession session, String activationProfileKey) {
        return Arrays.asList(split(deleteWhitespace(getActivationProfilesProperty(session, activationProfileKey)), ','));
    }
    
    protected static String getActivationProfilesProperty(MavenSession session, String activationProfileKey) {
        return session.getCurrentProject().getProperties().getProperty(activationProfileKey, "default");
    }

    
    private String getActivationProperty(MavenSession session, String activationPropertyKey) {
        return session.getCurrentProject().getProperties().getProperty(activationPropertyKey, Boolean.FALSE.toString());
    }

    public boolean isActivationProfilesPropertySet(MavenSession session, String activationProfileKey) {
        return !isBlank(session.getCurrentProject().getProperties().getProperty(activationProfileKey));
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

	public String getProperty(MavenSession s, String key, String defaultValue) {
		return s.getUserProperties().getProperty(key, defaultValue);
	}

	protected void registerExecutionListener(MavenSession session, ExecutionListener listener) {
		MavenExecutionRequest request = session.getRequest();
		ExecutionListener original = request.getExecutionListener();
		ExecutionListener chain = new ExecutionListenerChain(original, listener);
		request.setExecutionListener(chain);
	}
}
