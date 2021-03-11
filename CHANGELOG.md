# Change log

#### 1.9 (03/03/2013)

-   [JENKINS-16681](https://issues.jenkins-ci.org/browse/JENKINS-16681)
    Allow source file names and paths to contain whitespace
    -   Add Advanced Transfer Set option "Pattern separator"
-   [JENKINS-16976](https://issues.jenkins-ci.org/browse/JENKINS-16976)
    Upload creates unneeded folders
    -   Add Advanced FTP Server option "Don't make nested dirs"

#### 1.8 (22/10/2012)

-   [JENKINS-13126](https://issues.jenkins-ci.org/browse/JENKINS-13126)
    Option to create empty directories
-   No default excludes option now available for all versions of Jenkins
-   Exclude files pattern now available for all versions of Jenkins

#### 1.7 (10/09/2012)

-   [JENKINS-14283](https://issues.jenkins-ci.org/browse/JENKINS-14283)
    Enable FTP as a build step
-   [JENKINS-13693](https://issues.jenkins-ci.org/browse/JENKINS-13693)
    Add option to disable default excludes
-   Prefix Publish over to the global config section title
-   Move the defaults configuration in the global config to an Advanced
    section

#### 1.6 (11/01/2012)

-   Advanced option to enable the FTP control channel encoding to be set
    for an FTP server configuration

#### 1.5 (10/11/2011)

-   Enable the server credentials to be specified/overriden when
    configuring the publisher in a job

#### 1.4 (05/08/2011)

-   [JENKINS-10599](https://issues.jenkins-ci.org/browse/JENKINS-10599)
    When using the Flatten files option, do not create the Remote
    directory if there are no files to transfer (unless Clean remote is
    also selected)

#### 1.3 (21/07/2011)

-   [JENKINS-10363](https://issues.jenkins-ci.org/browse/JENKINS-10363)
    Allow the publisher default values to be changed in Manage Jenkins
    (on Jenkins and Hudson 1.391 - 1.395)

#### 1.2 (10/07/2011)

-   Remove duplicate delete button from the system config page

#### 1.1 (09/07/2011)

-   Fixed
    [JENKINS-10268](https://issues.jenkins-ci.org/browse/JENKINS-10268)

#### 1.0 (08/07/2011)

-   Add [Parameterized
    publishing](https://wiki.jenkins.io/display/JENKINS/Publish+Over#PublishOver-parampub)
    [JENKINS-10006](https://issues.jenkins-ci.org/browse/JENKINS-10006)
-   Add ability to
    [retry](https://wiki.jenkins.io/display/JENKINS/Publish+Over#PublishOver-retry)
    the publish
    [JENKINS-10094](https://issues.jenkins-ci.org/browse/JENKINS-10094)
-   Moved the "Verbose output in console" option to the new Advanced
    section containing the other new Server options

#### 0.8 (20/05/2011)

-   Remove "Give the master a NODE\_NAME" option when running on Jenkins
    1.414 or later
-   Default the "Give the master a NODE\_NAME" option to 'master' when
    run on a Jenkins older than 1.414

#### 0.7 (09/05/2011)

-   advanced Transfer Set option to Exclude files  
    (option only available with Jenkins 1.407 and later)

#### 0.6 (10/04/2011)

-   reduce horizontal space taken up by labels in the configuration
    views

#### 0.5 (01/04/2011)

-   clean remote - option to delete all files in the remote directory
    before publishing

#### 0.4 (18/02/2011)

-   passwords encrypted in config files and in UI (now requires
    Hudson \> 1.384 or any Jenkins)
-   environment variables for substitution now include build variables
    (including matrix build axis)

#### 0.3 (16/02/2011)

-   added form validation
-   removed debug logs for new configurations

#### 0.2 (12/02/2011)

-   2 new configuration options when in promotion
    -   Use the workspace when selecting "Source files"
    -   Use the time of the promotion when using "Remote directory is a
        date format"

#### 0.1 (08/02/2011)

-   Initial release

Questions, Comments, Bugs and Feature Requests

Please post questions or comments about this plugin to the [Jenkins User
mailing list](http://jenkins-ci.org/content/mailing-lists).  
To report a bug or request an enhancement to this plugin please [create
a ticket in
JIRA](http://issues.jenkins-ci.org/browse/JENKINS/component/15791).
