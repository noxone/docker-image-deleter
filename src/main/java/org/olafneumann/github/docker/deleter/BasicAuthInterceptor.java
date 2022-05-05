package org.olafneumann.github.docker.deleter;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*
 * https://stackoverflow.com/questions/22490057/android-okhttp-with-basic-
 * authentication
 */
@SuppressWarnings("javadoc")
public class BasicAuthInterceptor implements Interceptor {
	private final String credentials;

	public BasicAuthInterceptor(final String user, final String password) {
		this.credentials = Credentials.basic(user, password);
	}

	@Override
	public Response intercept(final Chain chain) throws IOException {
		final Request request = chain.request();
		final Request authenticatedRequest = request.newBuilder().header("Authorization", credentials).build();
		return chain.proceed(authenticatedRequest);
	}
}
