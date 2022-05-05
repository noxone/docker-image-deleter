package org.olafneumann.github.docker.deleter.data;

@SuppressWarnings("javadoc")
public class Login {
	private final String username;

	private final String password;

	public Login(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
}
