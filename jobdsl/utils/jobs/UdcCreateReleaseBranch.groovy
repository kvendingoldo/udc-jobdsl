package jobdsl.utils.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcCreateReleaseBranch {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam('COMMIT', '', '')
                stringParam('RELEASE_FAMILY', '', '')
            }
            wrappers {
                colorizeOutput()
                timestamps()
                preBuildCleanup()
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository, 'ssh')
                    }
                    branch('refs/heads/master')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
            }
            steps {
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
                envInjectBuilder {
                    propertiesFilePath('variables.txt')
                    propertiesContent('')
                }
            }
            publishers {
                gitPublisher {
                    branchesToPush {
                        branchToPush {
                            branchName('${RELEASE_BRANCH_NAME}')
                            targetRepoName('origin')
                        }
                    }
                    pushOnlyIfSuccess(true)
                    pushMerge(false)
                    forcePush(false)
                }
            }
        }
    }
}
