package com.lassekoskela.maven;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.lassekoskela.maven.buildevents.BuildEventListener;
import com.lassekoskela.maven.buildevents.BuildEventLog;
import com.lassekoskela.maven.buildevents.ExecutionListenerChain;
import com.lassekoskela.maven.buildevents.Log;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "buildevents")
public class BuildEventsExtension extends AbstractMavenLifecycleParticipant {

	@Requirement
	private Logger logger;

	@Override
	public void afterProjectsRead(MavenSession session)
			throws MavenExecutionException {
		Log destination = resolveLogDestination();
		BuildEventLog log = new BuildEventLog(destination);
		registerExecutionListener(session, new BuildEventListener(log));
	}

	protected Log resolveLogDestination() {
		Log destination = new ConsoleLog(logger);
		return destination;
	}

	protected void registerExecutionListener(MavenSession session,
			ExecutionListener listener) {
		MavenExecutionRequest request = session.getRequest();
		ExecutionListener original = request.getExecutionListener();
		ExecutionListener chain = new ExecutionListenerChain(original, listener);
		request.setExecutionListener(chain);
	}
}
