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

package jenkins.plugins.publish_over_ftp.options;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over.BPTransfer;
import org.kohsuke.stapler.DataBoundConstructor;

public class FtpOverrideTransferDefaults implements FtpTransferOptions, Describable<FtpOverrideTransferDefaults> {

    private final boolean asciiMode;
    private final String sourceFiles;
    private final String removePrefix;
    private final String remoteDirectory;
    private final String excludes;
    private final boolean remoteDirectorySDF;
    private final boolean flatten;
    private final boolean cleanRemote;
    private final boolean noDefaultExcludes;
    private final boolean makeEmptyDirs;
    private final String patternSeparator;

    @DataBoundConstructor
    public FtpOverrideTransferDefaults(final String sourceFiles, final String excludes, final String removePrefix,
                                       final String remoteDirectory, final boolean flatten, boolean remoteDirectorySDF,
                                       final boolean cleanRemote, final boolean asciiMode, final boolean noDefaultExcludes,
                                       final boolean makeEmptyDirs, final String patternSeparator) {
        this.asciiMode = asciiMode;
        this.cleanRemote = cleanRemote;
        this.excludes = excludes;
        this.flatten = flatten;
        this.remoteDirectory = remoteDirectory;
        this.remoteDirectorySDF = remoteDirectorySDF;
        this.removePrefix = removePrefix;
        this.sourceFiles = sourceFiles;
        this.noDefaultExcludes = noDefaultExcludes;
        this.makeEmptyDirs = makeEmptyDirs;
        this.patternSeparator = patternSeparator;
    }

    public String getSourceFiles() {
        return sourceFiles;
    }

    public String getRemovePrefix() {
        return removePrefix;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public String getExcludes() {
        return excludes;
    }

    public boolean isRemoteDirectorySDF() {
        return remoteDirectorySDF;
    }

    public boolean isFlatten() {
        return flatten;
    }

    public boolean isCleanRemote() {
        return cleanRemote;
    }

    public boolean isAsciiMode() {
        return asciiMode;
    }

    public boolean isNoDefaultExcludes() {
        return noDefaultExcludes;
    }

    public boolean isMakeEmptyDirs() {
        return makeEmptyDirs;
    }

    public String getPatternSeparator() {
        return patternSeparator;
    }

    public FtpOverrideTransferDefaultsDescriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(FtpOverrideTransferDefaultsDescriptor.class);
    }

    @Extension
    public static class FtpOverrideTransferDefaultsDescriptor extends Descriptor<FtpOverrideTransferDefaults> {

        @Override
        public String getDisplayName() {
            return "FtpOverrideTransferDefaultsDescriptor - not visible ...";
        }

        public jenkins.plugins.publish_over.view_defaults.BPTransfer.Messages getCommonFieldNames() {
            return new jenkins.plugins.publish_over.view_defaults.BPTransfer.Messages();
        }

    }

}
