package com.lassekoskela.maven;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.lassekoskela.maven.timeline.BuildTimelineListener;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "buildtimeline")
public class BuildTimelineExtension extends MavenExtension {

    static final String ACTIVATION_PROPERTY_KEY = "maven-build-utils.activate-timeline";
    static final String ACTIVATION_PROFILE_KEY = "maven-build-utils.activationTimelineProfiles";
    
	@Requirement
	Logger logger;

	@Override
	public void afterProjectsRead(MavenSession session)
			throws MavenExecutionException {

        if (shouldBeActive(session, ACTIVATION_PROPERTY_KEY, ACTIVATION_PROFILE_KEY)) {
            registerExecutionListener(session, new BuildTimelineListener(logger));
        }
    }
}
