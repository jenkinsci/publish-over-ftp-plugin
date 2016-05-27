/*
 * The MIT License
 *
 * Copyright (C) 2010-2011 by Anthony Robinson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.publish_over_ftp;

import hudson.util.Secret;
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over.Credentials;
import jenkins.plugins.publish_over_ftp.descriptor.BapFtpCredentialsDescriptor;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

public class BapFtpCredentials implements Credentials<BapFtpCredentials> {

    private static final long serialVersionUID = 1L;
    private final String username;
    private final Secret password;

    @DataBoundConstructor
    public BapFtpCredentials(final String username, final String password) {
        this.username = username;
        this.password = Secret.fromString(password);
    }

    public String getUsername() {
        return username;
    }

    public Secret getPassword() {
        return password;
    }

    public BapFtpCredentialsDescriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(BapFtpCredentialsDescriptor.class);
    }

    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || BapFtpCredentials.class != that.getClass()) return false;

        final BapFtpCredentials thatCreds = (BapFtpCredentials) that;
        return new EqualsBuilder()
                .append(username, thatCreds.username)
                .append(password, thatCreds.password)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(username).append(password).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("username", username)
                .append("password", "***")
                .toString();
    }

}
