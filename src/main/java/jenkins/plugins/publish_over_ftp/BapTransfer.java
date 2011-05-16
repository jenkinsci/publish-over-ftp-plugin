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

import jenkins.plugins.publish_over.BPTransfer;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

public class BapTransfer extends BPTransfer {

    private static final long serialVersionUID = 1L;

    private boolean asciiMode;

    public BapTransfer(final String sourceFiles, final String remoteDirectory, final String removePrefix, final boolean asciiMode,
                       final boolean remoteDirectorySDF, final boolean flatten) {
        this(sourceFiles, null, remoteDirectory, removePrefix, asciiMode, remoteDirectorySDF, flatten, false);
    }

    @DataBoundConstructor
    public BapTransfer(final String sourceFiles, final String excludes, final String remoteDirectory, final String removePrefix,
                       final boolean asciiMode, final boolean remoteDirectorySDF, final boolean flatten, final boolean cleanRemote) {
        super(sourceFiles, excludes, remoteDirectory, removePrefix, remoteDirectorySDF, flatten, cleanRemote);
        this.asciiMode = asciiMode;
    }

    public boolean isAsciiMode() { return asciiMode; }
    public void setAsciiMode(final boolean asciiMode) { this.asciiMode = asciiMode; }

    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        final BapTransfer thatTransfer = (BapTransfer) that;

        return createEqualsBuilder(thatTransfer)
            .append(asciiMode, thatTransfer.asciiMode)
            .isEquals();
    }

    public int hashCode() {
        return createHashCodeBuilder()
            .append(asciiMode)
            .toHashCode();
    }

    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE))
            .append("asciiMode", asciiMode)
            .toString();
    }

}
