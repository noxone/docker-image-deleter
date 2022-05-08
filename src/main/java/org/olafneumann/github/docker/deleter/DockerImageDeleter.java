package org.olafneumann.github.docker.deleter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.olafneumann.github.docker.client.DockerClient;
import org.olafneumann.github.docker.client.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.larssh.utils.Finals;
import picocli.CommandLine;
import picocli.CommandLine.Option;

@SuppressWarnings("javadoc")
public class DockerImageDeleter implements Callable<Integer> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DockerImageDeleter.class);

	private static final String DEFAULT_URL_STRING = Finals.constant("https://hub.docker.com/v2");

	public static void main(final String... args) {
		final int exitCode = new CommandLine(new DockerImageDeleter()).execute(args);
		System.exit(exitCode);
	}

	@Option(required = true,
			names = "--url",
			description = "The URL of the docker repository to look at.",
			paramLabel = "URL")
	private URL repositoryUrl;

	@Option(required = true, names = "--user", description = "The user to be used for repo actions.")
	private String username;

	@Option(names = "--token", description = "The password/ token to be used.")
	private String token;

	@Option(names = "--repositories",
			description = "Names of the repositories to handle. Leave out to handle all repositories.",
			split = "[ ,]+")
	private List<String> repositories;

	@Option(names = "--keep", description = "Number of tags to keep", required = true)
	private int numberOfTagsToKeep;

	@Override
	public Integer call() {
		try {
			doWork();
			return 0;
		} catch (final IOException e) {
			e.printStackTrace();
			return 2;
		}
	}

	private void doWork() throws IOException {
		final DockerClient client = new DockerClient(repositoryUrl.toString());

		client.login(username, token);

		final List<String> repositoryNames
				= repositories != null && !repositories.isEmpty() ? repositories : client.readRepositories();

		LOGGER.debug("Working for repos: {}", repositoryNames);

		for (final String repositoryName : repositoryNames) {
			workOnRepository(client, repositoryName);
		}
	}

	private void workOnRepository(final DockerClient client, final String repositoryName) throws IOException {
		final List<Tag> tags = client.readTagsForRepository(repositoryName);
		final List<Tag> tagsToDelete = tags.stream()//
				.sorted((x, y) -> y.getLastUpdated().compareTo(x.getLastUpdated()))
				.skip(numberOfTagsToKeep)
				.collect(Collectors.toList());
		for (final Tag tag : tagsToDelete) {
			LOGGER.info("Deleting tag '{}' in repository '{}'.", tag.getName(), tag.getRepositoryName());
			// tag.delete();
		}
	}
}
