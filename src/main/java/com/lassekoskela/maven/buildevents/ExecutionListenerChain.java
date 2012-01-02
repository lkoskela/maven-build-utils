package com.lassekoskela.maven.buildevents;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionListener;

/**
 * Chaining ExecutionListener
 */
public class ExecutionListenerChain implements ExecutionListener {

	private List<ExecutionListener> listeners;

	public ExecutionListenerChain(ExecutionListener... listeners) {
		this.listeners = new ArrayList<ExecutionListener>(asList(listeners));
	}

	@Override
	public void projectDiscoveryStarted(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.projectDiscoveryStarted(event);
		}
	}

	@Override
	public void sessionStarted(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.sessionStarted(event);
		}
	}

	@Override
	public void sessionEnded(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.sessionEnded(event);
		}
	}

	@Override
	public void projectSkipped(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.projectSkipped(event);
		}
	}

	@Override
	public void projectStarted(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.projectStarted(event);
		}
	}

	@Override
	public void projectSucceeded(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.projectSucceeded(event);
		}
	}

	@Override
	public void projectFailed(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.projectFailed(event);
		}
	}

	@Override
	public void mojoSkipped(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.mojoSkipped(event);
		}
	}

	@Override
	public void mojoStarted(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.mojoStarted(event);
		}
	}

	@Override
	public void mojoSucceeded(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.mojoSucceeded(event);
		}
	}

	@Override
	public void mojoFailed(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.mojoFailed(event);
		}
	}

	@Override
	public void forkStarted(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.forkStarted(event);
		}
	}

	@Override
	public void forkSucceeded(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.forkSucceeded(event);
		}
	}

	@Override
	public void forkFailed(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.forkFailed(event);
		}
	}

	@Override
	public void forkedProjectStarted(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.forkedProjectStarted(event);
		}
	}

	@Override
	public void forkedProjectSucceeded(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.forkedProjectSucceeded(event);
		}
	}

	@Override
	public void forkedProjectFailed(ExecutionEvent event) {
		for (ExecutionListener listener : listeners) {
			listener.forkedProjectFailed(event);
		}
	}
}