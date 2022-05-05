package org.olafneumann.github.docker.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jayway.jsonpath.JsonPath;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressWarnings("javadoc")
public class DockerClient {
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private final OptionalBearerInterceptor bearerInterceptor = new OptionalBearerInterceptor();

	private final OkHttpClient client;

	private final String baseUrlString;

	private Optional<String> username = Optional.empty();

	public DockerClient(final String baseUrlString) {
		this.baseUrlString = baseUrlString.replaceAll("^(.*?)/?$", "$1");
		client = new OkHttpClient.Builder().callTimeout(Duration.ofMinutes(1))
				.connectTimeout(Duration.ofMinutes(1))
				.readTimeout(Duration.ofMinutes(1))
				.addInterceptor(bearerInterceptor)
				.build();
	}

	private URL createUrl(final String path) {
		try {
			return new URL(baseUrlString + path);
		} catch (final MalformedURLException e) {
			throw new RuntimeException("Unable to create URL", e);
		}
	}

	public void login(final String username, final String password) throws IOException {
		this.username = Optional.of(username);
		@SuppressWarnings("resource")
		final String jsonResponse = readForString(postJsonForResponse(createUrl("/users/login"),
				String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password)));
		final String token = JsonPath.compile("$.token").read(jsonResponse);
		bearerInterceptor.setToken(Optional.of(token));
	}

	public List<String> readRepositories() throws IOException {
		final String jsonResponse = getForString(createUrl("/repositories/" + username.get()));
		return JsonPath.compile("$.results[*].name").read(jsonResponse);
	}

	public List<Tag> readTagsForRepository(final String repoName) throws IOException {
		final String jsonResponse = getForString(
				createUrl(String.format("/repositories/%s/%s/tags?page_size=100", username.get(), repoName)));
		final List<Map<String, String>> list
				= JsonPath.compile("$.results[*].['name','last_updated']").read(jsonResponse);
		return list.stream()//
				.map(map -> new Tag(repoName, map.get("name"), map.get("last_updated")))
				.collect(Collectors.toList());
	}

	public boolean delete(final Tag tag) throws IOException {
		final Request request = new Request.Builder()//
				.url(createUrl(String
						.format("repositories/%s/%s/tags/%s", username.get(), tag.getRepositoryName(), tag.getName())))
				.delete()
				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.isSuccessful();
		}
	}

	private Response postJsonForResponse(final URL url, final String json) throws IOException {
		final RequestBody requestBody = RequestBody.create(json, JSON);
		final Request request = new Request.Builder()//
				.url(url)
				.post(requestBody)
				.build();
		return client.newCall(request).execute();
	}

	private String readForString(final Response response) throws IOException {
		try (Response r = response;
				ResponseBody body = r.body()) {
			return body.string();
		}
	}

	private Response getForResponse(final URL url) throws IOException {
		final Request request = new Request.Builder().url(url).get().build();
		return client.newCall(request).execute();
	}

	private String getForString(final URL url) throws IOException {
		return readForString(getForResponse(url));
	}
}
