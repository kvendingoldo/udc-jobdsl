package jobdsl.build.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcPreCommit {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            concurrentBuild()
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            wrappers {
              colorizeOutput()
              timestamps()
              preBuildCleanup()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        url(jobConfig.job.repository)
                        refspec('+refs/pull/*:refs/remotes/origin/pr/*')
                    }
                    branch('${sha1}')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
            }
            triggers {
                githubPullRequest {
                    cron('*/5 * * * *')
                    permitAll()
                }
            }
            steps {
                maven {
                    goals('clean install')
                    goals(' -B')
                    mavenInstallation(jobConfig.tools.maven)
                }
            }
        }
    }
}
