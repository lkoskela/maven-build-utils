package com.lassekoskela.maven;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

import com.lassekoskela.maven.buildevents.BuildEventListener;
import com.lassekoskela.maven.buildevents.BuildEventLog;
import com.lassekoskela.maven.buildevents.ExecutionListenerChain;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "buildevents")
public class BuildEventsExtension extends AbstractMavenLifecycleParticipant {
	@Override
	public void afterProjectsRead(MavenSession session)
			throws MavenExecutionException {
		ExecutionListener original = session.getRequest()
				.getExecutionListener();
		BuildEventLog log = new BuildEventLog();
		ExecutionListener injected = new BuildEventListener(log);
		ExecutionListener chain = new ExecutionListenerChain(original, injected);
		session.getRequest().setExecutionListener(chain);
	}
}
