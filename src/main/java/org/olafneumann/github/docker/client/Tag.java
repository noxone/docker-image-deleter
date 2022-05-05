package org.olafneumann.github.docker.client;

import java.time.ZonedDateTime;

@SuppressWarnings("javadoc")
public class Tag {
	private final String repositoryName;

	private final String name;

	private final ZonedDateTime lastUpdated;

	Tag(final String repositoryName, final String name, final String lastUpdated) {
		this(repositoryName, name, ZonedDateTime.parse(lastUpdated));
	}

	Tag(final String repositoryName, final String name, final ZonedDateTime lastUpdated) {
		this.repositoryName = repositoryName;
		this.name = name;
		this.lastUpdated = lastUpdated;
	}

	public ZonedDateTime getLastUpdated() {
		return lastUpdated;
	}

	public String getName() {
		return name;
	}

	public String getRepositoryName() {
		return repositoryName;
	}
}
