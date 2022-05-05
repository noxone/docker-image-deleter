package org.olafneumann.github.docker.client;

import java.io.IOException;
import java.util.Optional;

import okhttp3.Interceptor;
import okhttp3.Response;

/*
 * https://stackoverflow.com/questions/22490057/android-okhttp-with-basic-
 * authentication
 */
@SuppressWarnings("javadoc")
public class OptionalBearerInterceptor implements Interceptor {
	private Optional<String> token = Optional.empty();

	public OptionalBearerInterceptor() {
		this.token = Optional.empty();
	}

	public OptionalBearerInterceptor(final String token) {
		this.token = Optional.of(token);
	}

	@Override
	public Response intercept(final Chain chain) throws IOException {
		return chain.proceed(token//
				.map(token -> chain.request()//
						.newBuilder()
						.header("Authorization", String.format("Bearer %s", token))
						.build())
				.orElse(chain.request()));
	}

	public void setToken(final Optional<String> token) {
		this.token = token;
	}
}
