package com.lassekoskela.maven;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.rtinfo.RuntimeInformation;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.lassekoskela.maven.buildevents.BuildEventListener;
import com.lassekoskela.maven.buildevents.BuildEventLog;
import com.lassekoskela.maven.buildevents.ExecutionListenerChain;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "buildevents")
public class BuildEventsExtension extends AbstractMavenLifecycleParticipant {

	@Requirement
	private Logger logger;

	@Requirement
	RuntimeInformation runtime;

	@Override
	public void afterProjectsRead(MavenSession session)
			throws MavenExecutionException {
		ExecutionListener original = session.getRequest()
				.getExecutionListener();
		BuildEventLog log = new BuildEventLog(logger);
		ExecutionListener injected = new BuildEventListener(log);
		ExecutionListener chain = new ExecutionListenerChain(original, injected);
		session.getRequest().setExecutionListener(chain);
	}

	void log(CharSequence message) {
		logger.info(message.toString());
	}
}
