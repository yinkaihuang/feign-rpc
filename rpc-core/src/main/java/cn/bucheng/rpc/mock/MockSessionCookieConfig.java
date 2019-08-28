package cn.bucheng.rpc.mock;

import javax.servlet.SessionCookieConfig;

import org.springframework.lang.Nullable;

/**
 * Mock implementation of the {@link javax.servlet.SessionCookieConfig} interface.
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see javax.servlet.ServletContext#getSessionCookieConfig()
 */
public class MockSessionCookieConfig implements SessionCookieConfig {

	@Nullable
	private String name;

	@Nullable
	private String domain;

	@Nullable
	private String path;

	@Nullable
	private String comment;

	private boolean httpOnly;

	private boolean secure;

	private int maxAge = -1;


	@Override
	public void setName(@Nullable String name) {
		this.name = name;
	}

	@Override
	@Nullable
	public String getName() {
		return this.name;
	}

	@Override
	public void setDomain(@Nullable String domain) {
		this.domain = domain;
	}

	@Override
	@Nullable
	public String getDomain() {
		return this.domain;
	}

	@Override
	public void setPath(@Nullable String path) {
		this.path = path;
	}

	@Override
	@Nullable
	public String getPath() {
		return this.path;
	}

	@Override
	public void setComment(@Nullable String comment) {
		this.comment = comment;
	}

	@Override
	@Nullable
	public String getComment() {
		return this.comment;
	}

	@Override
	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	@Override
	public boolean isHttpOnly() {
		return this.httpOnly;
	}

	@Override
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public boolean isSecure() {
		return this.secure;
	}

	@Override
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	@Override
	public int getMaxAge() {
		return this.maxAge;
	}

}